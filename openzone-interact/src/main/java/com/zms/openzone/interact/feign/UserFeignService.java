package com.zms.openzone.interact.feign;

import com.zms.openzone.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

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


    @GetMapping("/user/test")
    R getTest();
}
