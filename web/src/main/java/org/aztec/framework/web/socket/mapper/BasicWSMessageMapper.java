package org.aztec.framework.web.socket.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.aztec.framework.mybatis.conf.dao.ibatis.mapper.MyMapper;
import org.aztec.framework.web.socket.entity.WSMessage;
import org.aztec.framework.web.socket.entity.WSMessageQO;

public interface BasicWSMessageMapper<T extends WSMessage> extends MyMapper<T>{


    /**
     * 统计所有的主题消息
     * @return
     */
    public Long countTitleMessage();
    
    /**
     * 获取所有私信的消息
     * @param userID
     * @return
     */
    public List<WSMessage> getSecretMessages(@Param("excludeIds")List<Long> excludeIds);
    
    public List<T> query(@Param("param")WSMessageQO query);
    public int updateStatus(@Param("param")WSMessageQO param);
    public List<T> getUnreadMessage(@Param("readMsgIds")List<Long> readMsgIds);
}
