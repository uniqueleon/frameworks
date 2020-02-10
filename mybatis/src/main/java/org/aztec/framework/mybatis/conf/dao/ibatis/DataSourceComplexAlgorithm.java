package org.aztec.framework.mybatis.conf.dao.ibatis;

public abstract class DataSourceComplexAlgorithm extends SuffixShardingAlgorithm {

    @Override
    protected boolean isDatasourceAlgorithm() {
        return true;
    }

}
