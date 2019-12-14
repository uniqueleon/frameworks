package org.aztec.framework.elastic_job.beans;

import com.dangdang.ddframe.job.api.simple.SimpleJob;

public interface ShardingJobConfig {

    public Class<? extends SimpleJob> getJobClass();
    public String getJobName();
    public String getCron();
    public Integer getShardCount();
    public String getShardingParameter();
    //public List<Class<? extends DataflowJob>> getDataFlowJobs();
    
}
