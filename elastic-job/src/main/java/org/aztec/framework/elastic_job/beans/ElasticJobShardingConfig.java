package org.aztec.framework.elastic_job.beans;

import java.util.List;

import com.dangdang.ddframe.job.event.JobEventListener;

public interface ElasticJobShardingConfig {

    public Integer getCurrentNodeIndex();
    public boolean isElasticJobEnabled();
    public JobEventListener getEventListener();
    public List<ShardingJobConfig> getShardingJobConfigs();
}
