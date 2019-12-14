package org.aztec.framework.elastic_job;

import java.io.IOException;

import org.aztec.framework.elastic_job.beans.JobSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.Primary;

@Configuration
@ComponentScan(basePackages={
        "com.sjsc.framework.disconf.items",
        "com.sjsc.framework.elastic_job.*"
})
public class ElasticJobAutoConfiguration implements ApplicationContextAware,BeanFactoryAware{
    
    private static final Logger LOG = LoggerFactory.getLogger("ELASTIC_JOB");
    private static final String LOG_PREFIX = "[FRAMEWORK_ELASTIC_JOB]:";

    @Autowired
    private JobSchedulerFactory factory;
    
    @Bean
    @Primary
    public JobSchedulerFactory schedulerFactory() throws IOException{
        LOG.info(LOG_PREFIX + "Registing job schedulers.......................");
        factory.registScheduler();
        LOG.info(LOG_PREFIX + "Regist finished!");
        return factory;
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setApplicationContext(ApplicationContext arg0) throws BeansException {
        // TODO Auto-generated method stub
        
    }
}
