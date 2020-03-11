package org.aztec.framework.web.socket.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.aztec.framework.mybatis.conf.dao.ibatis.mapper.MyMapper;
import org.aztec.framework.web.socket.entity.WSConsumerQO;
import org.aztec.framework.web.socket.entity.WSMessageConsumer;

public interface BasicWSMessageConsumerMapper<T extends WSMessageConsumer> extends MyMapper<T>{

    public List<T> query(@Param("param")WSConsumerQO query);
    
}
