package org.aztec.framework.web.socket.entity;

import java.util.List;
import java.util.Map;

public class WSMessageRequest {
    private String sourceId;//`source_id` varchar(20) comment NULL COMMENT '来源ID，发送消息的实体ID',
  //`content` text DEFAULT '' COMMENT '消息内容',
    private Integer msgType;
    private Object sourceObj;//`source_id` varchar(100) comment NULL COMMENT '来源ID，发送消息的实体ID',
  //`content` text DEFAULT '' COMMENT '消息内容',
    private String consumerId;
    private List<String> consumerIds;
    private String consumerInfo;
    private String content;
  //`topic` varchar(200) DEFAULT ''  COMMENT '消息主题',
    private String topic;
    //`title` varchar(400) DEFAULT ''  COMMENT '消息标题',
    private String title;
    
    private List<Long> hasReadMsgIds;
    
    private Boolean unreadOnly;
    
    public Integer getMsgType() {
        return msgType;
    }
    public void setMsgType(Integer msgType) {
        this.msgType = msgType;
    }
    
    public Object getSourceObj() {
        return sourceObj;
    }
    public void setSourceObj(Object sourceObj) {
        this.sourceObj = sourceObj;
    }
    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }
    public String getTopic() {
        return topic;
    }
    public void setTopic(String topic) {
        this.topic = topic;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public List<Long> getHasReadMsgIds() {
        return hasReadMsgIds;
    }
    public void setHasReadMsgIds(List<Long> hasReadMsgIds) {
        this.hasReadMsgIds = hasReadMsgIds;
    }
    
    public String getConsumerInfo() {
        return consumerInfo;
    }
    public void setConsumerInfo(String consumerInfo) {
        this.consumerInfo = consumerInfo;
    }
    public Boolean getUnreadOnly() {
        return unreadOnly;
    }
    public void setUnreadOnly(Boolean unreadOnly) {
        this.unreadOnly = unreadOnly;
    }
    public String getConsumerId() {
        return consumerId;
    }
    public void setConsumerId(String consumerId) {
        this.consumerId = consumerId;
    }
    public List<String> getConsumerIds() {
        return consumerIds;
    }
    public void setConsumerIds(List<String> consumerIds) {
        this.consumerIds = consumerIds;
    }
    public String getSourceId() {
        return sourceId;
    }
    public void setSourceId(String sourceId) {
        this.sourceId = sourceId;
    }
    
}
