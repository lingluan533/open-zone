package com.zms.openzone.interact.controller;


import com.zms.openzone.common.constants.CommunityConstants;
import com.zms.openzone.common.entity.EventEntity;
import com.zms.openzone.common.entity.UserEntityVo;
import com.zms.openzone.interact.entity.DiscussPostEntity;
import com.zms.openzone.interact.service.CommentService;
import com.zms.openzone.interact.service.DiscussPostService;
import com.zms.openzone.interact.service.LikeService;
import com.zms.openzone.interact.utils.HostHolder;
import com.zms.openzone.interact.utils.PageUtils;
import com.zms.openzone.interact.utils.R;
import com.zms.openzone.interact.utils.SensitiveFilter;
import com.zms.openzone.interact.vo.CommentVo;
import com.zms.openzone.interact.vo.DiscussDetailVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.HtmlUtils;

import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

/**
 * @author: zms
 * @create: 2022/1/28 19:48
 */
@Controller
@RequestMapping("/discuss")
public class DisscussController {
    @Autowired
    private HostHolder hostHolder;
    @Autowired
    private SensitiveFilter sensitiveFilter;
    @Autowired
    private DiscussPostService discussPostService;
    //    @Autowired
//    private UserService userService;
    @Autowired
    private CommentService commentService;
    //    @Autowired
//    private EventProducer eventProducer;
    @Autowired
    private LikeService likeService;

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    @ResponseBody
    public R addDiscuss(String title, String content) {

        if (hostHolder.getUser() == null) {
            return R.error(403, "尚未登录！");
        }
        //转义HTML标记
        title = HtmlUtils.htmlEscape(title);
        content = HtmlUtils.htmlEscape(content);
        System.out.println("内容：" + content);
        //过滤敏感词
        title = sensitiveFilter.filter(title);
        content = sensitiveFilter.filter(content);
        DiscussPostEntity post = new DiscussPostEntity();
        post.setUserId(hostHolder.getUser().getId());
        post.setTitle(title);
        post.setContent(content);
        post.setCreateTime(new Date());
        discussPostService.addDiscussPost(post);

        //触发发帖事件
//        EventEntity eventEntity = new EventEntity().setTopic(CommunityConstants.TOPIC_PUBLISH)
//                .setUserId(hostHolder.getUser().getId())
//                .setEntityId(post.getId())  /*这里虽然没有显示的设置post的id，但是由于调用service层的插入的时候直接传的参数时post实体，所以
//                框架在执行完数据库的插入操作之后会自动地把id设置回参数post 省去了我们手动去查询刚刚插入post的id的过程！！
//                */
//                .setEntityType(CommunityConstants.LikeTypeEnum.DISCUSS.getCode());
//        eventProducer.fireEvent(eventEntity);
        return R.ok("帖子发布成功！");
    }

    //帖子详情页面
    @RequestMapping(value = "/detail/{discussPostId}", method = RequestMethod.GET)
    //@ResponseBody
    public String getPostContent(@PathVariable("discussPostId") int discussPostId, Model model, PageUtils page) throws ExecutionException, InterruptedException {
        UserEntityVo userEntity = hostHolder.getUser();
        DiscussDetailVo detailVo = discussPostService.findDiscussPostById(discussPostId);

        model.addAttribute("detailVo", detailVo);
        // model.addAttribute("post",discussPostEntity);
        //UserEntity userById = userService.findUserById(discussPostEntity.getUserId());
        // model.addAttribute("user",userById);

        //封装当前用户对该帖子的点赞状态
//        int entityLikeStatus = userEntity == null ? 0 :  likeService.findEntityLikeStatus(userEntity.getId(), CommunityConstants.LikeTypeEnum.DISCUSS.getCode(), discussPostId);
//        model.addAttribute("likeStatus",entityLikeStatus);
//        //封装该帖子的点赞数量
//        long entityLikeCount = likeService.findEntityLikeCount(CommunityConstants.LikeTypeEnum.DISCUSS.getCode(), discussPostId);
//        model.addAttribute("likeCount",entityLikeCount);
//        //评论分页数目
//        page.setLimit(5);
//        page.setPath("/discuss/detail/" + discussPostId);
//        page.setRows(discussPostEntity.getCommentCount());
//        //封装评论的信息
//       List<CommentEntity> commentEntities =  commentService.selectCommentsByEntity(CommunityConstants.CommenttypeEnum.COMMENT.getCode(),discussPostId,page.getOffset(),page.getLimit());
//      //System.out.println(commentEntities);
//        //1.封装评论的用户信息 以及评论本身实体
//      List<CommentVo> commentVos =   commentEntities.stream().map((item)->{
//                CommentVo commentVo  = new CommentVo();
//                commentVo.setCommentEntity(item);
//              //  commentVo.setUserEntity(userService.findUserById(item.getUserId()));
//                //封装评论的点赞状态以及点赞数量
//          int commentLikeStatus = userEntity == null ? 0 :  likeService.findEntityLikeStatus(userEntity.getId(), CommunityConstants.LikeTypeEnum.COMMENT.getCode(), item.getId());
//          commentVo.setLikeStatus(commentLikeStatus);
//          //封装评论的点赞数量
//          long likeCount = likeService.findEntityLikeCount(CommunityConstants.LikeTypeEnum.COMMENT.getCode(), item.getId());
//          commentVo.setLikeCount(likeCount);
//
//          //2.封装每条评论的回复
//          List<CommentEntity> replys = commentService.selectCommentsByEntity(CommunityConstants.CommenttypeEnum.REPLY.getCode(), item.getId(), 0, Integer.MAX_VALUE);
//          //如果评论下面有评论就封装
//          if (replys != null){
////              List<ReplyVo> replyVoList = replys.stream().map((reply)->{
////                  ReplyVo replyVo = new ReplyVo();
////                  replyVo.setCommentEntity(reply);
////                  replyVo.setReplyFrom(userService.findUserById(reply.getUserId()));
////                  if(reply.getTargetId() !=0)
////                  replyVo.setReplyTo(userService.findUserById(reply.getTargetId()));
////
////                  //封装评论的点赞状态以及点赞数量
////                  int replyLikeStatus = userEntity == null ? 0 :  likeService.findEntityLikeStatus(userEntity.getId(), CommunityConstants.LikeTypeEnum.COMMENT.getCode(), reply.getId());
////                  replyVo.setLikeStatus(replyLikeStatus);
////                  //封装评论的点赞数量
////                  long replylikeCount = likeService.findEntityLikeCount(CommunityConstants.LikeTypeEnum.COMMENT.getCode(), reply.getId());
////                  replyVo.setLikeCount(replylikeCount);
////                  return replyVo;
////              }).collect(Collectors.toList());
////              commentVo.setReplys(replyVoList);
////              commentVo.setReplyCount(replys.size());
//          }
//          return commentVo;
//        }).collect(Collectors.toList());
//        System.out.println(commentVos);
        model.addAttribute("page", detailVo.getPage());
//        model.addAttribute("comments",commentVos);
//      //  return commentVos;
        return "site/discuss-detail";
    }

    //置顶
    @PostMapping("/top")
    @ResponseBody
    public R top(int id) {
        discussPostService.updateType(id, 1);

        //再次触发发帖事件
        EventEntity eventEntity = new EventEntity().setTopic(CommunityConstants.TOPIC_PUBLISH)
                .setEntityId(id);
        //eventProducer.fireEvent(eventEntity);
        return R.ok("置顶成功！");
    }

    //加精
    @PostMapping("/wonderful")
    @ResponseBody
    public R wonderful(int id) {
        discussPostService.updateStatus(id, 1);

        //再次触发发帖事件
        EventEntity eventEntity = new EventEntity().setTopic(CommunityConstants.TOPIC_PUBLISH)
                .setEntityId(id);
        //eventProducer.fireEvent(eventEntity);
        return R.ok("加精成功！");
    }

    //删除
    @PostMapping("/delete")
    @ResponseBody
    public R delete(int id) {
        discussPostService.updateStatus(id, 2);
        //触发删除帖事件
        EventEntity eventEntity = new EventEntity().setTopic(CommunityConstants.TOPIC_DELETE)
                .setEntityId(id);
        //eventProducer.fireEvent(eventEntity);
        return R.ok("删除成功！");
    }

}
