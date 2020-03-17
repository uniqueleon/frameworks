package org.aztec.framework.web.controller;

import org.aztec.framework.api.rest.entity.RestRequest;
import org.aztec.framework.api.rest.entity.RestResult;
import org.aztec.framework.api.rest.entity.WSMessageDTO;
import org.aztec.framework.api.rest.entity.WSMessageRequest;
import org.aztec.framework.api.rest.entity.WSMsgStatisticInfo;
import org.aztec.framework.disconf.items.WebSocketConfig;
import org.aztec.framework.web.socket.MessageStatus;
import org.aztec.framework.web.socket.WSMessageSender;
import org.aztec.framework.web.socket.WSMessageService;
import org.aztec.framework.web.socket.event.WebSocketEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
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
    
    private static final Logger LOG = LoggerFactory.getLogger(WebSocketController.class);

    @MessageMapping("/login")
    public RestResult<WSMsgStatisticInfo> login(RestRequest<WSMessageRequest> regist) {
        WSMessageSender.addReceiver(regist.getParam().getConsumerId());
        RestResult<WSMsgStatisticInfo> result = new RestResult<WSMsgStatisticInfo>(true, "OK", "OK",
                msgService.getStatisticInfo(regist.getParam()));
        sender.broadcast(
                webSocketConfig.getDestinationPrefixes() + "/system/login/" + regist.getParam().getConsumerId(),
                result);
        return result;
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
    public RestResult<Boolean> send(RestRequest<WSMessageRequest> request) {
        WSMessageDTO newMessage = msgService.persist(request.getParam());
        context.publishEvent(new WebSocketEvent(Lists.newArrayList(newMessage), webSocketConfig.getEventSource(),
                webSocketConfig.getEventDestination()));
        int result = msgService.updateMsgStatus(Long.parseLong(newMessage.getId()), MessageStatus.SEND.getCode());
        return result > 0 ? new RestResult<>(true, "OK", "OK",true) :  new RestResult<>(true, "OK", "OK",false); 
    }
    
    @RequestMapping(value="/websocket/send",method={RequestMethod.POST},consumes={MediaType.APPLICATION_JSON_UTF8_VALUE}
    ,produces={MediaType.APPLICATION_JSON_UTF8_VALUE})
    public RestResult<Boolean> sendByRestful(@RequestBody RestRequest<WSMessageRequest> request){
        LOG.info("send msg by restful client!param:" + JSON.toJSONString(request));
        return send(request);
    }
    
    @RequestMapping(value="/websocket/getStatistic",method={RequestMethod.POST},consumes={MediaType.APPLICATION_JSON_UTF8_VALUE}
    ,produces={MediaType.APPLICATION_JSON_UTF8_VALUE})
    public RestResult<WSMsgStatisticInfo> getStatisInfo(@RequestBody RestRequest<WSMessageRequest> regist){
        LOG.info("get statistic information by restful client!param:" + JSON.toJSONString(regist));
        return  new RestResult<WSMsgStatisticInfo>(true, "OK", "OK",
                msgService.getStatisticInfo(regist.getParam()));
    }
}
