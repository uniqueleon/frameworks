package org.aztec.framework.redis;

import java.util.List;

public interface RedisQueueOperator {
	    public <T> T pop(String key) throws RedisException;
	    public <T> Long push(String queue, T value) throws RedisException;
	    public <T> List<T> lrang(String queue,Integer num)
	            throws RedisException;
}
