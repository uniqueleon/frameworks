package org.aztec.framework.mybatis.conf.dao.ibatis.transformer;

import java.text.SimpleDateFormat;

import org.aztec.framework.mybatis.conf.dao.ibatis.ShardValueTransformer;
import org.springframework.stereotype.Component;

@Component("mbt")
public class MonthBaseTransformer extends DatetimeTransformer implements ShardValueTransformer {
    
    public final static SimpleDateFormat dateFormat = new SimpleDateFormat("M");

    @Override
    public SimpleDateFormat getTransformDataFormat() {
        return dateFormat;
    }

    
    public static void main(String[] args) {
        MonthBaseTransformer mbt = new MonthBaseTransformer();
        System.out.println(mbt.transformToLong(null, 0, "2019-01-11 16:36:36"));
    }
    
    /*private long getDoubleHashCode(ShardingValue shardValue,int shardSize,Long longVal){
        String redisKey = getRedisKey(shardValue);
        Map<String,Long> redisValues = redisManager.hgetAll(redisKey);
        boolean[] flags = new boolean[shardSize];
        for(String hashStr :redisValues.keySet()){
            Integer hashCode = Integer.parseInt(hashStr);
            flags[hashCode] = true;
            Long seqID = redisValues.get(hashStr);
            if(seqID.equals(longVal)){
                return new Long(hashCode);
            }
        }
        HashAlgorithm hashAlgorith = new HashAlgorithm(shardSize);
        int hashVal = hashAlgorith.doubleHash(flags, longVal.intValue());
        redisManager.hset(redisKey, "" + hashVal, longVal);
        return hashVal;
    }*/
}
