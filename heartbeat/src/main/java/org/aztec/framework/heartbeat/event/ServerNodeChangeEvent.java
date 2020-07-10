package org.aztec.framework.heartbeat.event;

import org.springframework.cloud.bus.event.RemoteApplicationEvent;

public class ServerNodeChangeEvent extends RemoteApplicationEvent {

    /**
     * 
     */
    private static final long serialVersionUID = -170862120485014175L;
    

    public ServerNodeChangeEvent() {
        super();
    }

    public ServerNodeChangeEvent(Object source, String originService, String destinationService) {
        super(source, originService, destinationService);
    }

    public ServerNodeChangeEvent(Object source, String originService) {
        super(source, originService);
    }
    
    
}
