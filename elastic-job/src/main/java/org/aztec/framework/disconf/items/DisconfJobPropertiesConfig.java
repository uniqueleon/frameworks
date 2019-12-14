package org.aztec.framework.disconf.items;

import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.baidu.disconf.client.common.annotations.DisconfFile;
import com.baidu.disconf.client.common.annotations.DisconfFileItem;

@Service
@Scope("singleton")
@Primary
@DisconfFile(filename = "elastic_job_main_config.properties")
public class DisconfJobPropertiesConfig {

    
    private String enabled;
    
    private String jobNames;
    
    private String jobCrons;
    
    private String jobClasses;
    
    private String jobShardCount;
    
    private String jobShardParameters;
    
    private String index;

    @DisconfFileItem(name="enabled",associateField="enabled")
    public String getEnabled() {
        return enabled;
    }

    public void setEnabled(String enabled) {
        this.enabled = enabled;
    }

    @DisconfFileItem(name="job.names",associateField="jobNames")
    public String getJobNames() {
        return jobNames;
    }

    public void setJobNames(String jobNames) {
        this.jobNames = jobNames;
    }


    @DisconfFileItem(name="job.crons",associateField="jobCrons")
    public String getJobCrons() {
        return jobCrons;
    }

    public void setJobCrons(String jobCrons) {
        this.jobCrons = jobCrons;
    }


    @DisconfFileItem(name="job.classes",associateField="jobClasses")
    public String getJobClasses() {
        return jobClasses;
    }

    public void setJobClasses(String jobClasses) {
        this.jobClasses = jobClasses;
    }


    @DisconfFileItem(name="job.shard.count",associateField="jobShardCount")
    public String getJobShardCount() {
        return jobShardCount;
    }

    public void setJobShardCount(String jobShardCount) {
        this.jobShardCount = jobShardCount;
    }

    @DisconfFileItem(name="job.shard.params",associateField="jobShardParameters")
    public String getJobShardParameters() {
        return jobShardParameters;
    }

    public void setJobShardParameters(String jobShardParameters) {
        this.jobShardParameters = jobShardParameters;
    }

    @DisconfFileItem(name="job.server.index",associateField="index")
    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }
    
    
    
}
