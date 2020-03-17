package org.aztec.framework.web.socket;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.aztec.framework.api.rest.entity.WSMessageConsumerDTO;
import org.aztec.framework.api.rest.entity.WSMessageDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;

@Component
public class WSMessageSender {

    private static final List<String> receivers = Lists.newArrayList();


    public static synchronized void addReceiver(String user){
        if(!receivers.contains(user)){
            receivers.add(user);
        }
    }
    
    public static boolean isReceiverExists(String user){
        return receivers.contains(user);
    }
    
    @Autowired
    private SimpMessageSendingOperations simpMessageSendingOperations;
    
    @Autowired
    private MessageWrapper wrapper;

    public void send(WSMessageDTO message){
        DestinationType dType = DestinationType.getType(message.getMsgType());
        if(dType != null){
            switch(dType){
            case BROADCAST :
                simpMessageSendingOperations.convertAndSend(message.getTopic(), message);
                break;
            case MULTIPLE:
                if (CollectionUtils.isNotEmpty(message.getConsumers())) {
                    for (WSMessageConsumerDTO consumerDTO : message.getConsumers()) {
                        String id = getConsumerID(consumerDTO);
                        if(receivers.contains(id)){
                            simpMessageSendingOperations.convertAndSend(message.getTopic() + "/" + id
                                    , message);
                        }
                    }
                }
                break;
            case SINGLE:
                if(CollectionUtils.isNotEmpty(message.getConsumers())){
                    WSMessageConsumerDTO consumerDTO = message.getConsumers().get(0);
                    String id = getConsumerID(consumerDTO);
                    if(receivers.contains(id)){
                        simpMessageSendingOperations.convertAndSend(message.getTopic() + "/" + id
                                , message);
                    }
                }
                break;
            default:
            }
        }
    }
    

    public String getConsumerID(WSMessageConsumerDTO consumerDTO){
        //Map<String,Object> consumerInfo = JsonUtils.getJSONObject(consumerDTO.getReceiverInfo());
        //String id = consumerInfo.get("id").toString();
        String id = consumerDTO.getReceiverId();
        return id;
    }
    
    public void sendToUser(String destination,String user,Object payload){
        simpMessageSendingOperations.convertAndSendToUser(user, destination, payload);
    }
    
    public void broadcast(String destination,Object payload){
        simpMessageSendingOperations.convertAndSend(destination, payload);
    }
}
