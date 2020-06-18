package org.aztec.framework.redis.stat;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.aztec.framework.redis.CacheConfig;
import org.aztec.framework.redis.CacheLogger;
import org.slf4j.Logger;

/**
 * Cacheͳ����־ ��ʽ:
 * prefix=(ui_):hit_rate=(85%)get_hits=(1234)get_misses=(232)set=(133
 * )delete=(374)add=(123)
 * 
 * @author tanson lam
 * @create 2016��7��18��
 */
public class CacheStatLog {
	protected final static Logger logger = CacheLogger.logger;
	private static CacheStatLog instance = new CacheStatLog();
	private Map<String, ConcurrentHashMap<Long, ConcurrentHashMap<Integer, CacheStatState>>> records = new ConcurrentHashMap<String, ConcurrentHashMap<Long, ConcurrentHashMap<Integer, CacheStatState>>>();

	private final static String LOGGER_FORMAT = "[%s - %s][%s] prefix=(%s):hit_rate=(%s)get_hits=(%d)get_misses=(%d)set=(%d)delete=(%d)"
			+ "add=(%d)timeout=(%d)cancel=(%d)que_error=(%d)config_error=(%d)unknow_error=(%d)key_maxlen=(%d)body_maxlen=(%d)multi_maxitem=(%d)multi_maxlen=(%d)";

	// ��ʱ���ϴ�д��statlogʱ���
	private long lastModifyTime = 0;

	// ��־ʱ���ʽ
	private static SimpleDateFormat dateFormat = new SimpleDateFormat(
			"HH:mm:ss");

	// ��ʱ����д��־����
	private static ScheduledThreadPoolExecutor timer = null;

	private StatLogTask task = new StatLogTask();

	private Object mLock = new Object();

	private CacheStatLog() {
	}

	public static CacheStatLog getInstance() {
		return instance;
	}

	public void init() {
		lastModifyTime = System.currentTimeMillis();
		timer = new ScheduledThreadPoolExecutor(1);
		timer.schedule(task, 3, TimeUnit.SECONDS);
	}

	/**
	 * ���ݾɽӿ�
	 * 
	 * @param prefix
	 * @param opt
	 * @param count
	 */
	public void log(String prefix, CacheStatOpt opt, int count) {
		log(ChannelType.Redis, prefix, opt, count);
	}

	/**
	 * ͳ�Ʋ������쳣״̬
	 * 
	 * @param type
	 * @param prefix
	 * @param opt
	 * @param count
	 */
	public void log(ChannelType type, String prefix, CacheStatOpt opt, int count) {
		try {
			if (!CacheConfig.isStatLogOpen())
				return;
			// ȡ��ǰ1���ӿ�ʼ��ʱ���
			Calendar calendar = Calendar.getInstance();
			calendar.set(Calendar.SECOND, 0);
			calendar.set(Calendar.MILLISECOND, 0);
			long minuteStamp = calendar.getTimeInMillis();

			int cacheType = (type == ChannelType.Ehcache) ? 1 : 2;

			// ����Դ
			init(prefix, minuteStamp, cacheType);

			records.get(prefix).get(minuteStamp).get(cacheType)
					.increase(opt, count);
		} catch (Exception er) {
			logger.error("cache-statlog error", er);
		}
	}

	/**
	 * ����Դ
	 * 
	 * @param prefix
	 * @param minuteStamp
	 */
	private void init(String prefix, long minuteStamp, int cacheType) {
		if (!records.containsKey(prefix)) {
			synchronized (mLock) {
				if (!records.containsKey(prefix)) {
					records.put(
							prefix,
							new ConcurrentHashMap<Long, ConcurrentHashMap<Integer, CacheStatState>>());
				}
			}
		}

		if (!records.get(prefix).containsKey(minuteStamp)) {
			synchronized (mLock) {
				if (!records.get(prefix).containsKey(minuteStamp)) {
					records.get(prefix).put(minuteStamp,
							new ConcurrentHashMap<Integer, CacheStatState>());
				}
			}
		}

		if (!records.get(prefix).get(minuteStamp).containsKey(cacheType)) {
			synchronized (mLock) {
				if (!records.get(prefix).get(minuteStamp)
						.containsKey(cacheType)) {
					records.get(prefix).get(minuteStamp)
							.put(cacheType, new CacheStatState());
				}
			}
		}

		synchronized (mLock) {
			// ������ʱ����
			if (timer == null) {
				lastModifyTime = minuteStamp;
				timer = new ScheduledThreadPoolExecutor(1);
				timer.schedule(new StatLogTask(), 3, TimeUnit.SECONDS);
			}
		}
	}

	protected class StatLogTask implements Runnable {
		@Override
		public void run() {
			try {
				if (records.size() == 0) {
					// û���κ�prefix
					return;
				}

				// �����ϴ��޸�ʱ�仹û����60��
				long current = System.currentTimeMillis();
				long modifySpan = current - lastModifyTime;
				if (modifySpan <= 60000) {
					return;
				}

				// ��ǰ����
				Calendar calendar = Calendar.getInstance();
				calendar.set(Calendar.SECOND, 0);
				calendar.set(Calendar.MILLISECOND, 0);
				long currentMinute = calendar.getTimeInMillis();

				Iterator<Entry<String, ConcurrentHashMap<Long, ConcurrentHashMap<Integer, CacheStatState>>>> iterator = records
						.entrySet().iterator();
				while (iterator.hasNext()) {
					Entry<String, ConcurrentHashMap<Long, ConcurrentHashMap<Integer, CacheStatState>>> entry = iterator
							.next();
					String prefix = entry.getKey();
					ConcurrentHashMap<Long, ConcurrentHashMap<Integer, CacheStatState>> stampMap = entry
							.getValue();

					// ���ڼ�¼
					List<Long> removeList = new ArrayList<Long>();
					Iterator<Entry<Long, ConcurrentHashMap<Integer, CacheStatState>>> stampItera = stampMap
							.entrySet().iterator();
					while (stampItera.hasNext()) {
						Entry<Long, ConcurrentHashMap<Integer, CacheStatState>> stampEntry = stampItera
								.next();
						long stamp = stampEntry.getKey();
						if (stamp < currentMinute) {
							removeList.add(stamp);
						}
					}

					// �Ƴ����ڼ�¼������¼��־
					for (long stamp : removeList) {
						ConcurrentHashMap<Integer, CacheStatState> state = stampMap
								.remove(stamp);
						if (state == null) {
							return;
						}

						if (state.containsKey(1)) {
							log(1, prefix, state.get(1), stamp);
						}
						if (state.containsKey(2)) {
							log(2, prefix, state.get(2), stamp);
						}
					}
				}
				lastModifyTime = currentMinute;
			} catch (Exception er) {
				logger.error("Log task error", er);
			} finally {
				timer.schedule(task, 3, TimeUnit.SECONDS);
			}
		}
	}

	/**
	 * ��ӡ��־
	 * 
	 * @param prefix
	 * @param state
	 */
	private void log(Integer cacheType, String prefix, CacheStatState state,
			long currentSpan) {
		if (!CacheConfig.isStatLogOpen())
			return;
		Date beginDate = new Date(currentSpan);
		Date endDate = new Date(currentSpan + 59000);

		int total = state.getHitsCount() + state.getMissesCount();
		NumberFormat numberFormat = NumberFormat.getInstance();
		numberFormat.setMaximumFractionDigits(0);

		String rateString = "";
		if (total != 0) {
			rateString = numberFormat
					.format(((float) state.getHitsCount() / (float) total) * 100)
					+ "%";
		} else {
			rateString = "0%";
		}

		String log = String.format(LOGGER_FORMAT, dateFormat.format(beginDate),
				dateFormat.format(endDate), cacheType == 1 ? "ehcache"
						: "redis", prefix, rateString, state.getHitsCount(),
				state.getMissesCount(), state.setCount(), state.deleteCount(),
				state.addCount(), state.timeoutCount(), state.cancelCount(),
				state.queOverflowCount(), state.configErrorCount(), state
						.unknownExceptionCount(), state.maxKeyLenght(), state
						.maxBodyLenght(), state.maxMultiItem(), state
						.maxMultiLenght());
		logger.info(log);
	}

	public static void main(String[] args) {
		// ͳ��Key����
		AtomicInteger i = new AtomicInteger(1);
		System.out.println(i.addAndGet(-1));
		CacheStatLog.getInstance().log(ChannelType.Redis, "test",
				CacheStatOpt.MAX_KEY_LENGTH, 1111);
		CacheStatLog.getInstance().log(ChannelType.Redis, "test",
				CacheStatOpt.SET, 2);
		CacheStatLog.getInstance().log(ChannelType.Redis, "test",
				CacheStatOpt.GETHIT, 5);
		CacheStatLog.getInstance().log(ChannelType.Redis, "test",
				CacheStatOpt.GETMISS, 1);

	}
}