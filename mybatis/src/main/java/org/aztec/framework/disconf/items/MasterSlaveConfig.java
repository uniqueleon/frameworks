package org.aztec.framework.disconf.items;

import java.util.List;

import org.aztec.framework.core.utils.JsonUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.baidu.disconf.client.common.annotations.DisconfFile;
import com.baidu.disconf.client.common.annotations.DisconfFileItem;

/**
 * 数据库主从配置
 * @author liming
 *
 */
@Service
@Scope("singleton")
@DisconfFile(filename = "sharding_jdbc_master_slaves.properties")
public class MasterSlaveConfig {

    private String content;

    @DisconfFileItem(name="content",associateField="content")
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
    
    public List<MasterSlaveInfo> getMasterSlaveInfos(){
        if(content != null && content.isEmpty()){
            return JsonUtils.getList(content, MasterSlaveInfo.class);
        }
        return null;
    }
    
    public class MasterSlaveInfo{
        private String name;
        private List<String> masters;
        private List<String> slaves;
        public String getName() {
            return name;
        }
        public void setName(String name) {
            this.name = name;
        }
        public List<String> getMasters() {
            return masters;
        }
        public void setMasters(List<String> masters) {
            this.masters = masters;
        }
        public List<String> getSlaves() {
            return slaves;
        }
        public void setSlaves(List<String> slaves) {
            this.slaves = slaves;
        }
        
        
    }
}
