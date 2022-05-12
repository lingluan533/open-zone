package com.zms.openzone.interact.service.impl;


import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zms.openzone.common.constants.CommunityConstants;
import com.zms.openzone.common.entity.UserEntityVo;
import com.zms.openzone.common.utils.R;
import com.zms.openzone.interact.dao.DiscussPostDao;
import com.zms.openzone.interact.entity.CommentEntity;
import com.zms.openzone.interact.entity.DiscussPostEntity;
import com.zms.openzone.interact.entity.UserEntity;
import com.zms.openzone.interact.feign.UserFeignService;
import com.zms.openzone.interact.service.CommentService;
import com.zms.openzone.interact.service.DiscussPostService;

import com.zms.openzone.interact.service.LikeService;
import com.zms.openzone.interact.to.PostIdAuthorTo;
import com.zms.openzone.interact.utils.HostHolder;
import com.zms.openzone.interact.utils.PageUtils;
import com.zms.openzone.interact.vo.CommentVo;
import com.zms.openzone.interact.vo.DiscussDetailVo;
import com.zms.openzone.interact.vo.DiscussPostVo;
import com.zms.openzone.interact.vo.ReplyVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

/**
 * @author: zms
 * @create: 2022/1/18 23:44
 */
@Service
public class DiscussPostServiceImpl extends ServiceImpl<DiscussPostDao, DiscussPostEntity> implements DiscussPostService {
    @Autowired
    DiscussPostDao discussPostDao;
    @Autowired
    private UserFeignService userFeignService;
    @Autowired
    ThreadPoolExecutor executor;
    @Autowired
    private LikeService likeService;
    @Autowired
    private HostHolder hostHolder;
    @Autowired
    private CommentService commentService;

    @Override
    public List<DiscussPostVo> findDiscussPosts(int userId, int offset, int limit, int orderMode) throws ExecutionException, InterruptedException {
        List<DiscussPostVo> res = new ArrayList<>();
        //异步编排
        //1.查询分页的基本信息
        CompletableFuture<List<DiscussPostEntity>> postFuture = CompletableFuture.supplyAsync(() -> {
            List<DiscussPostEntity> discussPostEntities = discussPostDao.selectDiscussPosts(userId, offset, limit, orderMode);
            discussPostEntities.stream().forEach((item) -> {
                DiscussPostVo discussPostVo = new DiscussPostVo();
                discussPostVo.setDiscussPostEntity(item);
                res.add(discussPostVo);

            });
            return discussPostEntities;
        }, executor);
        CompletableFuture<Void> userFuture = postFuture.thenAcceptAsync((discussPostEntities) -> {
            //2.远程查询作者信息并封装
            List<Integer[]> userids = discussPostEntities.stream().map(post -> {
                return new Integer[]{post.getId(), post.getUserId()};
            }).collect(Collectors.toList());
            //远程调用获取文章的作者信息
            R r = userFeignService.getUserEntitysByPostIDs(userids);
            if (r.getCode() == 0) {
                Map<Integer, UserEntity> postIdAuthorTos = r.getData(new TypeReference<Map<Integer, UserEntity>>() {
                });
                res.stream().forEach((item) -> {
                    item.setUserEntity(postIdAuthorTos.get(item.getDiscussPostEntity().getId()));
                });
            }
        }, executor);
        //3.封装点赞信息
        CompletableFuture<Void> likeFuture = postFuture.thenAcceptAsync((discussPostEntities) -> {
            res.stream().forEach((postEntity) -> {
                long entityLikeCount = likeService.findEntityLikeCount(CommunityConstants.LikeTypeEnum.DISCUSS.getCode(), postEntity.getDiscussPostEntity().getId());
                postEntity.setLikeCount(entityLikeCount);
            });


        }, executor);
        //【阻塞】等待所有任务完成
        CompletableFuture.allOf(postFuture, userFuture, likeFuture).get();
        return res;
    }

    @Override
    public int findDiscussPostRows(int userId) {

        return discussPostDao.selectDiscussPostRows(userId);
    }

    @Override
    public void addDiscussPost(DiscussPostEntity postEntity) {
        baseMapper.insert(postEntity);
    }

//    @Override
//    public void addDiscussPost(int id, String title, String content) {
//        DiscussPostEntity discussPostEntity = new DiscussPostEntity();
//        discussPostEntity.setUserId(id);
//        discussPostEntity.setTitle(title);
//        discussPostEntity.setContent(content);
//        discussPostEntity.setCreateTime(new Date());
//
//    }

    @Override
    public DiscussDetailVo findDiscussPostById(int discussPostId) throws ExecutionException, InterruptedException {
        //查询一个帖子的详细信息，封装
        // 帖子实体
        // 作者
        // 当前用户对帖子的点赞信息
        //帖子点赞数量
        //帖子的评论信息 (分页)

        /*异步编排*/
        UserEntityVo curUser = hostHolder.getUser();
        DiscussDetailVo result = new DiscussDetailVo();
        //1.封装帖子实体
        CompletableFuture<DiscussPostEntity> postEntityFuture = CompletableFuture.supplyAsync(() -> {
            DiscussPostEntity discussPostEntity = baseMapper.selectById(discussPostId);
            result.setPostEntity(discussPostEntity);
            return discussPostEntity;
        }, executor);
        //2.封装作者信息
        CompletableFuture<Void> authorFuture = postEntityFuture.thenAcceptAsync((postEntity) -> {
            R r = userFeignService.getUserEntityByID(postEntity.getUserId());
            if (r.getCode() == 0) {
                UserEntity userEntity = r.getData(new TypeReference<UserEntity>() {
                });
                result.setUserEntity(userEntity);
            }
        }, executor);
        //3.封装当前用户的点赞信息
        CompletableFuture<Void> likestatusFuture = CompletableFuture.runAsync(() -> {
            int entityLikeStatus = curUser == null ? 0 : likeService.findEntityLikeStatus(curUser.getId(), CommunityConstants.LikeTypeEnum.DISCUSS.getCode(), discussPostId);
            result.setLikeStatus(entityLikeStatus);
        }, executor);

        //4.封装帖子点赞数量
        CompletableFuture<Void> likecountFuture = CompletableFuture.runAsync(() -> {
            long entityLikeCount = likeService.findEntityLikeCount(CommunityConstants.LikeTypeEnum.DISCUSS.getCode(), discussPostId);
            result.setLikeCount(entityLikeCount);
        }, executor);

        //5.封装帖子的评论信息 (分页)
        CompletableFuture<Void> commentsFuture = postEntityFuture.thenAcceptAsync((discussPostEntity) -> {
            //评论分页数目
            PageUtils page = new PageUtils();
            page.setLimit(5);
            page.setPath("/discuss/detail/" + discussPostId);
            page.setRows(discussPostEntity.getCommentCount());
            //封装分页信息
            result.setPage(page);
            //封装评论的信息
            List<CommentEntity> commentEntities = commentService.selectCommentsByEntity(CommunityConstants.CommenttypeEnum.COMMENT.getCode(), discussPostId, page.getOffset(), page.getLimit());
            //System.out.println(commentEntities);
            //1.封装评论的用户信息 以及评论本身实体
            List<CommentVo> commentVos = commentEntities.stream().map((item) -> {
                CommentVo commentVo = new CommentVo();
                commentVo.setCommentEntity(item);
                //封装评论的作者
                R rr = userFeignService.getUserEntityByID(item.getUserId());
                if (rr.getCode() == 0) {
                    UserEntity userEntity = rr.getData(new TypeReference<UserEntity>() {
                    });
                    commentVo.setUserEntity(userEntity);
                }


                //封装评论的点赞状态以及点赞数量
                int commentLikeStatus = curUser == null ? 0 : likeService.findEntityLikeStatus(curUser.getId(), CommunityConstants.LikeTypeEnum.COMMENT.getCode(), item.getId());
                commentVo.setLikeStatus(commentLikeStatus);
                //封装评论的点赞数量
                long likeCount = likeService.findEntityLikeCount(CommunityConstants.LikeTypeEnum.COMMENT.getCode(), item.getId());
                commentVo.setLikeCount(likeCount);

                //2.封装每条评论的回复
                List<CommentEntity> replys = commentService.selectCommentsByEntity(CommunityConstants.CommenttypeEnum.REPLY.getCode(), item.getId(), 0, Integer.MAX_VALUE);
                //如果评论下面有评论就封装
                if (replys != null) {
                    List<ReplyVo> replyVoList = replys.stream().map((reply) -> {
                        ReplyVo replyVo = new ReplyVo();
                        replyVo.setCommentEntity(reply);
                        R r = userFeignService.getUserEntityByID(reply.getUserId());
                        if (r.getCode() == 0) {
                            UserEntity userEntity = r.getData(new TypeReference<UserEntity>() {
                            });
                            replyVo.setReplyFrom(userEntity);
                        }
                        if (reply.getTargetId() != 0) {
                            r = userFeignService.getUserEntityByID(reply.getTargetId());
                            if (r.getCode() == 0) {
                                UserEntity userEntity = r.getData(new TypeReference<UserEntity>() {
                                });
                                replyVo.setReplyTo(userEntity);
                            }
                        }
                        //封装评论的点赞状态以及点赞数量
                        int replyLikeStatus = curUser == null ? 0 : likeService.findEntityLikeStatus(curUser.getId(), CommunityConstants.LikeTypeEnum.COMMENT.getCode(), reply.getId());
                        replyVo.setLikeStatus(replyLikeStatus);
                        //封装评论的点赞数量
                        long replylikeCount = likeService.findEntityLikeCount(CommunityConstants.LikeTypeEnum.COMMENT.getCode(), reply.getId());
                        replyVo.setLikeCount(replylikeCount);
                        return replyVo;
                    }).collect(Collectors.toList());
                    commentVo.setReplys(replyVoList);
                    commentVo.setReplyCount(replys.size());
                }
                return commentVo;
            }).collect(Collectors.toList());
            System.out.println(commentVos);

            result.setCommentVos(commentVos);
        }, executor);

        CompletableFuture<Void> allOf = CompletableFuture.allOf(postEntityFuture, authorFuture, likestatusFuture, likecountFuture, commentsFuture);
        allOf.get();
        return result;
    }

    @Override
    public void updatePostCommentCount(int entityId) {
        DiscussPostEntity discussPostEntity = baseMapper.selectById(entityId);
        discussPostEntity.setCommentCount(discussPostEntity.getCommentCount() + 1);
        baseMapper.updateById(discussPostEntity);
    }

    @Override
    public List<DiscussPostEntity> findAllPosts() {
        return baseMapper.selectList(new QueryWrapper<DiscussPostEntity>());
    }

    //修改状态
    @Override
    public void updateType(int id, int i) {
        DiscussPostEntity discussPostEntity = baseMapper.selectById(id);
        discussPostEntity.setType(i);
        baseMapper.updateById(discussPostEntity);
    }

    //加精
    @Override
    public void updateStatus(int id, int i) {
        DiscussPostEntity discussPostEntity = baseMapper.selectById(id);
        discussPostEntity.setStatus(i);
        baseMapper.updateById(discussPostEntity);
    }

    @Override
    public void updateScore(DiscussPostEntity post) {
        baseMapper.updateById(post);
    }


}
