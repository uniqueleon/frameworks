package org.aztec.framework.web.controller;

import org.aztec.framework.api.rest.entity.RestRequest;
import org.aztec.framework.api.rest.entity.RestResult;
import org.aztec.framework.disconf.items.WebSocketConfig;
import org.aztec.framework.web.socket.MessageStatus;
import org.aztec.framework.web.socket.WSMessageSender;
import org.aztec.framework.web.socket.WSMessageService;
import org.aztec.framework.web.socket.entity.WSMessageDTO;
import org.aztec.framework.web.socket.entity.WSMessageRequest;
import org.aztec.framework.web.socket.entity.WSMsgStatisticInfo;
import org.aztec.framework.web.socket.event.WebSocketEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.collect.Lists;

@RestController
public class WebSocketController {

    @Autowired
    WSMessageService msgService;
    @Autowired
    WSMessageSender sender;

    @Autowired
    ApplicationContext context;

    @Autowired
    WebSocketConfig webSocketConfig;

    @MessageMapping("/login")
    public void login(RestRequest<WSMessageRequest> regist) {
        WSMessageSender.addReceiver(regist.getParam().getConsumerId());
        RestResult result = new RestResult<WSMsgStatisticInfo>(true, "OK", "OK",
                msgService.getStatisticInfo(regist.getParam()));
        sender.broadcast(
                webSocketConfig.getDestinationPrefixes() + "/system/login/" + regist.getParam().getConsumerId(),
                result);
    }

    @MessageMapping("/read")
    public void notifyRead(RestRequest<WSMessageRequest> request) {
        msgService.notifyRead(request.getParam().getHasReadMsgIds(), request.getParam().getConsumerId());
        RestResult result = new RestResult<WSMsgStatisticInfo>(true, "OK", "OK",
                msgService.getStatisticInfo(request.getParam()));
        sender.broadcast(
                webSocketConfig.getDestinationPrefixes() + "/system/login/" + request.getParam().getConsumerId(),
                result);
    }

    @MessageMapping("/send")
    public void send(RestRequest<WSMessageRequest> request) {
        WSMessageDTO newMessage = msgService.persist(request.getParam());
        context.publishEvent(new WebSocketEvent(Lists.newArrayList(newMessage), webSocketConfig.getEventSource(),
                webSocketConfig.getEventDestination()));
        msgService.updateMsgStatus(Long.parseLong(newMessage.getId()), MessageStatus.SEND.getCode());
    }
}
