package org.aztec.framework.disconf.items;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.baidu.disconf.client.common.annotations.DisconfFile;
import com.baidu.disconf.client.common.annotations.DisconfFileItem;

/**
 * 库表信息配置
 * @author liming
 *
 */
@Service
@Scope("singleton")
@DisconfFile(filename = "sharding_jdbc_table_info.properties")
public class TableInfoConf {


	private String tables;
	
	private String primaryKeys;
	
	private String transformers;
	

	@DisconfFileItem(name = "table.pks",associateField = "primaryKeys")
	public String getPrimaryKeys() {
		return primaryKeys;
	}

	public void setPrimaryKeys(String primaryKeys) {
		this.primaryKeys = primaryKeys;
	}

	@DisconfFileItem(name = "tables",associateField = "tables")
	public String getTables() {
		return tables;
	}

	public void setTables(String tables) {
		this.tables = tables;
	}

    @DisconfFileItem(name = "table.transformers",associateField = "transformers")
    public String getTransformers() {
        return transformers;
    }

    public void setTransformers(String transformers) {
        this.transformers = transformers;
    }
	
	
}
