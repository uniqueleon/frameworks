package org.aztec.framework.async.task;

import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSON;

public class AsyncTaskException extends Exception {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -6499020599165284427L;

	private int errCode;
	
	public static final int TASK_MSG_SEND_ERROR = 1;
	public static final int TASK_MSG_EXECUTE_ERROR = 2;
	public static final int TASK_STATUS_ALREADY_BE_CHANGE = 3;
	public static final int TASK_ALREADY_SUBMIT = 4;
	public static final int NO_DATA_EXPORT = 6;
	public static final int TASK_DATA_SIZE_EXCEED = 5;
	public static final int DATA_FILE_SIZE_EXCEED = 7;
    public static final int TASK_SUMBIT_ERROR = 8;
	
	private Map<String,Object> attachment = new HashMap<>();

	public AsyncTaskException() {
		super();
		// TODO Auto-generated constructor stub
	}

	public AsyncTaskException(String message, int errorCode, Throwable cause) {
		super(message + ",errorCode :" + errorCode, cause);
		this.errCode = errorCode;
		// TODO Auto-generated constructor stub
	}

	public int getErrCode() {
		return errCode;
	}

	public void setErrCode(int errCode) {
		this.errCode = errCode;
	}
	
	public void attachData(String key,Object obj){
		attachment.put(key, obj);
	}

	public Map<String, Object> getAttachment() {
		return attachment;
	}

	public void setAttachment(Map<String, Object> attachment) {
		this.attachment = attachment;
	}
	
	public String toJson(){
		StringBuilder builder = new StringBuilder();
		Map<String,Object> msgDataMap = new HashMap<>();
		msgDataMap.put(AsyncTaskConst.ERR_MSG_ATTACHMENT_KEYS.ERR_CODE, errCode);
		if(attachment != null && attachment.size() > 0){
			msgDataMap.putAll(attachment);
		}
		builder.append(JSON.toJSONString(msgDataMap));
		return builder.toString();
	}
}
