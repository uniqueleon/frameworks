package org.aztec.framework.core;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

@Configuration
@ImportResource({"classpath:log4j2.xml"})
@ConditionalOnProperty(prefix="sjsc.framework",name="log4j2.enabled",matchIfMissing=false,havingValue="true")
public class Log4jAutoConfiguration {

}
