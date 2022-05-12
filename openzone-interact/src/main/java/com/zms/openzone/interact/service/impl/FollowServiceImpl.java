package com.zms.openzone.interact.service.impl;


import com.alibaba.fastjson.TypeReference;
import com.zms.openzone.common.constants.CommunityConstants;
import com.zms.openzone.common.constants.RabbitInfo;
import com.zms.openzone.common.entity.EventEntity;
import com.zms.openzone.common.entity.UserEntityVo;
import com.zms.openzone.common.utils.R;
import com.zms.openzone.interact.feign.UserFeignService;
import com.zms.openzone.interact.service.FollowService;
import com.zms.openzone.interact.utils.HostHolder;
import com.zms.openzone.interact.utils.RedisKeyUtil;
import com.zms.openzone.interact.vo.FollowVO;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author: zms
 * @create: 2022/2/8 15:16
 */
@Service
public class FollowServiceImpl implements FollowService {
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private UserFeignService userFeignService;
    @Autowired
    private HostHolder hostHolder;
    @Autowired
    private RabbitTemplate rabbitTemplate;
    //查询用户1是否已经关注另一个用户2  返回0表示未关注 1表示已关注
    @Override
    public int hasFollowed(int userId1, int entityType, int userId2) {
        String followeekey = RedisKeyUtil.getFolloweeKey(userId1, entityType);
        return redisTemplate.opsForZSet().score(followeekey, userId2) == null ? 0 : 1;

    }

    //关注
    @Override
    public void follow(int id, int entityType, int entityId) {

        Object execute = redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                //A关注B
                //构建A的关注列表key followeeKey
                //构建B的粉丝列表key followerKey
                String followerKey = RedisKeyUtil.getFollowerKey(entityId, CommunityConstants.LikeTypeEnum.USER.getCode());
                String followeeKey = RedisKeyUtil.getFolloweeKey(id, CommunityConstants.LikeTypeEnum.USER.getCode());
                operations.multi();
                operations.opsForZSet().add(followeeKey, entityId, System.currentTimeMillis());
                operations.opsForZSet().add(followerKey, id, System.currentTimeMillis());

                return operations.exec();
            }
        });
        //关注消息入队列
        EventEntity eventEntity  = new EventEntity().setTopic(CommunityConstants.TOPIC_FOLLOW)
                .setUserId(hostHolder.getUser().getId())
                .setEntityId(entityId)
                .setEntityType(entityType)
                .setEntityUserId(entityId);
        //发送到消息队列
        rabbitTemplate.convertAndSend(RabbitInfo.Interact.exchange,RabbitInfo.Interact.followRoutingKey,eventEntity);

        // return 0;
    }

    //取消关注
    @Override
    public void unfollow(int id, int entityType, int entityId) {
        Object execute = redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                //A取关B
                //构建A的关注列表key followeeKey
                //构建B的粉丝列表key followerKey
                String followerKey = RedisKeyUtil.getFollowerKey(entityId, CommunityConstants.LikeTypeEnum.USER.getCode());
                String followeeKey = RedisKeyUtil.getFolloweeKey(id, CommunityConstants.LikeTypeEnum.USER.getCode());
                operations.multi();
                operations.opsForZSet().remove(followeeKey, entityId);
                operations.opsForZSet().remove(followerKey, id);
                return operations.exec();
            }
        });
    }


    //查询某个用户关注的数量
    @Override
    public long findFolloweeCount(int userId, int entityType) {
        String followeeKey = RedisKeyUtil.getFolloweeKey(userId, entityType);
        return redisTemplate.opsForZSet().zCard(followeeKey);

    }

    //查询某个用户粉丝数量
    @Override
    public long findFollowerCount(int userId, int entityType) {
        String followerKey = RedisKeyUtil.getFollowerKey(userId, entityType);
        return redisTemplate.opsForZSet().zCard(followerKey);
    }
    //某人的关注列表
    @Override
    public List<FollowVO> findFolloweesByUserId(int entityType, int userId, int offset, int limit) {
        String followeesKey = RedisKeyUtil.getFolloweeKey(userId,entityType);
        Set<Integer> set = redisTemplate.opsForZSet().reverseRange(followeesKey, offset, offset + limit - 1);
        List<FollowVO> res = set.stream().map((id)-> {
            FollowVO followVO = new FollowVO();
            R r = userFeignService.getUserEntityByID(id);
            if(r.getCode() == 0){
                UserEntityVo data = r.getData(new TypeReference<UserEntityVo>() {
                });
                followVO.setFollow(data);
            }
            Double score = redisTemplate.opsForZSet().score(followeesKey, id);
           followVO.setFollowTime(new Date(score.longValue()));
           followVO.setHasFollowed(true);
           return followVO;
                }
        ).collect(Collectors.toList());
        return res;
    }
    //某人的粉丝列表
    @Override
    public List<FollowVO> findFollowersByUserId(int entityType, int userId, int offset, int limit) {
        String followersKey = RedisKeyUtil.getFollowerKey(userId,entityType);
        Set<Integer> set = redisTemplate.opsForZSet().reverseRange(followersKey, offset, offset + limit - 1);
        List<FollowVO> res = set.stream().map((id)->{
            FollowVO followVO = new FollowVO();
            R r = userFeignService.getUserEntityByID(id);
            if(r.getCode() == 0){
                UserEntityVo data = r.getData(new TypeReference<UserEntityVo>() {
                });
                followVO.setFollow(data);
            }
            Double score = redisTemplate.opsForZSet().score(followersKey, id);
            followVO.setFollowTime(new Date(score.longValue()));
            followVO.setHasFollowed(hasFollowed(userId,CommunityConstants.LikeTypeEnum.USER.getCode(), id) == 1? true:false);
            return followVO;
        } ).collect(Collectors.toList());
        return res;
    }
}
