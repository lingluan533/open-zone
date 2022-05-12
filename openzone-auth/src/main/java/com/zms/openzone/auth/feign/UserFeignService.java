package com.zms.openzone.auth.feign;


import com.zms.openzone.common.entity.UserEntityVo;
import com.zms.openzone.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @author: zms
 * @create: 2022/2/26 19:44
 */

@FeignClient("openzone-member")
public interface UserFeignService {
    @GetMapping("/user/login")
    R login(@RequestParam String username, @RequestParam String password);

    @GetMapping("/user/activation")
    R activation(@RequestParam Integer userId, @RequestParam String code);

    @GetMapping("/user/userentity/userid")
    R getUserEntityByID(@RequestParam Integer userId);

    @PostMapping("/user/userentity/postids")
    R getUserEntitysByPostIDs(@RequestBody List<Integer[]> postIds);

    @PostMapping("/user/register")
    R register(@RequestBody UserEntityVo userEntity);

    @GetMapping("/user/test")
    R getTest();
}
