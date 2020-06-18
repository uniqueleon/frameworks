package org.aztec.framework.redis;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.PooledObjectFactory;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.aztec.framework.redis.MasterSlaveHostAndPort.MasterSlaveHostAndPortComparator;
import org.slf4j.Logger;

import com.google.common.collect.Lists;

import redis.clients.jedis.Client;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;
import redis.clients.jedis.JedisSentinelPool;
import redis.clients.jedis.JedisShardInfo;
import redis.clients.jedis.Protocol;
import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPool;
import redis.clients.jedis.exceptions.JedisConnectionException;
import redis.clients.util.Hashing;
import redis.clients.util.Pool;
import redis.clients.util.Sharded;

/**
 * ����{@link JedisSentinelPool}������չ��֧�ַ�Ƭ���ӳأ���д���롣
 * 
 * @author tansonlam 2016��7��13��
 * 
 */
public class ShardedJedisSentinelPool extends Pool<ShardedJedis> {
	/**
	 * ���ӵ�sentinel��������Դ���.
	 */
	public static final int MAX_RETRY_SENTINEL = 5;

	private static Logger log = CacheLogger.logger;

	protected GenericObjectPoolConfig poolConfig;

	protected int timeout = 5000;

	protected int soTimeout = 20000;

	private int sentinelRetry = 0;

	private int MAX_TRY_GETCONNECTION = CacheConfig.getMaxTryGetconnection();

	private int MAX_TRY_CONNECTJEDIS = 2;

	protected String password;

	protected int database = Protocol.DEFAULT_DATABASE;

	protected Set<String> masterNames;

	protected Set<String> sentinels;

	protected Set<MasterListener> masterListeners = new HashSet<MasterListener>();

	protected Map<HostAndPort, ShardedJedisPool> readJedisPool = new ConcurrentHashMap<HostAndPort, ShardedJedisPool>();

	private volatile List<MasterSlaveHostAndPort> currentHostMasters;

	private ShardedCounter shardedUtil;

	private AtomicBoolean finishInit = new AtomicBoolean();
	
	private List<JedisShardInfo> currentWriteShareInfoList;

	public ShardedJedisSentinelPool(Set<String> masters, Set<String> sentinels) {
		this(masters, sentinels, new GenericObjectPoolConfig(),
				Protocol.DEFAULT_TIMEOUT, null, Protocol.DEFAULT_DATABASE);
	}

	public ShardedJedisSentinelPool(Set<String> masters, Set<String> sentinels,
			String password) {
		this(masters, sentinels, new GenericObjectPoolConfig(),
				Protocol.DEFAULT_TIMEOUT, password);
	}

	public ShardedJedisSentinelPool(final GenericObjectPoolConfig poolConfig,
			Set<String> masters, Set<String> sentinels) {
		this(masters, sentinels, poolConfig, Protocol.DEFAULT_TIMEOUT, null,
				Protocol.DEFAULT_DATABASE);
	}

	public ShardedJedisSentinelPool(Set<String> masters, Set<String> sentinels,
			final GenericObjectPoolConfig poolConfig, int timeout,
			final String password) {
		this(masters, sentinels, poolConfig, timeout, password,
				Protocol.DEFAULT_DATABASE);
	}

	public ShardedJedisSentinelPool(Set<String> masters, Set<String> sentinels,
			final GenericObjectPoolConfig poolConfig, final int timeout) {
		this(masters, sentinels, poolConfig, timeout, null,
				Protocol.DEFAULT_DATABASE);
	}

	public ShardedJedisSentinelPool(Set<String> masters, Set<String> sentinels,
			final GenericObjectPoolConfig poolConfig, final String password) {
		this(masters, sentinels, poolConfig, Protocol.DEFAULT_TIMEOUT, password);
	}

	public ShardedJedisSentinelPool(Set<String> masters, Set<String> sentinels,
			final GenericObjectPoolConfig poolConfig, int timeout,
			final String password, final int database) {
		this.poolConfig = poolConfig;
		this.timeout = timeout;
		this.password = password;
		this.database = database;
		this.masterNames = masters;
		this.sentinels = sentinels;
		initSentinelLiseners();
		initSentinelPool();

	}

	public ShardedJedisSentinelPool(Set<String> hosts,
			final GenericObjectPoolConfig poolConfig, int timeout,
			final String password) {
		this.poolConfig = poolConfig;
		this.timeout = timeout;
		this.password = password;
		initAllPool(host2MasterList(hosts));
	}

	private List<MasterSlaveHostAndPort> host2MasterList(Set<String> hosts) {
		if (hosts == null || hosts.isEmpty())
			throw new RedisException("hosts can not be null.");
		List<MasterSlaveHostAndPort> masterList = new ArrayList<MasterSlaveHostAndPort>();
		int index = 1;
		for (String host : hosts) {
			final HostAndPort master = toHostAndPort(Arrays.asList(host
					.split(":")));
			MasterSlaveHostAndPort masterSlaveHostAndPort = new MasterSlaveHostAndPort(
					"masterName" + (++index), master,
					new LinkedHashSet<HostAndPort>());
			masterList.add(masterSlaveHostAndPort);
		}
		return masterList;
	}

	/**
	 * ��ͣ����sentinel�ļ�����
	 */
	public void destroy() {
		for (MasterListener m : masterListeners) {
			m.shutdown();
		}

		// super.destroy();
	}

	public Boolean isFinishInit() {
		return finishInit.get();
	}

	public List<MasterSlaveHostAndPort> getCurrentHostMaster() {
		return Collections.unmodifiableList(currentHostMasters);
	}

	/**
	 * ��sentinel��ȡredis��Ⱥ��������Ϣ�����ҳ�ʼ����д���ӳ�
	 */
	protected synchronized void initSentinelPool() {
		try {
			List<MasterSlaveHostAndPort> masterList = sentinelGetMasterSlaves();
			
			Collections
					.sort(masterList, new MasterSlaveHostAndPortComparator());
			convertDockerIP(masterList);
			initAllPool(masterList);
			finishInit.set(true);
		} catch (Exception e) {
			finishInit.set(false);
			log.error("fail to initSentinelPool", e);
		}
	}
	
	private boolean isInDockerEnvironment(){

        String dockerFlag = System.getProperty("docker_env");
        boolean isRunInDocker = dockerFlag == null ? false : Boolean.parseBoolean(dockerFlag);
	    return isRunInDocker;
	}
	
	public void convertDockerIP(List<MasterSlaveHostAndPort> masterList){
	    if(!isInDockerEnvironment()){
	        for(int i = 0;i < masterList.size();i++){
	            MasterSlaveHostAndPort mPort = masterList.get(i);
	            HostAndPort oldMaster = mPort.getMaster();
	            HostAndPort newMaster = new HostAndPort(mPort.getMasterName() +hostmocksplitor+ "M"+hostmocksplitor+"0", oldMaster.getPort());
	            Set<HostAndPort> slaves = new HashSet<>();
	            int k = 0;
	            for(HostAndPort oldSlave : mPort.getSlaves()){
	                slaves.add(new HostAndPort(mPort.getMasterName() + hostmocksplitor+"S"+hostmocksplitor + k, oldSlave.getPort()));
	                k ++;
	            }
	            MasterSlaveHostAndPort mock = new MasterSlaveHostAndPort(mPort.getMasterName(), newMaster, slaves);
	            masterList.set(i, mock);
	        }
	    }
	}

	/**
	 * ��ʼ����д�����ӳ�
	 * 
	 * @param masters
	 */
	private void initAllPool(List<MasterSlaveHostAndPort> masters) {
		if (!equals(currentHostMasters, masters)) {
			initWritePool(masters);
			initReadPool(masters);
			currentHostMasters = masters;
		} else {
			log.info("nothing change,not need to init redis pool.");
		}
	}

	/**
	 * ����sentinel��ȡ��masters�Ƿ���������
	 * 
	 * @param masters
	 */
	private List<MasterSlaveHostAndPort> checkMastersHealth(
			List<MasterSlaveHostAndPort> masters) {
		if (masters.isEmpty())
			return Lists.newArrayList();
		List<MasterSlaveHostAndPort> healthMasters = new ArrayList<MasterSlaveHostAndPort>();
		for (MasterSlaveHostAndPort masterSlaveHostAndPort : masters) {
			HostAndPort master = masterSlaveHostAndPort.getMaster();
			if (!tryCheckConnectionAble(
					createJedisShardInfo(master.getHost(), master.getPort(),
							password), null))
				continue;
			Set<HostAndPort> slaves = new HashSet<HostAndPort>();
			for (HostAndPort slave : masterSlaveHostAndPort.getSlaves()) {
				if (tryCheckConnectionAble(
						createJedisShardInfo(slave.getHost(), slave.getPort(),
								password), null)) {
					slaves.add(slave);
				}
			}
			healthMasters.add(new MasterSlaveHostAndPort(masterSlaveHostAndPort
					.getMasterName(), master, slaves));
		}
		return healthMasters;

	}

	/**
	 * ��ʼ��д�����ӳ�
	 * 
	 * @param masters
	 * @throws RedisException
	 */
	private void initWritePool(List<MasterSlaveHostAndPort> masters) {
		StringBuffer sb = new StringBuffer();
		for (MasterSlaveHostAndPort master : masters) {
			sb.append(master.toString());
			sb.append(" ");
		}
		log.info("Created write JedisPool to master at [" + sb.toString() + "]");
		List<JedisShardInfo> shardMasters = makeShardInfoList(masters);
		List<JedisShardInfo> writeShareInfoList = new ArrayList<JedisShardInfo>();
		for (JedisShardInfo jedisShardInfo : shardMasters) {
			if (tryCheckConnectionAble(jedisShardInfo, RedisRole.MASTER)) {
				writeShareInfoList.add(jedisShardInfo);
			}
		}
		for (JedisShardInfo jedisShardInfo : writeShareInfoList) {
			log.info("write jedis:" + jedisShardInfo.getHost() + ":"
					+ jedisShardInfo.getPort());
		}
		currentWriteShareInfoList = writeShareInfoList;
		shardedUtil = new ShardedCounter(writeShareInfoList, Hashing.MURMUR_HASH);
		initPool(poolConfig, new ShardedJedisFactory(writeShareInfoList,
				Hashing.MURMUR_HASH, null));
	}

	/**
	 * ��ʼ���������ӳ�
	 * 
	 * @param masters
	 */
	private void initReadPool(List<MasterSlaveHostAndPort> masters) {

		for (MasterSlaveHostAndPort master : masters) {
			log.info("Created read JedisPool to master at [" + master + "]");
			List<JedisShardInfo> readShareInfoList = new ArrayList<JedisShardInfo>();
			// Create JedisShardInfo for one master.
			readShareInfoList.add(createJedisShardInfo(master.getMaster()
					.getHost(), master.getMaster().getPort(), password));
			// Create JedisShardInfo for slaves.
			if (master.getSlaves() != null) {
				for (HostAndPort slave : master.getSlaves()) {
					JedisShardInfo slaveJedisShardInfo = createJedisShardInfo(
							slave.getHost(), slave.getPort(), password);
					if (tryCheckConnectionAble(slaveJedisShardInfo,
							RedisRole.SLAVE)) {
						readShareInfoList.add(slaveJedisShardInfo);
					}
				}
			}
			ShardedJedisPool pool = new ShardedJedisPool(poolConfig,
					readShareInfoList, Hashing.MURMUR_HASH,
					Sharded.DEFAULT_KEY_TAG_PATTERN);
			readJedisPool.put(master.getMaster(), pool);
		}

	}

	/**
	 * ����jedisSharded������Ϣ
	 * 
	 * @param host
	 * @param port
	 * @param password
	 * @return
	 */
	private JedisShardInfo createJedisShardInfo(String host, int port,
			String password) {
//		JedisShardInfo jedisShardInfo = new JedisShardInfo(host, port, timeout,
//				soTimeout, Sharded.DEFAULT_WEIGHT);
		URI uri;
		try {
			uri = new URI("redis", null, host, port, "/"+database, null, null);
		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}
		JedisShardInfo jedisShardInfo = new JedisShardInfo(uri);
		jedisShardInfo.setConnectionTimeout(timeout);
		jedisShardInfo.setSoTimeout(soTimeout);
		if (!StringUtils.isEmpty(password))
			jedisShardInfo.setPassword(password);
		return jedisShardInfo;
	}

	private Boolean tryCheckConnectionAble(JedisShardInfo jedisShardInfo,
			RedisRole redisRole) {
		int tryTime = 0;
		while (true) {
			try {
				return checkConnectAble(jedisShardInfo, redisRole);
			} catch (Exception e) {
				tryTime++;
				if (tryTime > MAX_TRY_CONNECTJEDIS) {
					log.error(e.getMessage(), e);
					return false;
				}
				try {
					TimeUnit.MILLISECONDS.sleep(10);
				} catch (InterruptedException e1) {
				}
			}
		}
	}

	/**
	 * ����Ƿ�������ӣ����ҽڵ�Ľ�ɫ�Ƿ��Ԥ��һ��
	 * 
	 * @param jedisShardInfo
	 * @param redisRole
	 * @return
	 */
	private Boolean checkConnectAble(JedisShardInfo jedisShardInfo,
			RedisRole redisRole) {
		String nodeInfo = "redis node[" + jedisShardInfo.getHost() + ":"
				+ jedisShardInfo.getPort() + "]";
		Jedis jedis = null;
		try {
			jedis = new Jedis(jedisShardInfo);
			String replication = jedis.info("replication");
			if (redisRole == null) {
				return replication != null;
			} else if (replication.contains(redisRole.name().toLowerCase()))
				return true;
			throw new RuntimeException("not in role of " + redisRole.name());
		} catch (Exception e) {
			throw new RuntimeException("exception in " + nodeInfo
					+ e.getMessage(), e);
		} finally {
			if (jedis != null) {
				jedis.close();
			}
		}
	}

	private void printClientInfo(ShardedJedis shardedJedis, String key) {
		Jedis jedis = shardedJedis.getShard(key);
		CacheLogger.debug("jedis ip:" + jedis.getClient().getHost() + " port:"
				+ jedis.getClient().getPort());
	}

	/**
	 * ��ȡ������
	 * 
	 * @return
	 * @throws RedisException
	 */
	public ShardedJedis getReadConnection(String key) throws RedisException {

		ShardedJedisPool shardedJedisPool = matchReadJedisPool(key);
		if (shardedJedisPool == null)
			throw new RedisException("can not get read redis pool for key: "
					+ key);
		int tryTime = 0;
		while (true) {
			try {
				return shardedJedisPool.getResource();
			} catch (Exception e) {
				tryTime++;
				if (tryTime > MAX_TRY_GETCONNECTION)
					throw new RedisException(e);
				try {
					TimeUnit.MILLISECONDS.sleep(1);
				} catch (InterruptedException e1) {
				}
			}
		}
	}

	/**
	 * ����Ӷ���Ƭ�л�ȡjedis����ʵ��
	 * 
	 * @param shardedJedis
	 * @return
	 */
	public Jedis randomReadJedis(ShardedJedis shardedJedis) {
		if (shardedJedis == null)
			return null;
		String randomKey = String.valueOf(System.currentTimeMillis());
		printClientInfo(shardedJedis, randomKey);
		return shardedJedis.getShard(randomKey);
	}

	/**
	 * ����key��ȡд��jedis�ڵ�
	 * 
	 * @param shardedJedis
	 * @param key
	 * @return
	 */
	public Jedis getWriteJedis(ShardedJedis shardedJedis, String key) {
		if (shardedJedis == null)
			return null;
		return shardedJedis.getShard(key);
	}

	/**
	 * ����keyƥ���Ӧ������
	 * 
	 * @param key
	 * @return
	 */
	private ShardedJedisPool matchReadJedisPool(String key) {
		if (readJedisPool == null || readJedisPool.isEmpty())
			return null;
		Client client = shardedUtil.getShard(key.getBytes()).getClient();
		HostAndPort hostAndPort = new HostAndPort(client.getHost(),
				client.getPort());
		ShardedJedisPool shardedJedisPool = readJedisPool.get(hostAndPort);
		return shardedJedisPool;
	}

	/**
	 * �ر�д����
	 * 
	 * @param jedis
	 */
	public void closeReadConnection(String key, ShardedJedis jedis) {
		if (jedis == null)
			return;

		ShardedJedisPool shardedJedisPool = null;
		try {
			shardedJedisPool = matchReadJedisPool(key);
			if (shardedJedisPool == null)
				try {
					throw new RedisException(
							"can not close read redis pool for key: " + key);
				} catch (RedisException e) {
					e.printStackTrace();
				}
			shardedJedisPool.returnResource(jedis);
		} catch (java.lang.IllegalStateException e) {
			try {
				if (shardedJedisPool != null)
					shardedJedisPool.returnBrokenResource(jedis);
			} catch (Exception e1) {
				log.error(e.getMessage(), e);
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	public void returnBrokenReadConnection(String key, ShardedJedis jedis) {
		if (jedis == null)
			return;
		ShardedJedisPool shardedJedisPool = matchReadJedisPool(key);
		if (shardedJedisPool == null)
			try {
				throw new RedisException(
						"can not close read redis pool for key: " + key);
			} catch (RedisException e) {
				e.printStackTrace();
			}
		shardedJedisPool.returnBrokenResource(jedis);
	}

	/**
	 * ��ȡд����
	 * 
	 * @return
	 * @throws RedisException
	 */
	public ShardedJedis getWriteConnection() throws RedisException {
		int tryTime = 0;
		while (true) {
			try {
				return getResource();
			} catch (Exception e) {
				tryTime++;
				if (tryTime > MAX_TRY_GETCONNECTION)
					throw new RedisException(e);
				try {
					TimeUnit.MILLISECONDS.sleep(1);
				} catch (InterruptedException e1) {
				}
			}
		}

	}

	/**
	 * �ر�д����
	 * 
	 * @param jedis
	 */
	public void closeWriteConnection(ShardedJedis jedis) {
		try {
			if (jedis != null)
				returnResource(jedis);
		} catch (java.lang.IllegalStateException e) {
			try {
				 returnBrokenResource(jedis);
			} catch (Exception e1) {
				log.error(e.getMessage(), e);
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	/**
	 * �ر�д����
	 * 
	 * @param jedis
	 */
	public void returnBrokenWriteConnection(ShardedJedis jedis) {
		// if (jedis != null)
		// returnBrokenResource(jedis);
	}

	private boolean equals(List<MasterSlaveHostAndPort> currentShardMasters,
			List<MasterSlaveHostAndPort> shardMasters) {
		if (currentShardMasters != null && shardMasters != null) {
			if (currentShardMasters.size() == shardMasters.size()) {
				for (int i = 0; i < currentShardMasters.size(); i++) {
					if (!currentShardMasters.get(i).equals(shardMasters.get(i)))
						return false;
				}
				return true;
			}
		}
		return false;
	}

	private List<JedisShardInfo> makeShardInfoList(
			List<MasterSlaveHostAndPort> masters) {
		List<JedisShardInfo> shardMasters = new ArrayList<JedisShardInfo>();
		for (MasterSlaveHostAndPort master : masters) {
			JedisShardInfo jedisShardInfo = createJedisShardInfo(master
					.getMaster().getHost(), master.getMaster().getPort(),
					password);
			shardMasters.add(jedisShardInfo);
		}
		return shardMasters;
	}

	public List<JedisShardInfo> getCurrentWriteShardInfoList(){
		return currentWriteShareInfoList;
	}
	
	private List<MasterSlaveHostAndPort> sentinelGetMasterSlaves() {
		List<MasterSlaveHostAndPort> masterSlaveHostAndPorts = new ArrayList<MasterSlaveHostAndPort>();

		log.info("Trying to find all master from available Sentinels...");

		for (String masterName : masterNames) {
			HostAndPort master = null;
			boolean fetched = false;

			while (!fetched && sentinelRetry < MAX_RETRY_SENTINEL) {
				for (String sentinel : sentinels) {
					final HostAndPort hap = toHostAndPort(Arrays
							.asList(sentinel.split(":")));

					log.info("Connecting to Sentinel " + hap);
					try {
						Jedis jedis = new Jedis(hap.getHost(), hap.getPort());
						List<String> masterAddr = jedis
								.sentinelGetMasterAddrByName(masterName);
						List<Map<String, String>> slaveAddrs = jedis
								.sentinelSlaves(masterName);

						if (masterAddr == null || masterAddr.size() != 2) {
							log.warn(
									"Can not get master addr, master name: {}. Sentinel: {}.",
									masterName, hap);
							continue;
						}

						if (slaveAddrs == null || slaveAddrs.isEmpty()) {
							log.warn(
									"Can not get slave addr, master name: {}. Sentinel: {}.",
									masterName, hap);
							// continue;
						}

						master = toHostAndPort(masterAddr,masterName,true,0);
						
						Set<HostAndPort> slaves = new LinkedHashSet<HostAndPort>();
						if (slaveAddrs != null) {
						    int cursor = 0;
							for (Map<String, String> slave : slaveAddrs) {
								// if ("slave".equals(slave.get("flags"))) {
								slaves.add(toHostAndPort(Arrays.asList(
										slave.get("ip"), slave.get("port")),masterName,false,cursor));
								cursor++;
								// }
							}
						}
						MasterSlaveHostAndPort masterSlaveHostAndPort = new MasterSlaveHostAndPort(
								masterName, master, slaves);
						log.info("Found sharded master-slaves : {}",
								masterSlaveHostAndPort);
						masterSlaveHostAndPorts.add(masterSlaveHostAndPort);
						fetched = true;
						jedis.disconnect();
						break;
					} catch (JedisConnectionException e) {
						log.warn("Cannot connect to sentinel running @ " + hap
								+ ". Trying next one.");
					}
				}

				if (null == master) {
					try {
						log.error("All sentinels down, cannot determine where is "
								+ masterName
								+ " master is running... sleeping 1000ms, Will try again.");
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					fetched = false;
					sentinelRetry++;
				}
			}

			// Try MAX_RETRY_SENTINEL times.
			if (!fetched && sentinelRetry >= MAX_RETRY_SENTINEL) {
				sentinelRetry = 0;
				log.error("All sentinels down and try " + MAX_RETRY_SENTINEL
						+ " times, Abort.");
				throw new JedisConnectionException(
						"Cannot connect all sentinels, Abort.");
			}
		}

		return checkMastersHealth(masterSlaveHostAndPorts);
	}

	protected void initSentinelLiseners() {
		// if (currentHostMasters != null) {
		log.info("Starting Sentinel listeners...");

		for (String sentinel : sentinels) {
			final HostAndPort hap = toHostAndPort(Arrays.asList(sentinel
					.split(":")));
			MasterListener masterListener = new MasterListener(hap.getHost(),
					hap.getPort());
			masterListeners.add(masterListener);
			masterListener.start();
		}
		// }
	}
	private static String hostmocksplitor= "-";
	private HostAndPort toHostAndPortInDocker(List<String> getMasterAddrByNameResult,String masterName,boolean master,int cursor){
	    String host = masterName + hostmocksplitor + (master ? "M" : "S") + hostmocksplitor + cursor;
        int port = Integer.parseInt(getMasterAddrByNameResult.get(1));
        return new HostAndPort(host, port);
	}
	
	private HostAndPort toHostAndPort(List<String> getMasterAddrByNameResult){
	    String host = getMasterAddrByNameResult.get(0);
        int port = Integer.parseInt(getMasterAddrByNameResult.get(1));
        return new HostAndPort(host, port);
    }
	

	private HostAndPort toHostAndPort(List<String> getMasterAddrByNameResult,String masterName,boolean master,int cursor) {
		if(isInDockerEnvironment()){
		    return toHostAndPort(getMasterAddrByNameResult);
		}
		else{
		    return toHostAndPortInDocker(getMasterAddrByNameResult, masterName, master, cursor);
		}
	}

	protected static class ShardedJedisFactory implements
			PooledObjectFactory<ShardedJedis> {
		private List<JedisShardInfo> shards;
		private Hashing algo;
		private Pattern keyTagPattern;

		public ShardedJedisFactory(List<JedisShardInfo> shards, Hashing algo,
				Pattern keyTagPattern) {
			this.shards = shards;
			this.algo = algo;
			this.keyTagPattern = keyTagPattern;
		}

		public PooledObject<ShardedJedis> makeObject() throws Exception {
			ShardedJedis jedis = new ShardedJedis(shards, algo, keyTagPattern);
			return new DefaultPooledObject<ShardedJedis>(jedis);
		}

		public void destroyObject(PooledObject<ShardedJedis> pooledShardedJedis)
				throws Exception {
			final ShardedJedis shardedJedis = pooledShardedJedis.getObject();
			for (Jedis jedis : shardedJedis.getAllShards()) {
				try {
					try {
						jedis.quit();
					} catch (Exception e) {

					}
					jedis.disconnect();
				} catch (Exception e) {

				}
			}
		}

		public boolean validateObject(
				PooledObject<ShardedJedis> pooledShardedJedis) {

			try {
				ShardedJedis jedis = pooledShardedJedis.getObject();
				for (Jedis shard : jedis.getAllShards()) {
					if (!shard.ping().equals("PONG")) {
						return false;
					}
				}
				return true;
			} catch (Exception ex) {
				return false;
			}
		}

		public void activateObject(PooledObject<ShardedJedis> p)
				throws Exception {

		}

		public void passivateObject(PooledObject<ShardedJedis> p)
				throws Exception {

		}
	}

	protected class JedisPubSubAdapter extends JedisPubSub {
		@Override
		public void onMessage(String channel, String message) {
		}

		@Override
		public void onPMessage(String pattern, String channel, String message) {
		}

		@Override
		public void onPSubscribe(String pattern, int subscribedChannels) {
		}

		@Override
		public void onPUnsubscribe(String pattern, int subscribedChannels) {
		}

		@Override
		public void onSubscribe(String channel, int subscribedChannels) {
		}

		@Override
		public void onUnsubscribe(String channel, int subscribedChannels) {
		}
	}

	protected class MasterListener extends Thread {

		protected String host;
		protected int port;
		protected long subscribeRetryWaitTimeMillis = 5000;
		protected Jedis jedis;
		protected AtomicBoolean running = new AtomicBoolean(false);

		protected MasterListener() {
		}

		public MasterListener(String host, int port) {
			this.host = host;
			this.port = port;
		}

		public MasterListener(String host, int port,
				long subscribeRetryWaitTimeMillis) {
			this(host, port);
			this.subscribeRetryWaitTimeMillis = subscribeRetryWaitTimeMillis;
		}

		public void run() {
			running.set(true);
			while (running.get()) {
				log.debug("sentinel's master listening now...");
				jedis = new Jedis(host, port);

				try {
					if (jedis.sentinelMasters() != null) {
						log.info("sentinel {} connected now",
								new String[] { host + ":" + port });
						initSentinelPool();
					}
					jedis.subscribe(new JedisPubSub() {
						public void onMessage(String channel, String message) {
							log.info("Sentinel {} published: {} {}",
									new String[] { host + ":" + port, channel,
											message });
							initSentinelPool();
						}
					}, "+switch-master", "+sdown", "-sdown", "+slave");

				} catch (Exception e) {

					if (running.get()) {
						log.error("Lost connection to Sentinel at " + host
								+ ":" + port
								+ ". Sleeping 5000ms and retrying.");
						try {
							Thread.sleep(subscribeRetryWaitTimeMillis);
						} catch (InterruptedException e1) {
							e1.printStackTrace();
						}
					} else {
						log.info("Unsubscribing from Sentinel at " + host + ":"
								+ port);
					}
				}
			}
		}

		public void shutdown() {
			try {
				log.info("Shutting down listener on " + host + ":" + port);
				running.set(false);
				jedis.disconnect();
			} catch (Exception e) {
				log.error("Caught exception while shutting down: "
						+ e.getMessage());
			}
		}
	}
}