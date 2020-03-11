package org.aztec.framework.web.socket;

import java.util.List;

import org.aztec.framework.web.socket.entity.WSMessageDTO;
import org.aztec.framework.web.socket.entity.WSMessageRequest;
import org.aztec.framework.web.socket.entity.WSMsgStatisticInfo;

/**
 * 消息提供者，用于生产消息.
 * @author liming
 *
 */
public interface WSMessageService {

    /**
     * 获取统计信息
     * @param messageQO
     * @return
     */
    public WSMsgStatisticInfo getStatisticInfo(WSMessageRequest messageQO);
    
    /**
     * 获取消息列表，支持仅接收未读消息
     * @return
     */
    public List<WSMessageDTO> findMessage(WSMessageRequest messageQO);
    
    /**
     * 消息持久化
     * @param request
     * @return
     */
    public WSMessageDTO persist(WSMessageRequest request);
    
    /**
     * 更新消息状态 
     * @param msgId
     * @param status
     * @return
     */
    public int updateMsgStatus(Long msgId,Integer status);
    
    /**
     * 更新消息为已读
     * @param msgIds
     * @param consumerId
     * @return
     */
    public int notifyRead(List<Long> msgIds,String consumerId);
}
