package org.aztec.framework.mybatis.index;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.aztec.framework.mybatis.conf.dao.ibatis.mapper.MyMapper;

public interface BasicIndexMapper<T extends BasicIndex> extends MyMapper<T> {

    public T findOneByRemainder(@Param("remainder")Long remainder);
    public List<T> findByRemainder(@Param("query")IndexQuery qo);
    public Integer updateIndex(@Param("entity")BasicIndex record);
    public Integer insertIndex(@Param("entity")BasicIndex record);
    public Long countIndex(@Param("remainders")List<Long> remainders);
    public Long sumDataSize(@Param("remainders")List<Long> remainders);
    
}
