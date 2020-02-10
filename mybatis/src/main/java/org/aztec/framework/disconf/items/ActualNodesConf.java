package org.aztec.framework.disconf.items;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.baidu.disconf.client.common.annotations.DisconfFile;
import com.baidu.disconf.client.common.annotations.DisconfFileItem;

/**
 * 实际结点配置
 * @author liming
 *
 */
@Service
@Scope("singleton")
@DisconfFile(filename = "sharding_jdbc_actual_nodes.properties")
public class ActualNodesConf {


	private String actualDataNodes;

	@DisconfFileItem(name = "table.actualNodes",associateField = "actualDataNodes")
	public String getActualDataNodes() {
		return actualDataNodes;
	}

	public void setActualDataNodes(String actualDataNodes) {
		this.actualDataNodes = actualDataNodes;
	}
}
