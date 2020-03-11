package org.aztec.framework.web.socket.entity;

import java.util.List;

import javax.persistence.Id;

import org.aztec.framework.api.rest.entity.Paginator;

public class WSConsumerQO extends Paginator{

    private String receiverId;
    private Integer status;
    private Integer oldStatus;
    private List<Long> ids;
    private List<Long> msgIds;
    private List<String> consumers;
    @Id
    private Long id;
    
    
    public List<Long> getIds() {
        return ids;
    }
    public void setIds(List<Long> ids) {
        this.ids = ids;
    }
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getReceiverId() {
        return receiverId;
    }
    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }
    public List<String> getConsumers() {
        return consumers;
    }
    public void setConsumers(List<String> consumers) {
        this.consumers = consumers;
    }
    public Integer getStatus() {
        return status;
    }
    public void setStatus(Integer status) {
        this.status = status;
    }
    public Integer getOldStatus() {
        return oldStatus;
    }
    public void setOldStatus(Integer oldStatus) {
        this.oldStatus = oldStatus;
    }
    public List<Long> getMsgIds() {
        return msgIds;
    }
    public void setMsgIds(List<Long> msgIds) {
        this.msgIds = msgIds;
    }
    
}
