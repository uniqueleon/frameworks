package org.aztec.framework.elastic_job.impl.disconf;

import java.util.List;

import org.aztec.framework.disconf.items.DisconfJobPropertiesConfig;
import org.aztec.framework.elastic_job.beans.ElasticJobShardingConfig;
import org.aztec.framework.elastic_job.beans.JobExecuteContext;
import org.aztec.framework.elastic_job.beans.ShardingJobConfig;
import org.aztec.framework.elastic_job.listener.LogEventListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.baidu.disconf.client.usertools.IDisconfDataGetter;
import com.dangdang.ddframe.job.event.JobEventListener;
import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;

@Component
public class DisconfJobShardingConfig implements ElasticJobShardingConfig {
    
    @Autowired
    private DisconfJobPropertiesConfig jobConfig;
    @Autowired
    private IDisconfDataGetter disconfDataGetter;
    
    @Override
    public Integer getCurrentNodeIndex() {
        JobExecuteContext.put("", Integer.parseInt(jobConfig.getIndex()));
        return Integer.parseInt(jobConfig.getIndex());
    }

    @Override
    public boolean isElasticJobEnabled() {
        return Boolean.parseBoolean(jobConfig.getEnabled());
    }

    @Override
    public JobEventListener getEventListener() {
        return new LogEventListener();
    }
    
    private CONFIG_MODE getConfigMode(){
        if(jobConfig.getConfigMode() == null){
            return CONFIG_MODE.INTEGRATED;
        }
        return CONFIG_MODE.valueOf(jobConfig.getConfigMode());
    }
    
    static final String DEFAULT_SEPERATOR = ",";

    @Override
    public List<ShardingJobConfig> getShardingJobConfigs() {
        CONFIG_MODE mode = getConfigMode();
        switch(mode){
        case DISTRIBUTED:
            return getShardingJobConfigs2();
        case INTEGRATED:
            return getShardingJobConfigs1();
        default:
            return getShardingJobConfigs1();
        }
    }
    
    private enum CONFIG_MODE {
        DISTRIBUTED,INTEGRATED;
    }
    
    private List<ShardingJobConfig> getShardingJobConfigs1(){
        if(jobConfig.getJobNames() == null 
                || jobConfig.getJobShardCount() == null || jobConfig.getJobCrons() == null
                || jobConfig.getJobShardParameters() == null){
            return Lists.newArrayList();
        }
        String[] jobNames = jobConfig.getJobNames().split(DEFAULT_SEPERATOR);
        String[] crons = jobConfig.getJobCrons().split(DEFAULT_SEPERATOR);
        String[] shardCounts = jobConfig.getJobShardCount().split(DEFAULT_SEPERATOR);
        String[] shardParams= readJsonStringArray(jobConfig.getJobShardParameters());
        List<ShardingJobConfig> jobConfigList = Lists.newArrayList();
        for(int i = 0;i < jobNames.length;i++){
            jobConfigList.add(new DefaultShardJobConfig(jobNames[i], "", crons[i], Integer.parseInt(shardCounts[i]), 
                    shardParams[i]));
        }
        return jobConfigList;
    }
    

    private static class JobConfig{

        public static String FILE_NAME_PREFIX = "job_";
        public static String FILE_NAME_SUFFIX = "_conf.properties";
        
        private JobConfig(){};
        public static String getFileName(String job){
            return FILE_NAME_PREFIX + job + FILE_NAME_SUFFIX;
        }
        
        public static String CRON = "cron";
        public static String SHARDING_COUNT = "shard.count";
        public static String SHARDING_PARAMETER = "shard.param";
    }
    
    private List<ShardingJobConfig> getShardingJobConfigs2(){
        
        String[] jobNames = jobConfig.getJobNames().split(DEFAULT_SEPERATOR);
        List<ShardingJobConfig> jobConfigList = Lists.newArrayList();
        for(int i = 0;i < jobNames.length;i++){
            String jobName = jobNames[i];
            String fileName = JobConfig.getFileName(jobName);
            jobConfigList.add(new DefaultShardJobConfig(jobNames[i], "", 
                    (String)disconfDataGetter.getByFileItem(fileName, JobConfig.CRON), 
                    Integer.parseInt((String)disconfDataGetter.getByFileItem(fileName, JobConfig.SHARDING_COUNT)), 
                    (String)disconfDataGetter.getByFileItem(fileName, JobConfig.SHARDING_PARAMETER)));
        }
        return jobConfigList;
    }
    
    private String[] readJsonStringArray(String json){
        JsonParser parser = new JsonParser();
        JsonArray array = parser.parse(json).getAsJsonArray();
        String[] retString = new String[array.size()];
        for(int i = 0;i < array.size();i++){
            retString[i] = array.get(i).getAsString();
        }
        return retString;
    }

}
