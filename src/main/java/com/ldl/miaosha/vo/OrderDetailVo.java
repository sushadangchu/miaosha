package com.ldl.miaosha.vo;

import com.ldl.miaosha.domain.Goods;
import com.ldl.miaosha.domain.OrderInfo;

public class OrderDetailVo {
    private OrderInfo orderInfo;
    private GoodsVo goodsVo;

    public OrderInfo getOrderInfo() {
        return orderInfo;
    }

    public void setOrderInfo(OrderInfo orderInfo) {
        this.orderInfo = orderInfo;
    }

    public GoodsVo getGoodsVo() {
        return goodsVo;
    }

    public void setGoodsVo(GoodsVo goodsVo) {
        this.goodsVo = goodsVo;
    }
}
