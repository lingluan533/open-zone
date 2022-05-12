package com.zms.openzone.message.controller;

import com.alibaba.fastjson.JSONObject;

import com.alibaba.fastjson.TypeReference;
import com.zms.openzone.common.constants.CommunityConstants;
import com.zms.openzone.common.entity.UserEntityVo;
import com.zms.openzone.common.utils.R;
import com.zms.openzone.message.entity.MessageEntity;
import com.zms.openzone.message.entity.UserEntity;
import com.zms.openzone.message.feign.UserFeignService;
import com.zms.openzone.message.service.MessageService;
import com.zms.openzone.message.utils.HostHolder;
import com.zms.openzone.message.utils.PageUtils;
import com.zms.openzone.message.vo.NoticeVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author: zms
 * @create: 2022/2/10 15:40
 */
@Controller
@RequestMapping("/notice")
public class NoticeController {
    @Autowired
    private HostHolder hostHolder;
    @Autowired
    private MessageService messageService;
    @Autowired
    private UserFeignService userFeignService;
    @GetMapping("/list")
    public String getAllNotices(Model model){
        UserEntityVo user = hostHolder.getUser();
        if(user==null) throw new RuntimeException("用户未登录！");
        //1.封装评论的通知信息
        MessageEntity commentMessage = messageService.selectLastedNotice(user.getId(), CommunityConstants.TOPIC_COMMENT);
        if(commentMessage != null){
            NoticeVO commentVO = new NoticeVO();
            commentVO.setRecentNotice(commentMessage);
            commentVO.setNoticeType(CommunityConstants.TOPIC_COMMENT);
            HashMap<String,Object>map = JSONObject.parseObject(commentMessage.getContent(), HashMap.class);
            commentVO.setEntityId((Integer) map.get("entityId"));
            commentVO.setEntityType((Integer) map.get("entityType"));
            commentVO.setPostId((Integer)map.get("postId"));

            R r = userFeignService.getUserEntityByID((Integer) map.get("userId"));
            UserEntityVo userEntityVo = null;
            if(r.getCode()==0){
                userEntityVo = r.getData(new TypeReference<UserEntityVo>(){});
            }
            commentVO.setUserEntity(userEntityVo);
            commentVO.setTotalCount(messageService.selectNoticeCount(user.getId(),CommunityConstants.TOPIC_COMMENT));
            commentVO.setUnreadCount(messageService.selectUnreadNoticeCount(user.getId(),CommunityConstants.TOPIC_COMMENT));
            //设置到model中 供前台使用
            model.addAttribute("commentNotice",commentVO);
        }


        //2.封装点赞的通知信息
        MessageEntity likeMessage = messageService.selectLastedNotice(user.getId(), CommunityConstants.TOPIC_LIKE);
        if(likeMessage!=null){
            NoticeVO likeVO = new NoticeVO();
            likeVO.setRecentNotice(likeMessage);
            likeVO.setNoticeType(CommunityConstants.TOPIC_LIKE);
           HashMap<String,Object> map = JSONObject.parseObject(likeMessage.getContent(), HashMap.class);
            likeVO.setEntityId((Integer) map.get("entityId"));
            likeVO.setEntityType((Integer) map.get("entityType"));
            likeVO.setPostId((Integer)map.get("postId"));
           // likeVO.setUserEntity(userService.findUserById((Integer)map.get("userId")));
            R r = userFeignService.getUserEntityByID((Integer) map.get("userId"));
            UserEntityVo userEntityVo = null;
            if(r.getCode()==0){
                userEntityVo = r.getData(new TypeReference<UserEntityVo>(){});
            }
            likeVO.setUserEntity(userEntityVo);
            likeVO.setTotalCount(messageService.selectNoticeCount(user.getId(),CommunityConstants.TOPIC_LIKE));
            likeVO.setUnreadCount(messageService.selectUnreadNoticeCount(user.getId(),CommunityConstants.TOPIC_LIKE));
            //设置到model中 供前台使用
            model.addAttribute("likeNotice",likeVO);
        }


        //3.封装关注的通知信息
        MessageEntity followMessage = messageService.selectLastedNotice(user.getId(), CommunityConstants.TOPIC_FOLLOW);
        if(followMessage!= null){
            NoticeVO followVO = new NoticeVO();
            followVO.setRecentNotice(followMessage);
            followVO.setNoticeType(CommunityConstants.TOPIC_FOLLOW);
            HashMap<String,Object>  map = JSONObject.parseObject(followMessage.getContent(), HashMap.class);
            followVO.setEntityId((Integer) map.get("entityId"));
            followVO.setEntityType((Integer) map.get("entityType"));
            //followVO.setUserEntity(userService.findUserById((Integer)map.get("userId")));

            R r = userFeignService.getUserEntityByID((Integer) map.get("userId"));
            UserEntityVo userEntityVo = null;
            if(r.getCode()==0){
                userEntityVo = r.getData(new TypeReference<UserEntityVo>(){});
            }
            followVO.setUserEntity(userEntityVo);
            followVO.setTotalCount(messageService.selectNoticeCount(user.getId(),CommunityConstants.TOPIC_FOLLOW));
            followVO.setUnreadCount(messageService.selectUnreadNoticeCount(user.getId(),CommunityConstants.TOPIC_FOLLOW));
            //设置到model中 供前台使用
            model.addAttribute("followNotice",followVO);

            //4.封装未读信息数量
            model.addAttribute("unreadNoticeCount",messageService.selectUnreadNoticeCount(user.getId(), null));
            model.addAttribute("unreadLetterCount",messageService.selectUnreadMessageCount(user.getId()));
            //由于全部的未读消息数量要放在导航栏上面，所以需要每一个请求都检查一下最新的未读消息数量，可以使用Interceptor
        }

        return "site/notice";
    }

    @GetMapping("/detail/comment")
    public String getCommentNoticeDetail(Model model, PageUtils page){
        UserEntityVo userEntity = hostHolder.getUser();
        //打开详情页表示已读取
        page.setRows(messageService.selectNoticeCount(userEntity.getId(), CommunityConstants.TOPIC_COMMENT));
        page.setPath("/notice/detail/comment");
        page.setLimit(5);
        messageService.readUnreadNotice(userEntity.getId(),CommunityConstants.TOPIC_COMMENT);
        List<MessageEntity> messages =  messageService.selectMessagesByTopic(userEntity.getId(),CommunityConstants.TOPIC_COMMENT,page.getOffset(),page.getLimit());
        List<NoticeVO> noticeVOS = messages.stream().map((item)->{
            NoticeVO noticeVO = new NoticeVO();
            HashMap<String,Object> map = JSONObject.parseObject(item.getContent(), HashMap.class);
            noticeVO.setRecentNotice(item);
            noticeVO.setPostId((Integer)map.get("postId"));
            noticeVO.setEntityId((Integer) map.get("entityId"));
            noticeVO.setEntityType((Integer) map.get("entityType"));
            noticeVO.setNoticeType(item.getConversationId());
           // noticeVO.setUserEntity(userService.findUserById((Integer)map.get("userId")));
            R r = userFeignService.getUserEntityByID((Integer) map.get("userId"));
            UserEntityVo userEntityVo = null;
            if(r.getCode()==0){
                userEntityVo = r.getData(new TypeReference<UserEntityVo>(){});
            }
            noticeVO.setUserEntity(userEntityVo);
            return noticeVO;
        }).collect(Collectors.toList());
        model.addAttribute("notices",noticeVOS);
        model.addAttribute("page",page);
        return "site/notice-detail";
    }

    @GetMapping("/detail/like")
    public String getLikeNoticeDetail(Model model,PageUtils page){
        UserEntityVo userEntity = hostHolder.getUser();
        //打开详情页表示已读取
        page.setRows(messageService.selectNoticeCount(userEntity.getId(), CommunityConstants.TOPIC_LIKE));
        page.setPath("/notice/detail/like");
        page.setLimit(5);
        messageService.readUnreadNotice(userEntity.getId(),CommunityConstants.TOPIC_LIKE);
        List<MessageEntity> messages =  messageService.selectMessagesByTopic(userEntity.getId(),CommunityConstants.TOPIC_LIKE,page.getOffset(),page.getLimit());
        List<NoticeVO> noticeVOS = messages.stream().map((item)->{
            NoticeVO noticeVO = new NoticeVO();
            HashMap<String,Object> map = JSONObject.parseObject(item.getContent(), HashMap.class);
            noticeVO.setRecentNotice(item);
            noticeVO.setPostId((Integer)map.get("postId"));
            noticeVO.setEntityId((Integer) map.get("entityId"));
            noticeVO.setEntityType((Integer) map.get("entityType"));
            noticeVO.setNoticeType(item.getConversationId());
           // noticeVO.setUserEntity(userService.findUserById((Integer)map.get("userId")));
            R r = userFeignService.getUserEntityByID((Integer) map.get("userId"));
            UserEntityVo userEntityVo = null;
            if(r.getCode()==0){
                userEntityVo = r.getData(new TypeReference<UserEntityVo>(){});
            }
            noticeVO.setUserEntity(userEntityVo);
            return noticeVO;
        }).collect(Collectors.toList());
        model.addAttribute("notices",noticeVOS);
        model.addAttribute("page",page);
        return "site/notice-detail";
    }
    @GetMapping("/detail/follow")
    public String getfollowNoticeDetail(Model model,PageUtils page){
        UserEntityVo userEntity = hostHolder.getUser();
        //打开详情页表示已读取
        page.setRows(messageService.selectNoticeCount(userEntity.getId(), CommunityConstants.TOPIC_FOLLOW));
        page.setPath("/notice/detail/follow");
        page.setLimit(5);
        messageService.readUnreadNotice(userEntity.getId(),CommunityConstants.TOPIC_FOLLOW);
        List<MessageEntity> messages =  messageService.selectMessagesByTopic(userEntity.getId(),CommunityConstants.TOPIC_FOLLOW,page.getOffset(),page.getLimit());
        List<NoticeVO> noticeVOS = messages.stream().map((item)->{
            NoticeVO noticeVO = new NoticeVO();
            HashMap<String,Object> map = JSONObject.parseObject(item.getContent(), HashMap.class);
            noticeVO.setRecentNotice(item);

            noticeVO.setEntityId((Integer) map.get("entityId"));
            noticeVO.setEntityType((Integer) map.get("entityType"));
            noticeVO.setNoticeType(item.getConversationId());
          //  noticeVO.setUserEntity(userService.findUserById((Integer)map.get("userId")));
            R r = userFeignService.getUserEntityByID((Integer) map.get("userId"));
            UserEntityVo userEntityVo = null;
            if(r.getCode()==0){
                userEntityVo = r.getData(new TypeReference<UserEntityVo>(){});
            }
            noticeVO.setUserEntity(userEntityVo);
            return noticeVO;
        }).collect(Collectors.toList());
        model.addAttribute("notices",noticeVOS);
        model.addAttribute("page",page);
        return  "site/notice-detail";
    }


}
