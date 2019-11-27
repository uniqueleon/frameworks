package org.aztec.framework.mybatis.conf.dao.ibatis.mapper;

public interface MybatisMapperConfig {

    String getMapperScanPackage();

    Class<? extends MyMapper> getMapperCls();

    String notEmpty();

    String identity();

}
