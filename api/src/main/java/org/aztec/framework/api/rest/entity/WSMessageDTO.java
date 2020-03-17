package org.aztec.framework.api.rest.entity;

import java.io.Serializable;
import java.util.List;

public class WSMessageDTO implements Serializable{


  /**
     * 
     */
    private static final long serialVersionUID = -763532800023040148L;
    //`id` bigint(20) primary key NOT NULL auto_increment,
      protected String id;
    //`msg_type` int comment '消息类型.1.广播消息,2.多发消息,3.单发消息',
      protected Integer msgType;

      private String sourceId;
      private String sourceInfo;//`source_id` varchar(100) comment NULL COMMENT '来源ID，发送消息的实体ID',
    //`content` text DEFAULT '' COMMENT '消息内容',
      protected String content;
    //`topic` varchar(200) DEFAULT ''  COMMENT '消息主题',
      protected String topic;
      //`title` varchar(400) DEFAULT ''  COMMENT '消息标题',
      protected String title;
  //`status` int DEFAULT '' COMMENT '0.未发送.1.已发送',
      protected Integer status;
      protected Integer readStatus;
      private List<WSMessageConsumerDTO>  consumers;
    public String getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = "" + id;
    }
    public void setId(String id) {
        this.id = "" + id;
    }
    public Integer getMsgType() {
        return msgType;
    }
    public void setMsgType(Integer msgType) {
        this.msgType = msgType;
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
    public Integer getStatus() {
        return status;
    }
    public void setStatus(Integer status) {
        this.status = status;
    }
    
    public List<WSMessageConsumerDTO> getConsumers() {
        return consumers;
    }
    public void setConsumers(List<WSMessageConsumerDTO> consumers) {
        this.consumers = consumers;
    }
    public String getSourceInfo() {
        return sourceInfo;
    }
    public void setSourceInfo(String sourceInfo) {
        this.sourceInfo = sourceInfo;
    }
    public String getSourceId() {
        return sourceId;
    }
    public void setSourceId(String sourceId) {
        this.sourceId = sourceId;
    }
    public Integer getReadStatus() {
        return readStatus;
    }
    public void setReadStatus(Integer readStatus) {
        this.readStatus = readStatus;
    }
    
    
}
