package com.ldl.miaosha.service;

import com.ldl.miaosha.dao.GoodsDao;
import com.ldl.miaosha.domain.MiaoshaUser;
import com.ldl.miaosha.domain.OrderInfo;
import com.ldl.miaosha.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MiaoshaService {

    @Autowired
    OrderService orderService;

    @Autowired
    GoodsService goodsService;

    @Transactional
    public OrderInfo miaosha(MiaoshaUser miaoshaUser, GoodsVo goodsVo) {
        //下订单，减少库存，返回订单信息
        OrderInfo orderInfo = orderService.createOrder(miaoshaUser, goodsVo);
        goodsService.reduceStock(goodsVo);
        return orderInfo;
    }
}
