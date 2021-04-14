package com.ldl.miaosha.redis;

public class OrderKey extends BasePrefix{
    public OrderKey(String prefix) {
        super(prefix);
    }

    public static OrderKey getOrderById = new OrderKey("oi");
    public static OrderKey getMiaoshaOrderByUserIdGoodsId = new OrderKey("msug");
}
