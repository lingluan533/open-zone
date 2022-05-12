package com.zms.openzone.interact.vo;

import com.zms.openzone.interact.entity.DiscussPostEntity;
import com.zms.openzone.interact.entity.UserEntity;
import lombok.Data;

import java.util.Date;

/**
 * @author: zms
 * @create: 2022/2/26 19:15
 */
@Data
public class DiscussPostVo {

//    private int id;
//    private int userId;
//    private String title;
//    private String content;
//    private int type;
//    private int status;
//    private Date createTime;
//    private int commentCount;
//    private double score;

    /*封装帖子信息*/
    private DiscussPostEntity discussPostEntity;

    /*封装用户信息*/
    private UserEntity userEntity;

    /*封装点赞信息*/
    private Long likeCount;
}
