package org.aztec.framework.heartbeat.util;

public class MemoryInfo {

    private Long initTotalMemorySize;
    private long maxSysMemorySize;
    private long usedSysMemorySize;
    private long freeSysMemorySize;
    private long maxMemorySize ;
    private long usedMemorySize;
    private long totalMemorySize;
    private long freeMemorySize;
    private long freePhysicalMemorySize;
    private long usedMemory;
    public Long getInitTotalMemorySize() {
        return initTotalMemorySize;
    }
    public void setInitTotalMemorySize(Long initTotalMemorySize) {
        this.initTotalMemorySize = initTotalMemorySize;
    }
    public long getMaxMemorySize() {
        return maxMemorySize;
    }
    public void setMaxMemorySize(long maxMemorySize) {
        this.maxMemorySize = maxMemorySize;
    }
    public long getUsedMemorySize() {
        return usedMemorySize;
    }
    public void setUsedMemorySize(long usedMemorySize) {
        this.usedMemorySize = usedMemorySize;
    }
    public long getTotalMemorySize() {
        return totalMemorySize;
    }
    public void setTotalMemorySize(long totalMemorySize) {
        this.totalMemorySize = totalMemorySize;
    }
    public long getFreePhysicalMemorySize() {
        return freePhysicalMemorySize;
    }
    public void setFreePhysicalMemorySize(long freePhysicalMemorySize) {
        this.freePhysicalMemorySize = freePhysicalMemorySize;
    }
    public long getUsedMemory() {
        return usedMemory;
    }
    public void setUsedMemory(long usedMemory) {
        this.usedMemory = usedMemory;
    }
    public long getMaxSysMemorySize() {
        return maxSysMemorySize;
    }
    public void setMaxSysMemorySize(long maxSysMemorySize) {
        this.maxSysMemorySize = maxSysMemorySize;
    }
    public long getUsedSysMemorySize() {
        return usedSysMemorySize;
    }
    public void setUsedSysMemorySize(long usedSysMemorySize) {
        this.usedSysMemorySize = usedSysMemorySize;
    }
    public long getFreeSysMemorySize() {
        return freeSysMemorySize;
    }
    public void setFreeSysMemorySize(long freeSysMemorySize) {
        this.freeSysMemorySize = freeSysMemorySize;
    }
    public long getFreeMemorySize() {
        return freeMemorySize;
    }
    public void setFreeMemorySize(long freeMemorySize) {
        this.freeMemorySize = freeMemorySize;
    }
    
    
}
