package org.aztec.framework.disconf.items;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.baidu.disconf.client.common.annotations.DisconfFile;
import com.baidu.disconf.client.common.annotations.DisconfFileItem;

/**
 * 
 * @author liming
 *
 */
@Service
@Scope("singleton")
@DisconfFile(filename="websocket_conf.properties")
public class WebSocketConfig {

    private String eventSource;
    private String eventDestination;
    private String endPoint;
    private String destinationPrefixes;
    private String appDestinationPrefixes;
    private String duplicateCheck;
    private String duplicateCheckInterval;
    private String duplicateEventCheckInterval;
    

    @DisconfFileItem(name="eventSource",associateField="eventSource")
    public String getEventSource() {
        return eventSource;
    }
    public void setEventSource(String eventSource) {
        this.eventSource = eventSource;
    }
    
    @DisconfFileItem(name="eventDestination",associateField="eventDestination")
    public String getEventDestination() {
        return eventDestination;
    }
    public void setEventDestination(String eventDestination) {
        this.eventDestination = eventDestination;
    }
    @DisconfFileItem(name="endPoint",associateField="endPoint")
    public String getEndPoint() {
        return endPoint;
    }
    public void setEndPoint(String endPoint) {
        this.endPoint = endPoint;
    }
    @DisconfFileItem(name="destinationPrefixes",associateField="destinationPrefixes")
    public String getDestinationPrefixes() {
        return destinationPrefixes;
    }
    public void setDestinationPrefixes(String destinationPrefixes) {
        this.destinationPrefixes = destinationPrefixes;
    }
    @DisconfFileItem(name="appDestinationPrefixes",associateField="appDestinationPrefixes")
    public String getAppDestinationPrefixes() {
        return appDestinationPrefixes;
    }
    public void setAppDestinationPrefixes(String appDestinationPrefixes) {
        this.appDestinationPrefixes = appDestinationPrefixes;
    }
    @DisconfFileItem(name="duplicateCheck",associateField="duplicateCheck")
    public String getDuplicateCheck() {
        return duplicateCheck;
    }
    public void setDuplicateCheck(String duplicateCheck) {
        this.duplicateCheck = duplicateCheck;
    }
    @DisconfFileItem(name="duplicateCheckInterval",associateField="duplicateCheckInterval")
    public String getDuplicateCheckInterval() {
        return duplicateCheckInterval;
    }
    public void setDuplicateCheckInterval(String duplicateCheckInterval) {
        this.duplicateCheckInterval = duplicateCheckInterval;
    }
    @DisconfFileItem(name="duplicateEventCheckInterval",associateField="duplicateEventCheckInterval")
    public String getDuplicateEventCheckInterval() {
        return duplicateEventCheckInterval;
    }
    public void setDuplicateEventCheckInterval(String duplicateEventCheckInterval) {
        this.duplicateEventCheckInterval = duplicateEventCheckInterval;
    }
    
    
}
