package org.aztec.framework.api.rest.entity;

import java.io.Serializable;

public class WSMessageConsumerDTO implements Serializable{

    /**
     * 
     */
    private static final long serialVersionUID = -7809157929142721723L;
    private Long id;//`id` bigint(20) primary key NOT NULL auto_increment,
    private Integer status;//`status` int DEFAULT '' COMMENT '0.未送达,1.未读,2.已读',
    private String receiverId;
    private String receiverInfo;//`receiver_id` bigint NOT NULL COMMENT '接收方id',
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public Integer getStatus() {
        return status;
    }
    public void setStatus(Integer status) {
        this.status = status;
    }
    public String getReceiverInfo() {
        return receiverInfo;
    }
    public void setReceiverInfo(String receiverInfo) {
        this.receiverInfo = receiverInfo;
    }
    public String getReceiverId() {
        return receiverId;
    }
    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }
    
}
