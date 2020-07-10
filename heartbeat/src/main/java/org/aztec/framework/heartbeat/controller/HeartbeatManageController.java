package org.aztec.framework.heartbeat.controller;

import java.util.List;

import org.apache.commons.beanutils.BeanUtils;
import org.aztec.framework.heartbeat.HeartbeatManageService;
import org.aztec.framework.heartbeat.HeartbeatManagerBeans;
import org.aztec.framework.heartbeat.entity.ServerNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.collect.Lists;
import com.sjsc.framework.api.HeartbeatException;
import com.sjsc.framework.api.restful.entity.RestRequest;
import com.sjsc.framework.api.restful.entity.RestResult;
import com.sjsc.framework.api.restful.entity.hb.ServerNodeInfo;
import com.sjsc.framework.api.restful.feign.HeartbeatManager;

@HeartbeatManagerBeans
@RestController
@RequestMapping("/heartbeat")
public class HeartbeatManageController implements HeartbeatManager {
    
    @Autowired
    HeartbeatManageService service;
    
    @RequestMapping(path = "/regist",  method = { RequestMethod.POST }, consumes = {
            MediaType.APPLICATION_JSON_UTF8_VALUE }, produces = { MediaType.APPLICATION_JSON_UTF8_VALUE })
    public RestResult<ServerNodeInfo> regist(@RequestBody RestRequest<ServerNodeInfo> serverInfo) {
        try {
            ServerNode newNode = new ServerNode();
            BeanUtils.copyProperties(newNode, serverInfo.getParam());
            ServerNodeInfo nodeInfo = new ServerNodeInfo();
            ServerNode nextNode = service.append(newNode);
            BeanUtils.copyProperties(nodeInfo, nextNode);
            return new RestResult<>(true, "ok", "ok",nodeInfo);
        } catch (Exception e) {
            throw new HeartbeatException(e.getMessage(),e);
        }
    }

    @RequestMapping(path = "/report", method = { RequestMethod.POST }, consumes = {
            MediaType.APPLICATION_JSON_UTF8_VALUE }, produces = { MediaType.APPLICATION_JSON_UTF8_VALUE })
    public RestResult<ServerNodeInfo> report(@RequestBody RestRequest<ServerNodeInfo> serverInfo) {

        try {
            ServerNode newNode = new ServerNode();
            BeanUtils.copyProperties(newNode, serverInfo.getParam());
            ServerNode nextNode = service.updateInfo(newNode);
            ServerNodeInfo retNode = new ServerNodeInfo();
            BeanUtils.copyProperties(retNode, nextNode);
            return new RestResult<ServerNodeInfo>(true, "ok", "ok",retNode);
        } catch (Exception e) {
            throw new HeartbeatException(e.getMessage(),e);
        }
    }
    
    @RequestMapping(path = "/reportError", method = { RequestMethod.POST }, consumes = {
            MediaType.APPLICATION_JSON_UTF8_VALUE }, produces = { MediaType.APPLICATION_JSON_UTF8_VALUE })
    public RestResult<ServerNodeInfo> reportError(@RequestBody RestRequest<List<ServerNodeInfo>> failServer) {

        try {
            ServerNode newNode = new ServerNode();
            BeanUtils.copyProperties(newNode, failServer.getParam().get(0));
            ServerNode failNode = new ServerNode();
            BeanUtils.copyProperties(failNode, failServer.getParam().get(1));
            List<ServerNode> nodeList = Lists.newArrayList(new ServerNode[]{newNode,failNode});
            ServerNode nextNode = service.remove(nodeList);
            ServerNodeInfo retNode = new ServerNodeInfo();
            BeanUtils.copyProperties(retNode, nextNode);
            return new RestResult<ServerNodeInfo>(true, "ok", "ok",retNode);
        } catch (Exception e) {
            throw new HeartbeatException(e.getMessage(),e);
        }
    }

    
}
