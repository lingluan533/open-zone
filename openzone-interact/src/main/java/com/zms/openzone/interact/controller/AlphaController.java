package com.zms.openzone.interact.controller;


import com.zms.openzone.interact.utils.R;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author: zms
 * @create: 2022/1/28 18:46
 */
@Controller
@RequestMapping("/test")
public class AlphaController {

    @RequestMapping("/ajax")
    @ResponseBody
    public R testAjax(String name) {

        return R.ok().put("data", name + "后台");
    }
}
