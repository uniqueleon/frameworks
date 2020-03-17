package org.aztec.framework.mybatis.conf.dao.ibatis.transformer;

import java.util.List;

import org.aztec.framework.mybatis.conf.dao.ibatis.ShardValueTransformer;

import com.google.api.client.util.Lists;
import com.google.common.collect.Range;

import io.shardingsphere.core.api.algorithm.sharding.ListShardingValue;
import io.shardingsphere.core.api.algorithm.sharding.PreciseShardingValue;
import io.shardingsphere.core.api.algorithm.sharding.RangeShardingValue;
import io.shardingsphere.core.api.algorithm.sharding.ShardingValue;

public abstract class BasicValueTransformer implements ShardValueTransformer {

    protected abstract Long transformToLong(int shardSize,Object columnValue);
    protected abstract Object getStartPoint();
    protected abstract Object nextPoint(Object point);
    protected abstract Object previousPoint(Object point);
    
    
    public List<Object> getPossibleValues(Range range){
        if((range.hasUpperBound() && !range.hasLowerBound())
                || (range.hasLowerBound() && ! range.hasUpperBound())){
            throw new UnsupportedOperationException("Unsupport infinitive lower limit literator!");
        }
        Comparable lower = range.lowerEndpoint();
        Comparable upper = range.upperEndpoint();
        List<Object> retList = Lists.newArrayList();
        Object startPoint = getStartPoint();
        Object nextStartPoint = nextPoint(startPoint);
        Object nextPreviousPoint = nextPoint(startPoint);
        if(lower.compareTo(startPoint) > 0 && 
                lower.compareTo(nextStartPoint) > 0){
            while(lower.compareTo(startPoint) > 0
                    && lower.compareTo(nextStartPoint) > 0){
                startPoint = nextPoint(startPoint);
                nextStartPoint = nextPoint(startPoint);
            }
        }
        else if (lower.compareTo(startPoint) < 0
                && lower.compareTo(nextPreviousPoint) < 0){
            while(lower.compareTo(startPoint) < 0
                    && lower.compareTo(nextPreviousPoint) < 0){
                startPoint = previousPoint(startPoint);
                nextPreviousPoint = previousPoint(startPoint);
            }
        }
        retList.add(startPoint);
        Object point = nextPoint(startPoint);
        while(upper.compareTo(point) > 0){
            retList.add(point);
            point = nextPoint(point);
        }
        return retList;
    }
    
    @Override
    public List<Long> transformToLong(ShardingValue shardValue, int shardSize) {
        List<Long> retList = Lists.newArrayList();
        if (shardValue instanceof PreciseShardingValue) {

            PreciseShardingValue psv = (PreciseShardingValue) shardValue;
            Long longValue = transformToLong(shardSize,
                    psv.getValue());
            retList.add(longValue);
        }

        else if (shardValue instanceof ListShardingValue) {

            ListShardingValue psv = (ListShardingValue) shardValue;
            for (Object valueObj : psv.getValues()) {
                Long longValue = transformToLong(shardSize,
                        valueObj);
                if (longValue != null) {
                    retList.add(longValue);
                }
            }
        }
        else if (shardValue instanceof RangeShardingValue){
            RangeShardingValue psv = (RangeShardingValue) shardValue;
            for (Object valueObj : getPossibleValues(psv.getValueRange())) {
                Long longValue = transformToLong(shardSize,
                        valueObj);
                if (longValue != null) {
                    retList.add(longValue);
                }
            }
        }
        return retList;
    }

}
