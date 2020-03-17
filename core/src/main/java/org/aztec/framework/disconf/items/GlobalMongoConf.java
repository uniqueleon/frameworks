package org.aztec.framework.disconf.items;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.baidu.disconf.client.common.annotations.DisconfFile;
import com.baidu.disconf.client.common.annotations.DisconfFileItem;

@Service
@Scope("singleton")
@DisconfFile(filename="global_mongo_conf.properties",app="SJSC_FRAMEWORK",env="online",version="1_0_0")
public class GlobalMongoConf {

    private String usePool;
    private String poolSize;

    @DisconfFileItem(name="usePool",associateField="usePool")
    public String getUsePool() {
        return usePool;
    }

    public void setUsePool(String usePool) {
        this.usePool = usePool;
    }

    @DisconfFileItem(name="poolSize",associateField="poolSize")
    public String getPoolSize() {
        return poolSize;
    }


    public void setPoolSize(String poolSize) {
        this.poolSize = poolSize;
    }
    
    
}
