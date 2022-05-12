package com.zms.openzone.interact.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * @author: zms
 * @create: 2022/1/18 22:47
 */
@Data
@TableName("discuss_post")
public class DiscussPostEntity {

    private int id;
    private int userId;
    private String title;
    private String content;
    private int type;
    private int status;
    private Date createTime;
    private int commentCount;
    private double score;

}
