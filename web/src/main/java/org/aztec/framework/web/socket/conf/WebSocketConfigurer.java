package org.aztec.framework.web.socket.conf;

import org.aztec.framework.disconf.items.WebSocketConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
@ConditionalOnProperty(prefix="sjsc.framework",name="websocket.enabled",matchIfMissing=false,havingValue="true")
public class WebSocketConfigurer implements WebSocketMessageBrokerConfigurer{
    
    @Autowired
    private WebSocketConfig socketConf;

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
      config.enableSimpleBroker(socketConf.getDestinationPrefixes().split(","));
      config.setApplicationDestinationPrefixes(socketConf.getAppDestinationPrefixes().split(","));
          

    }
      
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint(socketConf.getEndPoint()).setAllowedOrigins("*").withSockJS();
        registry.addEndpoint(socketConf.getEndPoint()).setAllowedOrigins("*");
    }
    
    
}
