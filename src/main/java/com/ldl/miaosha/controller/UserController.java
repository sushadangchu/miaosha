package com.ldl.miaosha.controller;

import com.ldl.miaosha.domain.MiaoshaUser;
import com.ldl.miaosha.result.Result;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/user")
public class UserController {

    @RequestMapping("/info")
    @ResponseBody
    public Result<MiaoshaUser> toLogin(Model model, MiaoshaUser miaoshaUser) {
        return Result.success(miaoshaUser);
    }
}
