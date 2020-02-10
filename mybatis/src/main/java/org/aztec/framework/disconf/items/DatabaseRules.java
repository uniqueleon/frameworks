package org.aztec.framework.disconf.items;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.baidu.disconf.client.common.annotations.DisconfFile;
import com.baidu.disconf.client.common.annotations.DisconfFileItem;

/**
 * 数据库分片规则
 * @author liming
 *
 */
@Service
@Scope("singleton")
@DisconfFile(filename = "sharding_jdbc_db_rules.properties")
public class DatabaseRules {


	private String databaseRules;

	@DisconfFileItem(name = "db.rules",associateField = "databaseRules")
	public String getDatabaseRules() {
		return databaseRules;
	}

	public void setDatabaseRules(String databaseRules) {
		this.databaseRules = databaseRules;
	}

}
