package com.ldl.miaosha.redis;

public class GoodsKey extends BasePrefix{

    public GoodsKey(int expireSeconds, String prefix) {
        super(expireSeconds, prefix);
    }

    public static GoodsKey GoodsList = new GoodsKey(60, "gl");
    public static GoodsKey GoodsDetail = new GoodsKey(0, "gd");
    public static GoodsKey GoodsStock = new GoodsKey(0, "gs");
}
