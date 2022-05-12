package com.zms.openzone.interact.controller;


import com.zms.openzone.common.entity.UserEntityVo;

import com.zms.openzone.interact.entity.UserEntity;
import com.zms.openzone.interact.service.LikeService;
import com.zms.openzone.interact.utils.HostHolder;
import com.zms.openzone.interact.utils.R;
import com.zms.openzone.interact.utils.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * @author: zms
 * @create: 2022/2/7 20:05
 */
@RestController
@RequestMapping("like")
public class LikeController {
    @Autowired
    private HostHolder hostHolder;
    @Autowired
    private LikeService likeService;
    //    @Autowired
//   // private EventProducer eventProducer;
//    @Autowired
    private RedisTemplate redisTemplate;


    @GetMapping("/likecount")
    @ResponseBody
    public R getEntityLikeCount(@RequestParam int entityType, @RequestParam int entityId) {
        //查询实体赞的数量
        long likeCount = likeService.findEntityLikeCount(entityType, entityId);
        return R.ok().setData(likeCount);
    }

    @GetMapping("/userLikeCount")
    @ResponseBody
    public R findUserLikeCount(@RequestParam int userId) {
        Integer userLikeCount = likeService.findUserLikeCount(userId);
        return R.ok().setData(userLikeCount);
    }


    @PostMapping("/like")
    @ResponseBody
    public R likeEntity(int entityType, int entityId, int entityUserId, int postId) {
        UserEntityVo userEntity = hostHolder.getUser();
        //点赞/取消赞
        likeService.like(userEntity.getId(), entityType, entityId, entityUserId,postId);
        //查询实体赞的数量
        long likeCount = likeService.findEntityLikeCount(entityType, entityId);
        //当前操作完毕查询点赞状态
        int likeStatus = likeService.findEntityLikeStatus(userEntity.getId(), entityType, entityId);
        //点赞完毕，通知对方
//        if(likeStatus == 1){
//            EventEntity eventEntity = new EventEntity().setTopic(CommunityConstants.TOPIC_LIKE)
//                    .setUserId(userEntity.getId())
//                    .setEntityId(entityId)
//                    .setEntityType(entityType)
//                    .setEntityUserId(entityUserId)
//                    .setMap("postId",postId);
//            eventProducer.fireEvent(eventEntity);
//        }

        //如果点赞帖子，需要计算该帖子的新分数（加入到redis中，等待定时任务进行计算）
//        if(entityType == CommunityConstants.LikeTypeEnum.DISCUSS.getCode()){
//            String key = RedisKeyUtil.DISCUSSPOST_SCORE;
//            redisTemplate.opsForSet().add(key,entityId);
//        }

        return R.ok().put("likeCount", likeCount).put("likeStatus", likeStatus);
    }


}
