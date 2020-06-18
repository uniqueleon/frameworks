package org.aztec.framework.redis;

import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;

/**
 * redis������
 * 
 * @author tansonlam
 * @createDate 2016��2��1��
 * 
 */
public class RedisManage extends RedisOperatorImpl implements RedisOperator,RedisQueueOperator {

	private static Logger log =  CacheLogger.logger;
	/**
	 * �Ƿ��Ѿ���ʼ������ֹ�ظ����ù��캯��
	 */
	private static AtomicBoolean isInit = new AtomicBoolean(false);

	private PoolManager poolManager;

	/**
	 * ����ʵ��
	 */
	private static RedisManage redisManage = new RedisManage();

	private RedisManage() {

		if (!isInit.getAndSet(true)) {
			try {
				poolManager = new PoolManager();
				ShardedJedisSentinelPool pool = poolManager.initPool();
				init(pool);
			} catch (Throwable e) {
				log.error("fail to init redisManage");
				isInit.set(false);
			}
		}
	}

	public void reloadPool() {
		ShardedJedisSentinelPool newPool = poolManager.reloadPool();
		if (newPool != null) { 
			pool = newPool; 
		}
	}

	/**
	 * 获取redis manage 的单例。不支持在静态方法，或bean的成员变量中调用。只支持在非静态方法中调用。
	 * @return
	 */
	public static RedisManage getInstance() {
		/*if (!redisManage.pool.isFinishInit())
			throw new JedisConnectionException(
					"RedisManage's pool is not yet init successful.");*/
		return redisManage;
	}

}