package org.aztec.framework.async.task.event;

import org.springframework.cloud.bus.event.RemoteApplicationEvent;

import com.sjsc.framework.api.restful.entity.async.AsyncTaskDTO;

public class AsyncTaskEvent extends RemoteApplicationEvent {

    /**
     * 
     */
    private static final long serialVersionUID = -8460116877161242366L;


    public AsyncTaskEvent() {
        super();
    }

    public AsyncTaskEvent(Object source, String originService, String destinationService) {
        super(source, originService, destinationService);
        // TODO Auto-generated constructor stub
    }

    
}
