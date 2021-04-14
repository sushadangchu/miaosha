package com.ldl.miaosha.controller;

import com.ldl.miaosha.domain.MiaoshaOrder;
import com.ldl.miaosha.domain.MiaoshaUser;
import com.ldl.miaosha.domain.OrderInfo;
import com.ldl.miaosha.result.CodeMsg;
import com.ldl.miaosha.result.Result;
import com.ldl.miaosha.service.GoodsService;
import com.ldl.miaosha.service.MiaoshaService;
import com.ldl.miaosha.service.OrderService;
import com.ldl.miaosha.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/miaosha")
public class MiaoshaController {

    @Autowired
    MiaoshaService miaoshaService;

    @Autowired
    GoodsService goodsService;

    @Autowired
    OrderService orderService;

    /*
     * 5000 * 10
     * 2449 qps
     * 目前的秒杀功能会导致秒杀商品中的库存为负数
     */
    @RequestMapping(value = "/do_miaosha", method = RequestMethod.POST)
    @ResponseBody
    public Result<OrderInfo> doMiaosha(Model model, MiaoshaUser miaoshaUser, @RequestParam("goodsId") long goodsId) {
        model.addAttribute("user", miaoshaUser);
        if(miaoshaUser == null) {
            return Result.error(CodeMsg.SESSION_ERROR);
        }

        GoodsVo goodsVo = goodsService.getGoodsVoByGoodsId(goodsId);
        if (goodsVo.getStockCount() <= 0) {
            return Result.error(CodeMsg.MIAO_SHA_OVER);
        }

        MiaoshaOrder miaoshaOrder = orderService.getMiaoshaOrderByUserIdAndGoodsId(miaoshaUser.getId(), goodsVo.getId());
        if (miaoshaOrder != null) {
            return Result.error(CodeMsg.REPEATE_MIAOSHA);
        }

        OrderInfo orderInfo = miaoshaService.miaosha(miaoshaUser, goodsVo);

        return Result.success(orderInfo);
    }
}
