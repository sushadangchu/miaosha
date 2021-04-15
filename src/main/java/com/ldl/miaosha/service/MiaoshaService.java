package com.ldl.miaosha.service;

import com.ldl.miaosha.domain.MiaoshaOrder;
import com.ldl.miaosha.domain.MiaoshaUser;
import com.ldl.miaosha.domain.OrderInfo;
import com.ldl.miaosha.exception.GolbalException;
import com.ldl.miaosha.redis.MiaoshaKey;
import com.ldl.miaosha.redis.RedisService;
import com.ldl.miaosha.result.CodeMsg;
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

    @Autowired
    RedisService redisService;

    @Transactional
    public OrderInfo miaosha(MiaoshaUser miaoshaUser, GoodsVo goodsVo) {
        //减少库存,下订单，返回订单信息
        int row = goodsService.reduceStock(goodsVo);
        if (row == 0) {
            setGoodsOver(goodsVo.getId());
            return null;
        }
        return orderService.createOrder(miaoshaUser, goodsVo);
    }

    public Long getMiaoshaResult(long userId, long goodsId) {
        MiaoshaOrder miaoshaOrder = orderService.getMiaoshaOrderByUserIdAndGoodsId(userId, goodsId);
        if (miaoshaOrder == null) {
            if (getGoodsOver(goodsId)) {
                return Long.valueOf(-1);
            } else {
                return Long.valueOf(0);
            }
        } else {
            return miaoshaOrder.getOrderId();
        }
    }

    public void reset(List<GoodsVo> goodsVoList) {
        goodsService.resetStock(goodsVoList);
        orderService.deleteOrders();
    }

    private void setGoodsOver(long goodsId) {
        redisService.set(MiaoshaKey.GoodsOver, "" + goodsId, true);
    }

    private boolean getGoodsOver(long goodsId) {
        return redisService.exists(MiaoshaKey.GoodsOver, "" + goodsId);
    }
}
