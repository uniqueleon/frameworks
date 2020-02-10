package org.aztec.framework.disconf.items;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.baidu.disconf.client.common.annotations.DisconfFile;
import com.baidu.disconf.client.common.annotations.DisconfFileItem;

/**
 * 库表分片规则
 * @author liming
 *
 */
@Service
@Scope("singleton")
@DisconfFile(filename = "sharding_jdbc_table_rules.properties")
public class TableRules {

	private String tableRules;
	
	private String stragegies;

	@DisconfFileItem(name = "table.rules",associateField = "tableRules")
	public String getTableRules() {
		return tableRules;
	}

	public void setTableRules(String tableRules) {
		this.tableRules = tableRules;
	}


    @DisconfFileItem(name = "table.stragegies",associateField = "tableRules")
    public String getStragegies() {
        return stragegies;
    }

    public void setStragegies(String stragegies) {
        this.stragegies = stragegies;
    }
	
	

}
