package org.aztec.framework.heartbeat.util;

public class CpuInfo {

    private Integer core;
    private Double sysUsage;
    private Double userUsage;
    private Double ioWait;
    private Double idle;
    private Double loadBetweenTicks;
    private Double load;
    public Integer getCore() {
        return core;
    }
    public void setCore(Integer core) {
        this.core = core;
    }
    public Double getSysUsage() {
        return sysUsage;
    }
    public void setSysUsage(Double sysUsage) {
        this.sysUsage = sysUsage;
    }
    public Double getUserUsage() {
        return userUsage;
    }
    public void setUserUsage(Double userUsage) {
        this.userUsage = userUsage;
    }
    public Double getIoWait() {
        return ioWait;
    }
    public void setIoWait(Double ioWait) {
        this.ioWait = ioWait;
    }
    public Double getIdle() {
        return idle;
    }
    public void setIdle(Double idle) {
        this.idle = idle;
    }
    public Double getLoadBetweenTicks() {
        return loadBetweenTicks;
    }
    public void setLoadBetweenTicks(Double loadBetweenTicks) {
        this.loadBetweenTicks = loadBetweenTicks;
    }
    public Double getLoad() {
        return load;
    }
    public void setLoad(Double load) {
        this.load = load;
    }
    
    
}
