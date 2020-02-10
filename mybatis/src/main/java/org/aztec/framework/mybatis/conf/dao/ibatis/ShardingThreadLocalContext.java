package org.aztec.framework.mybatis.conf.dao.ibatis;

import java.util.HashMap;
import java.util.Map;

public class ShardingThreadLocalContext {

    public static enum ShardingColumnMatchType{
        FULL,PART;
    }
    
    public static interface ContextKeys{
        String ColumnMatchType = "columnMatchType";
    }
    
    private static final ThreadLocal<Map<String,Object>> context = new ThreadLocal<>();
    
    public static void put(String key,Object value){
        Map<String,Object> contextMap = getContextMap();
        if(contextMap == null){
            contextMap = new HashMap<>();
            context.set(contextMap);
        }
        contextMap.put(key, value);
    }
    
    private static Map<String,Object> getContextMap(){
        Map<String,Object> contextMap = context.get();
        if(contextMap == null){
            contextMap = new HashMap<>();
            context.set(contextMap);
        }
        return contextMap;
    }
    
    public static <T> T get(String key){

        Map<String,Object> contextMap = getContextMap();
        return (T) contextMap.get(key);
    }
}
