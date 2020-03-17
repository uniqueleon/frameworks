package org.aztec.framework.mybatis.conf.dao.ibatis.transformer;

import java.text.DateFormat.Field;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

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
        System.out.println(mbt.transformToLong( 0, "2019-01-11 16:36:36"));
    }




    @Override
    public Object nextPoint(Object point) {
        Date datePoint = (Date) point;
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(datePoint);
        calendar.set(Field.MONTH.getCalendarField(),calendar.get(Field.MONTH.getCalendarField()) + 1);
        return calendar.getTime();
    }


    @Override
    public Object previousPoint(Object point) {
        Date datePoint = (Date) point;
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(datePoint);
        calendar.set(Field.MONTH.getCalendarField(),calendar.get(Field.MONTH.getCalendarField()) - 1);
        return calendar.getTime();
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
