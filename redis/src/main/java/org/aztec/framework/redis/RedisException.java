package org.aztec.framework.redis;


public class RedisException extends RuntimeException {
	 
	private static final long serialVersionUID = 1L;

	public RedisException(Exception e) { 
		super(e);
	}

	public RedisException(String msg) {
		super(msg);
	}
	
	public RedisException(String msg,Throwable e) {
		super(msg,e);
	}
	
}
