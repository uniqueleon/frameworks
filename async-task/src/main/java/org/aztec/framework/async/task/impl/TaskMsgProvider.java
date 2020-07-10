package org.aztec.framework.async.task.impl;

import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.beanutils.BeanUtils;
import org.aztec.framework.async.task.AsyncTaskConst;
import org.aztec.framework.async.task.AsyncTaskException;
import org.aztec.framework.async.task.AsyncTaskServerBean;
import org.aztec.framework.async.task.entity.AsyncTaskDO;
import org.aztec.framework.async.task.entity.AsyncTaskDetailDO;
import org.aztec.framework.async.task.entity.MQSendResult;
import org.aztec.framework.async.task.event.AsyncTaskEvent;
import org.aztec.framework.async.task.mapper.AsyncTaskDetailMapper;
import org.aztec.framework.async.task.mapper.AsyncTaskMapper;
import org.aztec.framework.disconf.items.AsyncTaskConfigure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.simple.SimpleJob;
import com.sjsc.framework.api.restful.constant.AsyncDetailTaskStatus;
import com.sjsc.framework.api.restful.entity.async.AsyncTaskDTO;
import com.sjsc.framework.api.restful.entity.async.AsyncTaskQO;

@AsyncTaskServerBean
@Component("asyncTaskJob")
public class TaskMsgProvider implements SimpleJob {

	private static final Logger jobLogger = LoggerFactory.getLogger(AsyncTaskConst.TASK_LOG_NAME);

	@Resource
	private AsyncTaskMapper taskDAO;
	
	@Resource
	private AsyncTaskDAO notifier;
	@Resource
	AsyncTaskConfigure taskConfig;
    @Resource
    private AsyncTaskDetailMapper detailDao;
	
//	@Resource(name="dataExportTopicProducer")
//	private DefaultProducer dataExportTopicProducer;
	
	//@Resource
	//private Map<String,MQProducerBean> producerMap;
	@Resource
	private ApplicationContext context;

	public void run() {
		if(!taskConfig.isExecutable())
			return;
		try {
		    checkFinishedTask();
			setExpiredTaskAsFail();
			List<AsyncTaskDO> execTasks = taskDAO.findExecutableTask();
			for (AsyncTaskDO execTask : execTasks) {
				jobLogger.info("Unsend task[seqNo=" + execTask.getSeqNo() + "] found!");
				AsyncTaskDTO taskDto = new AsyncTaskDTO();
				BeanUtils.copyProperties(
						taskDto, execTask);
				boolean updateResult = notifier.notifySend(taskDto);
				if (updateResult == false) {
					throw new AsyncTaskException(
							"the status may be already change!",
							AsyncTaskException.TASK_STATUS_ALREADY_BE_CHANGE,
							null);
				}

				jobLogger.info("update task[seqNo=" + execTask.getSeqNo() + "] status finished!");
				try {
					MQSendResult sendResult = send(taskDto);
					if (!sendResult.isSuccess()) {
						jobLogger.error("Task[seqNo=" + execTask.getSeqNo() + "] send message failed!!reason:" + sendResult.getErrorMessage());
						notifier.revertStatus(taskDto);
					}
					else{
						jobLogger.info("Task[seqNo=" + execTask.getSeqNo() + "] send message finished!");
					}
				} catch (Exception e) {
					jobLogger.error(e.getMessage(), e);
					notifier.notifyFail(taskDto, e);
				}
			}
		} catch (Exception e) {
			jobLogger.error(e.getMessage(), e);
		}

	}
	
	
	private void checkFinishedTask(){
	    AsyncTaskQO taskDO = new AsyncTaskQO();
	    taskDO.setStatus(AsyncDetailTaskStatus.EXECUTED.getDbcode());
	    List<AsyncTaskDO> execTasks = taskDAO.findBySample(taskDO);
	    for(AsyncTaskDO task : execTasks){
            updateMainTaskStatus(task.getId());
	    }
	}
	

	
	private MQSendResult send(AsyncTaskDTO taskDto) {
		AsyncTaskEvent event = new AsyncTaskEvent(taskDto,"ASYNC-TASK-CENTER","ASYNC-TASK-CENTER:**");
        context.publishEvent(event);
		
		return MQSendResult.successResult("" + taskDto.getId(),event.getClass().getName());
	}
	
	private void setExpiredTaskAsFail(){
		List<AsyncTaskDO> expiredTask = taskDAO.findExpiredTask();
		if(expiredTask.size() > 0){
		    for(AsyncTaskDO task :expiredTask){
		        task.setStatus(AsyncTaskConst.TASK_STATUS.FAIL.getDbCode());
	            notifier.updateTask(task);
		    }
		}
	}
	

    private void updateMainTaskStatus(Long taskId){
        AsyncTaskDO taskDo = taskDAO.findById(taskId);
        List<AsyncTaskDetailDO> detailList = detailDao.findTaskDetails(taskId);
        boolean isAllComplete = detailList.size() > 0 ? true : false;
        boolean isFail = false;
        for(AsyncTaskDetailDO detailDo : detailList){
            if(detailDo.getStatus() == AsyncDetailTaskStatus.SUCCESS.getDbcode()){
                continue;
            }
            else if (detailDo.getStatus() == AsyncDetailTaskStatus.FAIL.getDbcode()){
                isAllComplete = false;
                isFail = true;
                break;
            }
            else{
                isAllComplete = false;
                break;
            }
        }
        if(isFail){
            taskDo.setStatus(AsyncTaskConst.TASK_STATUS.FAIL.getDbCode());
            taskDAO.updateByPrimaryKey(taskDo);
        }
        else if(isAllComplete){
            taskDo.setStatus(AsyncTaskConst.TASK_STATUS.SUCCESS.getDbCode());
            taskDAO.updateByPrimaryKey(taskDo);
        }
    }


	@Override
	public void execute(ShardingContext shardingContext) {
		// TODO Auto-generated method stub
		this.run();
	}


}
