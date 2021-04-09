package com.ldl.miaosha.controller;

import com.ldl.miaosha.domain.MiaoshaUser;
import com.ldl.miaosha.service.GoodsService;
import com.ldl.miaosha.service.MiaoshaUserService;
import com.ldl.miaosha.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/goods")
public class GoodsController {

    @Autowired
    GoodsService goodsService;

    @RequestMapping("/to_list")
    public String toLogin(Model model, MiaoshaUser miaoshaUser) {
        model.addAttribute("user", miaoshaUser);
        List<GoodsVo> goodsList = goodsService.listGoodsVo();
        System.out.println(goodsList.size());
        model.addAttribute("goodsList", goodsList);
        return "goods_list";
    }

    @RequestMapping("/to_detail/{goodsId}")
    public String toDetail(Model model, MiaoshaUser miaoshaUser, @PathVariable("goodsId") Long goodsId) {
        model.addAttribute("user", miaoshaUser);
        GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);
        model.addAttribute("goods", goods);

        Long startAt = goods.getStartDate().getTime();
        Long endAt = goods.getEndDate().getTime();
        Long now = System.currentTimeMillis();

        int miaoshaStatus = 0;
        int remainSeconds = 0;

        if (now < startAt) {
            miaoshaStatus = 0;
            remainSeconds = (int) ((startAt - now) / 1000);
        } else if (now > endAt) {
            miaoshaStatus = 2;
            remainSeconds = -1;
        } else {
            miaoshaStatus = 1;
        }

        model.addAttribute("miaoshaStatus", miaoshaStatus);
        model.addAttribute("remainSeconds", remainSeconds);

        return "goods_detail";
    }
}
