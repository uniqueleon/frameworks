package org.aztec.framework.mybatis;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import io.shardingsphere.jdbc.spring.boot.SpringBootConfiguration;

/**
 * Mybatis自动配置类
 * @author liming
 *
 */
@Configuration
@ConditionalOnProperty(prefix="sjsc.framework",name="mybatis.disconf_enabled",havingValue="true")
@ComponentScan(basePackages={"com.sjsc.framework.mybatis.conf.dao.ibatis.*"})
@EnableAutoConfiguration(exclude={SpringBootConfiguration.class})
public class MybatisAutoConfiguration {

}
