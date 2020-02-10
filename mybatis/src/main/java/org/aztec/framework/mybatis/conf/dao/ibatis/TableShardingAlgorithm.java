package org.aztec.framework.mybatis.conf.dao.ibatis;

public abstract class TableShardingAlgorithm extends SuffixShardingAlgorithm {

    @Override
    protected boolean isDatasourceAlgorithm() {
        return false;
    }

}
