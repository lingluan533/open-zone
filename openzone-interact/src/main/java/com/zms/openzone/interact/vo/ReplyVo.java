package com.zms.openzone.interact.vo;


import com.zms.openzone.interact.entity.CommentEntity;
import com.zms.openzone.interact.entity.UserEntity;
import lombok.Data;

/**
 * @author: zms
 * @create: 2022/1/29 16:07
 */
@Data
public class ReplyVo {
    private CommentEntity commentEntity;
    private UserEntity replyFrom;
    private UserEntity replyTo;

    private long likeCount;
    private int likeStatus;
}
