package com.ldl.miaosha.service;

import com.ldl.miaosha.dao.OrderDao;
import com.ldl.miaosha.domain.MiaoshaOrder;
import com.ldl.miaosha.domain.MiaoshaUser;
import com.ldl.miaosha.domain.OrderInfo;
import com.ldl.miaosha.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
public class OrderService {

    @Autowired
    OrderDao orderDao;

    @Transactional
    public OrderInfo createOrder(MiaoshaUser miaoshaUser, GoodsVo goodsVo) {
        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setCreateDate(new Date());
        orderInfo.setOrderChannel(1);
        orderInfo.setDeliveryAddrId(0L);
        orderInfo.setGoodsName(goodsVo.getGoodsName());
        orderInfo.setGoodsPrice(goodsVo.getMiaoshaPrice());
        orderInfo.setGoodsCount(1);
        orderInfo.setGoodsId(goodsVo.getId());
        orderInfo.setUserId(miaoshaUser.getId());
        orderInfo.setStatus(0);
        Long orderId = orderDao.insertOrder(orderInfo);

        MiaoshaOrder miaoshaOrder = new MiaoshaOrder();
        miaoshaOrder.setOrderId(orderId);
        miaoshaOrder.setGoodsId(goodsVo.getId());
        miaoshaOrder.setUserId(miaoshaUser.getId());
        orderDao.insertMiaoshaOrder(miaoshaOrder);

        return orderInfo;
    }

    public MiaoshaOrder getMiaoshaOrderByUserIdAndGoodsId(long userId, long goodsId) {
        return orderDao.getMiaoshaOrderByUserIdAndGoodsId(userId, goodsId);
    }
}
