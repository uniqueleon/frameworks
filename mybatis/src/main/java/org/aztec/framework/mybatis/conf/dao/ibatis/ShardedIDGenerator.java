package org.aztec.framework.mybatis.conf.dao.ibatis;

public interface ShardedIDGenerator {

    Long getLongID();

    String getUUID();

}
