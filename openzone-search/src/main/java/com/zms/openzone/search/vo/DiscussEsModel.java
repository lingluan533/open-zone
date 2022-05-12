package com.zms.openzone.search.vo;

import lombok.Data;

import java.util.Date;

/**
 * @author: zms
 * @create: 2022/2/12 13:21
 */
@Data
public class DiscussEsModel {
    //包括discuss实体的属性 以及 作者的相关属性 以及获赞的数量

    private int id;
    private int userId;
    private String title;
    private String content;
    private int type;
    private int status;
    private Date createTime;
    private int commentCount;
    private double score;

    //作者信息
    private String username;
    private String headerUrl;

    //点赞数量
    private long likeCount;
}
