package org.aztec.framework.async.task.impl;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.aztec.framework.async.task.AsyncTaskConst;
import org.aztec.framework.async.task.AsyncTaskContext;

import com.alibaba.fastjson.JSON;
import com.sjsc.framework.api.restful.entity.async.AsyncTaskDTO;

public class TaskContextBuilder {

	public static AsyncTaskContext build(AsyncTaskDTO taskDto) throws Exception{
		return new ExportContextImpl(taskDto);
	}
	
	private static class ExportContextImpl implements AsyncTaskContext{
		
		private HttpServletResponse response;
		private HttpServletRequest request;
		private String paramString;
		private String executorName;
		private String lang;
		private Long userID;
		private Long warehouseID;
		private Long taskID;
		private AsyncTaskDTO taskDTO;
		private Map<String,Object> parsedObjs = new HashMap<String,Object>();

		public ExportContextImpl(AsyncTaskDTO taskDto) throws IOException {
			super();
			this.taskID = taskDto.getId();
			this.taskDTO = taskDto;
			this.paramString = taskDto.getExcParam();
			this.executorName = taskDto.getExecutor();
			this.lang = taskDto.getLang();
			this.userID = taskDto.getUserId();
			this.warehouseID = taskDto.getWarehouseId();
			this.response = new OSSFileReponse(warehouseID, userID, taskID,taskDto.getDescription());
			this.request = new AsyncTaskRequest(paramString,new AsyncTaskSession(taskDto.getSessionData()));
		}

		@Override
		public String getExecutorName() {
			// TODO Auto-generated method stub
			return executorName;
		}

		@Override
		public String getLang() {
			// TODO Auto-generated method stub
			return lang;
		}

		@SuppressWarnings("unchecked")
		@Override
		public <T> T getExportParam(Class<T> paramCls) {
			// TODO Auto-generated method stub
			
			if(parsedObjs.containsKey(paramCls.getName())){
				return (T) parsedObjs.get(paramCls.getName());
			}
			else{
				String[] paramJsons = paramString.split(AsyncTaskConst.TASK_PARAM_SEPERATOR);
				for(String paramJson : paramJsons){
					try {
						T retObj = JSON.parseObject(paramJson,paramCls);
						return retObj;
					} catch (ClassCastException e) {
						continue;
					} catch (Exception e){
						continue;
					}
				}
			}
			return null;
		}

		@Override
		public Long getUserID() {
			// TODO Auto-generated method stub
			return userID;
		}

		@Override
		public Long getWarehouseID() {
			// TODO Auto-generated method stub
			return warehouseID;
		}

		@Override
		public HttpServletResponse getHttpResponse() {
			// TODO Auto-generated method stub
			return response;
		}

		@Override
		public void init() {
			// TODO Auto-generated method stub
		}

		@Override
		public Long getTaskID() {
			// TODO Auto-generated method stub
			return taskID;
		}

		@Override
		public HttpServletRequest getHttpRequest() {
			// TODO Auto-generated method stub
			return request;
		}

		@Override
		public String getResultFileUrl() {
			return ((OSSFileReponse) response).getOSSFileUrl();
		}

        @Override
        public AsyncTaskDTO getTaskDTO() {
            return taskDTO;
        }
		
	}
}
