package com.zms.openzone.message.service;


import com.zms.openzone.common.entity.UserEntityVo;
import com.zms.openzone.message.entity.MessageEntity;
import com.zms.openzone.message.entity.UserEntity;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author: zms
 * @create: 2022/2/5 15:36
 */
@Service
public interface MessageService {
    int selectConversationCount(int userId);

    List<MessageEntity> selectConversationRecentMessages(int userId, int offset, int id);

    Integer selectConversationMessageCount(String conversationId);

    Integer selectConversationUnreadMessageCount(String conversationId,int useId);

    UserEntityVo selectConversationFriend(int i);

    int selectUnreadMessageCount(int id);

    List<Map<String,Object>> selectMessagesByConversationId(String conversationId, int offset, int limit);

    void sendMessage(int from, int to, String conversationId, String lettercontent, Date create_time);
    void sendMessage(MessageEntity message);
    void readUnreadMessage(int id, String conversationId);

    MessageEntity selectLastedNotice(int id, String topicComment);

    int selectNoticeCount(int userId, String topicComment);

    int selectUnreadNoticeCount(int id, String topicComment);

    List<MessageEntity> selectMessagesByTopic(int id, String topicComment, int offset, int limit);

    void readUnreadNotice(int id, String topicComment);
}
