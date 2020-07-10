package org.aztec.framework.async.task.impl;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.aztec.framework.async.task.AsyncTaskConst;
import org.aztec.framework.async.task.AsyncTaskException;
import org.aztec.framework.async.task.AsyncTaskServerBean;
import org.aztec.framework.async.task.entity.AsyncTaskDO;
import org.aztec.framework.async.task.entity.AsyncTaskDetailDO;
import org.aztec.framework.async.task.mapper.AsyncTaskMapper;
import org.aztec.framework.disconf.items.AsyncTaskConfigure;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
import com.sjsc.framework.api.restful.entity.async.AsyncTaskDTO;

@AsyncTaskServerBean
@Component("exportTaskNotifier")
public class AsyncTaskDAO {

	@Resource
	private AsyncTaskMapper taskDAO;
	
	
	@Resource
	private AsyncTaskConfigure taskConfig;
	
	public boolean revertStatus(AsyncTaskDTO expTaskDO){

	    
		int result = taskDAO.updateStatus(toHashMap(new String[]{"id","status","gmtModified","ossFileUrl","errMsg","retryTime"},expTaskDO.getId(), AsyncTaskConst.TASK_STATUS.UNSTARTED.getDbCode(), expTaskDO.getUpdateTime(),null,null,AsyncTaskConst.TASK_DEFAULT_RETRY_TIME));
		//expTaskDO.setStatus(WmpExportTaskConst.TASK_STATUS.SUCCESS.getDbCode());
		return result > 0 ? true : false;
	}
	
	private Map<String,Object> toHashMap(String[] keys,Object... param){
	    Map<String,Object> hashMap = Maps.newHashMap();
	    for(int i = 0;i < keys.length;i++){
	        hashMap.put(keys[i], param[i]);
	    }
	    return hashMap;
	}
	
	public boolean notifySuccess(AsyncTaskDTO expTaskDO){
		
		int result = taskDAO.updateStatus(toHashMap(new String[]{"id","status","gmtModified","ossFileUrl","errMsg","retryTime"},expTaskDO.getId(), AsyncTaskConst.TASK_STATUS.SUCCESS.getDbCode(), expTaskDO.getUpdateTime(),expTaskDO.getOssFileUrl(),"",0));
		//expTaskDO.setStatus(WmpExportTaskConst.TASK_STATUS.SUCCESS.getDbCode());
		return result > 0 ? true : false;
	}
	
	public boolean notifyFail(AsyncTaskDTO expTaskDO,Exception e){
		if(expTaskDO.getRetryTime() > 0){
			int retryTime = expTaskDO.getRetryTime() - 1;
			int result = taskDAO.updateStatus(toHashMap(new String[]{"id","status","gmtModified","ossFileUrl","errMsg","retryTime"},expTaskDO.getId(), AsyncTaskConst.TASK_STATUS.FAIL.getDbCode(), expTaskDO.getUpdateTime(),null,buildExpceptionMsg(e),retryTime));
			return result > 0 ? true : false;
		}
		else{
			return notifyEternalFail(expTaskDO, buildExpceptionMsg(e));
		}
	}
	
	public boolean notifyEternalFail(AsyncTaskDTO expTaskDO,String errMsg){
		int result = taskDAO.updateStatus(toHashMap(new String[]{"id","status","gmtModified","ossFileUrl","errMsg","retryTime"},expTaskDO.getId(), AsyncTaskConst.TASK_STATUS.FAIL.getDbCode(), expTaskDO.getUpdateTime(),null,errMsg,0));
		return result > 0 ? true : false;
	}
	
	public boolean notifyTimeout(AsyncTaskDTO expTaskDO){
		int retryTime = expTaskDO.getRetryTime() - 1;
		if(retryTime < 0){
			return notifyEternalFail(expTaskDO, "The retry time is exhuasted!");
		}
		int result = taskDAO.updateStatus(toHashMap(new String[]{"id","status","gmtModified","ossFileUrl","errMsg","retryTime"},expTaskDO.getId(), AsyncTaskConst.TASK_STATUS.TIMEOUT.getDbCode(), expTaskDO.getUpdateTime(),null,AsyncTaskConst.DEFAULT_TASK_EXPIRED_TIPS.replace("{timeout}", "" + taskConfig.getTaskTimeout()) ,retryTime));
		return result > 0 ? true : false;
	}
	
	public boolean notifySend(AsyncTaskDTO expTaskDO){
		int retryTime = (expTaskDO.getRetryTime() != null ? expTaskDO.getRetryTime() : AsyncTaskConst.TASK_DEFAULT_RETRY_TIME);
		int result = taskDAO.updateStatus(toHashMap(new String[]{"id","status","gmtModified","ossFileUrl","errMsg","retryTime"},expTaskDO.getId(), AsyncTaskConst.TASK_STATUS.SEND.getDbCode(), expTaskDO.getUpdateTime(),null,null,retryTime));
		return result > 0 ? true : false;
	}
	
	public boolean notifyExecute(AsyncTaskDTO expTaskDO){
		int retryTime = expTaskDO.getRetryTime() - 1;
		if(retryTime < 0){
			return notifyEternalFail(expTaskDO, "The retry time is exhuasted!");
		}
		int result = taskDAO.updateStatus(toHashMap(new String[]{"id","status","gmtModified","ossFileUrl","errMsg","retryTime"},expTaskDO.getId(), AsyncTaskConst.TASK_STATUS.EXECUTED.getDbCode(), expTaskDO.getUpdateTime(),null,null,retryTime));
		return result > 0 ? true : false;
	}
	
	public boolean notifyExecutorNotFound(AsyncTaskDTO expTaskDO){
		int result = taskDAO.updateStatus(toHashMap(new String[]{"id","status","gmtModified","ossFileUrl","errMsg","retryTime"},expTaskDO.getId(), AsyncTaskConst.TASK_STATUS.FAIL.getDbCode(), expTaskDO.getUpdateTime(),null,AsyncTaskConst.DEFAULT_EXECUTOR_NOF_FOUND_TIPS.replace("{executor}", expTaskDO.getExecutor()),0));
		return result > 0 ? true : false;
	}
	
	public boolean notifyUnkownError(AsyncTaskDTO expTaskDO,Exception e){
		int result = taskDAO.updateStatus(toHashMap(new String[]{"id","status","gmtModified","ossFileUrl","errMsg","retryTime"},expTaskDO.getId(), AsyncTaskConst.TASK_STATUS.FAIL.getDbCode(), expTaskDO.getUpdateTime(),null,buildExpceptionMsg(e),0));
		return result > 0 ? true : false;
	}
	
	public void updateTask(AsyncTaskDO expTaskDO){
	    taskDAO.updateByPrimaryKey(expTaskDO);
	}
	
	public AsyncTaskDO findTask(Long id){
	    return taskDAO.findById(id);
	}
	
	public void createSubTask(AsyncTaskDetailDO detailDo){
	    
	}
	
	private String buildExpceptionMsg(Exception e){

		StringBuilder builder = new StringBuilder();
		if(e instanceof AsyncTaskException){
			AsyncTaskException dee = (AsyncTaskException) e;
			Map<String,Object> msgDataMap = new HashMap<>();
			msgDataMap.put(AsyncTaskConst.ERR_MSG_ATTACHMENT_KEYS.ERR_CODE, dee.getErrCode());
			if(dee.getAttachment() != null && dee.getAttachment().size() > 0){
				msgDataMap.putAll(dee.getAttachment());
			}
			builder.append(JSON.toJSONString(msgDataMap));
		}
		else{
			builder.append(e.getClass().getName() + ":");
			builder.append(e.getMessage());
		}
		//builder.append(ArrayUtils.toString(e.getStackTrace()));
		return builder.toString();
	}

}
