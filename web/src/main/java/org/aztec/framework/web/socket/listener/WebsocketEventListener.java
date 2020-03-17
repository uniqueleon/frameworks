package org.aztec.framework.web.socket.listener;

import java.util.List;

import org.aztec.framework.api.rest.entity.WSMessageDTO;
import org.aztec.framework.web.socket.WSMessageSender;
import org.aztec.framework.web.socket.event.WebSocketEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class WebsocketEventListener  implements ApplicationListener<WebSocketEvent>{
    

    @Autowired
    WSMessageSender sender;
    
    private static final Logger LOG = LoggerFactory.getLogger(WebsocketEventListener.class);
    
    
    @Override
    @EventListener
    public void onApplicationEvent(WebSocketEvent arg0) {
        // TODO Auto-generated method stub
        try {
            List<WSMessageDTO> messageDatas = (List<WSMessageDTO>) arg0.getSource();
            LOG.info("Synchronized web socket data...." + messageDatas);
            messageDatas.forEach(message -> {
                sender.send(message);
            });
        } catch (Exception e) {
            LOG.error(e.getMessage(),e);
        }
        
    }
    
}
