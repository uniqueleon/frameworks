package org.aztec.framework.async.task.entity;



/**
 * ��Ϣ���ؽ��
 * 
 * @author tansonlam
 * @create 2017��3��2��
 * 
 */
public class MQSendResult {
	private String messageId;
	private String topic;
	private boolean success;
	private String errorMessage;

	public MQSendResult() {
		
	}
	
//	public MQSendResult(com.taobao.wmpbasic.client.mq.MQSendResult mqResult) {
//		this.setSuccess(mqResult.isSuccess());
//		this.setTopic(mqResult.getTopic());
//		this.setMessageId(mqResult.getMessageId());
//		this.setErrorMessage(mqResult.getErrorMessage());
//	}
	
	public static MQSendResult successResult(String msgId,String topic) {
		MQSendResult result = new MQSendResult();
		result.messageId = msgId;
		result.topic = topic;
		result.success = true;
		return result;
	}
	
	public static MQSendResult successResult() {
		MQSendResult result = new MQSendResult(); 
		result.success = true;
		return result;
	}
	

	public static MQSendResult errorResult(String errMessage) {
		MQSendResult result = new MQSendResult();
		result.success = false;
		result.errorMessage = errMessage;
		return result;
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public String getMessageId() {
		return messageId;
	}

	public void setMessageId(String messageId) {
		this.messageId = messageId;
	}

	public String getTopic() {
		return topic;
	}

	public void setTopic(String topic) {
		this.topic = topic;
	}

	@Override
	public String toString() {
		return "MQSendResult [messageId=" + messageId + ", topic=" + topic
				+ ", success=" + success + ", errorMessage=" + errorMessage
				+ "]";
	}

}
