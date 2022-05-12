package com.zms.openzone.interact.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.zms.openzone.common.constants.CommunityConstants;

import com.zms.openzone.common.constants.RabbitInfo;
import com.zms.openzone.common.entity.EventEntity;
import com.zms.openzone.common.utils.RedisKeyUtil;
import com.zms.openzone.interact.dao.CommentDao;
import com.zms.openzone.interact.entity.CommentEntity;
import com.zms.openzone.interact.entity.DiscussPostEntity;
import com.zms.openzone.interact.service.CommentService;
import com.zms.openzone.interact.service.DiscussPostService;
import com.zms.openzone.interact.utils.HostHolder;
import com.zms.openzone.interact.utils.SensitiveFilter;
import com.zms.openzone.interact.vo.DiscussDetailVo;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.HtmlUtils;

import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * @author: zms
 * @create: 2022/1/29 13:14
 */
@Service
public class CommentServiceImpl extends ServiceImpl<CommentDao, CommentEntity> implements CommentService {
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private CommentDao commentDao;
    @Autowired
    private DiscussPostService discussPostService;
    @Autowired
    private SensitiveFilter sensitiveFilter;
    @Autowired
    private HostHolder hostHolder;
    @Autowired
    private CommentService commentService;
    @Autowired
    private RabbitTemplate rabbitTemplate;
    //  @Autowired
    //  private UserService userService;
    @Override
    public List<CommentEntity> selectCommentsByEntity(int type, int discussPostId, int offset, int limit) {


        return commentDao.selectCommentsByEntity(type, discussPostId, offset, limit);
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, propagation = Propagation.REQUIRED)
    public int insertComment(CommentEntity commentEntity,int discussPostId) throws ExecutionException, InterruptedException {
        //添加评论--事务操作
        //1.添加评论记录
        commentEntity.setContent(HtmlUtils.htmlEscape(commentEntity.getContent()));
        commentEntity.setContent(sensitiveFilter.filter(commentEntity.getContent()));
        int rows = baseMapper.insert(commentEntity);
        //2.如果评论的是帖子，则帖子的评论数修改
        if (commentEntity.getEntityType() == CommunityConstants.CommenttypeEnum.COMMENT.getCode()) {
            discussPostService.updatePostCommentCount(commentEntity.getEntityId());
        }

        //评论完成之后，通知对方（利用消息队列）
        EventEntity eventEntity = new EventEntity()
                .setTopic(CommunityConstants.TOPIC_COMMENT)
                .setUserId(hostHolder.getUser().getId())
                .setEntityId(commentEntity.getEntityId())
                .setEntityType(commentEntity.getEntityType())
                .setMap("postId",discussPostId);
      //  如果评论的目标是评论的话,注入评论的作者id
        if(commentEntity.getEntityType() == CommunityConstants.LikeTypeEnum.COMMENT.getCode()){
            CommentEntity target = commentService.selectCommentById(commentEntity.getEntityId()); //根据被评论的id 查询被评论的对象实体
            eventEntity.setEntityUserId(target.getUserId());  //获取到被评论的对象实体的作者
        }else if(commentEntity.getEntityType() == CommunityConstants.LikeTypeEnum.DISCUSS.getCode()){//如果评论的目标是帖子的话，注入帖子的主人id
          DiscussDetailVo target =  discussPostService.findDiscussPostById(commentEntity.getEntityId());
            eventEntity.setEntityUserId(target.getUserEntity().getId());
        }
       // 生产者处理并发出消息
        //(String exchange, String routingKey, Object object)
        rabbitTemplate.convertAndSend(RabbitInfo.Interact.exchange,RabbitInfo.Interact.commentRoutingKey,eventEntity);
      //  如果评论帖子，需要计算该帖子的新分数（加入到redis中，等待定时任务进行计算）
        //并且需要通过触发发帖事件，来更新es中的数据（评论数变化了）
//        if(commentEntity.getEntityType() == CommunityConstants.LikeTypeEnum.DISCUSS.getCode()){
//            String key = RedisKeyUtil.DISCUSSPOST_SCORE;
//            redisTemplate.opsForSet().add(key,commentEntity.getEntityId());
//            //再次触发发帖事件
//             eventEntity = new EventEntity().setTopic(CommunityConstants.TOPIC_PUBLISH)
//                    .setEntityId(discussPostId);
//           // eventProducer.fireEvent(eventEntity);
//        }




        return rows;
    }

    //根据评论的id获取该评论实体
    @Override
    public CommentEntity selectCommentById(int entityId) {
        return baseMapper.selectById(entityId);
    }


}
