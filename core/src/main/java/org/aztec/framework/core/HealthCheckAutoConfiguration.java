package org.aztec.framework.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages={
        "org.aztec.framework.core.health*"})
public class HealthCheckAutoConfiguration {
    
    private static final Logger LOG = LoggerFactory.getLogger(HealthCheckAutoConfiguration.class);

    static{
        LOG.info("Initialing eureka client health checker!");
    }
}
