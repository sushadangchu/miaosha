package com.ldl.miaosha.access;

import com.ldl.miaosha.domain.MiaoshaUser;

public class UserContext {

    private static ThreadLocal<MiaoshaUser> userHolder = new ThreadLocal<>();

    public static void setUser(MiaoshaUser miaoshaUser) {
        userHolder.set(miaoshaUser);
    }

    public static MiaoshaUser getUser() {
        return userHolder.get();
    }
}
