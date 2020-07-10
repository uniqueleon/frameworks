package org.aztec.framework.heartbeat.util;

import java.util.Date;

public class ComputerInfo {

    private String osName;
    private String applicationName;
    private int threadNum;
    private Date startTime;
    private long pid;
    private String javaHome;
    private String javaVersion;
    private String userHome;
    private String userName;
    
    private CpuInfo cpuInfo;
    private MemoryInfo memoryInfo;
    private DiskInfo diskInfo;
    public String getOsName() {
        return osName;
    }
    public void setOsName(String osName) {
        this.osName = osName;
    }
    public CpuInfo getCpuInfo() {
        return cpuInfo;
    }
    public void setCpuInfo(CpuInfo cpuInfo) {
        this.cpuInfo = cpuInfo;
    }
    public MemoryInfo getMemoryInfo() {
        return memoryInfo;
    }
    public void setMemoryInfo(MemoryInfo memoryInfo) {
        this.memoryInfo = memoryInfo;
    }
    public DiskInfo getDiskInfo() {
        return diskInfo;
    }
    public void setDiskInfo(DiskInfo diskInfo) {
        this.diskInfo = diskInfo;
    }
    public int getThreadNum() {
        return threadNum;
    }
    public void setThreadNum(int threadNum) {
        this.threadNum = threadNum;
    }
    public Date getStartTime() {
        return startTime;
    }
    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }
    public long getPid() {
        return pid;
    }
    public void setPid(long pid) {
        this.pid = pid;
    }
    public String getJavaHome() {
        return javaHome;
    }
    public void setJavaHome(String javaHome) {
        this.javaHome = javaHome;
    }
    public String getJavaVersion() {
        return javaVersion;
    }
    public void setJavaVersion(String javaVersion) {
        this.javaVersion = javaVersion;
    }
    public String getUserHome() {
        return userHome;
    }
    public void setUserHome(String userHome) {
        this.userHome = userHome;
    }
    public String getUserName() {
        return userName;
    }
    public void setUserName(String userName) {
        this.userName = userName;
    }
    public String getApplicationName() {
        return applicationName;
    }
    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }
    
    
}
