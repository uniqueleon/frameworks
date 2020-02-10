package org.aztec.framework.mybatis.conf.dao.ibatis;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.sql.DataSource;

import org.apache.commons.dbcp2.BasicDataSource;
import org.aztec.framework.core.DisconfAutoConfiguration;
import org.aztec.framework.disconf.items.ActualNodesConf;
import org.aztec.framework.disconf.items.Constants;
import org.aztec.framework.disconf.items.DataSourceConfig;
import org.aztec.framework.disconf.items.DatabaseRules;
import org.aztec.framework.disconf.items.MasterSlaveConfig;
import org.aztec.framework.disconf.items.MasterSlaveConfig.MasterSlaveInfo;
import org.aztec.framework.disconf.items.ShardingStragegy;
import org.aztec.framework.disconf.items.TableInfoConf;
import org.aztec.framework.disconf.items.TableRules;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import com.google.api.client.util.Maps;
import com.google.common.collect.Lists;

import io.shardingsphere.core.api.ShardingDataSourceFactory;
import io.shardingsphere.core.api.algorithm.sharding.complex.ComplexKeysShardingAlgorithm;
import io.shardingsphere.core.api.config.MasterSlaveRuleConfiguration;
import io.shardingsphere.core.api.config.ShardingRuleConfiguration;
import io.shardingsphere.core.api.config.TableRuleConfiguration;
import io.shardingsphere.core.api.config.strategy.ComplexShardingStrategyConfiguration;
import io.shardingsphere.core.api.config.strategy.InlineShardingStrategyConfiguration;
import io.shardingsphere.core.api.config.strategy.ShardingStrategyConfiguration;
import io.shardingsphere.core.jdbc.core.datasource.MasterSlaveDataSource;
import io.shardingsphere.core.rule.MasterSlaveRule;

/**
 * Disconf数据源配置
 * 
 * @author liming
 *
 */
@Configuration
@EnableAutoConfiguration
@ConditionalOnProperty(prefix = "sjsc.framework", name = "mybatis.disconf_enabled", havingValue = "true")
@AutoConfigureAfter({ DisconfAutoConfiguration.class })
public class DisconfDataSourceConfiguration {

    @Autowired
    private DataSourceConfig dsConfig;
    @Autowired
    private TableRules rules;
    @Autowired
    private ActualNodesConf nodes;
    @Autowired
    private TableInfoConf tables;
    @Autowired
    private DatabaseRules dbRulesConf;
    @Autowired(required = false)
    private MasterSlaveConfig msConf;
    @Autowired
    private Map<String, ComplexKeysShardingAlgorithm> algorithms;

    private static final Logger LOG = LoggerFactory.getLogger(DisconfDataSourceConfiguration.class);

    private Map<String, DataSource> createDataSource() throws SQLException {
        Map<String, DataSource> dataSourceMap = new HashMap<>();
        List<DataSourceInfo> dsInfos = getDataSourceInfo();
        for (int i = 0; i < dsInfos.size(); i++) {

            BasicDataSource dataSource1 = new BasicDataSource();
            DataSourceInfo dsi = dsInfos.get(i);
            dataSource1.setDriverClassName(dsi.getDriver());
            dataSource1.setUrl(dsi.getUrl());
            dataSource1.setUsername(dsi.getUser());
            dataSource1.setPassword(dsi.getPassword());
            dataSourceMap.put(dsi.getName(), dataSource1);
        }
        if (msConf != null) {
            List<MasterSlaveInfo> msInfos = msConf.getMasterSlaveInfos();
            if (msInfos != null) {
                for (int i = 0; i < msInfos.size(); i++) {
                    MasterSlaveInfo msi = msInfos.get(i);
                    MasterSlaveRule msr = new MasterSlaveRule(
                            new MasterSlaveRuleConfiguration(msi.getName(), msi.getMasters().get(0), msi.getSlaves()));
                    Map<String, Object> configMaps = Maps.newHashMap();
                    MasterSlaveDataSource msc = new MasterSlaveDataSource(dataSourceMap, new MasterSlaveRuleConfiguration(msi.getName(), msi.getMasters().get(0), msi.getSlaves()), configMaps, null);
                    dataSourceMap.put(msi.getName(), msc);
                }
            }
        }
        return dataSourceMap;
    }

    private ShardingRuleConfiguration getRuleConfiguration() {

        //
        ShardingRuleConfiguration shardingRuleConfig = new ShardingRuleConfiguration();
        //shardingRuleConfig.setDefaultDatabaseShardingStrategyConfig(new NoneShardingStrategyConfiguration());
        shardingRuleConfig.setDefaultDataSourceName("ds0");
        //shardingRuleConfig.setDefaultTableShardingStrategyConfig(new NoneShardingStrategyConfiguration());
        List<TableRuleInfo> ruleInfo = getRules();
        for (int i = 0; i < ruleInfo.size(); i++) {
            TableRuleInfo rule = ruleInfo.get(i);
            TableRuleConfiguration tableRuleConfig = new TableRuleConfiguration();
            ShardingStragegy stragegy = ShardingStragegy.valueOf(rule.getStragegy());
            tableRuleConfig.setLogicTable(rule.getName());
            tableRuleConfig.setDatabaseShardingStrategyConfig(getShardStragegyConfig(rule, stragegy, true));
            tableRuleConfig.setTableShardingStrategyConfig(getShardStragegyConfig(rule, stragegy, false));
            tableRuleConfig.setLogicIndex("id");
            tableRuleConfig.setActualDataNodes(rule.getActualDataNodes());
            shardingRuleConfig.getTableRuleConfigs().add(tableRuleConfig);
            shardingRuleConfig.getBindingTableGroups().add(rule.getName());
            ConfigurationUtils.addTableRule(rule);
        }
        return shardingRuleConfig;
    }

    private ShardingStrategyConfiguration getShardStragegyConfig(TableRuleInfo rule, ShardingStragegy stragegy,
            boolean isDatasource) {

        switch (stragegy) {
        case COMPLEX:
            ComplexKeysShardingAlgorithm algorithm = algorithms
                    .get(isDatasource ? rule.getDbRule() : rule.getTableRule());
            return new ComplexShardingStrategyConfiguration(
                    ConfigurationUtils.getShardKeysAsString(rule.getPrimaryKey(), isDatasource), algorithm);
        case INLINE:
            return new InlineShardingStrategyConfiguration(
                    ConfigurationUtils.getShardKeysAsString(rule.getPrimaryKey(), isDatasource),
                    isDatasource ? rule.getDbRule() : rule.getTableRule());
        }
        return null;
    }

    @Bean(name = "dataSource")
    @Primary
    public DataSource getShardingJdbcDataSource() throws SQLException {

        LOG.info("init datasource:" + dsConfig.getConnectionUrls() + " from disconf!");
        Map<String, DataSource> dataSourceMap = createDataSource();
        
        Properties prop = new Properties();
        prop.put("check.table.metadata.enabled", false);
        // 配置第一个数据源
        DataSource dataSource = ShardingDataSourceFactory.createDataSource(dataSourceMap, getRuleConfiguration(),
                new HashMap<String, Object>(), prop);

        LOG.info("init datasource:" + dsConfig.getConnectionUrls() + " from disconf finished!");
        return dataSource;
    }

    public List<TableRuleInfo> getRules() {
        String[] names = tables.getTables().split(Constants.DEFAULT_SEPERATOR);
        String[] tRules = rules.getTableRules().split(Constants.DEFAULT_SEPERATOR);
        String[] stragegies = new String[names.length];
        String[] transformers = tables.getTransformers().split(Constants.DEFAULT_SEPERATOR);
        if (rules.getStragegies() != null) {
            stragegies = rules.getStragegies().split(Constants.DEFAULT_SEPERATOR);
        } else {
            for (int i = 0; i < stragegies.length; i++) {
                stragegies[i] = ShardingStragegy.INLINE.name();
            }
        }
        String[] dbRules = dbRulesConf.getDatabaseRules().split(Constants.DEFAULT_SEPERATOR);
        String[] pks = tables.getPrimaryKeys().split(Constants.DEFAULT_SEPERATOR);
        String[] actualDataNodes = nodes.getActualDataNodes().split(Constants.DEFAULT_SEPERATOR);
        List<TableRuleInfo> ds = Lists.newArrayList();
        for (int i = 0; i < names.length; i++) {
            ds.add(new TableRuleInfo(names[i], dbRules[i], tRules[i], pks[i], actualDataNodes[i], stragegies[i]
                    ,transformers[i]));
        }
        return ds;
    }

    public static class TableRuleInfo {
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

    public List<DataSourceInfo> getDataSourceInfo() {
        String[] names = dsConfig.getDataSources().split(Constants.DEFAULT_SEPERATOR);
        String[] urls = dsConfig.getConnectionUrls().split(Constants.DEFAULT_SEPERATOR);

        String[] users = dsConfig.getUserNames().split(Constants.DEFAULT_SEPERATOR);
        String[] pwds = dsConfig.getPasswords().split(Constants.DEFAULT_SEPERATOR);
        String driver = "com.mysql.jdbc.Driver";
        List<DataSourceInfo> ds = Lists.newArrayList();
        for (int i = 0; i < names.length; i++) {
            ds.add(new DataSourceInfo(names[i], urls[i], driver, users[i], pwds[i]));
        }
        return ds;
    }

    public static class DataSourceInfo {

        private String name;
        private String url;
        private String driver;
        private String user;
        private String password;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getDriver() {
            return driver;
        }

        public void setDriver(String driver) {
            this.driver = driver;
        }

        public String getUser() {
            return user;
        }

        public void setUser(String user) {
            this.user = user;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public DataSourceInfo(String name, String url, String driver, String user, String password) {
            super();
            this.name = name;
            this.url = url;
            this.driver = driver;
            this.user = user;
            this.password = password;
        }

    }

}
