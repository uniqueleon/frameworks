package org.aztec.framework.mybatis.conf.dao.ibatis;

import io.shardingsphere.core.api.algorithm.sharding.ShardingValue;

public interface ShardValueTransformer {

    public Long transformToLong(ShardingValue shardValue,int shardSize,Object obj);
}
