package org.aztec.framework.mybatis.conf.dao.ibatis.transformer;

import java.util.Date;

import org.aztec.framework.mybatis.conf.dao.ibatis.ShardValueTransformer;
import org.springframework.stereotype.Component;

import io.shardingsphere.core.api.algorithm.sharding.ShardingValue;

@Component("lvt")
public class LongValueTransformer implements ShardValueTransformer {

    @Override
    public Long transformToLong(ShardingValue shardValue, int shardSize, Object obj) {
        Long longVal = null;
        if(obj instanceof Long){
            longVal = (Long) obj;
        }
        else if (obj instanceof String){
            longVal = Long.parseLong((String) obj);
        }
        else if (obj instanceof Integer){
            longVal = ((Integer) obj).longValue();
        }
        else if (obj instanceof Date){
            longVal = ((Date) obj).getTime();
        }
        else if (obj instanceof String){
            return Long.parseLong(obj.toString());
        }
        return longVal % shardSize;
    }

}
