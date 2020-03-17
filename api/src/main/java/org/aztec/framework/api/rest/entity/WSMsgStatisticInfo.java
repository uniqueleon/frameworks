package org.aztec.framework.api.rest.entity;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class WSMsgStatisticInfo {

    //未读总计
    private Long unreadCount;
    //总计收到消息数
    private Long totalCount;
    //主题总计
    private Long topicCount;
    //未读主题总计
    private Long unreadTopicCount;
    //私信统计
    private Long secretCount;
    
    private Long unreadSecretCount;
    //用户信息
    private String userInfo;
    
    private List<WSMessageDTO> unreadMessages;
    public Long getUnreadCount() {
        return unreadCount;
    }
    public void setUnreadCount(Long unreadCount) {
        this.unreadCount = unreadCount;
    }
    public Long getTotalCount() {
        return totalCount;
    }
    public void setTotalCount(Long totalCount) {
        this.totalCount = totalCount;
    }
    public Long getTopicCount() {
        return topicCount;
    }
    public void setTopicCount(Long topicCount) {
        this.topicCount = topicCount;
    }
    public String getUserInfo() {
        return userInfo;
    }
    public void setUserInfo(String userInfo) {
        this.userInfo = userInfo;
    }
    public Long getSecretCount() {
        return secretCount;
    }
    public void setSecretCount(Long secretCount) {
        this.secretCount = secretCount;
    }
    
    public List<WSMessageDTO> getUnreadMessages() {
        return unreadMessages;
    }
    public void setUnreadMessages(List<WSMessageDTO> unreadMessages) {
        this.unreadMessages = unreadMessages;
    }
    public Long getUnreadTopicCount() {
        return unreadTopicCount;
    }
    public void setUnreadTopicCount(Long unreadTopicCount) {
        this.unreadTopicCount = unreadTopicCount;
    }
    public Long getUnreadSecretCount() {
        return unreadSecretCount;
    }
    public void setUnreadSecretCount(Long unreadSecretCount) {
        this.unreadSecretCount = unreadSecretCount;
    }
    public WSMsgStatisticInfo(Long unreadCount, Long totalCount, Long topicCount, Long unreadTopicCount,
            Long secretCount, Long unreadSecretCount, String userInfo, List<WSMessageDTO> unreadMessages) {
        super();
        this.unreadCount = unreadCount;
        this.totalCount = totalCount;
        this.topicCount = topicCount;
        this.unreadTopicCount = unreadTopicCount;
        this.secretCount = secretCount;
        this.unreadSecretCount = unreadSecretCount;
        this.userInfo = userInfo;
        this.unreadMessages = unreadMessages;
    }
    
    
}
