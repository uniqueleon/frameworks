package org.aztec.framework.mybatis.conf.dao.ibatis;

import java.util.Map;

import com.google.common.collect.Maps;

public class ConfigurationUtils {
    
    private static final Map<String,TableRuleInfo> tableRules = Maps.newConcurrentMap();
    private static final Map<String,TableRuleInfo> dsRules = Maps.newConcurrentMap();
    
    public static void addTableRule(TableRuleInfo ruleInfo){
        tableRules.put(ruleInfo.getName(), ruleInfo);
    }
    
    public static TableRuleInfo getRuleByTableName(String tableName){
        return tableRules.get(tableName);
    }
    
    
    
    public static String getShardKeysAsString(String primaryKey,boolean isDatasource){
        StringBuilder builder = new StringBuilder();
        String[] shardKeys = getShardKeys(primaryKey, isDatasource);
        if(shardKeys == null){
            return "";
        }
        for(String shardKey : shardKeys){
            if(!builder.toString().isEmpty()){
                builder.append(",");
            }
            builder.append(shardKey);
        }
        return builder.toString();
    }
    
    public static String[] getShardKeys(String primaryKey,boolean isDatasource){
        String[] groupData = primaryKey.split("\\|");
        
        if(isDatasource){
            if(groupData.length == 1){
                return null;
            }
            else{
                String[] shardKey = primaryKey.split("\\|")[0].split("\\,");
                return shardKey;
            }
        }
        else{
            int location = 1;
            if(groupData.length == 1){
                location = 0;
            }
            String[] shardKey = primaryKey.split("\\|")[location].split("\\,");
            return shardKey;
        }
    }
}
