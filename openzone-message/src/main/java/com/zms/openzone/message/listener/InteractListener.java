package com.zms.openzone.message.listener;



import com.alibaba.fastjson.JSONObject;
import com.alibaba.nacos.client.utils.JSONUtils;
import com.rabbitmq.client.Channel;
import com.zms.openzone.common.constants.CommunityConstants;
import com.zms.openzone.common.constants.RabbitInfo;
import com.zms.openzone.common.entity.EventEntity;
import com.zms.openzone.message.entity.MessageEntity;
import com.zms.openzone.message.service.MessageService;
import com.zms.openzone.message.service.impl.MessageServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Map;
import java.util.Set;

/**
 * @author: zms
 * @create: 2022/3/2 17:39
 */
@Slf4j
@Component
public class InteractListener {
    @Autowired
    private MessageService messageService;
//    @RabbitListener(queues = RabbitInfo.Interact.commentQueue)
//    public void listenCommentQueue(EventEntity eventEntity,Message message, Channel channel){
//
//    }
    @RabbitListener(queues = {RabbitInfo.Interact.likeQueue,RabbitInfo.Interact.commentQueue,RabbitInfo.Interact.followQueue})
    public void listenLikeQueue(EventEntity eventEntity,Message message, Channel channel){
        System.out.println("监听到点赞的消息："+eventEntity);
       // EventEntity eventEntity = JSONObject.parseObject(record.value().toString(), EventEntity.class);
        //event  --->  message
        MessageEntity messageEntity = new MessageEntity();
        messageEntity.setFromId(CommunityConstants.SYSTEM_USER_ID); // fromId为管理员账号
        messageEntity.setToId(eventEntity.getEntityUserId());
        messageEntity.setStatus(0);
        messageEntity.setConversationId(eventEntity.getTopic());
        messageEntity.setCreateTime(new Date());

        Map<String,Object> content = eventEntity.getMap();
        content.put("userId",eventEntity.getUserId());
        content.put("entityType",eventEntity.getEntityType());
        content.put("entityId",eventEntity.getEntityId());
        Set<Map.Entry<String, Object>> entrys = content.entrySet();

        messageEntity.setContent(JSONObject.toJSONString(content));
        messageService.sendMessage(messageEntity);

    }
}
