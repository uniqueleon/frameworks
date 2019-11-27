package org.aztec.framework.mybatis.conf.dao.ibatis;

import java.util.UUID;

import org.springframework.stereotype.Component;

import io.shardingsphere.core.keygen.DefaultKeyGenerator;

@Component
public class DefaultIDGenerator implements ShardedIDGenerator{
    
    @Override
    public Long getLongID() {
        DefaultKeyGenerator keyGene = new DefaultKeyGenerator();
        return keyGene.generateKey().longValue();
    }

    @Override
    public String getUUID() {
        return UUID.randomUUID().toString();
    }

}
