package org.aztec.framework.web.socket.entity;

import java.util.List;

import org.aztec.framework.api.rest.entity.Paginator;

public class WSMessageQO extends Paginator<WSMessage>{

    private String sourceId;
    private String source;
    private Integer msgType;
    private Integer pageNo;
    private Integer pageSize;
    private Integer status;
    private Integer oldStatus;
    private List<Long> ids;
    
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
    public String getSource() {
        return source;
    }
    public void setSource(String source) {
        this.source = source;
    }
    public Integer getPageNo() {
        return pageNo;
    }
    public void setPageNo(Integer pageNo) {
        this.pageNo = pageNo;
    }
    public Integer getPageSize() {
        return pageSize;
    }
    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
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
    public String getSourceId() {
        return sourceId;
    }
    public void setSourceId(String sourceId) {
        this.sourceId = sourceId;
    }
    public Integer getMsgType() {
        return msgType;
    }
    public void setMsgType(Integer msgType) {
        this.msgType = msgType;
    }
    
    
}
