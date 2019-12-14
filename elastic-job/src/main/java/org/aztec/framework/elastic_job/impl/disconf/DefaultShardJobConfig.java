package org.aztec.framework.elastic_job.impl.disconf;

import org.aztec.framework.elastic_job.beans.ShardingJobConfig;
import org.springframework.stereotype.Component;

import com.dangdang.ddframe.job.api.simple.SimpleJob;

@Component
public class DefaultShardJobConfig implements ShardingJobConfig {
    
    private String jobName;
    private String className;
    private String cron;
    private Integer shardCount;
    private String shardingParameter;
    
    
    public DefaultShardJobConfig(String jobName, String className, String cron, Integer shardCount,
            String shardingParameter) {
        super();
        this.jobName = jobName;
        this.className = className;
        this.cron = cron;
        this.shardCount = shardCount;
        this.shardingParameter = shardingParameter;
    }

    public DefaultShardJobConfig() {
        // TODO Auto-generated constructor stub
    }

    @Override
    public Class<? extends SimpleJob> getJobClass()  {
        // TODO Auto-generated method stub
        try {
            return (Class<? extends SimpleJob>) Class.forName(className);
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public String getJobName() {
        // TODO Auto-generated method stub
        return jobName;
    }

    @Override
    public String getCron() {
        // TODO Auto-generated method stub
        return cron;
    }

    @Override
    public Integer getShardCount() {
        // TODO Auto-generated method stub
        return shardCount;
    }

    @Override
    public String getShardingParameter() {
        // TODO Auto-generated method stub
        return shardingParameter;
    }

}
