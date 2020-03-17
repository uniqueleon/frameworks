package org.aztec.framework.mybatis.conf.dao.ibatis;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import org.aztec.framework.core.FrameworkLogger;
import org.aztec.framework.mybatis.ShardingSQLException;
import org.aztec.framework.mybatis.conf.dao.ibatis.ShardingThreadLocalContext.ShardingColumnMatchType;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.api.client.util.Lists;
import com.google.common.collect.Maps;

import io.shardingsphere.core.api.algorithm.sharding.ShardingValue;
import io.shardingsphere.core.api.algorithm.sharding.complex.ComplexKeysShardingAlgorithm;

public abstract class SuffixShardingAlgorithm implements ComplexKeysShardingAlgorithm {

    @Autowired
    private Map<String, ShardValueTransformer> transformers;

    public SuffixShardingAlgorithm() {
    }

    protected abstract boolean isDatasourceAlgorithm();

    protected ShardValueTransformer getTransformer(TableRuleInfo ruleInfo, ShardingValue shardValue) {
        String[] pks = ConfigurationUtils.getShardKeysAsString(ruleInfo.getPrimaryKey(), isDatasourceAlgorithm())
                .split(",");
        String[] transformerStrs = ConfigurationUtils
                .getShardKeysAsString(ruleInfo.getTransformers(), isDatasourceAlgorithm()).split(",");
        for (int i = 0; i < pks.length; i++) {
            if (pks[i].equalsIgnoreCase(shardValue.getColumnName())) {
                return transformers.get(transformerStrs[i]);
            }
        }
        return null;
    }

    @Override
    public Collection<String> doSharding(Collection<String> availableTargetNames,
            Collection<ShardingValue> shardingValues) {
        // TODO Auto-generated method stub
        String bizID = UUID.randomUUID().toString();
        String logicName = getLoginName(availableTargetNames);
        if (isDatasourceAlgorithm()) {
            for (ShardingValue shardValue : shardingValues) {
                logicName = shardValue.getLogicTableName();
            }
        }
        TableRuleInfo ruleInfo = ConfigurationUtils.getRuleByTableName(logicName);
        String[] shardKeys = ConfigurationUtils.getShardKeys(ruleInfo.getPrimaryKey(), isDatasourceAlgorithm());
        List<ShardingInfo> shardInfoList = getShardingInfos(availableTargetNames, logicName, shardKeys);
        Map<String, Long> shardSizes = getShardSizes(shardInfoList);
        FrameworkLogger.info("[SHARDING_SQL]:<" + bizID + "> shard size:" + shardSizes);
        if (shardKeys == null) {
            return availableTargetNames;
        }
        Map<String, List<Long>> shardValues = Maps.newHashMap();
        List<String> retList = Lists.newArrayList();
        if (shardingValues.size() > 0) {
            for (ShardingValue shardValue : shardingValues) {
                // shardValue.get
                if (shardValue.getLogicTableName().equals(logicName) || isDatasourceAlgorithm()) {
                    String targetShardKey = getTargetShardKey(shardKeys, shardValue);
                    int shardSize = shardSizes.get(targetShardKey).intValue();
                    if (targetShardKey != null) {
                        List<Long> valueList = shardValues.get(targetShardKey);
                        if (valueList == null) {
                            valueList = Lists.newArrayList();
                        }
                        ShardValueTransformer transformer = getTransformer(ruleInfo, shardValue);
                        valueList.addAll(transformer.transformToLong(shardValue, shardSize));
                        shardValues.put(targetShardKey, valueList);
                    }

                }
            }
        }
       ShardingColumnMatchType matchType = ShardingThreadLocalContext
                .get(ShardingThreadLocalContext.ContextKeys.ColumnMatchType);
       if(matchType == null){
           matchType = ShardingColumnMatchType.PART;
       }
       FrameworkLogger.info("[SHARDING_SQL]:<" + bizID + ">columnMatchType:" + matchType + ",shardValues:" + shardValues);
        retList.addAll(loadHisShardInfo(bizID, shardInfoList, shardValues, ShardingColumnMatchType.FULL));
        if (retList.size() == 0) {
            if(ShardingColumnMatchType.PART.equals(matchType)){
                FrameworkLogger.info("[SHARDING_SQL]:<" + bizID + "> generate part shard info!");
                retList.addAll(loadHisShardInfo(bizID, shardInfoList, shardValues, matchType));
            }
            if(retList.size() == 0){
                FrameworkLogger.info("[SHARDING_SQL]:<" + bizID + "> no shard info hit!");
            }
        }
        validateSQL(retList,matchType);
        ShardingThreadLocalContext.put(ShardingThreadLocalContext.ContextKeys.ColumnMatchType, null);
        return retList.size() == 0 ? availableTargetNames : retList;
    }
    
    public List<String> loadHisShardInfo(String bizID,List<ShardingInfo> shardInfoList,Map<String, List<Long>> shardValues,ShardingColumnMatchType matchType){
        List<String> retList = Lists.newArrayList();
        for (ShardingInfo shardInfo : shardInfoList) {
            if (shardInfo.isHit(shardValues,matchType)) {
                FrameworkLogger.info("[SHARDING_SQL]:<" + bizID + "> shard info hit:" + shardInfo.getRawName()
                        + ",columnMatchType:" + matchType);
                retList.add(shardInfo.getRawName());
            }
        }
        return retList;
    }

    private void validateSQL(List<String> matchTables,ShardingColumnMatchType matchType) throws ShardingSQLException {
        
        Boolean showError = ShardingThreadLocalContext.get(ShardingThreadLocalContext.ContextKeys.showError);
        if(showError == null){
            showError = true;
        }
        if (matchTables.size() == 0 && ShardingColumnMatchType.FULL.equals(matchType) && showError) {
            throw new ShardingSQLException("No sharding table found ! While the match type is " + matchType + "");
        }
    }

    private String getTargetShardKey(String[] shardKeys, ShardingValue value) {

        for (String shardKey : shardKeys) {
            if (value.getColumnName().equalsIgnoreCase(shardKey)) {
                return shardKey;
            }
        }
        return null;
    }

    private String getLoginName(Collection<String> availableTargetNames) {

        String[] strArr = null;
        if (availableTargetNames.size() > 0) {
            strArr = availableTargetNames.iterator().next().split(DEFAULT_SPLITOR);
        }
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < strArr.length; i++) {
            try {
                Long.parseLong(strArr[i]);
                break;
            } catch (NumberFormatException e) {
                if (!builder.toString().isEmpty()) {
                    builder.append("_");
                }
                builder.append(strArr[i]);
            }
        }
        return builder.toString();
    }

    public static final String DEFAULT_SPLITOR = "_";

    private List<ShardingInfo> getShardingInfos(Collection<String> availableTargetNames, String logicName,
            String[] shardKeys) {
        List<ShardingInfo> shardInfo = Lists.newArrayList();
        for (String targetName : availableTargetNames) {
            shardInfo.add(new ShardingInfo(logicName, targetName, shardKeys));
        }
        return shardInfo;
    }

    private Map<String, Long> getShardSizes(List<ShardingInfo> shardInfos) {
        Map<String, Long> limits = Maps.newConcurrentMap();
        for (ShardingInfo info : shardInfos) {
            if (limits.isEmpty()) {
                limits.putAll(info.getSequeceNumbers());
            } else {
                for (Entry<String, Long> entry : info.getSequeceNumbers().entrySet()) {
                    Long upperLimit = limits.get(entry.getKey());
                    if (upperLimit < entry.getValue()) {
                        limits.put(entry.getKey(), entry.getValue());
                    }
                }
            }
        }
        for (String key : limits.keySet()) {
            limits.put(key, limits.get(key) + 1);
        }
        return limits;
    }

    private class ShardingInfo {

        private String rawName;
        Map<String, Long> sequeceNumbers = Maps.newHashMap();

        public ShardingInfo(String logicName, String targetName, String[] shardKeys) {
            String dataStr = targetName.substring(targetName.indexOf("_") + 1, targetName.length());
            if (targetName.contains(logicName)) {
                dataStr = targetName.substring(logicName.length() + 1, targetName.length());
            }
            String[] rawData = dataStr.split(DEFAULT_SPLITOR);
            int cursor = 0;
            this.rawName = targetName;
            for(String splitData : rawData){
                try {
                    Long targetValue = Long.parseLong(splitData);
                    sequeceNumbers.put(shardKeys[cursor], targetValue);
                    cursor++;
                } catch (NumberFormatException e) {
                    continue;
                }
            }
        }

        public boolean isHit(Map<String, List<Long>> shardKeyValues,ShardingColumnMatchType matchType) {
            boolean hit = false;
            if (matchType == null) {
                matchType = ShardingColumnMatchType.FULL;
            }
            for (String keyName : sequeceNumbers.keySet()) {
                hit = false;
                Long targetValue = sequeceNumbers.get(keyName);
                List<Long> targetValues = shardKeyValues.get(keyName);
                if (targetValues == null) {
                    if (matchType == ShardingColumnMatchType.FULL) {
                        return false;
                    } else {
                        continue;
                    }
                }
                for (Long value : targetValues) {
                    if (targetValue.equals(value)) {
                        hit = true;
                    }
                }
                if (!hit) {
                    break;
                } else if (matchType == ShardingColumnMatchType.PART) {
                    return hit;
                }
            }
            return hit;
        }

        public String getRawName() {
            return rawName;
        }

        public Map<String, Long> getSequeceNumbers() {
            return sequeceNumbers;
        }

    }

}
