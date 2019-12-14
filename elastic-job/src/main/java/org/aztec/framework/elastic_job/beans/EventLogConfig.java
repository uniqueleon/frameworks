package org.aztec.framework.elastic_job.beans;

import java.util.UUID;

import org.aztec.framework.elastic_job.listener.LogEventListener;
import org.springframework.stereotype.Component;

import com.dangdang.ddframe.job.event.JobEventConfiguration;
import com.dangdang.ddframe.job.event.JobEventListener;
import com.dangdang.ddframe.job.event.JobEventListenerConfigurationException;

@Component
public class EventLogConfig implements JobEventConfiguration {

    private String uuid = UUID.randomUUID().toString();
    
    @Override
    public String getIdentity() {
        return uuid;
    }

    @Override
    public JobEventListener createJobEventListener() throws JobEventListenerConfigurationException {
        return new LogEventListener();
    }

}
