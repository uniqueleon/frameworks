package org.aztec.framework.heartbeat.util;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.sun.management.OperatingSystemMXBean;

import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;

/**
 * 系统监控
 */
public class SystemMonitor {
    
    private static final Logger LOG = LoggerFactory.getLogger(SystemMonitor.class);
    
    static {
        init();
    }
    
    private static List<SystemInfoListener> listeners = Lists.newArrayList();
    
    public static void addListener(SystemInfoListener listener){
        listeners.add(listener);
    }
    
    public static ComputerInfo computerInfo = new ComputerInfo();
    
    public static ComputerInfo getComputerInfo() throws InterruptedException{
        while(computerInfo == null){
            Thread.currentThread().sleep(1000);
        }
        return computerInfo;
    }

    public static void init() {
        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {
            try {

                SystemInfo systemInfo = new SystemInfo();

                OperatingSystemMXBean osmxb = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
                MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
                // 椎内存使用情况
                MemoryUsage memoryUsage = memoryMXBean.getHeapMemoryUsage();
                ComputerInfo newInfo = new ComputerInfo();
                MemoryInfo memoryInfo = new MemoryInfo();
                // 初始的总内存
                memoryInfo.setInitTotalMemorySize( memoryUsage.getInit());
                // 最大可用内存
                memoryInfo.setMaxMemorySize( memoryUsage.getMax());
                // 已使用的内存
                memoryInfo.setUsedMemory( memoryUsage.getUsed());
                // spring 应用名称
                newInfo.setApplicationName(System.getProperty("spring.application.name"));
                // 操作系统
                newInfo.setOsName(System.getProperty("os.name"));
                // 总的物理内存
                String totalMemorySize = new DecimalFormat("#.##")
                        .format(osmxb.getTotalPhysicalMemorySize() / 1024.0 / 1024 / 1024) + "G";
                // 剩余的物理内存
                String freePhysicalMemorySize = new DecimalFormat("#.##")
                        .format(osmxb.getFreePhysicalMemorySize() / 1024.0 / 1024 / 1024) + "G";
                // 已使用的物理内存
                String usedMemory = new DecimalFormat("#.##").format(
                        (osmxb.getTotalPhysicalMemorySize() - osmxb.getFreePhysicalMemorySize()) / 1024.0 / 1024 / 1024)
                        + "G";
                // 获得线程总数
                ThreadGroup parentThread;
                for (parentThread = Thread.currentThread().getThreadGroup(); parentThread
                        .getParent() != null; parentThread = parentThread.getParent()) {

                }

                newInfo.setThreadNum(parentThread.activeCount());
                DiskInfo dInfo = new DiskInfo();
                
                // 磁盘使用情况
                File[] files = File.listRoots();
                long totalSpace = 0l;
                long freeSpace = 0l;
                long usableSpace = 0l;
                for (File file : files) {
                    totalSpace += file.getTotalSpace();
                    freeSpace += file.getFreeSpace();
                    usableSpace += file.getUsableSpace();
                }
                dInfo.setTotalSpace(totalSpace);
                dInfo.setUsableSpace(usableSpace);
                dInfo.setUsedRate(Double.parseDouble(new DecimalFormat("#.##").format(usableSpace * 1d /totalSpace)));
                newInfo.setDiskInfo(dInfo);
                newInfo.setStartTime(new Date(ManagementFactory.getRuntimeMXBean().getStartTime()));
                String pid = System.getProperty("PID");
                if(pid != null){
                    newInfo.setPid(Long.parseLong(System.getProperty("PID")));
                }
                CpuInfo cpuInfo = getCpuInfo(systemInfo);
                newInfo.setJavaHome(System.getProperty("java.home"));
                newInfo.setJavaVersion(System.getProperty("java.version"));
                newInfo.setUserHome(System.getProperty("user.home"));
                newInfo.setUserName(System.getProperty("user.name"));
                memoryInfo.setMaxSysMemorySize(systemInfo.getHardware().getMemory().getTotal());
                memoryInfo.setFreeMemorySize(systemInfo.getHardware().getMemory().getAvailable());
                memoryInfo.setFreePhysicalMemorySize((systemInfo.getHardware().getMemory().getTotal()
                                - systemInfo.getHardware().getMemory().getAvailable()));
                newInfo.setCpuInfo(cpuInfo);
                newInfo.setMemoryInfo(memoryInfo);
                computerInfo = newInfo;
                for(SystemInfoListener listener : listeners){
                    if(listener.trigger(computerInfo)){
                        listener.notify(computerInfo);
                    }
                }
            } catch (Throwable e) {
                LOG.error(e.getMessage(),e);
                e.printStackTrace();
            }
        }, 0, 10, TimeUnit.SECONDS);
    }
    

    /**
     * 打印 CPU 信息
     *
     * @param systemInfo
     * @throws InterruptedException 
     */
    public static CpuInfo getCpuInfo(SystemInfo systemInfo) throws InterruptedException,Exception {
        
        CentralProcessor processor = systemInfo.getHardware().getProcessor();
        long[] prevTicks = processor.getSystemCpuLoadTicks();
        // 睡眠1s
        TimeUnit.SECONDS.sleep(1);
        long[] ticks = processor.getSystemCpuLoadTicks();
        long nice = ticks[CentralProcessor.TickType.NICE.getIndex()]
                - prevTicks[CentralProcessor.TickType.NICE.getIndex()];
        long irq = ticks[CentralProcessor.TickType.IRQ.getIndex()]
                - prevTicks[CentralProcessor.TickType.IRQ.getIndex()];
        long softirq = ticks[CentralProcessor.TickType.SOFTIRQ.getIndex()]
                - prevTicks[CentralProcessor.TickType.SOFTIRQ.getIndex()];
        long steal = ticks[CentralProcessor.TickType.STEAL.getIndex()]
                - prevTicks[CentralProcessor.TickType.STEAL.getIndex()];
        long cSys = ticks[CentralProcessor.TickType.SYSTEM.getIndex()]
                - prevTicks[CentralProcessor.TickType.SYSTEM.getIndex()];
        long user = ticks[CentralProcessor.TickType.USER.getIndex()]
                - prevTicks[CentralProcessor.TickType.USER.getIndex()];
        long iowait = ticks[CentralProcessor.TickType.IOWAIT.getIndex()]
                - prevTicks[CentralProcessor.TickType.IOWAIT.getIndex()];
        long idle = ticks[CentralProcessor.TickType.IDLE.getIndex()]
                - prevTicks[CentralProcessor.TickType.IDLE.getIndex()];
        long totalCpu = user + nice + cSys + idle + iowait + irq + softirq + steal;
        CpuInfo cpuInfo = new CpuInfo();
        cpuInfo.setCore(processor.getLogicalProcessorCount());
        cpuInfo.setSysUsage(Double.parseDouble(new DecimalFormat("#.##").format(cSys * 1.0 / totalCpu)));
        cpuInfo.setUserUsage(Double.parseDouble(new DecimalFormat("#.##").format(user * 1.0 / totalCpu)));
        cpuInfo.setIoWait(Double.parseDouble(new DecimalFormat("#.##").format(iowait * 1.0 / totalCpu)));
        cpuInfo.setIdle(Double.parseDouble(new DecimalFormat("#.##").format(idle * 1.0 / totalCpu)));
        cpuInfo.setLoadBetweenTicks(processor.getSystemCpuLoadBetweenTicks() * 100);
        cpuInfo.setLoad(processor.getSystemCpuLoad() * 100);
        return cpuInfo;
    }
    
    public static void main(String[] args) {
        
        while(true){
            try {
                System.out.println(JSON.toJSON(getComputerInfo()));
                Thread.currentThread().sleep(3000);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
}