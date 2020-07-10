package org.aztec.framework.async.task.impl;

import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.aztec.framework.async.task.AsyncTaskConst;
import org.aztec.framework.async.task.AsyncTaskException;
import org.aztec.framework.async.task.AsyncTaskManager;
import org.aztec.framework.async.task.AsyncTaskServerBean;
import org.aztec.framework.async.task.entity.AsyncTaskDO;
import org.aztec.framework.async.task.entity.AsyncTaskDetailDO;
import org.aztec.framework.async.task.entity.AsyncTaskParam;
import org.aztec.framework.async.task.entity.TaskSubmitResult;
import org.aztec.framework.async.task.mapper.AsyncTaskDetailMapper;
import org.aztec.framework.async.task.mapper.AsyncTaskMapper;
import org.aztec.framework.async.task.util.OSSParam;
import org.aztec.framework.async.task.util.OSSUtils;
import org.aztec.framework.disconf.items.AsyncTaskConfigure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;
import com.sjsc.framework.api.restful.constant.AsyncDetailTaskStatus;
import com.sjsc.framework.api.restful.entity.RestRequest;
import com.sjsc.framework.api.restful.entity.async.AsyncTaskDTO;
import com.sjsc.framework.api.restful.entity.async.AsyncTaskDetailDTO;
import com.sjsc.framework.api.restful.entity.async.AsyncTaskQO;
import com.sjsc.framework.api.restful.entity.async.AsyncTaskReport;
import com.sjsc.framework.api.restful.feign.AsyncTaskInvokeService;
import com.sjsc.framework.api.restful.util.DynamicFeignUtils;

import io.micrometer.core.instrument.util.StringUtils;

@AsyncTaskServerBean
@Component("exportTaskManager")
public class AsyncTaskManagerImpl 
//implements WmpDataExportService 
implements AsyncTaskManager
{

	@Resource
	private AsyncTaskMapper taskDAO;

    @Resource
	private AsyncTaskDetailMapper detailDao;

	private LimitChecker checker = new LimitChecker();

	private static Logger logger = LoggerFactory
			.getLogger(AsyncTaskConst.TASK_LOG_NAME);

    @Resource
	AsyncTaskConfigure expConfig;

    @Autowired
    LoadBalancerClient lbClient;

	private AsyncTaskDO toTaskDO(AsyncTaskParam taskParam) throws IllegalAccessException, InvocationTargetException {

		AsyncTaskDO taskDO = new AsyncTaskDO();
		BeanUtils.copyProperties(taskDO, taskParam);
        taskDO.setStatus(AsyncTaskConst.TASK_STATUS.UNSTARTED.getDbCode());
		if(taskDO.getFileSuffix() == null){
			taskDO.setFileSuffix(".xls");
		}
		taskDO.setSeqNo(RandomStringUtils.randomAlphanumeric(16));
		taskDO.setRetryTime(3);
		return taskDO;
	}

	private AsyncTaskQO getQueryParam(AsyncTaskParam taskParam) {
		AsyncTaskQO queryParam = new AsyncTaskQO();
		queryParam.setExecutor(taskParam.getExecutor());
		queryParam.setLang(taskParam.getLang());
		queryParam.setExcParam(taskParam.getExcParam());
		queryParam.setWarehouseId(taskParam.getWarehouseId());
		queryParam.setModule(taskParam.getModule());
		return queryParam;
	}

	private AsyncTaskDO getAlreadySubmitedTask(AsyncTaskParam taskParam) {
		AsyncTaskQO queryParam = getQueryParam(taskParam);


		List<AsyncTaskDO> taskDOs = taskDAO.findBySample(queryParam);
		Date nowTime = new Date();
		for (AsyncTaskDO taskDO : taskDOs) {
			if ((nowTime.getTime() - taskDO.getCreateTime().getTime()) < expConfig
					.getDuplicateTaskInterval()) {
				return taskDO;
			}
		}
		return null;
	}

	private AsyncTaskDO submitTask(AsyncTaskParam taskParam)
			throws AsyncTaskException {

		AsyncTaskDO submitedTask = getAlreadySubmitedTask(taskParam);
		if (submitedTask == null) {
			AsyncTaskDO taskDO;
            try {
                taskDO = toTaskDO(taskParam);
            } catch (Exception e) {

                throw new AsyncTaskException("",
                        AsyncTaskException.TASK_SUMBIT_ERROR, null);
            }
			taskDO.setUpdateTime(new Date());
			taskDO.setCreateTime(new Date());
			long id = taskDAO.insert(taskDO);
			taskDO = taskDAO.findById(id);
			return taskDO;
		} else {
			throw new AsyncTaskException("",
					AsyncTaskException.TASK_ALREADY_SUBMIT, null);
		}
	}

	public TaskSubmitResult submit(AsyncTaskParam taskParam)  {
		TaskSubmitResult result = null;
		try {

//			String checkCode = checker
//					.check(new Object[] {
//							taskParam.getUserId(),
//							taskParam.getWarehouseId(),
//							taskParam.getModule() });
//			if (checkCode != null) {
//				result = new TaskSubmitResult(expConfig,checkCode,taskParam);
//				return result;
//			}
//			if(taskParam.getPreSubmit() == true){
//				result = new TaskSubmitResult(expConfig,null,
//						null, TaskSubmitResult.StatusCode.PRE_SUMBIT,taskParam);
//				return result;
//			}
//			else{

				AsyncTaskDO taskDO = submitTask(taskParam);
				result = new TaskSubmitResult(expConfig,taskDO.getName(),
						taskDO.getSeqNo(), TaskSubmitResult.StatusCode.SUCCESS,taskParam);
				return result;
//			}
		} catch (AsyncTaskException de) {
			if (de.getErrCode() == AsyncTaskException.TASK_ALREADY_SUBMIT) {
				AsyncTaskDO taskDO = getAlreadySubmitedTask(taskParam);
				result = new TaskSubmitResult(expConfig,taskDO.getName(),
						taskDO.getSeqNo(),
						TaskSubmitResult.StatusCode.DUPLICATED,taskParam);
				return result;
			} else {
				result = new TaskSubmitResult(expConfig,null, null,
						TaskSubmitResult.StatusCode.FAIL,taskParam);
				return result;
			}
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			result = new TaskSubmitResult(expConfig,null, null,
					TaskSubmitResult.StatusCode.FAIL,taskParam);
			return result;
		} 
	}
	
	


	@Override
	public List<AsyncTaskDO> findTasks(AsyncTaskQO sample){
		List<AsyncTaskDO> taskDOList = taskDAO.findBySample(sample);
		return taskDOList;
	}

	@Override
	public String getOssFileAccessURL(String key) {
		// TODO Auto-generated method stub

		OSSParam ossParam = new OSSParam(expConfig.getOssAccessID(),
				expConfig.getOssAccessKey(), expConfig.getOssUrlBase(),
				expConfig.getOssBucketName(), expConfig.getOssEndPoint());

		return OSSUtils.getAccessURL(key, ossParam,
				expConfig.getUrlExpiredDate());
	}

	@Override
	public boolean isAsynTaskEnable(String module) {
		// TODO Auto-generated method stub
		return expConfig.isEnabled(module);
	}

	private interface LimitJudgement {
		public boolean isLimitExceed(Object paramObj);

		public String getErrorCode();
	}

	public class LimitChecker {

		private LimitJudgement[] judgements = new LimitJudgement[] {
				new LimitJudgement() {

					@Override
					public boolean isLimitExceed(Object paramObj) {
						return isUserTaskUpperLimitExeceed((Long) paramObj);
					}

					@Override
					public String getErrorCode() {
						return TaskSubmitResult.StatusCode.USER_TASK_LIMIT_EXCEED;
					}

				}, new LimitJudgement() {

					@Override
					public boolean isLimitExceed(Object paramObj) {
						return isWarehouseTaskUpperLimitExeceed((Long) paramObj);
					}

					@Override
					public String getErrorCode() {
						return TaskSubmitResult.StatusCode.WAREHOUST_TASK_LIMIT_EXCEED;
					}

				}, new LimitJudgement() {

					@Override
					public boolean isLimitExceed(Object paramObj) {
						return isModuleTaskUpperLimitExeceed((String) paramObj);
					}

					@Override
					public String getErrorCode() {
						return TaskSubmitResult.StatusCode.MODULE_TASK_LIMIT_EXCEED;
					}

				}, };

		private String check(Object[] params) {
			for (int i = 0; i < params.length; i++) {
				if (judgements[i].isLimitExceed(params[i])) {
					return judgements[i].getErrorCode();
				}
			}
			return null;
		}

	}

	private boolean isUserTaskUpperLimitExeceed(Long userID) {
		AsyncTaskQO taskQO = new AsyncTaskQO();
		taskQO.setUserId(userID);
		Integer userTaskCount = taskDAO.countAllSumitedTask(taskQO);
		return userTaskCount != null
				&& userTaskCount > Integer.parseInt(expConfig
						.getUserTaskLimit()) ? true : false;
	}

	private boolean isWarehouseTaskUpperLimitExeceed(Long warehouseID) {
		AsyncTaskQO taskQO = new AsyncTaskQO();
		taskQO.setWarehouseId(warehouseID);
		Integer userTaskCount = taskDAO.countAllSumitedTask(taskQO);
		return userTaskCount != null
				&& userTaskCount > Integer.parseInt(expConfig
						.getWarehouseTaskLimit()) ? true : false;
	}

	private boolean isModuleTaskUpperLimitExeceed(String module) {
		AsyncTaskQO taskQO = new AsyncTaskQO();
		taskQO.setModule(module);
		Integer userTaskCount = taskDAO.countAllSumitedTask(taskQO);

		return userTaskCount != null
				&& userTaskCount > Integer.parseInt(expConfig
						.getModuleTaskLimit()) ? true : false;
	}

	@Override
	public List<AsyncTaskDO> findSummaryTasks(AsyncTaskQO sample) {
		// TODO Auto-generated method stub
	    return Lists.newArrayList();
		//return taskDAO.findSummaryBySample(sample);
	}
	
	
	private void updateMainTaskStatus(Long taskId){
	    AsyncTaskDO taskDo = taskDAO.findById(taskId);
	    List<AsyncTaskDetailDO> detailList = detailDao.findTaskDetails(taskId);
	    boolean isAllComplete = true;
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
    public AsyncTaskDetailDTO reportSubtask(AsyncTaskReport report) throws AsyncTaskException  {
        // TODO Auto-generated method stub
        AsyncTaskDO taskDO = taskDAO.findById(report.getTaskId());
        if(taskDO != null){

            AsyncTaskDetailDO detailDo = detailDao.findSubtask(report.getTaskId(), report.getSeqNo());
            if(detailDo != null){
                try {
                    if(report.getStatus() == null ){
                        report.setStatus(AsyncDetailTaskStatus.EXECUTED.getDbcode());
                    }
                    BeanUtils.copyProperties(detailDo, report);
                    //detailDo.setStatus(AsyncDetailTaskStatus.EXECUTED.getDbcode());
                    detailDao.updateByPrimaryKey(detailDo);
                    if(report.getComplete()){
                        detailDo.setStatus(AsyncDetailTaskStatus.SUCCESS.getDbcode());
                        if(detailDo.getCurrentDataNo() == null){
                            detailDo.setCurrentDataNo(0l);
                        }
                        if(detailDo.getDataSize() == null){
                            detailDo.setDataSize(0l);
                        }
                        detailDao.updateByPrimaryKey(detailDo);
                        AsyncTaskDetailDTO dto = new AsyncTaskDetailDTO();
                        BeanUtils.copyProperties(dto, detailDo);
                        return dto;
                    }
                    updateMainTaskStatus(report.getTaskId());
                    notifyTaskStatus(report);
                } catch (Exception e) {
                    throw new AsyncTaskException(e.getMessage(), 0, e);
                }
            }
        }
        return null;
    }
    
    private void notifyTaskStatus(AsyncTaskReport report) throws IllegalAccessException, InvocationTargetException {
        AsyncTaskDO taskDO = taskDAO.findById(report.getTaskId());
        if (StringUtils.isNotBlank(taskDO.getInvokeModule())) {
            AsyncTaskInvokeService service = DynamicFeignUtils.getFeignClient(lbClient, AsyncTaskInvokeService.class,
                    "lb://" + taskDO.getInvokeModule().toUpperCase());
            AsyncTaskDTO taskDTO = new AsyncTaskDTO();
            BeanUtils.copyProperties(taskDTO, taskDO);
            service.reveiveTaskReport(new RestRequest<AsyncTaskDTO>(taskDTO));
        }
    }

}
