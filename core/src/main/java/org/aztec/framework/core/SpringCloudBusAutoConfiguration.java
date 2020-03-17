package org.aztec.framework.core;

import org.aztec.framework.disconf.items.SpringBusConf;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory.CacheMode;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Configuration
@ConditionalOnProperty(prefix="sjsc.framework",name="bus.disconf_enabled",havingValue="true")
@ConditionalOnBean({DisconfAutoConfiguration.class,SpringBusConf.class})
public class SpringCloudBusAutoConfiguration {

    @Autowired
    SpringBusConf springBusConf;
    
    
 
    @Bean(name = "myRabbitTemplete")
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public RabbitTemplate myRabbitTemplete(ConnectionFactory connectionFactory) {
        final RabbitTemplate rabbitTemplete = new RabbitTemplate(connectionFactory);
        rabbitTemplete.setConfirmCallback(new RabbitTemplate.ConfirmCallback() {
 
            @Override
            public void confirm(CorrelationData correlationData, boolean ack, String cause) {
                if (!ack) {
                    FrameworkLogger.info("MQ消息发送失败，消息重发");
                }
            }
        });
        rabbitTemplete.setMessageConverter(new Jackson2JsonMessageConverter());
        return rabbitTemplete;
    }
    
    @Bean
    public ConnectionFactory connectionFactory(){
        CachingConnectionFactory connFactory =  new CachingConnectionFactory(springBusConf.getHost());
        connFactory.setPort(Integer.parseInt(springBusConf.getPort()));
        connFactory.setCacheMode(CacheMode.CONNECTION);
        connFactory.setChannelCacheSize(Integer.parseInt(springBusConf.getConnectionNum()));
        connFactory.setUsername(springBusConf.getUser());
        connFactory.setPassword(springBusConf.getPassword());
        return connFactory;
    }

}
