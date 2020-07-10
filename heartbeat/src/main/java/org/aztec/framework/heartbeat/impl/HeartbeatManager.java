package org.aztec.framework.heartbeat.impl;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.math.RandomUtils;
import org.apache.log4j.chainsaw.Main;
import org.aztec.framework.disconf.items.HeartbeatConf;
import org.aztec.framework.heartbeat.HeartbeatManageService;
import org.aztec.framework.heartbeat.HeartbeatManagerBeans;
import org.aztec.framework.heartbeat.entity.ServerNode;
import org.aztec.framework.heartbeat.mapper.ServerInfoMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.sjsc.framework.api.HeartbeatException;
import com.sjsc.framework.api.restful.feign.HeartbeatMonitor;
import com.sjsc.framework.api.restful.util.DynamicFeignUtils;

@HeartbeatManagerBeans
@Component
public class HeartbeatManager implements HeartbeatManageService {
    
    @Autowired
    private ServerInfoMapper<ServerNode> mapper;
    @Autowired
    private ServerEvaluator evaluator;
    @Autowired
    private HeartbeatConf heartbeatConf;
    
    private static Logger LOG = LoggerFactory.getLogger(HeartbeatManager.class);
    
    private static final ReentrantLock localLock = new ReentrantLock();

    @Override
    public LinkedList<ServerNode> getServerRing() {
        // TODO Auto-generated method stub

        List<ServerNode> originalNodes =  mapper.findAllEffectiveNodes();
        LinkedList<ServerNode> nodes;
        int tryCount = 3;
        try {
            nodes = buildRing(originalNodes);
        } catch (Exception e) {
            try{
                while(!acquireLock()){
                    Thread.sleep(10);
                    tryCount --;
                    if(tryCount < 0){
                        throw new HeartbeatException("Acquire lock timeout!");
                    }
                }
                nodes = rebuildRing();
            }
            catch (Throwable t){
                LOG.error(t.getMessage());
                throw new HeartbeatException(t);
            }
            finally{
                releaseLock();
            }
        }
        return nodes;
    }
    
    
    
    private List<ServerNode> doRebuild(List<ServerNode> nodes){
        for(int i = 0;i < nodes.size();i++){
            ServerNode node = nodes.get(i);
            if(i != nodes.size() - 1){
                node.setNext(nodes.get(i + 1).getId());
            }
            else{
                node.setNext(nodes.get(0).getId());
            }
        }
        return nodes;
    }
    
    @Transactional
    private LinkedList<ServerNode> rebuildRing(){
        List<ServerNode> nodes =  mapper.findAllEffectiveNodes();
        try {
            //双重检查,防止过度修正
            return buildRing(nodes);
        } catch (Exception e) {
            // 修正逻辑
            nodes =  mapper.findAllEffectiveNodes();
            List<ServerNode> newNodes = doRebuild(nodes);
            for(int i = 0;i < newNodes.size();i++){
                mapper.updateByPrimaryKey(newNodes.get(i));
            }
            return buildRing(newNodes);
        }
    }
    
    private boolean acquireLock(){
        if(!localLock.tryLock()){
            return false;
        }
        boolean lockResult = mapper.acquireLock() > 0;
        if(!lockResult){
            if(localLock.isHeldByCurrentThread()){
                localLock.unlock();
            }
        }
        return lockResult;
    }
    
    private void releaseLock(){
        if(localLock.isHeldByCurrentThread()){
            localLock.unlock();
        }
        if(localLock.getHoldCount() == 0){
            mapper.releaseLock();
        }
    }
    
    private LinkedList<ServerNode> buildRing(List<ServerNode> nodes){
        LinkedList<ServerNode> ring = new LinkedList<>();
        if(nodes.size() > 0){
            ring.add(nodes.get(0));
            nodes.remove(0);
            while(!nodes.isEmpty()){
                ServerNode lastNode = ring.get(ring.size() - 1);
                //int ringSize = ring.size();
                ServerNode findNode = null;
                for(int i = 0;i < nodes.size();i++){
                    ServerNode tmpNode = nodes.get(i);
                    if(tmpNode.getId().equals(lastNode.getNext())){
                        findNode = tmpNode;
                        break;
                    }
                    if(findNode == null){
                        throw new HeartbeatException("The heartbeat ring may be broken!");
                    }
                }
                ring.add(findNode);
                nodes.remove(findNode);
                
            }
            
        }
        return ring;
        
    }
    
    

    /* (non-Javadoc)
     * @see com.sjsc.framework.heartbeat.HeartbeatManageService#append(com.sjsc.framework.heartbeat.entity.ServerNode)
     */
    @Override
    public ServerNode append(ServerNode node)  {
        
        LinkedList<ServerNode> allNodes = getServerRing();
        if(!allNodes.contains(node)){
            if(allNodes.isEmpty()){
                node.setCreateTime(new Date());
                node.setUpdateTime(new Date());
                int count = mapper.insert(node);
                return count > 1 ? node : null;
            }
            else{
                ServerNode firstNode = allNodes.getFirst();
                ServerNode newNode = new ServerNode();
                try {
                    BeanUtils.copyProperties(newNode, node);
                    newNode.setCreateTime(new Date());
                    newNode.setUpdateTime(new Date());
                } catch (Exception e) {
                    throw new HeartbeatException("New node insert error!");
                }
                newNode.setNext(firstNode.getId());
                int count = mapper.insert(newNode);
                if(count > 0){

                    ServerNode lastNode = allNodes.getLast();
                    lastNode.setNext(newNode.getId());
                    count = mapper.updateByPrimaryKey(lastNode);
                    if(count > 0){
                        
                    }
                    else{
                        throw new HeartbeatException("New node insert error!");
                    }
                }
                else {
                    throw new HeartbeatException("New node insert error!");
                }
                return firstNode;
            }
        }
        else {
            int nodeIndex = allNodes.indexOf(node);
            return nodeIndex < (allNodes.size() - 1) ? allNodes.get(nodeIndex + 1) : allNodes.get(0);
        }
    }

    @Override
    public ServerNode getNextNode(ServerNode node) {
        LinkedList<ServerNode> nodes = getServerRing();
        int nodeIndex = nodes.indexOf(node);
        return nodeIndex != -1 ? (nodeIndex < nodes.size() - 1 ? nodes.get(nodeIndex + 1) : nodes.get(0)) : null;
    }

    @Override
    public ServerNode getPreviousNode(ServerNode node) {

        LinkedList<ServerNode> nodes = getServerRing();
        int nodeIndex = nodes.indexOf(node);
        return nodeIndex != -1 ? (nodeIndex > 0 ? nodes.get(nodeIndex - 1) : nodes.get(nodes.size() - 1)) : null;
    }

    @Override
    public ServerNode remove(List<ServerNode> node) {
        ServerNode reporter = node.get(0);
        ServerNode failuer = node.get(1);
        
        ServerNode targetNode = getNextNode(reporter);
        if(targetNode.equals(failuer)){
            String url = "http://" + targetNode.getHost() + ":" + targetNode.getPort();
            HeartbeatMonitor monitor = DynamicFeignUtils.getFeignClient(HeartbeatMonitor.class, url);
            try {
                monitor.touch();
                return targetNode;
            } catch (Throwable e) {
                if(Boolean.parseBoolean(heartbeatConf.getKickout())){
                    mapper.deleteByPrimaryKey(targetNode);
                }
                return getNextNode(reporter);
            }
        }
        
        
        return targetNode;
    }

    @Override
    public ServerNode pickBestServer(String appName) {
        List<ServerNode> nodes =  mapper.findAllEffectiveNodes();
        ServerNode retNode = null;
        double bestScore = 0d;
        for(ServerNode node : nodes){
            if(node.getAppName().equals(appName)){
                double score = evaluator.getScore(node);
                if(score > bestScore){
                    retNode = node;
                }
            }
        }
        return retNode;
    }

    @Override
    public ServerNode updateInfo(ServerNode node)  {
        ServerNode dbRecord = mapper.findServerByHost(node.getHost(), node.getPort());
        if(dbRecord != null){
            //防止被客户端无意覆盖
            node.setNext(node.getNext());
            node.setId(dbRecord.getId());
            try {
                BeanUtils.copyProperties(dbRecord, node);
                mapper.updateByPrimaryKey(dbRecord);
            } catch (Exception e) {
                throw new HeartbeatException(e.getMessage(),e);
            }
            return dbRecord;
        }
        else {
            throw new HeartbeatException("Can't find targetNode!");
        }
    }



    @Override
    public List<ServerNode> pickServers(String appName) {
        List<ServerNode> nodes = mapper.findServerByAppName(appName);
        List<Double> scores = Lists.newArrayList();
        nodes.sort((node1,node2) -> {
            double score1 = evaluator.getScore(node1);
            double score2 = evaluator.getScore(node2);
            if(!scores.contains(score1)){
                scores.add(score1);
            }
            if(!scores.contains(score2)){
                scores.add(score2);
            }
            return score2 - score1 == 0 ? 0 : (score2 - score1 > 0 ? 1 : -1 );
        });
        scores.sort((score1,score2) -> {
            return score2 - score1 == 0 ? 0 : (score2 - score1 > 0 ? 1 : -1 );
        });
        LOG.info("HB-Machine score:" + scores);
        return nodes;
    }

    
    public static void main(String[] args) {

        List<Double> scores = Lists.newArrayList();
        for(int i = 0;i < 10;i ++){
            scores.add(RandomUtils.nextDouble());
        }
        scores.sort((score1,score2) -> {
            return score2 - score1 == 0 ? 0 : (score2 - score1 > 0 ? 1 : -1 );
        });
        System.out.println(scores);
    }
}
