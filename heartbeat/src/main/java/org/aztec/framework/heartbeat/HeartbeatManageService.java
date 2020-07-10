package org.aztec.framework.heartbeat;

import java.util.LinkedList;
import java.util.List;

import org.aztec.framework.heartbeat.entity.ServerNode;

public interface HeartbeatManageService {

    public LinkedList<ServerNode> getServerRing();
    
    public ServerNode append(ServerNode node);
    
    public ServerNode updateInfo(ServerNode node);
    
    public ServerNode getNextNode(ServerNode node);
    
    public ServerNode getPreviousNode(ServerNode node);
    
    public ServerNode remove(List<ServerNode> node);
    
    public ServerNode pickBestServer(String appName);
    
    public List<ServerNode> pickServers(String appName);
    
}
