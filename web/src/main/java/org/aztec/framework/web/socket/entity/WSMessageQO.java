package org.aztec.framework.web.socket.entity;

import java.util.Date;
import java.util.List;

import com.sjsc.framework.api.restful.entity.Paginator;

public class WSMessageQO extends Paginator<WSMessage>{

    private String sourceId;
    private String source;
    private Integer msgType;
    private Integer pageNo;
    private Integer pageSize;
    private Integer status;
    private Integer oldStatus;
    private String topic;
    private String title;
    private String content;
    private Long priority;
    private List<Long> ids;
    private Date checkPoint;
    
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
    public Long getPriority() {
        return priority;
    }
    public void setPriority(Long priority) {
        this.priority = priority;
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
    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }
    public Date getCheckPoint() {
        return checkPoint;
    }
    public void setCheckPoint(Date checkPoint) {
        this.checkPoint = checkPoint;
    }
    
}
