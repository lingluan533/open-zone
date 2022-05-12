package com.zms.openzone.interact.controller;


import com.zms.openzone.interact.entity.CommentEntity;
import com.zms.openzone.interact.entity.DiscussPostEntity;

import com.zms.openzone.interact.service.CommentService;
import com.zms.openzone.interact.service.DiscussPostService;
import com.zms.openzone.interact.utils.HostHolder;
import com.zms.openzone.interact.utils.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Date;
import java.util.concurrent.ExecutionException;

/**
 * @author: zms
 * @create: 2022/1/29 18:19
 */
@Controller
@RequestMapping("/comment")
public class CommentController {

    @Autowired
    private HostHolder hostHolder;
    @Autowired
    private CommentService commentService;


    @RequestMapping("/add/{discussPostId}")
    public String addComment(@PathVariable("discussPostId") int discussPostId, CommentEntity commentEntity) throws ExecutionException, InterruptedException {
        commentEntity.setUserId(hostHolder.getUser().getId());
        commentEntity.setStatus(0);
        commentEntity.setCreateTime(new Date());
        commentService.insertComment(commentEntity,discussPostId);

        //评论完成之后，通知对方（利用kafka消息队列）
//        EventEntity eventEntity = new EventEntity()
//                .setTopic(CommunityConstants.TOPIC_COMMENT)
//                .setUserId(hostHolder.getUser().getId())
//                .setEntityId(commentEntity.getEntityId())
//                .setEntityType(commentEntity.getEntityType())
//                .setMap("postId",discussPostId);
        //如果评论的目标是评论的话,注入评论的作者id
//        if(commentEntity.getEntityType() == CommunityConstants.LikeTypeEnum.COMMENT.getCode()){
//            CommentEntity target = commentService.selectCommentById(commentEntity.getEntityId()); //根据被评论的id 查询被评论的对象实体
//            eventEntity.setEntityUserId(target.getUserId());  //获取到被评论的对象实体的作者
//        }else if(commentEntity.getEntityType() == CommunityConstants.LikeTypeEnum.DISCUSS.getCode()){//如果评论的目标是帖子的话，注入帖子的主人id
//          DiscussPostEntity target =  discussPostService.findDiscussPostById(commentEntity.getEntityId());
//            eventEntity.setEntityUserId(target.getUserId());
//        }
        //生产者处理并发出消息
        // eventProducer.fireEvent(eventEntity);
        //如果评论帖子，需要计算该帖子的新分数（加入到redis中，等待定时任务进行计算）
        //并且需要通过触发发帖事件，来更新es中的数据（评论数变化了）
//        if(commentEntity.getEntityType() == CommunityConstants.LikeTypeEnum.DISCUSS.getCode()){
//            String key = RedisKeyUtil.DISCUSSPOST_SCORE;
//            redisTemplate.opsForSet().add(key,commentEntity.getEntityId());
//            //再次触发发帖事件
//             eventEntity = new EventEntity().setTopic(CommunityConstants.TOPIC_PUBLISH)
//                    .setEntityId(discussPostId);
//            eventProducer.fireEvent(eventEntity);
//        }


        return "redirect:http://interact.openzone.com/discuss/detail/" + discussPostId;
    }

}
