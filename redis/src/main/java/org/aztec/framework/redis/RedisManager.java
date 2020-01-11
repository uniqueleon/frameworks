package org.aztec.framework.redis;

import java.util.Map;

import com.google.common.base.Predicate;

public class RedisManager implements RedisOperator{

	@Override
	public <T> T get(String key) throws RedisException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> T getByMaster(String key) throws RedisException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long del(String key) throws RedisException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void persist(String key) throws RedisException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void expire(String key, Integer seconds) throws RedisException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public <T> String set(String key, T value) throws RedisException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> String set(String key, T value, Integer expireSeconds) throws RedisException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> void batchSet(Map<String, T> valueMap) throws RedisException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public <T> void batchSetnx(Map<String, T> valueMap) throws RedisException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public <T> Long hset(String key, String field, T value) throws RedisException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> Map<String, T> hgetAll(String key) throws RedisException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long hdel(String key, String... fields) throws RedisException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> void batchHset(Map<String, Map<String, T>> valueMap) throws RedisException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public <T> T hget(String key, String field) throws RedisException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> Map<String, T> batchHget(String key, String... fields) throws RedisException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> T hgetByMaster(String key, String field) throws RedisException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> Map<String, T> batchGet(String... keys) throws RedisException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long batchDelByPrefixKeyAndValue(String prefixKey, Object value) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long countByPrefixKey(String prefixKey) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long expireByPrefixKey(String prefixKey, int seconds) {
		// TODO Auto-generated method stub
		return null;
	}



	@Override
	public <T> Long incr(String key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> Long incrByValue(String key, Long increment) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void releaseLock(String lockKey) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public <T> Long batchDelByPrefixKeyAndPredicate(String prefixKey, Predicate<T> predicate, Class<?> type)
			throws RedisException {
		// TODO Auto-generated method stub
		return null;
	}

	
	
}
