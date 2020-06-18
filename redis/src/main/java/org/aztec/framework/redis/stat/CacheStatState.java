package org.aztec.framework.redis.stat;

import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.atomic.AtomicInteger;

public class CacheStatState {
	private AtomicInteger getHitsCounter = new AtomicInteger(0);
	private AtomicInteger getMissesCounter = new AtomicInteger(0);
	private AtomicInteger setCounter = new AtomicInteger(0);
	private AtomicInteger deleteCounter = new AtomicInteger(0);
	private AtomicInteger addCounter = new AtomicInteger(0);
	private AtomicInteger timeoutCounter = new AtomicInteger(0);
	private AtomicInteger cancelCounter = new AtomicInteger(0);
	private AtomicInteger queOverflowCounter = new AtomicInteger(0);
	private AtomicInteger configErrorCounter = new AtomicInteger(0);
	private AtomicInteger unknownExceptionCounter = new AtomicInteger(0);
	private int maxKeyLenght;
	private int maxBodyLenght;
	private int maxMultiItem;
	private int maxMultiLenght;
	private AtomicInteger flowCounter = new AtomicInteger(0);
	private AtomicInteger totalCounter = new AtomicInteger(0);

	/**
	 * �ɹ����ʵļ�¼���������кͷ�����
	 */
	private ConcurrentSkipListMap<Long, AtomicInteger> successRecords = new ConcurrentSkipListMap<Long, AtomicInteger>();

	public void increase(CacheStatOpt opt, int count) {
		if (opt == CacheStatOpt.GETHIT) {
			getHitsCounter.addAndGet(count);
		} else if (opt == CacheStatOpt.GETMISS) {
			getMissesCounter.addAndGet(count);
		} else if (opt == CacheStatOpt.SET) {
			setCounter.addAndGet(count);
		} else if (opt == CacheStatOpt.DELETE) {
			deleteCounter.addAndGet(count);
		} else if (opt == CacheStatOpt.ADD) {
			addCounter.addAndGet(count);
		} else if (opt == CacheStatOpt.TIMEOUT) {
			timeoutCounter.addAndGet(count);
		} else if (opt == CacheStatOpt.CANCEL) {
			cancelCounter.addAndGet(count);
		} else if (opt == CacheStatOpt.QUE_OVERFLOW) {
			queOverflowCounter.addAndGet(count);
		} else if (opt == CacheStatOpt.CONFIG_ERROR) {
			configErrorCounter.addAndGet(count);
		} else if (opt == CacheStatOpt.UNKNOWN_EXCEPTION) {
			unknownExceptionCounter.addAndGet(count);
		} else if (opt == CacheStatOpt.MAX_KEY_LENGTH) {
			maxKeyLenght = (count > maxKeyLenght) ? count : maxKeyLenght;
		} else if (opt == CacheStatOpt.MAX_BODY_LENGTH) {
			maxBodyLenght = (count > maxBodyLenght) ? count : maxBodyLenght;
		} else if (opt == CacheStatOpt.MAX_MULTI_ITEM) {
			maxMultiItem = (count > maxMultiItem) ? count : maxMultiItem;
		} else if (opt == CacheStatOpt.MAX_MULTI_LENGTH) {
			maxMultiLenght = (count > maxMultiLenght) ? count : maxMultiLenght;
		}
	}

	/**
	 * ��¼����ɹ���ʱ�䣬������
	 * 
	 * @param span
	 */
	public void increaseStamp(long span) {
		if (!successRecords.containsKey(span)) {
			synchronized (this) {
				if (!successRecords.containsKey(span)) {
					successRecords.put(span, new AtomicInteger(0));
				}
			}
		}
		successRecords.get(span).addAndGet(1);
		totalCounter.addAndGet(1);
	}

	/**
	 * ����ͳ��
	 * 
	 * @param flow
	 */
	public void increaseFlow(int flow) {
		flowCounter.addAndGet(flow);
	}

	public ConcurrentSkipListMap<Long, AtomicInteger> getSuccessRecords() {
		return successRecords;
	}

	public int getHitsCount() {
		return getHitsCounter.get();
	}

	public int getMissesCount() {
		return getMissesCounter.get();
	}

	public int setCount() {
		return setCounter.get();
	}

	public int deleteCount() {
		return deleteCounter.get();
	}

	public int addCount() {
		return addCounter.get();
	}

	public int timeoutCount() {
		return timeoutCounter.get();
	}

	public int cancelCount() {
		return cancelCounter.get();
	}

	public int queOverflowCount() {
		return queOverflowCounter.get();
	}

	public int configErrorCount() {
		return configErrorCounter.get();
	}

	public int unknownExceptionCount() {
		return unknownExceptionCounter.get();
	}

	public int maxKeyLenght() {
		return maxKeyLenght;
	}

	public int maxBodyLenght() {
		return maxBodyLenght;
	}

	public int maxMultiItem() {
		return maxMultiItem;
	}

	public int maxMultiLenght() {
		return maxMultiLenght;
	}

	public int flowCount() {
		return flowCounter.get();
	}

	public AtomicInteger getTotalCounter() {
		return totalCounter;
	}

	public void setTotalCounter(AtomicInteger totalCounter) {
		this.totalCounter = totalCounter;
	}
}