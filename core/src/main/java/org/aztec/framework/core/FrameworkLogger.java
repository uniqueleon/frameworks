package org.aztec.framework.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FrameworkLogger {

    public static final Logger LOG = LoggerFactory.getLogger(FrameworkLogger.class);
    
    private static String LOG_PREFIX = "[SJSC_FRAMEWORK]:";
    
    public static void info(String msg){
        LOG.info(LOG_PREFIX + msg);
    }
    
    public static void warn(String msg){
        LOG.warn(LOG_PREFIX + msg);
    }
    
    public static void error(Throwable t){
        LOG.error(t.getMessage(),t);
    }
}
