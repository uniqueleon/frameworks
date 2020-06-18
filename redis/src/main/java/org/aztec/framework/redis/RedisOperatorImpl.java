package org.aztec.framework.redis;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.aztec.framework.redis.stat.CacheStatLog;
import org.aztec.framework.redis.stat.CacheStatOpt;
import org.slf4j.Logger;

import com.google.common.base.Objects;
import com.google.common.base.Predicate;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;
import redis.clients.jedis.JedisShardInfo;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.Response;
import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPipeline;
import redis.clients.jedis.exceptions.JedisConnectionException;

/**
 * 
 * @author tanson lam
 * @create 2016��9��21��
 * 
 */
public class RedisOperatorImpl implements RedisOperator,RedisQueueOperator {
    private static Logger log = CacheLogger.logger;

    protected ShardedJedisSentinelPool pool;

    private CacheStatLog cacheStatLog;

    private static Map<String, SubscribeInfo> subscribeRecords = new ConcurrentHashMap<String, SubscribeInfo>();

    public void init(ShardedJedisSentinelPool pool) {
        this.pool = pool;
        this.cacheStatLog = CacheStatLog.getInstance();
        new SubscribeListMonitor().start();
    }

    private byte[] getByteKey(String key) {
        try {
            return key.getBytes("utf-8");
        } catch (UnsupportedEncodingException e) {
            throw new RedisException("cant not convert to byte for key :" + key);
        }
    }

    /**
     * ��ȡ�������󻺴�,��������Ӽ�Ⱥ��ȡ������ڴӿ��ȡʧ�ܻ����Ե������ȡ���ݡ�
     * 
     * @param key
     * @return
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    @Override
    public <T> T get(String key) throws RedisException {
        if (StringUtils.isEmpty(key))
            throw new IllegalArgumentException("key can not be null.");
        ShardedJedis shardedJedis = null;
        Jedis jedis = null;
        try {
            shardedJedis = pool.getReadConnection(key);
            jedis = pool.randomReadJedis(shardedJedis);

            cacheStatLog.log(key, CacheStatOpt.MAX_KEY_LENGTH,
                    getByteKey(key).length);
            byte[] data = jedis.get(getByteKey(key));
            if (data == null) {
                // �������Ϊ���������Ⳣ��һ��
                T resultInMaster = getByMaster(key);
                if (resultInMaster != null)
                    return resultInMaster;
                CacheStatLog.getInstance().log(key, CacheStatOpt.GETMISS, 1);
                return null;
            }
            cacheStatLog.log(key, CacheStatOpt.MAX_BODY_LENGTH, data.length);
            cacheStatLog.log(key, CacheStatOpt.GETHIT, 1);
            return (T) SerializingTranscoder.deserialize(data);
        } catch (Exception e) {
            cacheStatLog.log(key, CacheStatOpt.UNKNOWN_EXCEPTION, 1);
            throw new RedisException(e);
        } finally {
            pool.closeReadConnection(key, shardedJedis);
        }
    }

    /**
     * �����ǻ�ȡ�������󻺴�
     * 
     * @param key
     * @return
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    @Override
    public <T> T getByMaster(String key) throws RedisException {
        if (StringUtils.isEmpty(key))
            throw new IllegalArgumentException("key can not be null.");

        ShardedJedis shardedJedis = null;
        try {
            shardedJedis = pool.getWriteConnection();
            cacheStatLog.log(key, CacheStatOpt.MAX_KEY_LENGTH,
                    getByteKey(key).length);
            byte[] data = shardedJedis.get(getByteKey(key));
            if (data == null) {
                CacheStatLog.getInstance().log(key, CacheStatOpt.GETMISS, 1);
                return null;
            }
            cacheStatLog.log(key, CacheStatOpt.MAX_BODY_LENGTH, data.length);
            cacheStatLog.log(key, CacheStatOpt.GETHIT, 1);
            return (T) SerializingTranscoder.deserialize(data);
        } catch (Exception e) {
            cacheStatLog.log(key, CacheStatOpt.UNKNOWN_EXCEPTION, 1);
            throw new RedisException(e);
        } finally {
            pool.closeWriteConnection(shardedJedis);
        }
    }

    /**
     * ɾ�����󻺴�
     * 
     * @param key
     * @return
     * @throws Exception
     */
    @Override
    public Long del(String key) throws RedisException {
        if (StringUtils.isEmpty(key))
            throw new IllegalArgumentException("key can not be null.");

        ShardedJedis shardedJedis = null;
        try {
            cacheStatLog.log(key, CacheStatOpt.MAX_KEY_LENGTH,
                    getByteKey(key).length);
            shardedJedis = pool.getWriteConnection();
            Long result = shardedJedis.del(getByteKey(key));
            cacheStatLog.log(key, CacheStatOpt.DELETE, 1);
            return result;
        } catch (Exception e) {
            cacheStatLog.log(key, CacheStatOpt.UNKNOWN_EXCEPTION, 1);
            throw new RedisException(e);
        } finally {
            pool.closeWriteConnection(shardedJedis);
        }
    }

    /**
     * ����ָ������KEY��ʧЧʱ��
     * 
     * @param key
     * @param seconds
     * @return
     * @throws Exception
     */
    @Override
    public void expire(String key, Integer seconds) throws RedisException {
        if (StringUtils.isEmpty(key))
            throw new IllegalArgumentException("key can not be null.");
        if (seconds == null)
            throw new IllegalArgumentException("seconds can not be null.");

        ShardedJedis shardedJedis = null;
        try {
            shardedJedis = pool.getWriteConnection();
            shardedJedis.expire(getByteKey(key), seconds);
        } catch (Exception e) {
            throw new RedisException(e);
        } finally {
            pool.closeWriteConnection(shardedJedis);
        }
    }

    /**
     * ȡ������KEY��ʧЧʱ��
     * 
     * @param key
     * @return
     * @throws Exception
     */
    @Override
    public void persist(String key) throws RedisException {
        if (StringUtils.isEmpty(key))
            throw new IllegalArgumentException("key can not be null.");
        ShardedJedis shardedJedis = null;
        try {
            shardedJedis = pool.getWriteConnection();
            shardedJedis.persist(getByteKey(key));
        } catch (Exception e) {
            throw new RedisException(e);
        } finally {
            pool.closeWriteConnection(shardedJedis);
        }
    }

    /**
     * ���õ������󻺴�
     * 
     * @param key
     * @param value
     * @return
     * @throws RedisException
     */
    @Override
    public <T> String set(String key, T value) throws RedisException {
        if (StringUtils.isEmpty(key))
            throw new IllegalArgumentException("key can not be null.");
        if (value == null)
            throw new IllegalArgumentException("value can not be null.");
        ShardedJedis shardedJedis = null;
        try {
            cacheStatLog.log(key, CacheStatOpt.MAX_KEY_LENGTH,
                    getByteKey(key).length);
            shardedJedis = pool.getWriteConnection();
            byte[] data = SerializingTranscoder.serialize(value);
            cacheStatLog.log(key, CacheStatOpt.SET, 1);
            return shardedJedis.set(getByteKey(key), data);
        } catch (Exception e) {
            cacheStatLog.log(key, CacheStatOpt.UNKNOWN_EXCEPTION, 1);
            throw new RedisException(e);
        } finally {
            pool.closeWriteConnection(shardedJedis);
        }
    }

    /**
     * ���õ������󻺴�
     * 
     * @param key
     * @param value
     * @param expireSeconds
     *            ��Чʱ�䣬��λ��
     * @return
     * @throws RedisException
     */
    @Override
    public <T> String set(String key, T value, Integer expireSeconds)
            throws RedisException {
        if (StringUtils.isEmpty(key))
            throw new IllegalArgumentException("key can not be null.");
        if (value == null)
            throw new IllegalArgumentException("value can not be null.");
        if (expireSeconds == null)
            throw new IllegalArgumentException("expireSeconds can not be null.");

        ShardedJedis shardedJedis = null;
        try {
            shardedJedis = pool.getWriteConnection();
            cacheStatLog.log(key, CacheStatOpt.MAX_KEY_LENGTH,
                    getByteKey(key).length);
            byte[] data = SerializingTranscoder.serialize(value);
            String result = shardedJedis.set(getByteKey(key), data);
            shardedJedis.expire(getByteKey(key), expireSeconds);
            cacheStatLog.log(key, CacheStatOpt.SET, 1);
            return result;
        } catch (Exception e) {
            cacheStatLog.log(key, CacheStatOpt.UNKNOWN_EXCEPTION, 1);
            throw new RedisException(e);
        } finally {
            pool.closeWriteConnection(shardedJedis);
        }
    }

    /**
     * ���õ������󻺴�
     * 
     * @param key
     * @param value
     * @return
     * @throws RedisException
     */
    @Override
    public <T> void batchSet(Map<String, T> valueMap) throws RedisException {
        if (valueMap == null || valueMap.isEmpty())
            throw new IllegalArgumentException("valueMap can not be null.");
        ShardedJedis shardedJedis = null;
        try {
            shardedJedis = pool.getWriteConnection();
            ShardedJedisPipeline pipline = shardedJedis.pipelined();
            for (String key : valueMap.keySet()) {
                cacheStatLog.log(key, CacheStatOpt.MAX_KEY_LENGTH,
                        getByteKey(key).length);
                byte[] data = SerializingTranscoder
                        .serialize(valueMap.get(key));
//              shardedJedis.set(getByteKey(key), data);
                pipline.set(getByteKey(key), data);
                cacheStatLog.log(key, CacheStatOpt.SET, 1);
            }
            pipline.sync();
        } catch (Exception e) {
            for (String key : valueMap.keySet()) {
                cacheStatLog.log(key, CacheStatOpt.SET, -1);
                cacheStatLog.log(key, CacheStatOpt.UNKNOWN_EXCEPTION, 1);
            }
            throw new RedisException(e);
        } finally {
            pool.closeWriteConnection(shardedJedis);
        }
    }

    /**
     * ���õ������󻺴棬����Ѿ��ж���������
     * 
     * @param key
     * @param value
     * @return
     * @throws RedisException
     */
    @Override
    public <T> void batchSetnx(Map<String, T> valueMap) throws RedisException {
        if (valueMap == null || valueMap.isEmpty())
            throw new IllegalArgumentException("valueMap can not be null.");
        ShardedJedis shardedJedis = null;
        try {
            shardedJedis = pool.getWriteConnection();
            ShardedJedisPipeline pipline = shardedJedis.pipelined();
            for (String key : valueMap.keySet()) {
                cacheStatLog.log(key, CacheStatOpt.MAX_KEY_LENGTH,
                        getByteKey(key).length);
                byte[] data = SerializingTranscoder
                        .serialize(valueMap.get(key));
                shardedJedis.setnx(getByteKey(key), data);
                cacheStatLog.log(key, CacheStatOpt.SET, 1);
            }
            pipline.sync();
        } catch (Exception e) {
            for (String key : valueMap.keySet()) {
                cacheStatLog.log(key, CacheStatOpt.SET, -1);
                cacheStatLog.log(key, CacheStatOpt.UNKNOWN_EXCEPTION, 1);
            }
            throw new RedisException(e);
        } finally {
            pool.closeWriteConnection(shardedJedis);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> Map<String, T> batchGet(String... keys) throws RedisException {
        if (keys == null || keys.length == 0)
            throw new IllegalArgumentException("keys can not be null.");
        ShardedJedis shardedJedis = null;
        Map<String, T> returnMap = Maps.newHashMap();
        try {
            shardedJedis = pool.getWriteConnection();
            ShardedJedisPipeline pipline = shardedJedis.pipelined();
            HashMap<byte[], Response<byte[]>> newMap = new HashMap<byte[], Response<byte[]>>();

            for (String key : keys) {
                newMap.put(getByteKey(key), pipline.get(getByteKey(key)));
            }

            pipline.sync();
            for (Entry<byte[], Response<byte[]>> entry : newMap.entrySet()) {
                Response<byte[]> sResponse = (Response<byte[]>) entry
                        .getValue();
                byte[] b = sResponse.get();
                if (b == null)
                    continue;

                Object respData = SerializingTranscoder.deserialize(b);
                returnMap.put(new String(entry.getKey()), (T) respData);
            }
            return returnMap;
        } catch (Exception e) {
            throw new RedisException(e);
        } finally {
            pool.closeWriteConnection(shardedJedis);
        }
    }

    /**
     * ʹ�ù�ϣ���û���ֵ
     * 
     * @param key
     * @param score
     * @param member
     * 
     * @return
     * @throws RedisException
     */
    @Override
    public <T> Long hset(String key, String field, T value)
            throws RedisException {
        if (StringUtils.isEmpty(key))
            throw new IllegalArgumentException("key can not be null.");
        if (StringUtils.isEmpty(field))
            throw new IllegalArgumentException("field can not be null.");
        if (value == null)
            throw new IllegalArgumentException("value can not be null.");

        ShardedJedis shardedJedis = null;
        try {
            shardedJedis = pool.getWriteConnection();
            cacheStatLog.log(key, CacheStatOpt.MAX_KEY_LENGTH,
                    getByteKey(key).length);
            byte[] data = SerializingTranscoder.serialize(value);
            Long result = shardedJedis.hset(getByteKey(key), field.getBytes(),
                    data);
            cacheStatLog.log(key, CacheStatOpt.SET, 1);
            return result;
        } catch (Exception e) {
            cacheStatLog.log(key, CacheStatOpt.UNKNOWN_EXCEPTION, 1);
            throw new RedisException(e);
        } finally {
            pool.closeWriteConnection(shardedJedis);
        }
    }
    /**
     * ʹ�ù�ϣ���û���ֵ
     * 
     * @param key
     * @param score
     * @param member
     * 
     * @return
     * @throws RedisException
     */
    @Override
    public <T> void batchHset(Map<String, Map<String, T>> valueMap)
            throws RedisException {
        if (valueMap == null)
            throw new IllegalArgumentException("valueMap can not be null.");

        ShardedJedis shardedJedis = null;
        try {
            shardedJedis = pool.getWriteConnection();
            ShardedJedisPipeline pipeline = shardedJedis.pipelined();
            for (String key : valueMap.keySet()) {
                cacheStatLog.log(key, CacheStatOpt.MAX_KEY_LENGTH,
                        getByteKey(key).length);
                // ȡ��ʧЧʱ��
                pipeline.persist(getByteKey(key));
                Map<String, T> innerMap = valueMap.get(key);
                for (String field : innerMap.keySet()) {
                    byte[] data = SerializingTranscoder.serialize(innerMap
                            .get(field));
                    pipeline.hset(getByteKey(key), field.getBytes(), data);
                }
                cacheStatLog.log(key, CacheStatOpt.SET, 1);
            }
            pipeline.sync();
        } catch (Exception e) {
            throw new RedisException(e);
        } finally {
            pool.closeWriteConnection(shardedJedis);
        }
    }

    /**
     * ��ȡ��ϣ���û���ֵ
     * 
     * @param key
     * @param field
     * 
     * @return
     * @throws RedisException
     */
    @SuppressWarnings("unchecked")
    @Override
    public <T> T hget(String key, String field) throws RedisException {
        if (StringUtils.isEmpty(key))
            throw new IllegalArgumentException("key can not be null.");
        if (StringUtils.isEmpty(field))
            throw new IllegalArgumentException("field can not be null.");

        ShardedJedis shardedJedis = null;
        Jedis jedis = null;
        try {
            shardedJedis = pool.getReadConnection(key);
            jedis = pool.randomReadJedis(shardedJedis);
            cacheStatLog.log(key, CacheStatOpt.MAX_KEY_LENGTH,
                    getByteKey(key).length);
            byte[] data = jedis.hget(getByteKey(key), field.getBytes());
            if (data == null) {
                // �������Ϊ���������Ⳣ��һ��
                T resultInMaster = hgetByMaster(key, field);
                if (resultInMaster != null)
                    return (T) resultInMaster;
                CacheStatLog.getInstance().log(key, CacheStatOpt.GETMISS, 1);
                return null;
            }
            cacheStatLog.log(key, CacheStatOpt.MAX_BODY_LENGTH, data.length);
            cacheStatLog.log(key, CacheStatOpt.GETHIT, 1);
            return (T) SerializingTranscoder.deserialize(data);
        } catch (Exception e) {
            cacheStatLog.log(key, CacheStatOpt.UNKNOWN_EXCEPTION, 1);
            throw new RedisException(e);
        } finally {
            pool.closeReadConnection(key, shardedJedis);
        }
    }

    /**
     * ������ȡ��ϣ���û���ֵ
     * 
     * @param key
     * @param fields
     * 
     * @return
     * @throws RedisException
     */
    @SuppressWarnings("unchecked")
    @Override
    public <T> Map<String, T> batchHget(String key, String... fields)
            throws RedisException {
        if (StringUtils.isEmpty(key))
            throw new IllegalArgumentException("key can not be null.");
        if (fields == null)
            throw new IllegalArgumentException("fields can not be null.");

        ShardedJedis shardedJedis = null;
        Map<String, T> returnMap = new HashMap<String, T>();
        try {
            shardedJedis = pool.getReadConnection(key);
            Jedis jedis = pool.randomReadJedis(shardedJedis);
            Pipeline pipline = jedis.pipelined();
            cacheStatLog.log(key, CacheStatOpt.MAX_KEY_LENGTH,
                    getByteKey(key).length);

            int totalLength = 0;

            HashMap<byte[], Response<byte[]>> newMap = new HashMap<byte[], Response<byte[]>>();

            for (String field : fields) {
                newMap.put(field.getBytes(),
                        pipline.hget(getByteKey(key), field.getBytes()));
            }

            pipline.sync();
            for (Entry<byte[], Response<byte[]>> entry : newMap.entrySet()) {
                Response<byte[]> sResponse = (Response<byte[]>) entry
                        .getValue();
                byte[] b = sResponse.get();
                if (b == null)
                    continue;
                totalLength += b.length;
                Object respData = SerializingTranscoder.deserialize(b);
                String fieldKey = new String(entry.getKey());
                returnMap.put(fieldKey, (T) respData);
            }
            cacheStatLog.log(key, CacheStatOpt.MAX_BODY_LENGTH, totalLength);
            cacheStatLog.log(key, CacheStatOpt.GETHIT, 1);
            return returnMap;
        } catch (Exception e) {
            cacheStatLog.log(key, CacheStatOpt.UNKNOWN_EXCEPTION, 1);
            throw new RedisException(e);
        } finally {
            pool.closeReadConnection(key, shardedJedis);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T hgetByMaster(String key, String field) throws RedisException {
        if (StringUtils.isEmpty(key))
            throw new IllegalArgumentException("key can not be null.");
        if (StringUtils.isEmpty(field))
            throw new IllegalArgumentException("field can not be null.");

        ShardedJedis shardedJedis = null;
        try {
            shardedJedis = pool.getWriteConnection();
            cacheStatLog.log(key, CacheStatOpt.MAX_KEY_LENGTH,
                    getByteKey(key).length);
            byte[] data = shardedJedis.hget(getByteKey(key), field.getBytes());
            if (data == null) {
                CacheStatLog.getInstance().log(key, CacheStatOpt.GETMISS, 1);
                return null;
            }
            cacheStatLog.log(key, CacheStatOpt.MAX_BODY_LENGTH, data.length);
            cacheStatLog.log(key, CacheStatOpt.GETHIT, 1);
            return (T) SerializingTranscoder.deserialize(data);
        } catch (Exception e) {
            cacheStatLog.log(key, CacheStatOpt.UNKNOWN_EXCEPTION, 1);
            throw new RedisException(e);
        } finally {
            pool.closeWriteConnection(shardedJedis);
        }
    }

    /**
     * ��ȡ���й�ϣ�б�����ֵ
     * 
     * @param key
     * @return
     * @throws RedisException
     */
    @SuppressWarnings("unchecked")
    @Override
    public <T> Map<String, T> hgetAll(String key) throws RedisException {
        if (StringUtils.isEmpty(key))
            throw new IllegalArgumentException("key can not be null.");

        ShardedJedis shardedJedis = null;
        Jedis jedis = null;
        try {
            shardedJedis = pool.getReadConnection(key);
            jedis = pool.randomReadJedis(shardedJedis);
            cacheStatLog.log(key, CacheStatOpt.MAX_KEY_LENGTH,
                    getByteKey(key).length);
            Map<String, T> allMap = new HashMap<String, T>();
            Map<byte[], byte[]> map = jedis.hgetAll(getByteKey(key));
            if (map == null)
                return Maps.newHashMapWithExpectedSize(0);
            int bodyLength = 0;
            int totalItem = 0;
            for (byte[] mapkey : map.keySet()) {
                byte[] valueByte = map.get(mapkey);
                allMap.put(new String(mapkey),
                        (T) SerializingTranscoder.deserialize(valueByte));
                bodyLength += valueByte.length;
                totalItem++;
            }
            cacheStatLog.log(key, CacheStatOpt.MAX_MULTI_ITEM, totalItem);
            cacheStatLog.log(key, CacheStatOpt.MAX_BODY_LENGTH, bodyLength);
            cacheStatLog.log(key, CacheStatOpt.GETHIT, 1);
            return allMap;
        } catch (Exception e) {
            cacheStatLog.log(key, CacheStatOpt.UNKNOWN_EXCEPTION, 1);
            throw new RedisException(e);
        } finally {
            pool.closeReadConnection(key, shardedJedis);
        }
    }

    /**
     * ɾ�����󻺴�
     * 
     * @param key
     * @return
     * @throws Exception
     */
    @Override
    public Long hdel(String key, String... fields) throws RedisException {
        if (StringUtils.isEmpty(key))
            throw new IllegalArgumentException("key can not be null.");
        if (fields == null || fields.length == 0)
            throw new IllegalArgumentException("fields can not be null.");
        ShardedJedis shardedJedis = null;
        try {
            cacheStatLog.log(key, CacheStatOpt.MAX_KEY_LENGTH,
                    getByteKey(key).length);
            shardedJedis = pool.getWriteConnection();
            Long result = shardedJedis.hdel(key, fields);
            cacheStatLog.log(key, CacheStatOpt.DELETE, fields.length);
            return result;
        } catch (Exception e) {
            cacheStatLog.log(key, CacheStatOpt.UNKNOWN_EXCEPTION, 1);
            throw new RedisException(e);
        } finally {
            pool.closeWriteConnection(shardedJedis);
        }
    }

    /**
     * ������Ϣ
     * 
     * @param channel
     * @param operation
     */
    public void subscribe(String channel, JedisPubSub operation) {
        ShardedJedis shardedJedis = null;
        try {
            shardedJedis = pool.getWriteConnection();
            Jedis jedis = pool.getWriteJedis(shardedJedis, channel);
            subscribeRecords.put(channel, new SubscribeInfo(channel, operation,
                    jedis));
            jedis.subscribe(operation, channel);
        } catch (Exception e) {
            throw new RedisException(e);
        } finally {
            pool.closeWriteConnection(shardedJedis);
        }
    }

    /**
     * ������Ϣ
     * 
     * @param channel
     * @param operation
     */
    public void publish(String channel, String message) {
        ShardedJedis shardedJedis = null;
        try {
            shardedJedis = pool.getWriteConnection();
            Jedis jedis = pool.getWriteJedis(shardedJedis, channel);
            jedis.publish(channel, message);
        } catch (Exception e) {
            throw new RedisException(e);
        } finally {
            pool.closeWriteConnection(shardedJedis);
        }
    }

    /**
     * ��ѯ�����ֵ����
     * 
     * @param prefixKey
     * @return
     */
    @Override
    public Long countByPrefixKey(String prefixKey) {
        if (StringUtils.isEmpty(prefixKey))
            throw new IllegalArgumentException("prefixKey can not be null.");
        long count = 0l;
        ShardedJedis shardedJedis = null;
        try {
            shardedJedis = pool.getWriteConnection();
            Collection<Jedis> jedisList = shardedJedis.getAllShards();
            if (jedisList == null || jedisList.isEmpty())
                throw new RedisException("jedisList is empty.");

            for (Jedis jedis : jedisList) {
                try {
                    Set<String> keys = jedis.keys(prefixKey);
                    count += keys.size();
                } finally {
                    if (jedis != null)
                        jedis.close();
                }
            }
            return count;
        } catch (Exception e) {
            throw new RedisException(e);
        } finally {
            pool.closeWriteConnection(shardedJedis);
        }
    }
    /**
     * ���þ����ֵʧЧʱ��
     * 
     * @param prefixKey
     * @return
     */
    @Override
    public Long expireByPrefixKey(String prefixKey, int seconds) {
        if (StringUtils.isEmpty(prefixKey))
            throw new IllegalArgumentException("prefixKey can not be null.");
        long count = 0l;
        ShardedJedis shardedJedis = null;
        try {
            shardedJedis = pool.getWriteConnection();
            Collection<Jedis> jedisList = shardedJedis.getAllShards();
            if (jedisList == null || jedisList.isEmpty())
                throw new RedisException("jedisList is empty.");

            for (Jedis jedis : jedisList) {
                try {
                    Set<String> keys = jedis.keys(prefixKey);
                    int index = 0;
                    List<String[]> splits = splitSet(keys, 2000);
                    for (String[] group : splits) {
                        Pipeline pip = jedis.pipelined();
                        for (String key : group) {
                            pip.expire(key, seconds);
                            index++;
                        }
                        pip.sync();
                        try {
                            TimeUnit.MILLISECONDS.sleep(100);
                        } catch (InterruptedException e) {
                        }
                    }
                    log.info("prefixKey[" + prefixKey + "] size:" + keys.size()
                            + " process:" + index);
                    jedis.close();
                } finally {
                    if (jedis != null)
                        jedis.close();
                }
            }
            return count;
        } catch (Exception e) {
            throw new RedisException(e);
        } finally {
            pool.closeWriteConnection(shardedJedis);
        }
    }

    private List<String[]> splitSet(Collection<String> keys, int splitSize) {
        List<String[]> splitSet = Lists.newArrayList();
        int size = 0;
        int index = 0;
        int keysSize = keys.size();
        List<String> min = Lists.newArrayList();
        for (String key : keys) {
            min.add(key);
            if (splitSize == size) {
                splitSet.add(min.toArray(new String[min.size()]));
                min.clear();
                size = 0;
            }
            if (index == keysSize - 1) {
                splitSet.add(min.toArray(new String[min.size()]));
            }
            index++;
            size++;
        }
        return splitSet;
    }

    public void touchAllShards() {
        ShardedJedis shardedJedis = null;
        try {
            shardedJedis = pool.getWriteConnection();
            Collection<Jedis> allShards = shardedJedis.getAllShards();
            if (CollectionUtils.isEmpty(allShards))
                throw new JedisConnectionException("allshards is empty.");

            for (Jedis jedis : allShards) {
                jedis.get("touchShard");
            }
        } catch (Exception e) {
            throw new RedisException(e);
        } finally {
            pool.closeWriteConnection(shardedJedis);
        }
    }

    /**
     * ��ȡ�ֲ�ʽ��
     * 
     * @param lockKey
     *            ������ȡ��key
     * @param acquireTimeoutInMS
     *            ��ȡ����ʱʱ��
     * @param lockTimeoutInMS
     *            ���ĳ�ʱʱ��
     * @return
     */
    @Override
    public DistributedLockResp acquireLock(String lockKey,
            long acquireTimeoutInMS, long lockTimeoutInMS) {
        ShardedJedis shardedJedis = null;
        try {
            shardedJedis = pool.getWriteConnection();
            Jedis jedis = pool.getWriteJedis(shardedJedis, lockKey);
            long start = System.currentTimeMillis();
            int tryTotalTime = 0;
            while (true) {
                tryTotalTime++;
                long expires = System.currentTimeMillis() + lockTimeoutInMS + 1;
                String expiresStr = String.valueOf(expires);
                // ʹ��setnx��ȡ��
                if (jedis.setnx(lockKey, expiresStr) == 1) {
                    jedis.pexpire(lockKey, lockTimeoutInMS);
                    return new DistributedLockResp(lockKey, true, tryTotalTime);
                }
                // ��ȡ���Ļ���ʱ��
                String lockTimeInCache = jedis.get(lockKey);

                if (lockTimeInCache != null
                        && Long.parseLong(lockTimeInCache) < System
                                .currentTimeMillis()) {
                    String oldLockTimeInCache = jedis.getSet(lockKey,
                            expiresStr);
                    if (oldLockTimeInCache != null
                            && oldLockTimeInCache.equals(lockTimeInCache)) {
                        return new DistributedLockResp(lockKey, true,
                                tryTotalTime);
                    }
                }
                if ((System.currentTimeMillis() - start) < acquireTimeoutInMS) {
                    Thread.sleep(50);
                } else {
                    break;
                }
            }
            return new DistributedLockResp(lockKey, false, tryTotalTime);
        } catch (Exception e) {
            throw new RedisException(e);
        } finally {
            pool.closeWriteConnection(shardedJedis);
        }
    }
    
    @Override
    public Long batchDelByPrefixKeyAndValue(String prefixKey, Object value)
            throws RedisException {
        if (StringUtils.isEmpty(prefixKey))
            throw new IllegalArgumentException("prefixKey can not be null.");
        ShardedJedis shardedJedis = null;
        long count = 0;
        try {
            shardedJedis = pool.getWriteConnection();
            Collection<Jedis> jedisList = shardedJedis.getAllShards();
            if (jedisList == null || jedisList.isEmpty())
                throw new RedisException("jedisList is empty.");

            for (Jedis jedis : jedisList) {
                try {
                    Set<String> keys = jedis.keys(prefixKey);
                    List<String[]> splits = splitSet(keys, 2000);
                    for (String[] group : splits) {
                        Pipeline pipline = jedis.pipelined();
                        if (value == null) {
                            count+=group.length;
                            pipline.del(group);
                            pipline.sync();
                            try {
                                TimeUnit.MILLISECONDS.sleep(100);
                            } catch (InterruptedException e) {
                            }
                            continue;
                        }
                        HashMap<byte[], Response<byte[]>> newMap = new HashMap<byte[], Response<byte[]>>();

                        for (String key : group) {
                            newMap.put(getByteKey(key), pipline.get(getByteKey(key)));
                        }

                        pipline.sync();
                        Pipeline delPipline = jedis.pipelined();
                        for (Entry<byte[], Response<byte[]>> entry : newMap.entrySet()) {
                            Response<byte[]> sResponse = (Response<byte[]>) entry
                                    .getValue();
                            byte[] b = sResponse.get();
                            if (b == null)
                                continue;

                            Object respData = SerializingTranscoder.deserialize(b);
                            if (Objects.equal(respData, value)) {
                                count++;
                                delPipline.del(entry.getKey());
                            }
                        }
                        delPipline.sync();
                        try {
                            TimeUnit.MILLISECONDS.sleep(100);
                        } catch (InterruptedException e) {
                        }
                    }
                } finally {
                    if (jedis != null)
                        jedis.close();
                }
            }
            return count;
        } catch (Exception e) {
            throw new RedisException(e);
        } finally {
            pool.closeWriteConnection(shardedJedis);
        }
    }
    @Override
    public <T> Long batchDelByPrefixKeyAndPredicate(String prefixKey,Predicate<T> predicate,Class<?> type)
            throws RedisException {
        if (StringUtils.isEmpty(prefixKey))
            throw new IllegalArgumentException("prefixKey can not be null.");
        ShardedJedis shardedJedis = null;
        long count = 0;
        try {
            shardedJedis = pool.getWriteConnection();
            Collection<Jedis> jedisList = shardedJedis.getAllShards();
            if (jedisList == null || jedisList.isEmpty())
                throw new RedisException("jedisList is empty.");

            for (Jedis jedis : jedisList) {
                try {
                    Set<String> keys = jedis.keys(prefixKey);
                    List<String[]> splits = splitSet(keys, 2000);
                    for (String[] group : splits) {
                        Pipeline pipline = jedis.pipelined();
                        if (predicate == null) {
                            count+=group.length;
                            pipline.del(group);
                            pipline.sync();
                            try {
                                TimeUnit.MILLISECONDS.sleep(100);
                            } catch (InterruptedException e) {
                            }
                            continue;
                        }
                        HashMap<byte[], Object> newMap = new HashMap<byte[], Object>();

                        for (String key : group) {
                            if( type.isAssignableFrom(Byte[].class)  ){
                                newMap.put(getByteKey(key), pipline.get(getByteKey(key)));
                            }else if(type.isAssignableFrom(Map.class) ){
                                newMap.put(getByteKey(key), pipline.hgetAll(getByteKey(key)));
                            }
                        }

                        pipline.sync();
                        Pipeline delPipline = jedis.pipelined();
                        for (Entry<byte[], Object> entry : newMap.entrySet()) {
                            byte[] key = entry.getKey();
                            Response res = (Response)entry.getValue();
                            Object object = res.get();
                            if(object instanceof Map){
                                Map<byte[], byte[]> map = (Map<byte[], byte[]>) object;
                                Map<String, Object> allMap = new HashMap<String, Object>();
                                for (byte[] mapkey : map.keySet()) {
                                    byte[] valueByte = map.get(mapkey);
                                    allMap.put(new String(mapkey),SerializingTranscoder.deserialize(valueByte));
                                }
                                
                                if ( predicate.apply((T)allMap)) {
                                    count++;
                                    log.info("delete key:{}",new String(key));
                                    delPipline.del(key);
                                }
                            }else{
                                if ( predicate.apply((T)SerializingTranscoder.deserialize((byte[])object))) {
                                    count++;
                                    log.info("delete key:{}",new String(key));
                                    delPipline.del(key);
                                }
                            }
                        }
                        delPipline.sync();
                        try {
                            TimeUnit.MILLISECONDS.sleep(100);
                        } catch (InterruptedException e) {
                        }
                    }
                } finally {
                    if (jedis != null)
                        jedis.close();
                }
            }
            return count;
        } catch (Exception e) {
            throw new RedisException(e);
        } finally {
            pool.closeWriteConnection(shardedJedis);
        }
    }

    /**
     * ����
     * 
     * @param lockKey
     */
    @Override
    public void releaseLock(String lockKey) {
        this.del(lockKey);
    }

    public List<JedisShardInfo> getCurrentWriteShardInfoList() {
        return pool.getCurrentWriteShardInfoList();
    }

    /**
     * ������Ϣ
     */
    class SubscribeInfo {
        private String channel;
        private JedisPubSub jedisPubSub;
        private Jedis jedis;

        public SubscribeInfo(String channel, JedisPubSub jedisPubSub,
                Jedis jedis) {
            super();
            this.channel = channel;
            this.jedisPubSub = jedisPubSub;
            this.jedis = jedis;
        }

        public String getChannel() {
            return channel;
        }

        public void setChannel(String channel) {
            this.channel = channel;
        }

        public JedisPubSub getJedisPubSub() {
            return jedisPubSub;
        }

        public void setJedisPubSub(JedisPubSub jedisPubSub) {
            this.jedisPubSub = jedisPubSub;
        }

        public Jedis getJedis() {
            return jedis;
        }

        public void setJedis(Jedis jedis) {
            this.jedis = jedis;
        }

        public Boolean equalJedis(Jedis jedis) {
            if (this.jedis == null || jedis == null)
                return false;

            if (this.jedis.getClient().getHost()
                    .equals(jedis.getClient().getHost())
                    && this.jedis.getClient().getPort() == jedis.getClient()
                            .getPort())
                return true;
            return false;
        }
    }

    class SubscribeListMonitor extends Thread {
        protected AtomicBoolean running = new AtomicBoolean(false);

        @Override
        public void run() {
            running.set(true);
            while (running.get()) {
                try {
                    ShardedCounter shardedCounter = null;
                    try {
                        shardedCounter = new ShardedCounter(
                                getCurrentWriteShardInfoList());
                    } catch (Exception e1) {
                        log.error(e1.getMessage(), e1);
                    }
                    if (shardedCounter != null) {
                        for (String channel : subscribeRecords.keySet()) {
                            try {
                                SubscribeInfo subscribeInfo = subscribeRecords
                                        .get(channel);
                                Jedis jedis = shardedCounter.getShard(channel);
                                // ����channel���ƻ�ȡ�ڵ�λ���Ƿ�ı�
                                /*
                                 * log.info("current channel " + channel +
                                 * "SubscribeInfo:" +
                                 * jedis.getClient().getHost() + " port:" +
                                 * jedis.getClient().getPort());
                                 */
                                if (!subscribeInfo.equalJedis(jedis)) {
                                    log.info("changed Subscribe for channel:"
                                            + channel);
                                    // ȡ���ɽڵ㶩��
                                    try {
                                        subscribeRecords.remove(channel);
                                        subscribeInfo.getJedisPubSub()
                                                .unsubscribe(channel);
                                    } catch (Throwable e) {
                                        log.error(
                                                "fail to unsubscribe for channel:"
                                                        + channel, e);
                                    }
                                }
                            } catch (Throwable e) {
                                log.error("error in SubscribeListMonitor for channel:"
                                        + channel);
                            }
                        }
                    }
                } catch (Throwable e1) {
                    e1.printStackTrace();
                }
                try {
                    TimeUnit.SECONDS.sleep(5);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            log.info(this.getName() + " quit SubscribeListMonitor running...");
        }

        public void shutdown() {
            try {
                running.set(false);
                for (String channel : subscribeRecords.keySet()) {
                    SubscribeInfo subscribeInfo = subscribeRecords.get(channel);
                    log.info("unsubscribe for channel:" + channel);
                    subscribeInfo.getJedisPubSub().unsubscribe(channel);
                }
            } catch (Exception e) {
                log.error("Caught exception while shutting down: "
                        + e.getMessage());
            }
        }
    }

    @Override
    public <T> Long incr(String key) {
        if (StringUtils.isEmpty(key))
            throw new IllegalArgumentException("key can not be null.");
        ShardedJedis shardedJedis = null;
        try {
            shardedJedis = pool.getWriteConnection();
            Long data = shardedJedis.incr(getByteKey(key));
            return data;
        } catch (Exception e) {
            throw new RedisException(e);
        } finally {
            pool.closeWriteConnection(shardedJedis);
        }
    }

    @Override
    public <T> Long incrByValue(String key, Long increment) {
        if (StringUtils.isEmpty(key))
            throw new IllegalArgumentException("key can not be null.");
        ShardedJedis shardedJedis = null;
        try {
            shardedJedis = pool.getWriteConnection();
            Long data = shardedJedis.incrBy(getByteKey(key), increment);
            return data;
        } catch (Exception e) {
            throw new RedisException(e);
        } finally {
            pool.closeWriteConnection(shardedJedis);
        }
    }
@Override
	public <T> Long push(String queue, T value)
	        throws RedisException {
	    if (StringUtils.isEmpty(queue))
	        throw new IllegalArgumentException("key can not be null.");
	    if (value == null)
	        throw new IllegalArgumentException("value can not be null.");
	    ShardedJedis shardedJedis = null;
	    try {
	        shardedJedis = pool.getWriteConnection();
	        cacheStatLog.log(queue, CacheStatOpt.MAX_KEY_LENGTH,
	                getByteKey(queue).length);
	        byte[] data = SerializingTranscoder.serialize(value);
	        Long result = shardedJedis.rpush(getByteKey(queue),data);
	        cacheStatLog.log(queue, CacheStatOpt.ADD, 1);
	        return result;
	    } catch (Exception e) {
	        cacheStatLog.log(queue, CacheStatOpt.UNKNOWN_EXCEPTION, 1);
	        throw new RedisException(e);
	    } finally {
	        pool.closeWriteConnection(shardedJedis);
	    }
	}
@Override
	public <T> T pop(String queue)
	        throws RedisException {
	    if (StringUtils.isEmpty(queue))
	        throw new IllegalArgumentException("key can not be null.");
	    ShardedJedis shardedJedis = null;
	    try {
	        shardedJedis = pool.getWriteConnection();
	        cacheStatLog.log(queue, CacheStatOpt.MAX_KEY_LENGTH,
	                getByteKey(queue).length);
	        byte[] data = shardedJedis.lpop(getByteKey(queue));
	        cacheStatLog.log(queue, CacheStatOpt.DELETE, 1);
	        if( data == null) {
	        	return null;
	        }
	        return (T) SerializingTranscoder.deserialize(data);
	    } catch (Exception e) {
	        cacheStatLog.log(queue, CacheStatOpt.UNKNOWN_EXCEPTION, 1);
	        throw new RedisException(e);
	    } finally {
	        pool.closeWriteConnection(shardedJedis);
	    }
	}
@Override
public <T> List<T> lrang(String queue,Integer num)
        throws RedisException {
    if (StringUtils.isEmpty(queue))
        throw new IllegalArgumentException("key can not be null.");
    ShardedJedis shardedJedis = null;
    try {
        shardedJedis = pool.getWriteConnection();
        cacheStatLog.log(queue, CacheStatOpt.MAX_KEY_LENGTH,
                getByteKey(queue).length);
       List<byte[]> dataList = shardedJedis.lrange(getByteKey(queue), 0, num-1);
        cacheStatLog.log(queue, CacheStatOpt.GETHIT, 1);
        List<T> transferList  = new ArrayList<>();
        for(byte[] data :dataList) {
        	T transferData = (T) SerializingTranscoder.deserialize(data);
        	transferList.add(transferData);
        }
        return transferList;
    } catch (Exception e) {
        cacheStatLog.log(queue, CacheStatOpt.UNKNOWN_EXCEPTION, 1);
        throw new RedisException(e);
    } finally {
        pool.closeWriteConnection(shardedJedis);
    }
}
}
