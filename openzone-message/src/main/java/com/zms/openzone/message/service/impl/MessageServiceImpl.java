package com.zms.openzone.message.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.zms.openzone.common.entity.UserEntityVo;
import com.zms.openzone.common.utils.R;
import com.zms.openzone.message.dao.MessageDao;
import com.zms.openzone.message.entity.MessageEntity;
import com.zms.openzone.message.entity.UserEntity;
import com.zms.openzone.message.feign.UserFeignService;
import com.zms.openzone.message.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author: zms
 * @create: 2022/2/5 15:37
 */
@Service
public class MessageServiceImpl extends ServiceImpl<MessageDao, MessageEntity> implements MessageService {
    @Autowired
    private MessageDao messageDao;
    @Autowired
    private UserFeignService userFeignService;
    //查询当前用户的所有会话的数量
    @Override
    public int selectConversationCount(int userId) {
        return messageDao.selectConversationCount(userId);
    }
    //查询当前用户的所有会话的最近一条消息
    @Override
    public List<MessageEntity> selectConversationRecentMessages(int userId, int offset, int limit) {
        return messageDao.selectConversationRecentMessage(userId,offset,limit);
    }
    //查询会话的消息总数量
    @Override
    public Integer selectConversationMessageCount(String conversationId) {

        return   baseMapper.selectCount(new QueryWrapper<MessageEntity>().eq("conversation_id",conversationId));
    }
    //查找会话的未读消息数量
    @Override
    public Integer selectConversationUnreadMessageCount(String conversationId,int userId) {
        return baseMapper.selectCount(new QueryWrapper<MessageEntity>().eq("conversation_id",conversationId).eq("status",0).eq("to_id",userId));
    }

    @Override
    public UserEntityVo selectConversationFriend(int i) {
        R r=  userFeignService.getUserEntityByID(i);
        if(r.getCode() == 0){
            UserEntityVo data = r.getData(new TypeReference<UserEntityVo>() {
            });
            return data;
        }
        return null;
    }
    //查找用户的未读消息数量
    @Override
    public int selectUnreadMessageCount(int id) {

        return baseMapper.selectCount(new QueryWrapper<MessageEntity>().eq("to_id",id).eq("status",0).ne("from_id",1));
    }
    //根据会话id查询消息的内容
    @Override
    public List<Map<String, Object>> selectMessagesByConversationId(String conversationId, int offset, int limit) {

        return messageDao.selectMessagesByConversationId(conversationId,offset,limit);
    }
    //发送消息
    @Override
    public void sendMessage(int from, int to, String conversationId, String lettercontent, Date create_time) {
        MessageEntity message = new MessageEntity();
        message.setContent(lettercontent);
        message.setFromId(from);
        message.setToId(to);
        message.setConversationId(conversationId);
        message.setCreateTime(create_time);
        baseMapper.insert(message);
    }
    //发送消息 参数为消息对象实体
    @Override
    public void sendMessage(MessageEntity message) {
        baseMapper.insert(message);
    }

    @Override
    public void readUnreadMessage(int toId, String conversationId) {
        messageDao.readMessageByConversationId(toId,conversationId);
    }
    //查询某用户对应主题下的最近一条通知消息
    @Override
    public MessageEntity selectLastedNotice(int userId, String topicComment) {
        return  messageDao.selectRecentNotice(userId,topicComment);

    }
    //查询用户的通知数量
    @Override
    public int selectNoticeCount(int userId, String topicComment) {
        if(topicComment!=null)
        return baseMapper.selectCount(new QueryWrapper<MessageEntity>().eq("to_id",userId).eq("conversation_id",topicComment).eq("from_id",1));
        else
            return baseMapper.selectCount(new QueryWrapper<MessageEntity>().eq("to_id",userId).eq("from_id",1));
    }
    //查询用户未读通知数量
    @Override
    public int selectUnreadNoticeCount(int userId, String topicComment) {
        if(topicComment!=null)
            return baseMapper.selectCount(new QueryWrapper<MessageEntity>().eq("to_id",userId).eq("conversation_id",topicComment).eq("status",0));
        else
            return baseMapper.selectCount(new QueryWrapper<MessageEntity>().eq("to_id",userId).eq("from_id",1).eq("status",0));

    }

    @Override
    public List<MessageEntity> selectMessagesByTopic(int id, String topicComment, int offset, int limit) {



        return messageDao.selectMessagesByTopic(id,topicComment,offset,limit);
    }
    //读取通知
    @Override
    public void readUnreadNotice(int id, String topicComment) {
        messageDao.readNotice(id,topicComment);
    }
}
