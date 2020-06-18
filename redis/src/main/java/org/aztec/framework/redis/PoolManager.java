package org.aztec.framework.redis;

import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.slf4j.Logger;

import redis.clients.jedis.Protocol;

/**
 * Redis���ӳع���
 * 
 * @author tanson lam
 * @create 2016��7��18��
 */
public class PoolManager {
	private static Logger log =  CacheLogger.logger;

	/**
	 * ���ӳ�
	 */
	public ShardedJedisSentinelPool pool = null;

	private String currentSentinel;

	private Object lockObject = new Object();

	/**
	 * ��ʼ�����ӳ�
	 * 
	 * @return
	 * @throws Exception
	 */
	public ShardedJedisSentinelPool initPool() throws Exception {
		synchronized (lockObject) {
			log.info("begin to initialize the redis pool...");
			pool = newPool();
			return pool;
		}
	}
	
	public List<MasterSlaveHostAndPort> getCurrentHostMaster() {
		return pool.getCurrentHostMaster(); 
	}

	private ShardedJedisSentinelPool newPool() throws Exception {
		GenericObjectPoolConfig config = new GenericObjectPoolConfig();
		config.setMaxIdle(CacheConfig.getClientIdleConnection());
		config.setMaxTotal(CacheConfig.getClientMaxConnection());
		config.setTestOnReturn(true);
		config.setTestWhileIdle(true);
		config.setMaxWaitMillis(2000);
		config.setTestOnBorrow(true);
		CacheConfig connectInfo = new CacheConfig();
		this.currentSentinel = connectInfo.getSentinelInfo();
		Set<String> masters = connectInfo.getMasters();
		Set<String> sentinels = connectInfo.getSentinelUrl();
		Integer dababaseIndexUse = Protocol.DEFAULT_DATABASE;
		Integer databaseIndexConf = connectInfo.getDatabaseIndex();
		if( databaseIndexConf != null) {
			dababaseIndexUse = databaseIndexConf;
		}
		if (StringUtils.isEmpty(connectInfo.getPwd())) {
//			return new ShardedJedisSentinelPool(masters, sentinels, config,
//					60000);
			return new ShardedJedisSentinelPool(masters, sentinels, config,
					60000,null,dababaseIndexUse);
		} else {
//			return new ShardedJedisSentinelPool(masters, sentinels, config,
//					connectInfo.getPwd());
			return new ShardedJedisSentinelPool(masters, sentinels, config,Protocol.DEFAULT_TIMEOUT,
					connectInfo.getPwd(),dababaseIndexUse);
		}
	}

	public String getSentinel() {
		return currentSentinel;
	}

	/**
	 * �ع����ӳ�,��ǰsentinel��ͬ���������ĵ�ʱ�򣬲��������³�ʼ��pool
	 * 
	 * @throws Exception
	 */
	public ShardedJedisSentinelPool reloadPool() {  
		synchronized (lockObject) {
			if (pool != null) {
				log.info("begin to destroy the redis pool...");
				pool.destroy();
			}
			
			try {
				log.info("begin to reload the redis pool...");
				pool = newPool();
				log.info("finish reload the redis pool...");
				return pool;
			} catch (Exception e) {
				log.info("error occur while reload redis's pool.");
				return null;
			}

		} 
	}
}
