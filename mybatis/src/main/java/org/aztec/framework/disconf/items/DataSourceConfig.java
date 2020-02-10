package org.aztec.framework.disconf.items;

import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.baidu.disconf.client.common.annotations.DisconfFile;
import com.baidu.disconf.client.common.annotations.DisconfFileItem;
import com.google.common.collect.Lists;

/**
 * 数据源配置
 * @author liming
 *
 */
@Service
@Scope("singleton")
@DisconfFile(filename = "sharding_jdbc_datasource.properties")
public class DataSourceConfig {

	private String dataSources;
	
	private String connectionUrls;
	
	private String userNames;
	
	private String passwords;


	@DisconfFileItem(name = "datasources",associateField = "dataSources")
	public String getDataSources() {
		return dataSources;
	}

	
	public void setDataSources(String dataSources) {
		this.dataSources = dataSources;
	}
	
	@DisconfFileItem(name = "urls",associateField = "connectionUrls")
	public String getConnectionUrls() {
		return connectionUrls;
	}

	public void setConnectionUrls(String connectionUrls) {
		this.connectionUrls = connectionUrls;
	}
	
	@DisconfFileItem(name = "users",associateField = "userNames")
	public String getUserNames() {
		return userNames;
	}

	public void setUserNames(String userNames) {
		this.userNames = userNames;
	}

	@DisconfFileItem(name = "passwords",associateField = "passwords")
	public String getPasswords() {
		return passwords;
	}

	public void setPasswords(String passwords) {
		this.passwords = passwords;
	}
	
}
