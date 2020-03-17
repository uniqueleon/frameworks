package org.aztec.framework.mybatis.conf.dao.ibatis.transformer;

import java.util.Date;

import org.aztec.framework.mybatis.conf.dao.ibatis.ShardValueTransformer;
import org.springframework.stereotype.Component;

@Component("lvt")
public class LongValueTransformer extends BasicValueTransformer implements ShardValueTransformer {

    @Override
    public Long transformToLong(int shardSize, Object obj) {
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

    @Override
    public Object getStartPoint() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Object nextPoint(Object point) {
        Long longVal = (Long) point;
        return longVal + 1;
    }

    @Override
    public Object previousPoint(Object point) {
        Long longVal = (Long) point;
        return longVal - 1;
    }


}
