package com.ldl.miaosha.service;

import com.ldl.miaosha.domain.MiaoshaUser;
import com.ldl.miaosha.domain.OrderInfo;
import com.ldl.miaosha.exception.GolbalException;
import com.ldl.miaosha.result.CodeMsg;
import com.ldl.miaosha.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MiaoshaService {

    @Autowired
    OrderService orderService;

    @Autowired
    GoodsService goodsService;

    @Transactional
    public OrderInfo miaosha(MiaoshaUser miaoshaUser, GoodsVo goodsVo) {
        //减少库存,下订单，返回订单信息
        int row = goodsService.reduceStock(goodsVo);
        if (row == 0) {
            throw new GolbalException(CodeMsg.MIAO_SHA_OVER);
        }
        return orderService.createOrder(miaoshaUser, goodsVo);
    }
}
