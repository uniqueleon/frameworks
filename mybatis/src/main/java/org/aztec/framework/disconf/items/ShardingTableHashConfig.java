package org.aztec.framework.disconf.items;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.baidu.disconf.client.common.annotations.DisconfFile;

@Service
@Scope("singleton")
@DisconfFile(filename = "shard_table_hash_config.properties",app="SJSC_FRAMEWORK",env="online",version="1_0_0")
public class ShardingTableHashConfig {

    private String sourceTables;
    
    public static enum HashAlgorithm{
        MOD;
    }

    public String getSourceTables() {
        return sourceTables;
    }

    public void setSourceTables(String sourceTables) {
        this.sourceTables = sourceTables;
    }
    
    
}
