package org.aztec.framework.disconf.items;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.baidu.disconf.client.common.annotations.DisconfFile;
import com.baidu.disconf.client.common.annotations.DisconfFileItem;

@Service
@Scope("singleton")
@DisconfFile(filename = "hb_conf.properties")
public class HeartbeatConf {

    private String checkInterval;
    
    private String tolerantTime;
    
    private String heartbeatManagerName;
    
    private String kickout;

    @DisconfFileItem(name="checkInterval",associateField="checkInterval")
    public String getCheckInterval() {
        return checkInterval;
    }

    public void setCheckInterval(String checkInterval) {
        this.checkInterval = checkInterval;
    }

    @DisconfFileItem(name="tolerantTime",associateField="tolerantTime")
    public String getTolerantTime() {
        return tolerantTime;
    }

    public void setTolerantTime(String tolerantTime) {
        this.tolerantTime = tolerantTime;
    }

    @DisconfFileItem(name="heartbeatManagerName",associateField="heartbeatManagerName")
    public String getHeartbeatManagerName() {
        return heartbeatManagerName;
    }

    public void setHeartbeatManagerName(String heartbeatManagerName) {
        this.heartbeatManagerName = heartbeatManagerName;
    }

    @DisconfFileItem(name="kickout",associateField="kickout")
    public String getKickout() {
        return kickout;
    }

    public void setKickout(String kickout) {
        this.kickout = kickout;
    }
    
    
}
