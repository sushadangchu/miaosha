package com.ldl.miaosha.controller;

import com.ldl.miaosha.domain.User;
import com.ldl.miaosha.redis.RedisService;
import com.ldl.miaosha.redis.UserKey;
import com.ldl.miaosha.result.CodeMsg;
import com.ldl.miaosha.result.Result;
import com.ldl.miaosha.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/demo")
public class DemoController {

    @Autowired
    UserService userService;
    @Autowired
    RedisService redisService;

    @RequestMapping("/thymeleaf")
    public String thymeleaf(Model model) {
        model.addAttribute("name", "ldl");
        return "hello";
    }

    @RequestMapping("/db/get")
    @ResponseBody
    public Result<User> dbGet() {

        User user = userService.getById(1);

        return Result.success(user);
    }

    @RequestMapping("/redis/get")
    @ResponseBody
    public Result<String> redisGet() {

        String v1 = redisService.get(UserKey.getById, "" + 1, String.class);

        return Result.success(v1);
    }

    @RequestMapping("/redis/set")
    @ResponseBody
    public Result<Boolean> redisSet() {

        Boolean result = redisService.set(UserKey.getById, "" + 1, "hello neu");

        return Result.success(result);
    }

    @RequestMapping("/db/tx")
    @ResponseBody
    public Result<Boolean> dbTx() {

        userService.tx();

        return Result.success(true);
    }
}
