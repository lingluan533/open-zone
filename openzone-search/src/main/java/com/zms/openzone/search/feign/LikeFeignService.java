package com.zms.openzone.search.feign;



import com.zms.openzone.search.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author: zms
 * @create: 2022/2/28 16:15
 */
@FeignClient("openzone-interact")
public interface LikeFeignService {
    @GetMapping("/like/likecount")
    R getEntityLikeCount(@RequestParam int entityType, @RequestParam int entityId);
}
