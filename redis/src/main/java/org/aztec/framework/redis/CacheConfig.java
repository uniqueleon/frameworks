package org.aztec.framework.redis;

import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.StringUtils;
import org.aztec.framework.disconf.items.DisconfItemBeanNames;
import org.aztec.framework.disconf.items.RedisConfig;
import org.aztec.framework.redis.util.SpringContextHolderUtil;
import org.slf4j.Logger;

/**
 * redis������Ϣ
 * 
 * @author tanson lam
 * @create 2016��7��18��
 */
public class CacheConfig {

	private static Logger log = CacheLogger.logger;
	public final static String DIAMOND_CONFIG_SENTINEL = "com.taobao.basic.sentinel";

	private final static String DIAMOND_GROUP_ID = "DEFAULT_GROUP";
	private final static int DIAMOND_TIMEOUT = 1000 * 1000;
	private final Map<String, String> CONF_MAP = new ConcurrentHashMap<String, String>();
	private String sentinelInfo;
	private static Boolean REDIS_OPEN = false;
	private static Boolean STATLOG_OPEN = false;
	private static Boolean DEBUG_OPEN = false;
	private static Integer CLIENT_MAX_CONNECTION = 300;
	private static Integer CLIENT_IDLE_CONNECTION = 25;
	private static Integer MAX_TRY_GETCONNECTION = 2;
	private static Boolean LOCALCACHE_OPEN = false;
	private static Boolean LOCALCACHE_REFRESH_OPEN = true;
	private static Boolean LOCALCACHE_FLUSH_OPEN = true;
	private static Long LOCALCACHE_REFRESHTIME = 10 * 60 * 1000l;
	private static Long LOCALCACHE_WAITQUEUE_TIMEOUT = 120 * 1000l;
	private static Long LOCALCACHE_WAITQUEUE_CHECKTIME = 30 * 1000l;
	private static Boolean PARAMTYPE_DETAIL_QUERYDB = true;
	private static Boolean PACKAGING_MATERIAL_QUERYDB = true;
	private static Boolean REDIS_NULLVALUE_OPEN = true;
	private static Boolean DISABLE_BEFORE_UPDATECHANNEL = false;
	private static Integer ADDRESS_SYNC_TIME= 20 * 60;
	private static Integer DATABASE_INDEX= null;
	/**
	 * �Ƿ����sql��������
	 */
	private static Boolean SQLAOP_OPEN = false;

	public static final String DEDBUG_SENTINEL_PROPERTY = "redis.sentinel";
	static {
		init();
	}

	public static void init() {
		new CacheConfig();
	}

	public CacheConfig() {
		try {
		    RedisConfig redisDisconf =  SpringContextHolderUtil.getBean(DisconfItemBeanNames.REDIS_DISCONFIG_BEAN_NAME);
			if (System.getProperty(DEDBUG_SENTINEL_PROPERTY) != null) {
				sentinelInfo = System.getProperty(DEDBUG_SENTINEL_PROPERTY);
			} else {
				/*sentinelInfo = Diamond.getConfig(DIAMOND_CONFIG_SENTINEL,
						DIAMOND_GROUP_ID, DIAMOND_TIMEOUT);*/
			    if(redisDisconf != null){
	                sentinelInfo = redisDisconf.getContent();
			    }
			}
			log.info("cache sentinelInfo:" + sentinelInfo);
            String[] itemArray = sentinelInfo.split("###");
			
			
			System.setProperty("docker_env","false");
			if(!StringUtils.isEmpty(redisDisconf.getDockerEnv())){
			    System.setProperty("docker_env",redisDisconf.getDockerEnv());
			}
			
			for (String item : itemArray) {
				String[] keyValue = StringUtils.split(item, "=");
				if (keyValue != null && keyValue.length == 2)
					CONF_MAP.put(keyValue[0].trim(), keyValue[1].trim());
			}

			if (getProperty("redis_open") != null) {
				REDIS_OPEN = Boolean.valueOf(getProperty("redis_open"));
			}
			if (getProperty("database_index") != null) {
				DATABASE_INDEX = Integer.valueOf(getProperty("database_index"));
			}
			if (getProperty("statlog_open") != null) {
				STATLOG_OPEN = Boolean.valueOf(getProperty("statlog_open"));
			}

			if (getProperty("debug_open") != null) {
				DEBUG_OPEN = Boolean.valueOf(getProperty("debug_open"));
			}

			if (getProperty("sqlaop_open") != null) {
				SQLAOP_OPEN = Boolean.valueOf(getProperty("sqlaop_open"));
			}

			if (getProperty("client_max_connection") != null) {
				CLIENT_MAX_CONNECTION = Integer
						.valueOf(getProperty("client_max_connection"));
			}

			if (getProperty("client_idle_connection") != null) {
				CLIENT_IDLE_CONNECTION = Integer
						.valueOf(getProperty("client_idle_connection"));
			}

			if (getProperty("max_try_getconnection") != null) {
				MAX_TRY_GETCONNECTION = Integer
						.valueOf(getProperty("max_try_getconnection"));
			}

			if (getProperty("localcache_open") != null) {
				LOCALCACHE_OPEN = Boolean
						.valueOf(getProperty("localcache_open"));
			}
			if (getProperty("localcache_flush_open") != null) {
				LOCALCACHE_FLUSH_OPEN = Boolean
						.valueOf(getProperty("localcache_flush_open"));
			}

			if (getProperty("localcache_refreshtime") != null) {
				LOCALCACHE_REFRESHTIME = Long
						.valueOf(getProperty("localcache_refreshtime"));
			}

			if (getProperty("localcache_waitqueue_timeout") != null) {
				LOCALCACHE_WAITQUEUE_TIMEOUT = Long
						.valueOf(getProperty("localcache_waitqueue_timeout"));
			}

			if (getProperty("localcache_waitqueue_checktime") != null) {
				LOCALCACHE_WAITQUEUE_CHECKTIME = Long
						.valueOf(getProperty("localcache_waitqueue_checktime"));
			}

			if (getProperty("localcache_refresh_open") != null) {
				LOCALCACHE_REFRESH_OPEN = Boolean
						.valueOf(getProperty("localcache_refresh_open"));
			}

			if (getProperty("paramtype_detail_querydb") != null) {
				PARAMTYPE_DETAIL_QUERYDB = Boolean
						.valueOf(getProperty("paramtype_detail_querydb"));
			}

			if (getProperty("packaging_material_querydb") != null) {
				PACKAGING_MATERIAL_QUERYDB = Boolean
						.valueOf(getProperty("packaging_material_querydb"));
			}
			if (getProperty("redis_nullvalue_open") != null) {
				REDIS_NULLVALUE_OPEN = Boolean
						.valueOf(getProperty("redis_nullvalue_open"));
			}
			if (getProperty("disable_before_updatechannel") != null) {
				DISABLE_BEFORE_UPDATECHANNEL = Boolean
						.valueOf(getProperty("disable_before_updatechannel"));
			}
			if (getProperty("address_sync_time") != null) {
				ADDRESS_SYNC_TIME = Integer
						.valueOf(getProperty("address_sync_time"));
			}

			
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	public static Integer getClientMaxConnection() {
		return CLIENT_MAX_CONNECTION;
	}

	public static Integer getClientIdleConnection() {
		return CLIENT_IDLE_CONNECTION;
	}

	public static Integer getMaxTryGetconnection() {
		return MAX_TRY_GETCONNECTION;
	}

	public String getSentinelInfo() {
		return sentinelInfo;
	}

	private String getProperty(String key) throws Exception {
		String value = System.getProperty(key);
		if (value == null)
			value = CONF_MAP.get(key);
		return value;
	}

	/**
	 * ��ȡ���ӷ�������
	 * 
	 * @return
	 * @throws Exception
	 */
	public Set<String> getMasters() throws Exception {
		Set<String> masterSet = new HashSet<String>();
		String masters = getProperty("masters");
		if (StringUtils.isEmpty(masters))
			throw new Exception("no found configuration of redis's masters.");
		String[] masterArray = masters.split(",");
		for (String masterName : masterArray)
			masterSet.add(masterName);
		return masterSet;
	}

	/**
	 * ��ȡSentinels������Ϣ
	 * 
	 * @return
	 * @throws Exception
	 */
	public Set<String> getSentinelUrl() throws Exception {
		Set<String> sentinelSet = new HashSet<String>();
		String sentinels = getProperty("sentinel_url");
		if (StringUtils.isEmpty(sentinels))
			throw new RedisException(
					"no found configuration of redis's sentinels.");
		String[] sentinelArray = sentinels.split(",");
		for (String sentinel : sentinelArray)
			sentinelSet.add(sentinel);
		return sentinelSet;
	}

	/**
	 * ��ȡRedis��������
	 * 
	 * @return
	 * @throws IOException
	 */
	public String getPwd() throws Exception {
		return getProperty("password");
	}
	public Integer getDatabaseIndex() throws Exception {
		String property = getProperty("database_index");
		if( property != null) {
			return Integer.parseInt(property);
		}else {
			return null;
		}
	}
	/**
	 * ���濪��
	 * 
	 * @return
	 */
	public static Boolean isRedisOpen() {
		return REDIS_OPEN;
	}

	/**
	 * ������־����
	 * 
	 * @return
	 */
	public static Boolean isStatLogOpen() {
		return STATLOG_OPEN;
	}

	/**
	 * ���ػ��濪��
	 * 
	 * @return
	 */
	public static Boolean isLocalCacheOpen() {
		return LOCALCACHE_OPEN;
	}

	/**
	 * ���ػ���ˢ��
	 * 
	 * @return
	 */
	public static Boolean isLocalFlushOpen() {
		return LOCALCACHE_FLUSH_OPEN;
	}

	/**
	 * ���ػ����Զ�ˢ�¿���
	 * 
	 * @return
	 */
	public static Boolean isLocalRefreshOpen() {
		return LOCALCACHE_REFRESH_OPEN;
	}

	/**
	 * ��ȡ���ػ������ʱ��
	 * 
	 * @return
	 */
	public static Long getLocalRefreshTime() {
		return LOCALCACHE_REFRESHTIME;
	}

	/**
	 * ���ػ���ȴ����г�ʱʱ��
	 * 
	 * @return
	 */
	public static Long getLocalCacheWaitQueueTimeOut() {
		return LOCALCACHE_WAITQUEUE_TIMEOUT;
	}

	/**
	 * ��鱾�ػ���ȴ����г�ʱ��ʱ����
	 * 
	 * @return
	 */
	public static Long getLocalCacheWaitQueueCheckTime() {
		return LOCALCACHE_WAITQUEUE_CHECKTIME;
	}

	/**
	 * debug����
	 * 
	 * @return
	 */
	public static Boolean isDebugOpen() {
		return DEBUG_OPEN;
	}

	/**
	 * SQL���ؿ���
	 * 
	 * @return
	 */
	public static Boolean isSqlAopOpen() {
		return SQLAOP_OPEN;
	}

	/**
	 * �����Ƿ�͸���ݿ��ѯ
	 * 
	 * @return
	 */
	public static Boolean isPackagingMaterialQuerydb() {
		return PACKAGING_MATERIAL_QUERYDB;
	}

	/**
	 * ϵͳ�����Ƿ�͸���ݿ��ѯ
	 * 
	 * @return
	 */
	public static Boolean isParamTypeDetailQuerydb() {
		return PARAMTYPE_DETAIL_QUERYDB;
	}

	/**
	 * redis���Ƿ񵱲�ѯ����Ϊ�պ�����NULLֵ
	 * 
	 * @return
	 */
	public static Boolean isRedisNullValueOpen() {
		return REDIS_NULLVALUE_OPEN;
	}
	
	/**
	 * �Ƿ�ر�ʵ�����ǰchannel
	 * @return
	 */
	public static Boolean disableBeforeUpdateChannel() {
		return DISABLE_BEFORE_UPDATECHANNEL;
	}
	/**
	 * ��ȡ��ַͬ��ʱ��
	 * @return
	 */
	public static Integer getAddressSyncTime(){
		return ADDRESS_SYNC_TIME;
	}
	
}
