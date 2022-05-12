package com.zms.openzone.interact.controller;

import com.alibaba.fastjson.TypeReference;
import com.zms.openzone.common.constants.CommunityConstants;
import com.zms.openzone.common.entity.EventEntity;
import com.zms.openzone.common.entity.UserEntityVo;
import com.zms.openzone.common.utils.R;
import com.zms.openzone.interact.entity.UserEntity;
import com.zms.openzone.interact.feign.UserFeignService;
import com.zms.openzone.interact.service.FollowService;
import com.zms.openzone.interact.utils.HostHolder;
import com.zms.openzone.interact.utils.PageUtils;
import com.zms.openzone.interact.vo.FollowVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author: zms
 * @create: 2022/2/8 15:12
 */
@Controller
@RequestMapping("follow")
public class FollowController {
    @Autowired
    private FollowService followService;
    @Autowired
    private HostHolder hostHolder;
    @Autowired
    private UserFeignService userFeignService;

    @GetMapping("/userFollowerCount")
    @ResponseBody
    public R findFollowerCount(@RequestParam int userId, @RequestParam int type) {
        long followerCount = followService.findFollowerCount(userId, type);
        return R.ok().setData(followerCount);
    }

    @GetMapping("/userFolloweeCount")
    @ResponseBody
    public R findFolloweeCount(@RequestParam int userId, @RequestParam int type) {
        long followeeCount = followService.findFolloweeCount(userId, type);
        return R.ok().setData(followeeCount);
    }

    @GetMapping("/hasFollowed")
    @ResponseBody
    public R hasFollowed(@RequestParam int fromuserId, @RequestParam int type, @RequestParam int touserId) {
        int i = followService.hasFollowed(fromuserId, CommunityConstants.LikeTypeEnum.USER.getCode(), touserId);
        return R.ok().setData(i);
    }

    @PostMapping("/follow")
    @ResponseBody
    public R followEntity(int entityType, int entityId){
        UserEntityVo userEntity = hostHolder.getUser();
        followService.follow(userEntity.getId(),entityType,entityId);

        return R.ok("关注成功！");
    }

    @PostMapping("/unfollow")
    @ResponseBody
    public R unfollowEntity(int entityType,int entityId){
        UserEntityVo userEntity = hostHolder.getUser();
        followService.unfollow(userEntity.getId(),entityType,entityId);
        return R.ok("关注成功！");
    }
    //关注的人-页面
    @GetMapping("/followees/{userId}")
    public String getFollowees(@PathVariable("userId")int userId, Model model, PageUtils page){
        R r = userFeignService.getUserEntityByID(userId);
        if(r.getCode() == 0){
            UserEntityVo data = r.getData(new TypeReference<UserEntityVo>() {
            });
            model.addAttribute("user", data);
        }

        page.setLimit(5);
        page.setRows((int)followService.findFolloweeCount(userId,CommunityConstants.LikeTypeEnum.USER.getCode()));
        page.setPath("/followees/" + userId);

        List<FollowVO> followees =  followService.findFolloweesByUserId(CommunityConstants.LikeTypeEnum.USER.getCode(),userId,page.getOffset(),page.getLimit());
        model.addAttribute("followees",followees);
        return "site/followee";
    }

    //所有粉丝-页面
    @GetMapping("/followers/{userId}")
    public String getFollowers(@PathVariable("userId")int userId, Model model, PageUtils page){
        R r = userFeignService.getUserEntityByID(userId);
        if(r.getCode() == 0){
            UserEntityVo data = r.getData(new TypeReference<UserEntityVo>() {
            });
            model.addAttribute("user", data);
        }

        page.setLimit(5);
        page.setRows((int)followService.findFollowerCount(userId,CommunityConstants.LikeTypeEnum.USER.getCode()));
        page.setPath("/followers/" + userId);

        List<FollowVO> followers =  followService.findFollowersByUserId(CommunityConstants.LikeTypeEnum.USER.getCode(),userId,page.getOffset(),page.getLimit());
        model.addAttribute("followers",followers);
        return "site/follower";
    }

}
