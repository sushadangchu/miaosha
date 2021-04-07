package com.ldl.miaosha.redis;

public interface KeyPrefix {

    public int getExpireSeconds();

    public String getPrefix();
}
