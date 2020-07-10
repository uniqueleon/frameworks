package org.aztec.framework.async.task.entity;

public class TaskResult {

	private String ossFileUrl;
	private boolean toBeContinued = false;
	private int currentIndex = 0;

	public String getOssFileUrl() {
		return ossFileUrl;
	}

	public void setOssFileUrl(String ossFileUrl) {
		this.ossFileUrl = ossFileUrl;
	}

	public boolean isToBeContinued() {
		return toBeContinued;
	}

	public void setToBeContinued(boolean toBeContinued) {
		this.toBeContinued = toBeContinued;
	}

	public int getCurrentIndex() {
		return currentIndex;
	}

	public void setCurrentIndex(int currentIndex) {
		this.currentIndex = currentIndex;
	}
	
	public void append(TaskResult result){
		this.toBeContinued = result.toBeContinued;
		this.ossFileUrl += "," + result.getOssFileUrl();
	}
}
