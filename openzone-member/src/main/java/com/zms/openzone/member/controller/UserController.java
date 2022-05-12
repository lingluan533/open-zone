package com.zms.openzone.member.controller;

import com.alibaba.fastjson.TypeReference;
import com.mysql.cj.util.StringUtils;

import com.zms.openzone.common.constants.CommunityConstants;
import com.zms.openzone.common.entity.UserEntityVo;
import com.zms.openzone.common.exception.BizCodeEnum;
import com.zms.openzone.common.utils.CommunityUtils;
import com.zms.openzone.common.utils.R;
import com.zms.openzone.member.entity.UserEntity;
import com.zms.openzone.member.feign.InteractFeignService;
import com.zms.openzone.member.service.UserService;
import com.zms.openzone.member.to.PostIdAuthorTo;
import com.zms.openzone.member.utils.HostHolder;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.mail.MessagingException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author: zms
 * @create: 2022/2/26 19:27
 */
@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private HostHolder hostHolder;
    @Autowired
    private InteractFeignService interactFeignService;

    @RequestMapping("/setting")
    public String getSettingPage() {
        return "site/setting";
    }


//    //上传图片的逻辑
//    @RequestMapping("/upload")
//    public String uploadHeader(MultipartFile headerImage, Model model){
//        if(headerImage == null){
//            model.addAttribute("error","您还没有选择图片！");
//            return "/site/setting";
//        }
//        String fileName = headerImage.getOriginalFilename();
//        System.out.println("前台传入的文件名："+fileName);
//        String suffix = fileName.substring(fileName.lastIndexOf('.')+1);
//
//        if(StringUtils.isNullOrEmpty(suffix) ){
//            model.addAttribute("error","图片格式不正确！");
//            return "/site/setting";
//        }else if(!(suffix.equalsIgnoreCase("jpg") || suffix.equalsIgnoreCase("jpeg") || suffix.equalsIgnoreCase("png") || suffix.equalsIgnoreCase("gif")) ){
//            model.addAttribute("error","图片格式不正确！");
//            return "/site/setting";
//        }
//
//        //生产随机文件名
//        fileName = CommunityUtils.gennerateUUID() + "." + suffix;
//        //确定文件存放路径
//        File dest = new File(uploadPath + "/"+ fileName);
//
//        try {
//            headerImage.transferTo(dest);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        //更新用户头像的url在数据库中
//        //http://localhost:8888/openzone/user/header/xxx.png
//        UserEntityVo user = hostHolder.getUser();
//        String headerUrl = domain + contextPath + "/user/header/" + fileName;
//        userService.updateHeader(user,headerUrl);
//
//        return "redirect:/index";
//    }

//    @RequestMapping("/header/{fileName}")
//    public void getHeaderImage(@PathVariable("fileName") String fileName, HttpServletResponse response) throws IOException {
//        //服务器存放路径
//        fileName = uploadPath + "/" + fileName;
//        //文件后缀
//        String suffix = fileName.substring(fileName.lastIndexOf('.')+1);
//        System.out.println("image/"+suffix);
//        //响应图片
//        response.setContentType("image/"+suffix);
//        FileInputStream fileInputStream = null;
//        try {
//
//            ServletOutputStream outputStream = response.getOutputStream();
//            fileInputStream  = new FileInputStream(fileName);
//
//            BufferedImage image = ImageIO.read(fileInputStream);
//            ImageIO.write(image,suffix,outputStream);
//
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }finally {
//            fileInputStream.close();
//        }
//
//    }
//    @LoginRequired
//    @RequestMapping("/updatePass")
//    public String updatePassword(String oldPass,String newPass,Model model){
//        if(!CommunityUtils.md5(oldPass + hostHolder.getUser().getSalt()).equals((hostHolder.getUser().getPassword()))){
//            model.addAttribute("passMsg","原密码错误，请重新输入！");
//            return "/site/setting";
//        }
//        userService.updatePassword(hostHolder.getUser(),newPass);
//
//        return "redirect:/logout";
//    }

    //用户详情页
    @GetMapping("/profile/{userId}")
    public String getProfilePage(@PathVariable("userId") int userId, Model model) {
        UserEntityVo userEntity = hostHolder.getUser();
        R r = interactFeignService.findUserLikeCount(userId);
        if (r.getCode() == 0) {
            model.addAttribute("likeCount", r.getData(new TypeReference<Integer>() {
            }));
            UserEntity userById = userService.findUserById(userId);
            model.addAttribute("user", userById);
        }


        //关注数
        R r2 = interactFeignService.findFolloweeCount(userId, CommunityConstants.LikeTypeEnum.USER.getCode());
        if (r2.getCode() != 0) {
            model.addAttribute("followeeCount", r2.getData(new TypeReference<Long>() {
            }));
        }
        //粉丝数
        R r3 = interactFeignService.findFollowerCount(userId, CommunityConstants.LikeTypeEnum.USER.getCode());
        if (r3.getCode() != 0) {

            model.addAttribute("followerCount", r3.getData(new TypeReference<Long>() {
            }));
        }

        //查询当前用户的关注情况 0:未关注 1：已关注
        R r4 = interactFeignService.hasFollowed(userEntity.getId(), CommunityConstants.LikeTypeEnum.USER.getCode(), userId);
        if (r4.getCode() == 0) {
            int followed = userEntity == null ? 0 : r4.getData(new TypeReference<Integer>() {
            });
            model.addAttribute("followed", followed == 0 ? false : true);
        }

        return "site/profile";
    }

    @GetMapping("/login")
    public R login(@RequestParam String username, @RequestParam String password) {
        HashMap<String, Object> login = userService.login(username, password);
        if (login == null || login.isEmpty()) {
            UserEntity userByUserName = userService.findUserByUserName(username);
            UserEntityVo userEntityVo = new UserEntityVo();
            BeanUtils.copyProperties(userByUserName, userEntityVo);
            return R.ok().setData(userEntityVo);
        } else {
            return R.error((String) login.get("msg"));
        }
    }


    @GetMapping("/activation")
    @ResponseBody
    public R activation(@RequestParam Integer userId, @RequestParam String code) {
        int result = userService.activation(userId, code);
        if (result == CommunityConstants.ActivationEnum.ACTIVATION_SUCCESS.getCode()) {
            // model.addAttribute("msg", "激活成功，您的账号可以正常使用");
            //  model.addAttribute("target", "/toLogin");
            return R.ok("激活成功，您的账号可以正常使用");
        } else if (result == CommunityConstants.ActivationEnum.ACTIVATION_REPEAT.getCode()) {
//            model.addAttribute("msg", "无效操作，该账号已经激活");
//            model.addAttribute("target", "/index");
            R.error("无效操作，该账号已经激活");
        } else {
//            model.addAttribute("msg", "激活失败，您的激活码不正确");
//            model.addAttribute("target", "/index");
            return R.error("激活失败，您的激活码不正确");
        }
        return R.error(BizCodeEnum.UNKNOW_EXCEPTION.getCode(), BizCodeEnum.UNKNOW_EXCEPTION.getMsg());

    }

    @PostMapping("/register")
    @ResponseBody
    public R register(@RequestBody UserEntityVo userEntity) throws MessagingException, javax.mail.MessagingException {
        UserEntity userEntity1 = new UserEntity();
        BeanUtils.copyProperties(userEntity, userEntity1);
        Map<String, Object> map = userService.register(userEntity1);
        if (map == null || map.isEmpty()) {
            return R.ok("注册成功，我们已向您的邮箱发送量一封激活邮件，请尽快激活");
        } else if (map.containsKey("usernameMsg")) {
            return R.error(BizCodeEnum.USERNAME_REP_EXCEPTION.getCode(), BizCodeEnum.USERNAME_REP_EXCEPTION.getMsg());
        } else if (map.containsKey("emailMsg")) {
            return R.error(BizCodeEnum.EMAIL_REP_EXCEPTION.getCode(), BizCodeEnum.EMAIL_REP_EXCEPTION.getMsg());
        }
        return R.error();
    }
    //根据用户id查找用户实体
    @GetMapping("/userentity/userid")
    @ResponseBody
    public R getUserEntityByID(@RequestParam Integer userId) {
        UserEntity userById = userService.findUserById(userId);
        return R.ok().setData(userById);
    }
    //根据用户名查找用户实体
    @GetMapping("/userentity/username")
    @ResponseBody
    public R findUserByUserName(@RequestParam String  userName) {
        UserEntity userById = userService.findUserByUserName(userName);
        return R.ok().setData(userById);
    }

    @PostMapping("/userentity/postids")
    @ResponseBody
    public R getUserEntitysByPostIDs(@RequestBody List<Integer[]> postIds) {
        Map<Integer, UserEntity> postIdAuthorTos = userService.findUserByPostIds(postIds);
        return R.ok().setData(postIdAuthorTos);
    }

    @GetMapping("/test")
    @ResponseBody
    public R getTest() {
        return R.ok().setData(" feign  test ok!!");
    }

}
