package com.zms.openzone.interact.controller;


import com.zms.openzone.interact.entity.DiscussPostEntity;
import com.zms.openzone.interact.entity.UserEntity;
import com.zms.openzone.interact.service.DiscussPostService;
import com.zms.openzone.interact.service.LikeService;

import com.zms.openzone.interact.utils.PageUtils;
import com.zms.openzone.interact.vo.DiscussPostVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * @author: zms
 * @create: 2022/1/18 23:48
 */
@Controller
public class HomeController {

    @Autowired(required = false)
    private DiscussPostService discussPostService;
    //    @Autowired(required = false)
//    //private UserService userService;
//    @Autowired
    private LikeService likeService;

    @GetMapping("/")
    public String root() {
        return "forward:/index";
    }

    @RequestMapping("/index")
    public String getIndexPage(Model model, PageUtils page,
                               @RequestParam(defaultValue = "0") int orderMode
    ) throws ExecutionException, InterruptedException {
        //SpringMVC会自动实例化Model和page,并将page注入到model中
        page.setRows(discussPostService.findDiscussPostRows(0));
        page.setPath("/index?orderMode=" + orderMode);

        List<DiscussPostVo> discussPosts = discussPostService.findDiscussPosts(0, page.getOffset(), page.getLimit(), orderMode);

//        List<Map<String, Object>> discussPosts = new ArrayList<>();
//        if(list != null){
//            for(DiscussPostEntity postEntity : list){
//                Map<String,Object> map = new HashMap<>();
//                map.put("post",postEntity);
//             //   UserEntity userEntity = userService.findUserById(postEntity.getUserId());
//              //  map.put("user",userEntity);
//                //封装点赞数
//               // map.put("likeCount",likeService.findEntityLikeCount(CommunityConstants.LikeTypeEnum.DISCUSS.getCode(), postEntity.getId()));
//                discussPosts.add(map);
//            }
//        }
        model.addAttribute("discussPosts", discussPosts);
        model.addAttribute("page", page);
        model.addAttribute("orderMode", orderMode);
        return "index";
    }

    @RequestMapping("/toerror") //springboot自己定义了一个/error 所以我们需要改一下名字
    public String getErrorPage() {
        return "site/error/500";
    }

    // 拒绝访问时的提示页面
    @RequestMapping(path = "/denied", method = RequestMethod.GET)
    public String getDeniedPage() {
        return "site/error/404";
    }


}
