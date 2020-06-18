package org.aztec.framework.redis;

/**
 * �ֲ�ʽ�����ؽ��
 * 
 * @author tanson lam
 * @create 2016��10��9��
 */
public class DistributedLockResp {
	/**
	 * �Ƿ��ȡ��
	 */
	private boolean hasLock;
	/**
	 * ���Ի�ȡ�����ܴ���
	 */
	private int tryTotalTime;
	/**
	 * ������
	 */
	private String lockKey;

	public DistributedLockResp(String lockKey, boolean hasLock, int tryTotalTime) {
		super();
		this.hasLock = hasLock;
		this.tryTotalTime = tryTotalTime;
		this.lockKey = lockKey;
	}

	public boolean isHasLock() {
		return hasLock;
	}

	public void setHasLock(boolean hasLock) {
		this.hasLock = hasLock;
	}

	public int getTryTotalTime() {
		return tryTotalTime;
	}

	public void setTryTotalTime(int tryTotalTime) {
		this.tryTotalTime = tryTotalTime;
	}

	public String getLockKey() {
		return lockKey;
	}

	public void setLockKey(String lockKey) {
		this.lockKey = lockKey;
	}

	public static Boolean isSuccessLocked(
			DistributedLockResp distributedLockResp) {
		if (distributedLockResp == null || !distributedLockResp.isHasLock())
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "DistributedLockResp [hasLock=" + hasLock + ", tryTotalTime="
				+ tryTotalTime + ", lockKey=" + lockKey + "]";
	}

}
