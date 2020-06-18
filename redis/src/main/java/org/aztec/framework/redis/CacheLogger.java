package org.aztec.framework.redis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author tanson lam
 * @create 2016��9��19��
 * 
 */
public class CacheLogger {
	public final static Logger logger = LoggerFactory.getLogger("cachelog");

	public static void debug(String msg) {
		if (CacheConfig.isDebugOpen())
			logger.info(msg);
	}
}
