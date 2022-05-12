package com.zms.openzone.interact.service.impl;

import com.zms.openzone.common.constants.CommunityConstants;
import com.zms.openzone.common.constants.RabbitInfo;
import com.zms.openzone.common.entity.EventEntity;

import com.zms.openzone.interact.service.LikeService;
import com.zms.openzone.interact.utils.RedisKeyUtil;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;

/**
 * @author: zms
 * @create: 2022/2/7 20:11  pushtest
 */
@Service
public class LikeServiceImpl implements LikeService {
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Override
    public void like(int userId, int entityType, int entityId, int entityUserId,int postId) {
        //检查是否相应的实体赞的set中包含当前用户的id ，来决定是点赞还是取消赞
//        String entityLikeKey = RedisKeyUtil.getEntityLikeKey(entityType, entityId);
//        Boolean ismember = redisTemplate.opsForSet().isMember(entityLikeKey, userId);
//        //执行取消点赞的操作
//        if(ismember){
//            redisTemplate.opsForSet().remove(entityLikeKey,userId);
//        }else{
//            redisTemplate.opsForSet().add(entityLikeKey,userId);
//        }

        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                String entityLikeKey = RedisKeyUtil.getEntityLikeKey(entityType, entityId);
                String userLikeKey = RedisKeyUtil.getUserLikeKey(entityUserId);
                Boolean ismember = redisTemplate.opsForSet().isMember(entityLikeKey, userId);
                operations.multi();
                //执行取消/点赞的操作
                if (ismember) {

                    operations.opsForSet().remove(entityLikeKey, userId);
                    operations.opsForValue().decrement(userLikeKey);
                } else {
                    operations.opsForSet().add(entityLikeKey, userId);
                    operations.opsForValue().increment(userLikeKey);
                    //点赞之后通知对方

                }
                operations.exec();
                return null;
            }
        });

        //点赞完毕，通知对方

            EventEntity eventEntity = new EventEntity().setTopic(CommunityConstants.TOPIC_LIKE)
                    .setUserId(userId)
                    .setEntityId(entityId)
                    .setEntityType(entityType)
                    .setEntityUserId(entityUserId)
                    .setMap("postId",postId);
        //(String exchange, String routingKey, Object message, MessagePostProcessor messagePostProcessor, @Nullable CorrelationData correlationData)
        //发送消息给消息队列
        rabbitTemplate.convertAndSend(RabbitInfo.Interact.exchange,RabbitInfo.Interact.likeRoutingKey,eventEntity);
    }

    @Override
    public long findEntityLikeCount(int entityType, int entityId) {
        String entityLikeKey = RedisKeyUtil.getEntityLikeKey(entityType, entityId);
        Long size = redisTemplate.opsForSet().size(entityLikeKey);
        return size;
    }

    @Override
    public Integer findEntityLikeStatus(int userId, int entityType, int entityId) {
        String entityLikeKey = RedisKeyUtil.getEntityLikeKey(entityType, entityId);
        Boolean ismember = redisTemplate.opsForSet().isMember(entityLikeKey, userId);
        //执行取消点赞的操作
        if (ismember) {  //1表示已赞
            return 1;
        } else {
            return 0;//0表示未赞
        }


    }

    //查询某个用户获取的全部的赞
    @Override
    public Integer findUserLikeCount(int userId) {
        String userLikekey = RedisKeyUtil.getUserLikeKey(userId);
        Integer count = (Integer) redisTemplate.opsForValue().get(userLikekey);
        return count == null ? 0 : count;
    }
}
