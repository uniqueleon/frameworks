package org.aztec.framework.mybatis.index;

import java.util.Date;

import javax.persistence.Id;
import javax.persistence.Transient;

public class BasicIndex {

    protected byte[] indexData;
    @Id
    protected Long id;
    protected Long remainder;
    protected Long dataSize;
    @Transient
    protected Long newSize;
    protected Long maxDataSize;
    protected Date createTime;
    protected Date updateTime;
    public byte[] getIndexData() {
        return indexData;
    }
    public void setIndexData(byte[] indexData) {
        this.indexData = indexData;
    }
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public Long getRemainder() {
        return remainder;
    }
    public void setRemainder(Long remainder) {
        this.remainder = remainder;
    }
    public Long getDataSize() {
        return dataSize;
    }
    public void setDataSize(Long dataSize) {
        this.dataSize = dataSize;
    }
    public Long getMaxDataSize() {
        return maxDataSize;
    }
    public void setMaxDataSize(Long maxDataSize) {
        this.maxDataSize = maxDataSize;
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
    public Long getNewSize() {
        return newSize;
    }
    public void setNewSize(Long newSize) {
        this.newSize = newSize;
    }
    
    
}
