package org.aztec.framework.disconf.items;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.baidu.disconf.client.common.annotations.DisconfFile;
import com.baidu.disconf.client.common.annotations.DisconfFileItem;

/**
 * 
 * @author liming
 *
 */
@Service
@Scope("singleton")
@DisconfFile(filename = "sharding_index_conf.properties")
public class ShardIndexConf {

    private String maxDataSize;
    
    private String modular;
    
    private String affordableSize;
    
    private String checkInterval;
    
    private String checkNum;

    @DisconfFileItem(name="maxDataSize",associateField="maxDataSize")
    public String getMaxDataSize() {
        return maxDataSize;
    }

    public void setMaxDataSize(String maxDataSize) {
        this.maxDataSize = maxDataSize;
    }

    @DisconfFileItem(name="modular",associateField="modular")
    public String getModular() {
        return modular;
    }

    public void setModular(String modular) {
        this.modular = modular;
    }
    
    @DisconfFileItem(name="affordable",associateField="affordableSize")
    public String getAffordableSize() {
        return affordableSize;
    }

    public void setAffordableSize(String affordableSize) {
        this.affordableSize = affordableSize;
    }

    @DisconfFileItem(name="checkInterval",associateField="checkInterval")
    public String getCheckInterval() {
        return checkInterval;
    }

    public void setCheckInterval(String checkInterval) {
        this.checkInterval = checkInterval;
    }

    @DisconfFileItem(name="checkNum",associateField="checkNum")
    public String getCheckNum() {
        return checkNum;
    }

    public void setCheckNum(String checkNum) {
        this.checkNum = checkNum;
    }
    
    
}
