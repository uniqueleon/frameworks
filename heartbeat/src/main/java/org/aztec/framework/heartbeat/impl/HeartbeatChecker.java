package org.aztec.framework.heartbeat.impl;

import java.text.DecimalFormat;
import java.util.Date;
import java.util.List;

import org.aztec.framework.disconf.items.HeartbeatConf;
import org.aztec.framework.heartbeat.ServerNodeStatus;
import org.aztec.framework.heartbeat.TaskInfoRecorder;
import org.aztec.framework.heartbeat.util.ComputerInfo;
import org.aztec.framework.heartbeat.util.SystemInfoListener;
import org.aztec.framework.heartbeat.util.SystemMonitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.dangdang.ddframe.job.util.env.IpUtils;
import com.google.common.collect.Lists;
import com.sjsc.framework.api.HeartbeatException;
import com.sjsc.framework.api.restful.entity.RestRequest;
import com.sjsc.framework.api.restful.entity.RestResult;
import com.sjsc.framework.api.restful.entity.hb.ServerNodeInfo;
import com.sjsc.framework.api.restful.feign.HeartbeatManager;
import com.sjsc.framework.api.restful.feign.HeartbeatMonitor;
import com.sjsc.framework.api.restful.util.DynamicFeignUtils;
import com.sjsc.framework.core.FrameworkLogger;
import com.sjsc.framework.core.utils.StoppableThread;


@Component
@Scope("singleton")
public class HeartbeatChecker extends StoppableThread implements SystemInfoListener {

    ServerNodeInfo lastInfo;
    ServerNodeInfo nextNode;
    @Autowired
    HeartbeatConf conf;
    @Autowired
    LoadBalancerClient lbClient;
    
    @Value("${server.port}")
    private String port;
    
    @Value("${spring.application.name}")
    private String appName;
    
    private static final String LOG_PREFIX = "[HEARTBEAT-CHECKER]:";
    
    private static final Logger LOG = LoggerFactory.getLogger(HeartbeatChecker.class);

    public HeartbeatChecker() {
        super("Heart-beat-checker");
        SystemMonitor.addListener(this);
    }

    @Override
    protected void doRun() throws InterruptedException {
        // TODO Auto-generated method stub
        LOG.debug(LOG_PREFIX + "Checking heartbeat....");
        setSleepInterval((conf != null && conf.getCheckInterval() != null) ? Long.parseLong(conf.getCheckInterval()) : 3000);
        if (nextNode == null) {
            HeartbeatManager manager = getHeartbeatManager();
            if(manager != null){
                RestResult<ServerNodeInfo> result = manager.regist(new RestRequest<>(getCurrentServerInfo()));
                if (result.isSuccess()) {
                    nextNode = result.getData();
                }
            }
        } else {
            try {
                HeartbeatMonitor monitor = DynamicFeignUtils.getFeignClient(HeartbeatMonitor.class,
                        "http://" + nextNode.getHost() + ":" + nextNode.getPort() + "/");
                RestResult result = monitor.touch();
                if (!result.isSuccess()) {
                    throw new HeartbeatException("The next server may be down!");
                }
            } catch (Throwable t) {

                HeartbeatManager manager = getHeartbeatManager();
                List<ServerNodeInfo> nodes = Lists.newArrayList();
                nodes.add(lastInfo);
                nodes.add(nextNode);
                RestResult<ServerNodeInfo> result = manager.reportError(new RestRequest<List<ServerNodeInfo>>(nodes));
                if (result.isSuccess()) {
                    nextNode = result.getData();
                }
            }
        }

        LOG.debug(LOG_PREFIX + "Check finished!");

    }
    
    private HeartbeatManager getHeartbeatManager(){

        HeartbeatManager manager = DynamicFeignUtils.getFeignClient(lbClient, HeartbeatManager.class,
                "lb://" + conf.getHeartbeatManagerName());
        return manager;
    }

    public ServerNodeInfo getCurrentServerInfo() throws InterruptedException {
        if (lastInfo == null) {

            ComputerInfo computerInfo = SystemMonitor.getComputerInfo();
            notify(computerInfo);
        }
        return lastInfo;
    }

    @Override
    public boolean trigger(ComputerInfo comInfo) {
        // TODO Auto-generated method stub
        return true;
    }
    
    public void reportStatus() {
        try {
            LOG.info(LOG_PREFIX + "Reporting status!");
            HeartbeatManager manager = getHeartbeatManager();
            notify(SystemMonitor.getComputerInfo());
            manager.report(new RestRequest<>(lastInfo));
        } catch (InterruptedException e) {
            FrameworkLogger.error(e);
        }
    }

    @Override
    public void notify(ComputerInfo comInfo) {

        LOG.debug(LOG_PREFIX + "Updating status....");
        lastInfo = new ServerNodeInfo();
        lastInfo.setCpu(comInfo.getCpuInfo().getUserUsage());
        lastInfo.setCreateTime(comInfo.getStartTime());
        lastInfo.setDisk(comInfo.getDiskInfo().getUsableSpace());
        lastInfo.setDiskRatio(comInfo.getDiskInfo().getUsedRate());
        lastInfo.setPort(Integer.parseInt(port));
        lastInfo.setHost(IpUtils.getIp());
        lastInfo.setMemory(comInfo.getMemoryInfo().getFreeMemorySize());
        double memRatio = Double.parseDouble(new DecimalFormat("#.##").format(
                comInfo.getMemoryInfo().getFreeMemorySize() * 1d / 
                comInfo.getMemoryInfo().getMaxMemorySize()));
        lastInfo.setMemoryRatio(memRatio);
        lastInfo.setPayload(TaskInfoRecorder.getPayLoad().intValue());
        lastInfo.setLatency(TaskInfoRecorder.getAverageLatency());
        lastInfo.setStartTime(comInfo.getStartTime());
        lastInfo.setStatus(ServerNodeStatus.UP.getDbcode());
        lastInfo.setAppName(appName);
        lastInfo.setUpdateTime(new Date());
        //lastInfo.setHost(comInfo.get);
        // lastInfo.setDisk(disk);

    }
}
