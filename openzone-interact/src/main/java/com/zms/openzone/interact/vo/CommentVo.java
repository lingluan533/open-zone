package com.zms.openzone.interact.vo;


import com.zms.openzone.interact.entity.CommentEntity;
import com.zms.openzone.interact.entity.UserEntity;
import lombok.Data;

import java.util.List;

/**
 * @author: zms
 * @create: 2022/1/29 13:00
 */
@Data
public class CommentVo {
    private CommentEntity commentEntity;
    private UserEntity userEntity;
    private List<ReplyVo> replys;
    private int replyCount;

    private long likeCount;
    private int likeStatus;
}
