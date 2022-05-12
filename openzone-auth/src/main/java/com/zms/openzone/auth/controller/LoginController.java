package com.zms.openzone.auth.controller;


import com.alibaba.fastjson.TypeReference;

import com.zms.openzone.auth.feign.UserFeignService;

import com.zms.openzone.common.constants.AuthServerConstant;
import com.zms.openzone.common.constants.CommunityConstants;

import com.zms.openzone.common.entity.UserEntityVo;
import com.zms.openzone.common.exception.BizCodeEnum;
import com.zms.openzone.common.utils.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;


/**
 * @author: zms
 * @create: 2022/1/19 12:13
 */
@Controller
@RequestMapping("auth")
public class LoginController {
    @Autowired
    private UserFeignService userFeignService;


    @RequestMapping(value = "/toRegister", method = RequestMethod.GET)
    public String getregisterPage() {
        return "site/register";
    }

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public String register(Model model, UserEntityVo user) {

        R r = userFeignService.register(user);
        if (r.getCode() == 0) {
            model.addAttribute("msg", "注册成功，我们已向您的邮箱发送量一封激活邮件，请尽快激活");
            model.addAttribute("target", "http://interact.lingluan.vip");
            return "site/operate-result";
        } else {

            model.addAttribute("usernameMsg", (int) r.get("code") == BizCodeEnum.USERNAME_REP_EXCEPTION.getCode() ? BizCodeEnum.USERNAME_REP_EXCEPTION.getMsg() : "");
            model.addAttribute("emailMsg", (int) r.get("code") == BizCodeEnum.EMAIL_REP_EXCEPTION.getCode() ? BizCodeEnum.EMAIL_REP_EXCEPTION.getMsg() : "");
            return "site/register";
        }
    }

    //激活装态
    @GetMapping("/activation/{userId}/{code}")
    public String activation(Model model, @PathVariable("userId") int userId,
                             @PathVariable("code") String code) {
        R r = userFeignService.activation(userId, code);

        if (r.getCode() == CommunityConstants.ActivationEnum.ACTIVATION_SUCCESS.getCode()) {
            model.addAttribute("msg", "激活成功，您的账号可以正常使用");
            model.addAttribute("target", "http://auth.openzone.com/auth/toLogin");
        } else {
            model.addAttribute("msg", r.get("msg"));
            model.addAttribute("target", "http://interact.openzone.com");
        }
        return "site/operate-result";
    }

    //惊奇地发现这个路径没有生效，难道是starter帮我们连这个也做了？ 注释后发现刷新验证码仍然可用，说明starter连路径也帮我们做了，真不错。
//    @RequestMapping("/kaptcha")
//    public void getKaptch(HttpServletResponse response, HttpSession session){
//        //生产验证码
//
//        String text = producer.createText();
//        System.out.println("生成的验证码："+text);
//        BufferedImage image =  producer.createImage(text);
//        //将验证码存入session
//        session.setAttribute("kaptcha",text);
//        try {
//            response.setDateHeader("Expires", 0);
//            response.setContentType("image/png");
//            OutputStream outputStream = response.getOutputStream();//获取输出流
//            ImageIO.write(image, "png", outputStream);//输出图片
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
    @GetMapping("/toLogin")
    public String getLoginPage() {
        return "site/login";
    }

    @RequestMapping("/login")
    public String login(String username, String password, String code, boolean rememberme,
                        HttpSession session, HttpServletResponse response, Model model) {

        //1验证验证码是否正确
        if (!code.equalsIgnoreCase((String) session.getAttribute("KAPTCHA_SESSION_KEY"))) {
            model.addAttribute("codeMsg", "验证码不正确！");
            return "site/login";
        }
        //2.验证用户名密码
        int expiredSeconds = rememberme ? CommunityConstants.LoginExpireEnum.REMEMBER_EXIREDSECONDS.getExpiredseconds() : CommunityConstants.LoginExpireEnum.DEFAULT_EXPIREDSECONDS.getExpiredseconds();
        R r = userFeignService.login(username, password);
        if (r.getCode() == 0) {
            // 登录成功
            UserEntityVo userEntity = r.getData("data", new TypeReference<UserEntityVo>() {
            });
            // 放入session   这里的session其实是spring session封装的session对象
            System.out.println(session.getClass().getName());

            session.setAttribute(AuthServerConstant.LOGIN_USER, userEntity);//loginUser
            System.out.println(("\n欢迎 [" + userEntity.getUsername() + "] 登录"));
            return "redirect:http://interact.openzone.com";
        } else {
            model.addAttribute("usernameMsg", r.get("msg"));
            model.addAttribute("userpasswordMsg", r.get("msg"));
            return "site/login";
        }
    }

//    @RequestMapping("/logout")
//    public String logout(@CookieValue("ticket") String ticket){
//            loginService.updateStatus(ticket,1);
//        SecurityContextHolder.clearContext();//清除授权结果
//        return "redirect:/toLogin";
//    }

}
