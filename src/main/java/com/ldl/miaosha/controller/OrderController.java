package com.ldl.miaosha.controller;

import com.ldl.miaosha.domain.MiaoshaUser;
import com.ldl.miaosha.domain.OrderInfo;
import com.ldl.miaosha.redis.GoodsKey;
import com.ldl.miaosha.redis.RedisService;
import com.ldl.miaosha.result.CodeMsg;
import com.ldl.miaosha.result.Result;
import com.ldl.miaosha.service.GoodsService;
import com.ldl.miaosha.service.OrderService;
import com.ldl.miaosha.vo.GoodsDetailVo;
import com.ldl.miaosha.vo.GoodsVo;
import com.ldl.miaosha.vo.OrderDetailVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.thymeleaf.spring4.context.SpringWebContext;
import org.thymeleaf.spring4.view.ThymeleafViewResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Controller
@RequestMapping("/order")
public class OrderController {

    @Autowired
    GoodsService goodsService;

    @Autowired
    RedisService redisService;

    @Autowired
    OrderService orderService;

    @RequestMapping(value = "/detail")
    @ResponseBody
    public Result<OrderDetailVo> toDetail(MiaoshaUser miaoshaUser, @RequestParam("orderId") long orderId) {
        if (miaoshaUser == null) {
            return Result.error(CodeMsg.SESSION_ERROR);
        }
        OrderInfo orderInfo = orderService.getOrderById(orderId);
        if (orderInfo == null) {
            return Result.error(CodeMsg.ORDER_NOT_EXIST);
        }
        GoodsVo goods = goodsService.getGoodsVoByGoodsId(orderInfo.getGoodsId());
        OrderDetailVo orderDetailVo = new OrderDetailVo();

        orderDetailVo.setGoodsVo(goods);
        orderDetailVo.setOrderInfo(orderInfo);

        return Result.success(orderDetailVo);
    }
}
