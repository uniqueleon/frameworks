package org.aztec.framework.mybatis.conf.dao.ibatis;

public class TableRuleInfo {
    private String name;
    private String dbRule;
    private String tableRule;
    private String stragegy;
    private String primaryKey;
    private String transformers;
    private String actualDataNodes;

    public String getActualDataNodes() {
        return actualDataNodes;
    }

    public void setActualDataNodes(String actualDataNodes) {
        this.actualDataNodes = actualDataNodes;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDbRule() {
        return dbRule;
    }

    public void setDbRule(String dbRule) {
        this.dbRule = dbRule;
    }

    public String getTableRule() {
        return tableRule;
    }

    public void setTableRule(String tableRule) {
        this.tableRule = tableRule;
    }

    public String getPrimaryKey() {
        return primaryKey;
    }

    public void setPrimaryKey(String primaryKey) {
        this.primaryKey = primaryKey;
    }

    public String getStragegy() {
        return stragegy;
    }

    public void setStragegy(String stragegy) {
        this.stragegy = stragegy;
    }

    public String getTransformers() {
        return transformers;
    }

    public void setTransformers(String transformers) {
        this.transformers = transformers;
    }

    public TableRuleInfo(String name, String dbRule, String tableRule, String primaryKey, String actualDataNodes,
            String stragegy,String transformers) {
        super();
        this.name = name;
        this.dbRule = dbRule;
        this.tableRule = tableRule;
        this.primaryKey = primaryKey.replaceAll("#", ",");
        this.transformers = transformers.replaceAll("#", ",");
        this.actualDataNodes = actualDataNodes;
        this.stragegy = stragegy;
        
    }
}
