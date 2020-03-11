package org.aztec.framework.web.socket.entity;

import java.util.Date;

import javax.persistence.Table;

@Table(name="ws_msg_consumer")
public class WSMessageConsumer {
    private Long id;//`id` bigint(20) primary key NOT NULL auto_increment,
    private Long msgId;//`msg_id` bigint NOT NULL COMMENT '消息表关联id',
    private Integer status;//`status` int DEFAULT '' COMMENT '0.未送达,1.未读,2.已读',
    private String receiverId;//`receiver_id` bigint NOT NULL COMMENT '接收方id',
    private String receiverInfo;//`receiver_id` bigint NOT NULL COMMENT '接收方id',
    private Date createTime;//`create_time` datetime NOT NULL COMMENT '创建时间',
    private Date updateTime;//`update_time` datetime NOT NULL COMMENT '更新时间',
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public Long getMsgId() {
        return msgId;
    }
    public void setMsgId(Long msgId) {
        this.msgId = msgId;
    }
    
    public String getReceiverInfo() {
        return receiverInfo;
    }
    public void setReceiverInfo(String receiverInfo) {
        this.receiverInfo = receiverInfo;
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
    public String getReceiverId() {
        return receiverId;
    }
    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }
    
    
    
}
