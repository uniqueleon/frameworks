package org.aztec.framework.disconf.items;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.baidu.disconf.client.common.annotations.DisconfFile;
import com.baidu.disconf.client.common.annotations.DisconfFileItem;

@Service
@Scope("singleton")
@DisconfFile(filename = "sharding_jdbc_global_conf.properties")
public class GlobalConf {

    
    private String dataSources;
    private String tables;

    @DisconfFileItem(name = "datasources",associateField = "dataSources")
    public String getDataSources() {
        return dataSources;
    }
    public void setDataSources(String dataSources) {
        this.dataSources = dataSources;
    }

    @DisconfFileItem(name = "tables",associateField = "tables")
    public String getTables() {
        return tables;
    }
    public void setTables(String tables) {
        this.tables = tables;
    }
    
    
}
