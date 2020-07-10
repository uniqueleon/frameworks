package org.aztec.framework.async.task;

public interface AsyncTaskConst {

	/*public static final int TASK_STATUS_SUCCESS = 100;
	public static final int TASK_STATUS_FAIL = 99;
	public static final int TASK_STATUS_UNSTARTED = 0;
	public static final int TASK_STATUS_SEND = 1;
	public static final int TASK_STATUS_EXECUTED = 2;
	public static final int TASK_STATUS_RETRY = 3;
	public static final int TASK_STATUS_TIMEOUT = 4;
	public static final int TASK_STATUS_UPLOAD_FAIL = 5;*/
	
    public static final String SUB_TASK_INVOKE_URL = "/async/subTask";
    

	public static final String TASK_LOG_NAME = "EXPORT_TASK_LOG";

	public static final String TASK_PARAM_COOKIE_KEY = "task_pre_submit_param";
	
	public static final String CONTEXT_REQUEST_BEGIN_PARAM_NAME = "begin";
	public static final String CONTEXT_REQUEST_NUM_PARAM_NAME = "num";
	public static final String CONTEXT_REQUEST_ZIP_FILE_ENCODING = "zip_file_encoding";
	public static final String CONTEXT_REQUEST_TOTAL_NUM_PARAM_NAME = "totalNum";
    public static final String CONTEXT_REQUEST_SUB_TASK_NO = "subTaskNo";
	
	public static final String CONTEXT_REQUEST_ATTR_PREFIX = "req:";
	public static final String EXPORT_TASK_PARAM_REDIS_KEY_PREFIX = "EXPORT_TASK_PRESUBMIT_PARAM_";
	
	
	
	public static enum TASK_STATUS{
		SUCCESS(100),FAIL(99),UNSTARTED(0),SEND(1),EXECUTED(2),RETRY(3),TIMEOUT(4),UPLOAD_FAIL(5);
		
		private int dbCode;

		public int getDbCode() {
			return dbCode;
		}

		public void setDbCode(int dbCode) {
			this.dbCode = dbCode;
		}

		private TASK_STATUS(int dbCode) {
			this.dbCode = dbCode;
		}
		
	}
	
	public static enum EXPORT_TASK_INNER_ERR_CODE{
		FILE_MAX_SIZE_EXCEED("EXP_TASK_ERR_01" ,AsyncTaskException.DATA_FILE_SIZE_EXCEED),
		EXPORT_DATA_SIZE_EXCEED("EXP_TASK_ERR_02",AsyncTaskException.TASK_DATA_SIZE_EXCEED);
		
		public static final String ERROR_CODE_FREFIX = "errCode:";
		private String errCode;
		/*
		 * ����DataExportException
		 */
		private int exceptionCode;

		public String getErrCode() {
			return errCode;
		}

		public void setErrCode(String errCode) {
			this.errCode = errCode;
		}

		private EXPORT_TASK_INNER_ERR_CODE(String errCode,int expCode) {
			this.errCode = errCode;
		}
		
		public String getErrorMsg(){
			return ERROR_CODE_FREFIX + errCode;
		}

		public int getExceptionCode() {
			return exceptionCode;
		}

		public void setExceptionCode(int exceptionCode) {
			this.exceptionCode = exceptionCode;
		}
		
		public static EXPORT_TASK_INNER_ERR_CODE findByExceptionCode(int excptCode){
			for(EXPORT_TASK_INNER_ERR_CODE errCode : EXPORT_TASK_INNER_ERR_CODE.values()){
				if(errCode.exceptionCode == excptCode)
					return errCode;
			}
			return null;
		}
	}
	
	public static final String TASK_PARAM_SEPERATOR = "##";

	public static final String EXPORT_TASK_INFO_TOPIC_PREFIX = "ALOG_WMS_DATA_EXPORT_";
	
	public static final String EXPORT_TASK_INFO_TOPIC_SUFFIX = "_TOPIC";
	public static final long TASK_MSG_PROVIDER_SLEEP_INTERVAL = 1000l;
	public static final long TASK_MSG_CONSUMER_SLEEP_INTERVAL = 1000l;
	public static final int TASK_DEFAULT_RETRY_TIME = 1;
	

	public static final String DEFAULT_TASK_EXPIRED_TIPS = "TASK TIMEOUT IN {timeout} seconds";
	public static final String DEFAULT_EXECUTOR_NOF_FOUND_TIPS = "TASK Executor({executor}) not found!";
	
	
	public static class ERR_MSG_ATTACHMENT_KEYS {
		public static final String ERR_CODE = "errCode";
		public static final String MAX_DATA_SIZE = "maxDataSize";
		public static final String EXPORT_DATA_SIZE = "exportDataSize";
	}
}
