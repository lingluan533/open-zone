package com.zms.openzone.message.vo;


import com.zms.openzone.common.entity.UserEntityVo;
import com.zms.openzone.message.entity.MessageEntity;
import com.zms.openzone.message.entity.UserEntity;
import lombok.Data;

/**
 * @author: zms
 * @create: 2022/2/10 15:49
 */
@Data
public class NoticeVO {
    //最近一条通知的内容
     private MessageEntity recentNotice;
    //通知的产生来源用户
    private UserEntityVo userEntity;
    //通知的类型
    private String noticeType;
    //通知的对应实体ID
    private int entityId;
    //通知的对应实体的类型
    private int entityType;
    //通知所在的帖子
    private int postId;
    //通知全部的数量
    private int totalCount;
    //通知未读的数量
    private int unreadCount;


}
