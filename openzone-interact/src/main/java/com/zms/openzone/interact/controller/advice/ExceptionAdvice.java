package com.zms.openzone.interact.controller.advice;


import com.zms.openzone.interact.utils.R;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author: zms
 * 异常的全局处理
 * @create: 2022/2/7 14:01
 */
@ControllerAdvice(annotations = Controller.class)
public class ExceptionAdvice {
    private static final Logger logger = LoggerFactory.getLogger(ExceptionAdvice.class);

    @ExceptionHandler({Exception.class})
    public void handleException(Exception e, HttpServletRequest request, HttpServletResponse response) throws IOException {
        logger.error("服务器异常:" + e.getMessage());
        for (StackTraceElement element : e.getStackTrace()) {
            logger.error(element.toString());
        }
        String requestheader = request.getHeader("x-requested-with");
        if ("XMLHttpRequest".equals(requestheader)) {//如果是ajax异步请求
            response.setContentType("application/plain;charset=UTF-8");
            PrintWriter writer = response.getWriter();
            writer.write(R.error().toString());
        } else {
            response.sendRedirect(request.getContextPath() + "/toerror");
        }

    }

}
