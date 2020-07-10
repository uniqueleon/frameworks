package org.aztec.framework.async.task.entity;

public class TaskServerInfo {

    private String host;
    
    private int port;
    
    private int payload;
    
    private double cpu;
    
    private long memory;
    
    private long disk;
    
    private long speed;
    

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPayload() {
        return payload;
    }

    public void setPayload(int payload) {
        this.payload = payload;
    }

    public double getCpu() {
        return cpu;
    }

    public void setCpu(double cpu) {
        this.cpu = cpu;
    }

    public long getMemory() {
        return memory;
    }

    public void setMemory(long memory) {
        this.memory = memory;
    }

    public long getDisk() {
        return disk;
    }

    public void setDisk(long disk) {
        this.disk = disk;
    }

    public long getSpeed() {
        return speed;
    }

    public void setSpeed(long speed) {
        this.speed = speed;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
    
}
