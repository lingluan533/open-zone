package com.zms.openzone.message.interceptor;

import com.zms.openzone.common.constants.AuthServerConstant;
import com.zms.openzone.common.entity.UserEntityVo;

import com.zms.openzone.message.utils.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;


import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * @author: zms
 * @create: 2022/3/1 20:29
 */
@Component
public class LoginUserInterceptor implements HandlerInterceptor {
    //    default boolean preHandle
//
//    default void postHandle
//
//    default void afterCompletion
//}
    //HandlerInterceptor这个接口定义了三个default修饰的抽象方法 ， 所以可以按需实现这三个方法，实现或不实现均可

    @Autowired
    private HostHolder hostHolder;
    @Resource
    private Environment env;
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //获取session判断登陆状态
        HttpSession session = request.getSession();
        UserEntityVo userEntity = (UserEntityVo) session.getAttribute(AuthServerConstant.LOGIN_USER);
        if (userEntity != null) {
            //注入当前登录的用户到threadLocal
            hostHolder.setUser(userEntity);
            //放行
            return true;
        } else {
            // 没登陆就去登录
            System.out.println("还没有登录，快去登录吧！");
            session.setAttribute("msg", AuthServerConstant.NOT_LOGIN);
            response.sendRedirect(env.getProperty("authUrl")+"/auth/toLogin");
            return false;
        }

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        System.out.println("当前线程清除hostHolder");
        hostHolder.clear();
    }
}
