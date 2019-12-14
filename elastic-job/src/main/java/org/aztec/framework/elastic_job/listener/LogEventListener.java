package org.aztec.framework.elastic_job.listener;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.dangdang.ddframe.job.event.JobEventListener;
import com.dangdang.ddframe.job.event.type.JobExecutionEvent;
import com.dangdang.ddframe.job.event.type.JobStatusTraceEvent;

@Component
public class LogEventListener implements JobEventListener {
    
    private String identity = UUID.randomUUID().toString();
    
    private static final Logger LOG = LoggerFactory.getLogger("ELASTIC_JOB");

    @Override
    public String getIdentity() {
        return identity;
    }

    @Override
    public void listen(JobExecutionEvent jobExecutionEvent) {
        // TODO Auto-generated method stub
       //System.out.println("Default event execute trigger:" + jobExecutionEvent.getId());
    }

    @Override
    public void listen(JobStatusTraceEvent jobStatusTraceEvent) {
        // TODO Auto-generated method stub

        //System.out.println("Default event trace trigger:" + jobStatusTraceEvent.getId());
    }

}
