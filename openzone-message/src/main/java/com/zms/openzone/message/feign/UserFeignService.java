package com.zms.openzone.message.feign;

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

    @GetMapping("/user/userentity/userid")
    R getUserEntityByID(@RequestParam Integer userId);

    @PostMapping("/user/userentity/postids")
    R getUserEntitysByPostIDs(@RequestBody List<Integer[]> postIds);
    @GetMapping("/user/userentity/userid")
   R  findUserByUserName(@RequestParam String userName);

    @GetMapping("/user/test")
    R getTest();
}
