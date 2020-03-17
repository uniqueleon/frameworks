package org.aztec.framework.disconf.items;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.baidu.disconf.client.common.annotations.DisconfFile;
import com.baidu.disconf.client.common.annotations.DisconfFileItem;

/**
 * MongoDB连接配置
 * @author liming
 *
 */
@Service
@Scope("singleton")
@DisconfFile(filename="mongo_conf.properties")
public class MongoDBConf {

    
    private String connectionUrl;

    @DisconfFileItem(name="url",associateField="connectionUrl")
    public String getConnectionUrl() {
        return connectionUrl;
    }

    public void setConnectionUrl(String connectionUrl) {
        this.connectionUrl = connectionUrl;
    }

    
}
