package org.aztec.framework.elastic_job.beans;

import java.io.IOException;
import java.util.List;

import org.aztec.framework.elastic_job.job.SimpleBaseJob;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.dangdang.ddframe.job.config.JobCoreConfiguration;
import com.dangdang.ddframe.job.config.simple.SimpleJobConfiguration;
import com.dangdang.ddframe.job.event.JobEventConfiguration;
import com.dangdang.ddframe.job.lite.api.JobScheduler;
import com.dangdang.ddframe.job.lite.config.LiteJobConfiguration;
import com.dangdang.ddframe.job.reg.zookeeper.ZookeeperRegistryCenter;
import com.google.common.collect.Lists;

@Component
public class JobSchedulerFactory {
    
    @Autowired
    private ZookeeperRegistryCenter regCenter;
    
    @Autowired
    private JobEventConfiguration jobEventConfig;
    
    @Autowired
    private ElasticJobShardingConfig shardConfig;

    public List<JobScheduler> registScheduler() throws IOException{
        
        
        List<JobScheduler> retSchedulers = Lists.newArrayList();
        
        retSchedulers.addAll(setUpSimpleJob());
        return retSchedulers;
    }
    
    private List<JobScheduler> setUpSimpleJob() {
        List<JobScheduler> jobSchedulers = Lists.newArrayList();
        if(!CollectionUtils.isEmpty(shardConfig.getShardingJobConfigs()) ){
            shardConfig.getShardingJobConfigs().stream().forEach(jobConfig -> {
                JobCoreConfiguration coreConfig = JobCoreConfiguration.newBuilder(jobConfig.getJobName(), jobConfig.getCron(), jobConfig.getShardCount()).shardingItemParameters(jobConfig.getShardingParameter()).build();
                SimpleJobConfiguration simpleJobConfig = new SimpleJobConfiguration(coreConfig, SimpleBaseJob.class.getCanonicalName());
                new JobScheduler(regCenter, LiteJobConfiguration.newBuilder(simpleJobConfig).build(), jobEventConfig).init();
            });
        }
        return jobSchedulers;
    }
}
