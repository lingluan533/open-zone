package com.zms.openzone.message.vo;


import com.zms.openzone.common.entity.UserEntityVo;
import com.zms.openzone.message.entity.MessageEntity;
import com.zms.openzone.message.entity.UserEntity;
import lombok.Data;

/**
 * @author: zms
 * @create: 2022/2/5 15:26
 */
@Data
public class ConversationVO {
    //会话下的所有消息列表
    //private List<Message> messages;

    //会话ID
    private String conversationId;
    //会话下的所有消息数量
    private Integer totalNum;
    //会话中未读消息的数量
    private Integer unreadNums;
    //会话的对方
    private UserEntityVo conversationFriend;
    //会话的最近一条消息的内容
     private MessageEntity recentMessage;

}
