package org.aztec.framework.elastic_job.beans;

import java.util.Map;

import com.google.common.collect.Maps;

public class JobExecuteContext {

    public static final Map<String,Object> context = Maps.newConcurrentMap();
    
    public static interface ContextKeys{
        public static final String SERVER_INDEX = "ELASTIC_JOB_SERVER";
        public static final String SHARD_JOB_ENABLED = "ELASTIC_JOB_ENABLE";
    }
    
    public static void put(String key,Object value){
        context.put(key, value);
    }
    
    public static <T> T getValue(String key){
        if(!context.containsKey(key)){
            return null;
        }
        return (T) context.get(key);
    }
    
    public static <T> T getValueWithDefault(String key,T defaultValue){
        T contextValue = getValue(key);
        return contextValue == null ? defaultValue : contextValue;
    }
}
