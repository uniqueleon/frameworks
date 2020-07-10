package org.aztec.framework.heartbeat.util;

public class DiskInfo {

    private long totalSpace;
    private long usableSpace;
    private double usedRate;
    public long getTotalSpace() {
        return totalSpace;
    }
    public void setTotalSpace(long totalSpace) {
        this.totalSpace = totalSpace;
    }
    
    public long getUsableSpace() {
        return usableSpace;
    }
    public void setUsableSpace(long usableSpace) {
        this.usableSpace = usableSpace;
    }
    public double getUsedRate() {
        return usedRate;
    }
    public void setUsedRate(double usedRate) {
        this.usedRate = usedRate;
    }
    
    
}
