package com.ldl.miaosha.redis;

public class MiaoshaKey extends BasePrefix{

    public MiaoshaKey(int expireSeconds, String prefix) {
        super(expireSeconds, prefix);
    }

    public static MiaoshaKey GoodsOver = new MiaoshaKey(0, "go");
    public static MiaoshaKey GetMiaoshaPath = new MiaoshaKey(60, "mp");
    public static MiaoshaKey GetMiaoshaVerifyCode = new MiaoshaKey(60, "mvc");
}
