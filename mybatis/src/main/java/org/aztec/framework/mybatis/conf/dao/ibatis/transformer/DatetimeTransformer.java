package org.aztec.framework.mybatis.conf.dao.ibatis.transformer;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.aztec.framework.core.FrameworkLogger;
import org.aztec.framework.mybatis.conf.dao.ibatis.ShardValueTransformer;

import io.shardingsphere.core.api.algorithm.sharding.ShardingValue;

public abstract class DatetimeTransformer implements ShardValueTransformer{
    

    public final static SimpleDateFormat dateStrFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
    
    public abstract SimpleDateFormat getTransformDataFormat();
    
    @Override
    public Long transformToLong(ShardingValue shardValue,int shardSize,Object columnValue) {
        Long longVal = null;
        if (columnValue instanceof Date){
            longVal = Long.parseLong(getTransformDataFormat().format((Date) columnValue));
        }
        else if (columnValue instanceof String){
            try {
                longVal = Long.parseLong(getTransformDataFormat().format(dateStrFormat.parse(columnValue.toString())));
            } catch (ParseException e) {
                FrameworkLogger.error(e);
            }
        }
        
        //if(redisValues.containsKey(longVal))
        //return getDoubleHashCode(shardValue, shardSize, longVal);
        return longVal == null ? null : longVal;
    }
}
