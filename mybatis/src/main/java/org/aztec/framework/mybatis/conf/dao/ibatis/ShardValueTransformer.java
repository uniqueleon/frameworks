package org.aztec.framework.mybatis.conf.dao.ibatis;

import java.util.List;

import io.shardingsphere.core.api.algorithm.sharding.ShardingValue;

public interface ShardValueTransformer {

    public List<Long> transformToLong(ShardingValue shardValue,int shardSize);
}
