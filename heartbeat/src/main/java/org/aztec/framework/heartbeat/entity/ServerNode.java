package org.aztec.framework.heartbeat.entity;

import java.util.Date;

import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
@Table(name="hb_server_node")
public class ServerNode {

    @Id
    private Long id;
    private String appName;
    private String host;
    private Integer payload;
    private Double cpu;
    private Date startTime;
    private Long memory;
    private Double memoryRatio;
    private Long disk;
    private Double diskRatio;
    private Long latency;
    private Integer port;
    private Integer status;
    private Long next;
    private Date createTime;
    private Date updateTime;
    private Integer ringLock;
    
    public String getHost() {
        return host;
    }
    public void setHost(String host) {
        this.host = host;
    }
    public Integer getPort() {
        return port;
    }
    public void setPort(int port) {
        this.port = port;
    }
    public Long getLatency() {
        return latency;
    }
    public void setLatency(Long latency) {
        this.latency = latency;
    }
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public Integer getPayload() {
        return payload;
    }
    public void setPayload(int payload) {
        this.payload = payload;
    }
    public Double getCpu() {
        return cpu;
    }
    public void setCpu(double cpu) {
        this.cpu = cpu;
    }
    public Long getMemory() {
        return memory;
    }
    public void setMemory(long memory) {
        this.memory = memory;
    }
    public Long getDisk() {
        return disk;
    }
    public void setDisk(long disk) {
        this.disk = disk;
    }
    public Long getNext() {
        return next;
    }
    public void setNext(Long next) {
        this.next = next;
    }
    public void setPort(Integer port) {
        this.port = port;
    }
    public Double getMemoryRatio() {
        return memoryRatio;
    }
    public void setMemoryRatio(double memoryRatio) {
        this.memoryRatio = memoryRatio;
    }
    public Double getDiskRatio() {
        return diskRatio;
    }
    public void setDiskRatio(double diskRatio) {
        this.diskRatio = diskRatio;
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
    public void setPayload(Integer payload) {
        this.payload = payload;
    }
    public void setCpu(Double cpu) {
        this.cpu = cpu;
    }
    public void setMemory(Long memory) {
        this.memory = memory;
    }
    public void setMemoryRatio(Double memoryRatio) {
        this.memoryRatio = memoryRatio;
    }
    public void setDisk(Long disk) {
        this.disk = disk;
    }
    public void setDiskRatio(Double diskRatio) {
        this.diskRatio = diskRatio;
    }
    
    public String getAppName() {
        return appName;
    }
    public void setAppName(String appName) {
        this.appName = appName;
    }
    public Date getStartTime() {
        return startTime;
    }
    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }
    
    
    public Integer getRingLock() {
        return ringLock;
    }
    public void setRingLock(Integer ringLock) {
        this.ringLock = ringLock;
    }
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((host == null) ? 0 : host.hashCode());
        result = prime * result + ((port == null) ? 0 : port.hashCode());
        return result;
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ServerNode other = (ServerNode) obj;
        if (host == null) {
            if (other.host != null)
                return false;
        } else if (!host.equals(other.host))
            return false;
        if (port == null) {
            if (other.port != null)
                return false;
        } else if (!port.equals(other.port))
            return false;
        return true;
    }
    
}
