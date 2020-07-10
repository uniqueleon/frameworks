package org.aztec.framework.async.task.entity;

import java.io.Serializable;

import org.apache.commons.lang3.StringUtils;
import org.aztec.framework.disconf.items.AsyncTaskConfigure;

public class TaskSubmitResult implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6894676950210556042L;
	private String taskName;
	private String redirectURL;
	private String seqNo;
	private String statusCode;
	private AsyncTaskParam param;
	
	public TaskSubmitResult() {
		super();
	}

	public TaskSubmitResult(AsyncTaskConfigure config,String taskName,String seqNo,String statusCode,AsyncTaskParam param) {
		super();
		this.taskName = taskName;
		this.seqNo = seqNo;
		this.statusCode = statusCode;
		this.param = param;
		this.redirectURL = genRedirectUrl(config);
	}
	
	public TaskSubmitResult(AsyncTaskConfigure config,String statusCode,AsyncTaskParam param) {
		super();
		this.statusCode = statusCode;
		this.param = param;
		this.redirectURL = genRedirectUrl(config);
	}
	
	public static class StatusCode{
		public static final String SUCCESS = "TS_001";
		public static final String ASYN_TASK_NOT_ENABLED = "TS_003";
		public static final String PRE_SUMBIT = "TS_002";
		public static final String DUPLICATED = "TS_F_001";
		public static final String FAIL = "TS_F_002";
		public static final String USER_TASK_LIMIT_EXCEED = "TS_F_003";
		public static final String WAREHOUST_TASK_LIMIT_EXCEED = "TS_F_004";
		public static final String MODULE_TASK_LIMIT_EXCEED = "TS_F_005";
		public static final String AUTO_HANDLE_SYNC_ERROR = "TS_F_006";
		public static final String AUTO_HANDLE_ASYNC_ERROR = "TS_F_007";
		public static final String CHARSET_NOT_SUPPORT = "TS_F_008";
	}
	
	public String getTaskName() {
		return taskName;
	}
	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}
	public String getRedirectURL() {
		return redirectURL;
	}
	public void setRedirectURL(String redirectURL) {
		this.redirectURL = redirectURL;
	}
	public String getStatusCode() {
		return statusCode;
	}
	public void setStatusCode(String statusCode) {
		this.statusCode = statusCode;
	}
	public String getSeqNo() {
		return seqNo;
	}
	public void setSeqNo(String seqNo) {
		this.seqNo = seqNo;
	}
	
	private String genRedirectUrl(AsyncTaskConfigure config){
		
		StringBuilder baseUrl = new StringBuilder(config.getBaseRedirectUrl());
		baseUrl.append(getRedirectParams());
		return baseUrl.toString();
	}

	private String getRedirectParams(){
		StringBuilder qryParams = new StringBuilder();
		if(param.getPreSubmit() == true){
			qryParams.append((qryParams.toString().isEmpty() ? "?" : "&") + "optType=CREATE");
			/*try {
				qryParams.append("&" + param.toUrlQueryString());
			} catch (UnsupportedEncodingException e) {
				qryParams.append("&statusCode=" + StatusCode.CHARSET_NOT_SUPPORT);
			}*/
		}
		else{
			if(StringUtils.isNotBlank(statusCode)){
				qryParams.append("?statusCode=" + statusCode);
			}
			if(StringUtils.isNotBlank(taskName)){
				qryParams.append((qryParams.toString().isEmpty() ? "?" : "&") + "taskName=" + taskName);
			}
			if(StringUtils.isNotBlank(seqNo)){
				qryParams.append((qryParams.toString().isEmpty() ? "?" : "&") + "seqNo=" + seqNo);
			}
		}
		return qryParams.toString();
	}
}
