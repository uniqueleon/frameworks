package org.aztec.framework.async.task.impl;

import java.util.List;

import javax.annotation.Resource;

import org.aztec.framework.async.task.AsyncTaskDataService;
import org.aztec.framework.async.task.AsyncTaskManager;
import org.aztec.framework.async.task.AsyncTaskServerBean;
import org.aztec.framework.async.task.entity.AsyncTaskDO;
import org.aztec.framework.async.task.entity.AsyncTaskParam;
import org.aztec.framework.async.task.entity.TaskSubmitResult;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import com.sjsc.framework.api.restful.entity.async.AsyncTaskDTO;
import com.sjsc.framework.api.restful.entity.async.AsyncTaskQO;

@AsyncTaskServerBean
@Component
public class AsyncTaskSevice implements AsyncTaskDataService {
	
	@Resource
	private AsyncTaskManager manager;
	@Resource
	private AsyncTaskDAO notifier;

	@Override
	public Boolean isAsynTaskEnable(String module) {
		// TODO Auto-generated method stub
		return manager.isAsynTaskEnable(module);
	}

	@Override
	public TaskSubmitResult submit(AsyncTaskParam param) {
		// TODO Auto-generated method stub
		return manager.submit(param);
	}

	@Override
	public List<AsyncTaskDO> findTasks(AsyncTaskQO sample)
			throws Exception {
		// TODO Auto-generated method stub
		return manager.findTasks(sample);
	}

	@Override
	public String getOssFileAccessURL(String key) {
		// TODO Auto-generated method stub
		return manager.getOssFileAccessURL(key);
	}

	@Override
	public boolean notifySuccess(AsyncTaskDTO expTaskDO) {
		// TODO Auto-generated method stub
		return notifier.notifySuccess(expTaskDO);
	}

	@Override
	public boolean notifyFail(AsyncTaskDTO expTaskDO, Exception e) {
		// TODO Auto-generated method stub
		return notifier.notifyFail(expTaskDO, e);
	}

	@Override
	public boolean notifyEternalFail(AsyncTaskDTO expTaskDO, String errMsg) {
		// TODO Auto-generated method stub
		return notifier.notifyEternalFail(expTaskDO, errMsg);
	}

	@Override
	public boolean notifyTimeout(AsyncTaskDTO expTaskDO) {
		// TODO Auto-generated method stub
		return notifier.notifyTimeout(expTaskDO);
	}

	@Override
	public boolean notifySend(AsyncTaskDTO expTaskDO) {
		// TODO Auto-generated method stub
		return notifier.notifySend(expTaskDO);
	}

	@Override
	public boolean notifyExecute(AsyncTaskDTO expTaskDO) {
		// TODO Auto-generated method stub
		return notifier.notifyExecute(expTaskDO);
	}

	@Override
	public boolean notifyExecutorNotFound(AsyncTaskDTO expTaskDO) {
		// TODO Auto-generated method stub
		return notifier.notifyExecutorNotFound(expTaskDO);
	}

	@Override
	public boolean notifyUnkownError(AsyncTaskDTO expTaskDO, Exception e) {
		// TODO Auto-generated method stub
		return notifier.notifyUnkownError(expTaskDO, e);
	}

	@Override
	public boolean revertStatus(AsyncTaskDTO expTaskDO) {
		// TODO Auto-generated method stub
		return notifier.revertStatus(expTaskDO);
	}

	@Override
	public List<AsyncTaskDO> getSummaryTasks(AsyncTaskQO sample)
			throws Exception {
		// TODO Auto-generated method stub
		return manager.findSummaryTasks(sample);
	}

    @Override
    public void updateTask(AsyncTaskDTO expTaskDO) {
        // TODO Auto-generated method stub
        AsyncTaskDO taskDo = new AsyncTaskDO();
        BeanUtils.copyProperties(expTaskDO, taskDo);
        notifier.updateTask(taskDo);
    }

}
