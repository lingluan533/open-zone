package com.zms.openzone.message.controller;


import com.alibaba.fastjson.TypeReference;
import com.zms.openzone.common.entity.UserEntityVo;
import com.zms.openzone.common.utils.PageUtils;
import com.zms.openzone.common.utils.R;

import com.zms.openzone.message.entity.MessageEntity;
import com.zms.openzone.message.entity.UserEntity;
import com.zms.openzone.message.feign.UserFeignService;
import com.zms.openzone.message.service.MessageService;
import com.zms.openzone.message.utils.HostHolder;
import com.zms.openzone.message.utils.SensitiveFilter;
import com.zms.openzone.message.vo.ConversationVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author: zms
 * @create: 2022/2/5 15:12
 */
@Controller
@RequestMapping("/letter")
public class MessageController {
    @Autowired
    private HostHolder hostHolder;
    @Autowired
    private MessageService messageService;
   @Autowired
    private UserFeignService userFeignService;
    @Autowired
    private SensitiveFilter sensitiveFilter;
    @RequestMapping("/list")
    public String getAllLetters(Model model, PageUtils page){
        UserEntityVo user = hostHolder.getUser();
        if(user!=null)
        System.out.println("消息服务，当前登录："+user.getUsername());
        else
            System.out.println("消息服务，未登录");
        //分页信息
        page.setLimit(5);
        page.setPath("/letter/list");
        page.setRows(messageService.selectConversationCount(user.getId()));

        List<MessageEntity> messages = messageService.selectConversationRecentMessages(user.getId(),page.getOffset(),page.getLimit());

        List<ConversationVO> conversationVOS;
        conversationVOS =  messages.stream().map((item)->{
            ConversationVO vo = new ConversationVO();
            vo.setConversationId(item.getConversationId());
            vo.setRecentMessage(item);
            vo.setTotalNum(messageService.selectConversationMessageCount(item.getConversationId()));
            vo.setUnreadNums(messageService.selectConversationUnreadMessageCount(item.getConversationId(),user.getId()));
            vo.setConversationFriend(messageService.selectConversationFriend(hostHolder.getUser().getId()==item.getFromId()? item.getToId():item.getFromId()));
            return vo;
        }).collect(Collectors.toList());
        for (ConversationVO s: conversationVOS
             ) {
            System.out.println(s);
        }
        model.addAttribute("unreadmessageNum",messageService.selectUnreadMessageCount(hostHolder.getUser().getId()));
        model.addAttribute("data",conversationVOS);
        //这一句必须要有！！！！
        model.addAttribute("page",page);


        //4.封装未读信息数量
        model.addAttribute("unreadNoticeCount",messageService.selectUnreadNoticeCount(user.getId(), null));
        model.addAttribute("unreadLetterCount",messageService.selectUnreadMessageCount(user.getId()));
        //由于全部的未读消息数量要放在导航栏上面，所以需要每一个请求都检查一下最新的未读消息数量，可以使用Interceptor
        return "site/letter";
    }

    @RequestMapping("/detail/{conversationId}")
    public String getConversationDetails(@PathVariable("conversationId") String conversationId,Model model,PageUtils page){
        UserEntityVo user = hostHolder.getUser();
        //分页信息
        page.setLimit(5);
        page.setPath("/letter/detail/"+conversationId);
        page.setRows(messageService.selectConversationMessageCount(conversationId));
        System.out.println("会话的消息数："+messageService.selectConversationMessageCount(conversationId));
        //打开详情页表示已读取
        messageService.readUnreadMessage(hostHolder.getUser().getId(),conversationId);
        List<Map<String, Object>> maps = messageService.selectMessagesByConversationId(conversationId,page.getOffset(),page.getLimit());
        String[] s = conversationId.split("_");
        String firendId = (user.getId()+"").equals(s[0]) ? s[1] : s[0];
        UserEntity firend =null;
        R r = userFeignService.getUserEntityByID(Integer.parseInt(firendId));
        if(r.getCode() == 0) {
            firend = r.getData(new TypeReference<UserEntity>(){});
        }

        UserEntity finalFirend = firend;
        maps =  maps.stream().map((item)->{
            if((Integer)item.get("from_id") == user.getId()){
                item.put("userName",user.getUsername());
                item.put("userHeader",user.getHeaderUrl());
            }else{
                item.put("userName", finalFirend.getUsername());
                item.put("userHeader", finalFirend.getHeaderUrl());
            }
           return item;
       }).collect(Collectors.toList());

        model.addAttribute("firend",firend);
        model.addAttribute("data",maps);
        model.addAttribute("page",page);
        return "site/letter-detail";
    }


    @RequestMapping("/send")
    @ResponseBody
    public R sendLetter(String targetUsername, String lettercontent){
        R r = userFeignService.findUserByUserName(targetUsername);
        UserEntity toUser = null;
        if(r.getCode() == 0) {
            toUser = r.getData(new TypeReference<UserEntity>(){});
        }
        if(toUser == null){
            return R.error(1,"该用户不存在！");
        }
        //组装会话id 小的在前
        String conversationId = hostHolder.getUser().getId() < toUser.getId()?  hostHolder.getUser().getId() + "_" + toUser.getId(): toUser.getId() +"_"+ hostHolder.getUser().getId();

        messageService.sendMessage(hostHolder.getUser().getId(),toUser.getId(),conversationId, sensitiveFilter.filter(lettercontent),new Date());
        return R.ok();
    }
}
