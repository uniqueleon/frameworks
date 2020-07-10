package org.aztec.framework.heartbeat.controller;

import org.aztec.framework.heartbeat.impl.HeartbeatChecker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sjsc.framework.api.HeartbeatException;
import com.sjsc.framework.api.restful.entity.RestResult;
import com.sjsc.framework.api.restful.entity.hb.ServerNodeInfo;
import com.sjsc.framework.api.restful.feign.HeartbeatMonitor;

@RestController
public class HeartbeatMonitorController implements HeartbeatMonitor{
    
    @Autowired
    private HeartbeatChecker checker;
    
    @RequestMapping("/heartbeat/touch")
    public RestResult<ServerNodeInfo> touch() {
        try {
            return new RestResult<ServerNodeInfo>(true, "OK", "OK",checker.getCurrentServerInfo());
        } catch (InterruptedException e) {
            throw new HeartbeatException(e.getMessage());
        }
    }

}
