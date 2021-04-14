package com.ldl.miaosha.vo;

import com.ldl.miaosha.domain.Goods;
import com.ldl.miaosha.domain.MiaoshaUser;

public class GoodsDetailVo {
    MiaoshaUser miaoshaUser;
    Goods goods;
    //0为未开始，1为进行中，2位已结束
    int miaoshaStatus;
    int remainSeconds;

    public MiaoshaUser getMiaoshaUser() {
        return miaoshaUser;
    }

    public void setMiaoshaUser(MiaoshaUser miaoshaUser) {
        this.miaoshaUser = miaoshaUser;
    }

    public Goods getGoods() {
        return goods;
    }

    public void setGoods(Goods goods) {
        this.goods = goods;
    }

    public int getMiaoshaStatus() {
        return miaoshaStatus;
    }

    public void setMiaoshaStatus(int miaoshaStatus) {
        this.miaoshaStatus = miaoshaStatus;
    }

    public int getRemainSeconds() {
        return remainSeconds;
    }

    public void setRemainSeconds(int remainSeconds) {
        this.remainSeconds = remainSeconds;
    }
}
