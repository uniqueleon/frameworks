package org.aztec.framework.async.task.util;

public class UploadResultInfo {

	private boolean successfull;

	private String fileName;

	private String fileUrl;

	private long fileSize;
	
	private long nextPosition; 

	public long getFileSize() {
		return fileSize;
	}

	public void setFileSize(long fileSize) {
		this.fileSize = fileSize;
	}

	public boolean getSuccessfull() {
		return successfull;
	}

	public void setSuccessfull(boolean successfull) {
		this.successfull = successfull;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getFileUrl() {
		return fileUrl;
	}

	public void setFileUrl(String fileUrl) {
		this.fileUrl = fileUrl;
	}

	public long getNextPosition() {
		return nextPosition;
	}

	public void setNextPosition(long nextPosition) {
		this.nextPosition = nextPosition;
	}
	
}