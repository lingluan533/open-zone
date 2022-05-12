package com.zms.openzone.member.feign;

import com.zms.openzone.common.constants.CommunityConstants;
import com.zms.openzone.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author: zms
 * @create: 2022/3/1 21:57
 */
@FeignClient("openzone-interact")
public interface InteractFeignService {

    @GetMapping("/like/userLikeCount")
    R findUserLikeCount(@RequestParam int userId);

    @GetMapping("/follow/userFollowerCount")
    R findFollowerCount(@RequestParam int userId, @RequestParam int type);

    @GetMapping("/follow/userFolloweeCount")
    R findFolloweeCount(@RequestParam int userId, @RequestParam int type);

    @GetMapping("/follow/hasFollowed")
    R hasFollowed(@RequestParam int fromuserId, @RequestParam int type, @RequestParam int touserId);
}
