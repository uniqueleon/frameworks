package org.aztec.framework.redis;

import java.util.Map;

import com.google.common.base.Predicate;


public interface RedisOperator {
    /**
     * ��ȡ�������󻺴�,��������Ӽ�Ⱥ��ȡ������ڴӿ��ȡʧ�ܻ����Ե������ȡ���ݡ�
     * 
     * @param key
     * @return
     * @throws Exception
     */
    public <T> T get(String key) throws RedisException;

    /**
     * �����ǻ�ȡ�������󻺴�
     * 
     * @param key
     * @return
     * @throws Exception
     */
    public <T> T getByMaster(String key) throws RedisException;

    /**
     * ɾ�����󻺴�
     * 
     * @param key
     * @return
     * @throws Exception
     */
    public Long del(String key) throws RedisException;
    /**
     * ȡ������KEY��ʧЧʱ��
     * 
     * @param key 
     * @return
     * @throws Exception
     */
    public void persist(String key) throws RedisException;

    /**
     * ����ָ������KEY��ʧЧʱ��
     * 
     * @param key
     * @param seconds
     * @return
     * @throws Exception
     */
    public void expire(String key, Integer seconds) throws RedisException;

    /**
     * ���õ������󻺴�
     * 
     * @param key
     * @param value
     * @return
     * @throws RedisException
     */
    public <T> String set(String key, T value) throws RedisException;

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
    public <T> String set(String key, T value, Integer expireSeconds)
            throws RedisException;

    /**
     * ���õ������󻺴�
     * 
     * @param key
     * @param value
     * @return
     * @throws RedisException
     */
    public <T> void batchSet(Map<String, T> valueMap) throws RedisException;
    
    /**
     * ���õ������󻺴棬����Ѿ���ֵ������
     * 
     * @param key
     * @param value
     * @return
     * @throws RedisException
     */
    public <T> void batchSetnx(Map<String, T> valueMap) throws RedisException;

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
    public <T> Long hset(String key, String field, T value)
            throws RedisException;

    /**
     * ��ȡ���й�ϣ�б�����ֵ
     * 
     * @param key
     * @return
     * @throws RedisException
     */
    public <T> Map<String, T> hgetAll(String key) throws RedisException;

    /**
     * ɾ�����󻺴�
     * 
     * @param key
     * @param fields
     * @return
     * @throws Exception
     */
    public Long hdel(String key, String... fields) throws RedisException;

    /**
     * �������ù�ϣ����ֵ
     * 
     * @param key
     * @param fieldValue
     * @return
     * @throws RedisException
     */
    /*public <T> String hmset(String key, Map<String, T> fieldValue)
            throws RedisException;*/

    /**
     * �������ö����ϣ����ֵ
     * 
     * @param valueMap
     * @throws RedisException
     */
    public <T> void batchHset(Map<String, Map<String, T>> valueMap)
            throws RedisException;

    /**
     * ��ȡ��ϣ���û���ֵ
     * 
     * @param key
     * @param field
     * 
     * @return
     * @throws RedisException
     */
    public <T> T hget(String key, String field) throws RedisException;

    /**
     * ������ȡ��ϣ���û���ֵ
     * 
     * @param key
     * @param fields
     * 
     * @return
     * @throws RedisException
     */
    public <T> Map<String, T> batchHget(String key, String... fields)
            throws RedisException;

    /**
     * �����ڵ�ʵʱ��ȡ��ϣ���û���ֵ
     * 
     * @param key
     * @param field
     * @return
     * @throws RedisException
     */
    public <T> T hgetByMaster(String key, String field) throws RedisException;

    /**
     * ������ȡ����
     * 
     * @param keys
     * @return
     * @throws RedisException
     */
    public <T> Map<String, T> batchGet(String... keys) throws RedisException;
    
    /**
     * ����ɾ������
     * ���ݸ�����ǰ׺�ҳ�����key��ֵ���Ա�value�������һ���ľ�ɾ��
     * 
     * @param prefixKey
     * @param value ���valueΪ���򲻶Ա�
     * @return
     * @throws RedisException
     */
    public Long batchDelByPrefixKeyAndValue(String prefixKey, Object value);

    /**
     * ��ѯ�����ֵ����
     * 
     * @param prefixKey
     * @return
     */
    public Long countByPrefixKey(String prefixKey);

    /**
     * ���þ����ֵʧЧʱ��
     * 
     * @param prefixKey
     * @return
     */
    public Long expireByPrefixKey(String prefixKey, int seconds);
    
    /**
     * ����
     * 
     * @param lockKey
     */ 
    public void releaseLock(String lockKey);

    public <T> Long batchDelByPrefixKeyAndPredicate(String prefixKey, Predicate<T> predicate,Class<?> type) throws RedisException;
    
    public <T> Long incr(String key);

    public <T> Long incrByValue(String key, Long increment);

}
