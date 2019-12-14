package org.aztec.framework.core;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

@Configuration
@ImportResource({"classpath:default-disconf.xml"})
@ConditionalOnProperty(prefix="aztec.framework",name="disconf.enabled",havingValue="true")
public class DisconfAutoConfiguration {
    
    static {
        System.out.println("Configuration inited!");
    }

}
