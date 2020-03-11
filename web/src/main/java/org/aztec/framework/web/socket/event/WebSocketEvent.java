package org.aztec.framework.web.socket.event;

import org.springframework.cloud.bus.event.RemoteApplicationEvent;

public class WebSocketEvent extends RemoteApplicationEvent{

    /**
     * 
     */
    private static final long serialVersionUID = 3902768233263413439L;

    public WebSocketEvent() {
        super();
        // TODO Auto-generated constructor stub
    }

    public WebSocketEvent(Object source, String originService, String destinationService) {
        super(source, originService, destinationService);
        // TODO Auto-generated constructor stub
    }

    public WebSocketEvent(Object source, String originService) {
        super(source, originService);
    }

    
}
