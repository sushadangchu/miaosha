package com.ldl.miaosha.controller;

import com.ldl.miaosha.result.Result;
import com.ldl.miaosha.service.MiaoshaUserService;
import com.ldl.miaosha.vo.LoginVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@Controller
@RequestMapping("/login")
public class LoginController {

    @Autowired
    MiaoshaUserService miaoshaUserService;

    @RequestMapping("/to_login")
    public String toLogin() {
        return "login";
    }

    @RequestMapping("/do_login")
    @ResponseBody
    public Result<Boolean> doLogin(HttpServletResponse httpServletResponse, @Valid LoginVo loginVo) {
        miaoshaUserService.login(httpServletResponse, loginVo);
        return Result.success(true);
    }
}
