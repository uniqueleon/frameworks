package org.aztec.framework.heartbeat.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.aztec.framework.heartbeat.entity.ServerNode;

import com.sjsc.framework.mybatis.conf.dao.ibatis.mapper.MyMapper;

public interface ServerInfoMapper<T extends ServerNode> extends MyMapper<T> {

    public List<ServerNode> findAllEffectiveNodes();
    public List<ServerNode> findServerByAppName(@Param("appName")String appName);
    public ServerNode findServerByHost(@Param("host")String host,@Param("port")Integer port);
    public Integer acquireLock();
    public Integer releaseLock();
    public Integer deleteNodes(@Param("ids")List<Long> ids); 
}
