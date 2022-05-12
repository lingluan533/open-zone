package com.zms.openzone.member.to;

import com.zms.openzone.member.entity.UserEntity;
import lombok.Data;

/**
 * @author: zms
 * @create: 2022/2/26 19:35
 */
@Data
public class PostIdAuthorTo {
    private int id; //帖子id
    private UserEntity userEntity;
}
