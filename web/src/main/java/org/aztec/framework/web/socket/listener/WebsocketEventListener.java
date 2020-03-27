package org.aztec.framework.web.socket.listener;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.aztec.framework.api.rest.entity.WSMessageDTO;
import org.aztec.framework.disconf.items.WebSocketConfig;
import org.aztec.framework.web.annotation.WebSocketBean;
import org.aztec.framework.web.socket.WSMessageSender;
import org.aztec.framework.web.socket.event.WebSocketEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.google.common.collect.Maps;


@WebSocketBean
@Component
public class WebsocketEventListener  implements ApplicationListener<WebSocketEvent>{
    

    @Autowired
    WSMessageSender sender;
    
    @Autowired
    WebSocketConfig socketConfig;

    private static final Map<String,WebSocketEvent> duplicatedEvent =  Maps.newConcurrentMap();
    private static final Map<String,Long> duplicatedEventTime  = Maps.newConcurrentMap();
    
    public static OldEventCacheCleaner eventCleaner;
    
    public static final Object lockObj = new Object();
    
    private static final Logger LOG = LoggerFactory.getLogger(WebsocketEventListener.class);
    
    
    @Override
    @EventListener
    public void onApplicationEvent(WebSocketEvent arg0) {
        // TODO Auto-generated method stub
        try {
            if(!duplicatedEvent.containsKey(arg0.getId())){
                List<WSMessageDTO> messageDatas = (List<WSMessageDTO>) arg0.getSource();
                LOG.info("Synchronized web socket data...." + messageDatas);
                messageDatas.forEach(message -> {
                    sender.send(message);
                });
                duplicatedEvent.put(arg0.getId(), arg0);
                duplicatedEventTime.put(arg0.getId(), new Date().getTime());
                LOG.info("Send websocket data finished!");
            }
            else {
                LOG.info("Duplicated event[" + arg0.getId() + "]! just ignore!");
            }
        } catch (Exception e) {
            LOG.error("Something wrong id handler code!");
            LOG.error(e.getMessage(),e);
        }
        
    }
    
    public void startCacheCleanThread(){
        if(eventCleaner == null){
            synchronized (lockObj) {
                if(eventCleaner == null){
                    eventCleaner = new OldEventCacheCleaner();
                    eventCleaner.start();
                }
            }
        }
    }
    
    private class OldEventCacheCleaner extends Thread{
        
        private boolean runnable = true;
        
        public OldEventCacheCleaner() {
            super("WebsocketEventCleaner");
        }

        @Override
        public void run() {
            LOG.info("Start event cache cleaner thread!");
            while(runnable){
                try {
                    for(String eventId :duplicatedEvent.keySet()){
                        Long time = duplicatedEventTime.get(eventId);
                        Long interval = Long.parseLong(socketConfig.getDuplicateEventCheckInterval());
                        Long nowTime = new Date().getTime();
                        if((nowTime - time) > interval){
                            duplicatedEvent.remove(eventId);
                            duplicatedEventTime.remove(eventId);
                        }
                    }
                    Thread.currentThread().sleep(1000l);
                } catch (Exception e) {
                    LOG.error(e.getMessage(),e);
                }
            }
        }
    }
    
}
