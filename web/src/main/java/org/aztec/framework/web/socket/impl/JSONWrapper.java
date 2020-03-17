package org.aztec.framework.web.socket.impl;

import org.aztec.framework.api.rest.entity.WSMessageDTO;
import org.aztec.framework.web.socket.MessageWrapper;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

@Component
public class JSONWrapper implements MessageWrapper {

    @Override
    public String wrap(WSMessageDTO message) {
        JSONObject sendObj = new JSONObject();
        sendObj.put("content", message.getContent());
        sendObj.put("topic", message.getTopic());
        sendObj.put("title", message.getTitle());
        sendObj.put("source", message.getSourceInfo());
        sendObj.put("sourceId", message.getSourceInfo());
        return JSON.toJSONString(sendObj);
    }

}
