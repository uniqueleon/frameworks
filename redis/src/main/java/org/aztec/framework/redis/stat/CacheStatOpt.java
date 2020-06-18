package org.aztec.framework.redis.stat;

public enum CacheStatOpt {
	GETHIT,
	GETMISS,
	SET,
	DELETE,
	ADD,
	TIMEOUT,
	CANCEL,
	QUE_OVERFLOW,
	CONFIG_ERROR,
	MAX_KEY_LENGTH,
	MAX_BODY_LENGTH,
	MAX_MULTI_ITEM,
	MAX_MULTI_LENGTH,
	UNKNOWN_EXCEPTION
}