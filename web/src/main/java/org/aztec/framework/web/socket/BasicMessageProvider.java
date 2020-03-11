package org.aztec.framework.web.socket;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.aztec.framework.core.utils.JsonUtils;
import org.aztec.framework.web.socket.entity.WSConsumerQO;
import org.aztec.framework.web.socket.entity.WSMessage;
import org.aztec.framework.web.socket.entity.WSMessageConsumer;
import org.aztec.framework.web.socket.entity.WSMessageConsumerDTO;
import org.aztec.framework.web.socket.entity.WSMessageDTO;
import org.aztec.framework.web.socket.entity.WSMessageQO;
import org.aztec.framework.web.socket.entity.WSMessageRequest;
import org.aztec.framework.web.socket.entity.WSMsgStatisticInfo;
import org.aztec.framework.web.socket.mapper.BasicWSMessageConsumerMapper;
import org.aztec.framework.web.socket.mapper.BasicWSMessageMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * Web socket信息 bean 工厂
 * 
 * @author liming
 *
 */
@Component
public class BasicMessageProvider implements WSMessageService {

    @Autowired
    BasicWSMessageMapper<WSMessage> messageMapper;
    @Autowired
    BasicWSMessageConsumerMapper<WSMessageConsumer> consumerMapper;

    /**
     * 获取消息提供者接口
     * 
     * @param type
     * @param source
     * @param consumers
     * @param topic
     * @param title
     * @param contents
     * @return
     */
    public WSMessage getMesage(DestinationType type, String source, String[] consumers, String topic, String title,
            String content) {

        if (content == null || content.isEmpty()) {
            throw new IllegalArgumentException("Can't send empty msg!");
        }
        if (source.isEmpty()) {
            throw new IllegalArgumentException("The source ID can't be empty!");
        }
        if (type == null) {
            throw new IllegalArgumentException("The destination can't be null!");
        }
        if (type.equals(DestinationType.BROADCAST) && (topic == null || topic.isEmpty())) {
            throw new IllegalArgumentException("The destination is BROADCAST,but the topic  is empty!");
        }
        if (!type.equals(DestinationType.BROADCAST) && (consumers == null || consumers.length == 0)) {
            throw new IllegalArgumentException("The destination is " + type + ",but consumer list is empty!");
        }
        // return new PlainTextMessage(type, content, consumerList, msgSource,
        // title, topic);
        return new WSMessage();
    }

    public Map<Long, WSMessageConsumerDTO> groupByMessageId(List<WSMessageConsumer> participants) {
        Map<Long, WSMessageConsumerDTO> retMap = Maps.newHashMap();
        participants.stream().forEach(consumer -> {
            WSMessageConsumerDTO dto = new WSMessageConsumerDTO();
            BeanUtils.copyProperties(consumer, dto);
            retMap.put(consumer.getMsgId(), dto);
        });
        return retMap;
    }

    @Override
    public List<WSMessageDTO> findMessage(WSMessageRequest messageQO) {
        WSConsumerQO consumerQO = new WSConsumerQO();
        consumerQO.setReceiverId(messageQO.getConsumerId());
        List<WSMessageConsumer> consumers = consumerMapper.query(consumerQO);
        List<WSMessageDTO> messageList = Lists.newArrayList();
        List<Long> readMsgIds = Lists.newArrayList();
        for (WSMessageConsumer consumer : consumers) {
            if (consumer.getStatus().equals(ReadStatus.READ.getCode())) {
                readMsgIds.add(consumer.getMsgId());
            }
        }
        // 读取已读消息
        if (consumers.size() > 0 && !messageQO.getUnreadOnly()) {
            Map<Long, WSMessageConsumerDTO> groups = groupByMessageId(consumers);
            WSMessageQO qo = new WSMessageQO();
            qo.setIds(readMsgIds);
            List<WSMessage> messages = messageMapper.query(qo);
            messages.forEach(message -> {
                WSMessageDTO dto = new WSMessageDTO();
                BeanUtils.copyProperties(message, dto);
                dto.setId(message.getId());
                WSMessageConsumerDTO c = groups.get(message.getId());
                dto.setReadStatus(c.getStatus());
                messageList.add(dto);
            });
        }
        //获取私信
        List<WSMessage> allSecretMsgs = messageMapper.getSecretMessages(readMsgIds.size() >0? readMsgIds : null);
        allSecretMsgs.forEach(msg -> {
            WSMessageDTO dto = new WSMessageDTO();
            dto.setReadStatus(ReadStatus.UNREAD.getCode());
            BeanUtils.copyProperties(msg, dto);
            dto.setId(msg.getId());
            messageList.add(dto);
        });

        //获取未读主题 消息
        List<WSMessage> messages = messageMapper.getUnreadTopicMessage(readMsgIds.size() > 0 ? readMsgIds : null);
        messages.forEach(message -> {
            WSMessageDTO dto = new WSMessageDTO();
            dto.setReadStatus(ReadStatus.UNREAD.getCode());
            BeanUtils.copyProperties(message, dto);
            dto.setId(message.getId());
            messageList.add(dto);
        });
        return messageList;
    }

    @Override
    @Transactional
    public WSMessageDTO persist(WSMessageRequest request) {
        // TODO Auto-generated method stub
        WSMessage message = new WSMessage();
        BeanUtils.copyProperties(request, message);
        if (request.getSourceObj() != null) {
            message.setSourceInfo(JsonUtils.obj2String(request.getSourceObj()));
        }
        message.setStatus(1);
        message.setCreateTime(new Date());
        message.setUpdateTime(new Date());
        messageMapper.insert(message);
        List<WSMessageConsumerDTO> consumerList = Lists.newArrayList();
        if (request.getConsumerIds() != null && request.getConsumerIds().size() > 0) {
            List<String> consumerIds = request.getConsumerIds();
            for (int i = 0; i < consumerIds.size(); i++) {
                WSMessageConsumer consumer = new WSMessageConsumer();
                consumer.setMsgId(message.getId());
                consumer.setReceiverId(consumerIds.get(i));
                consumer.setStatus(ReadStatus.UNREAD.getCode());
                consumer.setCreateTime(new Date());
                consumer.setUpdateTime(new Date());
                consumer.setStatus(ReadStatus.UNREAD.getCode());
                consumerMapper.insert(consumer);
                WSMessageConsumerDTO consumerDTO = new WSMessageConsumerDTO();
                BeanUtils.copyProperties(consumer, consumerDTO);
                consumerList.add(consumerDTO);
            }
        }
        WSMessageDTO newMessage = new WSMessageDTO();
        BeanUtils.copyProperties(message, newMessage);
        newMessage.setId(message.getId());
        return newMessage;
    }

    @Override
    public int updateMsgStatus(Long msgId, Integer status) {
        WSMessage message = messageMapper.selectByPrimaryKey(msgId);
        if (message == null) {
            return 0;
        } else {
            WSMessageQO qo = new WSMessageQO();
            qo.setId(msgId);
            qo.setOldStatus(message.getStatus());
            qo.setStatus(status);
            return messageMapper.updateStatus(qo);
        }
    }

    @Override
    @Transactional
    public int notifyRead(List<Long> msgIds, String consumerId) {
        WSConsumerQO query = new WSConsumerQO();
        query.setIds(msgIds);
        List<WSMessageConsumer> consumers = consumerMapper.query(query);
        List<Long> missIds = Lists.newArrayList();
        missIds.addAll(msgIds);
        int count = 0;
        if (consumers != null) {
            for (WSMessageConsumer consumer : consumers) {
                consumer.setStatus(ReadStatus.READ.getCode());
                consumer.setUpdateTime(new Date());
                count += consumerMapper.updateByPrimaryKey(consumer);
                missIds.remove(consumer.getMsgId());
            }
            ;
        }
        for (Long missId : missIds) {
            WSMessageConsumer newData = new WSMessageConsumer();
            newData.setMsgId(missId);
            newData.setReceiverId(consumerId);
            newData.setCreateTime(new Date());
            newData.setUpdateTime(new Date());
            newData.setStatus(ReadStatus.READ.getCode());
            count += consumerMapper.insert(newData);
        }
        return 0;
    }

    private List<WSMessage> getSecretMsgs(List<WSMessage> secretMsgs, String receiverId, boolean unreadOnly,
            List<WSMessageConsumer> consumerList) {

        List<WSMessage> unreadSecretIds = Lists.newArrayList();
        if (secretMsgs.size() != 0) {
            WSConsumerQO qo = new WSConsumerQO();
            qo.setReceiverId(receiverId);
            consumerList.forEach(consumer -> {
                secretMsgs.forEach(msg -> {
                    if (msg.getId().equals(consumer.getMsgId())
                            && consumer.getStatus().equals(ReadStatus.UNREAD.getCode())) {
                        unreadSecretIds.add(msg);
                    }
                });
            });

        }
        return unreadSecretIds;
    }

    @Override
    public WSMsgStatisticInfo getStatisticInfo(WSMessageRequest messageQO) {

        if(messageQO.getUnreadOnly() == null){
            messageQO.setUnreadOnly(true);
        }
        List<WSMessageDTO> msg = findMessage(messageQO);
        List<WSMessageDTO> unreadMsg = Lists.newArrayList();
        Long titleCount = messageMapper.countTitleMessage();
        Long readTitleCount = 0l;
        Long secretCount = 0l;
        Long readSecretCount = 0l;
        String userInfo = null;
        for (WSMessageDTO dto : msg) {
            DestinationType dType = DestinationType.getType(dto.getMsgType());
            switch (dType) {
            case BROADCAST:
                if (dto.getReadStatus() == ReadStatus.READ.getCode()) {
                    readTitleCount++;
                }
                break;
            case MULTIPLE:
                secretCount++;
                if (dto.getReadStatus() == ReadStatus.READ.getCode()) {
                    readSecretCount++;
                }
                break;
            case SINGLE:
                secretCount++;
                if (dto.getReadStatus() == ReadStatus.READ.getCode()) {
                    readSecretCount++;
                }
                break;
            default:
                break;

            }
            if (dto.getReadStatus() == ReadStatus.UNREAD.getCode()) {
                unreadMsg.add(dto);
            }
        }
        Long unreadSecretCount = secretCount - readSecretCount;
        Long unreadTitleCount = titleCount - readTitleCount;
        Long unreadCount = unreadSecretCount + unreadTitleCount;
        Long totalCount = secretCount + titleCount;

        return new WSMsgStatisticInfo(unreadCount, totalCount, titleCount, unreadTitleCount, secretCount,
                unreadSecretCount, userInfo, unreadMsg);
    }

}
