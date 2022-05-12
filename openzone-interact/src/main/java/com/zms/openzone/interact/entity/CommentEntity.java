package com.zms.openzone.interact.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * @author: zms
 * @create: 2022/1/18 22:47
 */

@Data
@TableName("comment")
public class CommentEntity {
    private int id;
    private int userId;
    private int entityId;  //被评论的实体的id
    private int entityType; //被评论的实体的类型
    private int targetId;   //目标回复对象的id 用于回复评论
    private String content;
    private int status;
    private Date createTime;

}
