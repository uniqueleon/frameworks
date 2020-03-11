package org.aztec.framework.web.socket.entity;

import java.util.Date;

import javax.persistence.Id;
import javax.persistence.Table;

@Table(name="ws_message")
public class WSMessage {

//`id` bigint(20) primary key NOT NULL auto_increment,
    @Id
    protected Long id;
  //`msg_type` int comment '消息类型.1.广播消息,2.多发消息,3.单发消息',
    protected Integer msgType;
    private String sourceId;//`source_id` varchar(20) comment NULL COMMENT '来源ID，发送消息的实体ID',
    private String sourceInfo;//`source_info` text comment NULL COMMENT '消息来源，json格式字串',
  //`content` text DEFAULT '' COMMENT '消息内容',
    protected String content;
  //`topic` varchar(200) DEFAULT ''  COMMENT '消息主题',
    protected String topic;
    //`title` varchar(400) DEFAULT ''  COMMENT '消息标题',
    protected String title;
    protected Integer status;//`status` int DEFAULT '' COMMENT '0.未发送.1.已发送,2.已取消',
//`create_time` datetime NOT NULL COMMENT '创建时间',
    protected Date createTime;
//`update_time` datetime NOT NULL COMMENT '更新时间',
    protected Date updateTime;
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
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
    public Date getCreateTime() {
        return createTime;
    }
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
    public Date getUpdateTime() {
        return updateTime;
    }
    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
    public WSMessage() {
        super();
    }
    public WSMessage(Long id, Integer msgType, String content, String topic, String title, Integer status,
            Date createTime, Date updateTime) {
        super();
        this.id = id;
        this.msgType = msgType;
        this.content = content;
        this.topic = topic;
        this.title = title;
        this.status = status;
        this.createTime = createTime;
        this.updateTime = updateTime;
    }
    public String getSourceId() {
        return sourceId;
    }
    public void setSourceId(String sourceId) {
        this.sourceId = sourceId;
    }
    public String getSourceInfo() {
        return sourceInfo;
    }
    public void setSourceInfo(String sourceInfo) {
        this.sourceInfo = sourceInfo;
    }
    
    
}
