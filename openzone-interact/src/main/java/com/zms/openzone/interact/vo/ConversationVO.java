package com.zms.openzone.interact.vo;


import com.zms.openzone.interact.entity.UserEntity;
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
    private UserEntity conversationFriend;
    //会话的最近一条消息的内容
    // private Message recentMessage;

}
