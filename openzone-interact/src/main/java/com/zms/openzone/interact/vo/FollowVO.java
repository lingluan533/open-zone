package com.zms.openzone.interact.vo;


import com.zms.openzone.common.entity.UserEntityVo;
import com.zms.openzone.interact.entity.UserEntity;
import lombok.Data;

import java.util.Date;

/**
 * @author: zms
 * @create: 2022/2/8 17:24
 */
@Data
public class FollowVO {  //作为封装关注/粉丝列表页面的vo
    private UserEntityVo follow;
    private Date followTime;
    private boolean hasFollowed;
}
