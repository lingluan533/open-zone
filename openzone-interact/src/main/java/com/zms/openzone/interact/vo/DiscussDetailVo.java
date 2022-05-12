package com.zms.openzone.interact.vo;

import com.zms.openzone.interact.entity.DiscussPostEntity;
import com.zms.openzone.interact.entity.UserEntity;
import com.zms.openzone.interact.utils.PageUtils;
import lombok.Data;

import java.util.List;

/**
 * @author: zms
 * @create: 2022/2/28 11:21
 */
@Data
public class DiscussDetailVo {

    /*帖子信息*/
    private DiscussPostEntity postEntity;

    //作者信息
    private UserEntity userEntity;

    //当前用户对其的点赞状态
    private Integer likeStatus;

    //帖子点赞数量
    private Long likeCount;

    //评论的分页信息
    private PageUtils page;
    //帖子的评论信息（分页）
    private List<CommentVo> commentVos;
}
