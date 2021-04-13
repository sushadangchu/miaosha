package com.ldl.miaosha.service;

import com.ldl.miaosha.dao.MiaoshaUserDao;
import com.ldl.miaosha.domain.MiaoshaUser;
import com.ldl.miaosha.exception.GolbalException;
import com.ldl.miaosha.redis.MiaoshaUserKey;
import com.ldl.miaosha.redis.RedisService;
import com.ldl.miaosha.result.CodeMsg;
import com.ldl.miaosha.util.MD5Util;
import com.ldl.miaosha.util.UUidUtil;
import com.ldl.miaosha.vo.LoginVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

@Service
public class MiaoshaUserService {

    public static final String COOKI_NAME_TOKEN = "token";

    @Autowired
    MiaoshaUserDao miaoshaUserDao;

    @Autowired
    RedisService redisService;

    public MiaoshaUser getById(Long id) {
        MiaoshaUser miaoshaUser = redisService.get(MiaoshaUserKey.getById, "" + id, MiaoshaUser.class);
        if (miaoshaUser != null) {
            return miaoshaUser;
        }
        miaoshaUser =  miaoshaUserDao.getById(id);
        if (miaoshaUser != null) {
            redisService.set(MiaoshaUserKey.getById, "" + id, miaoshaUser);
        }

        return miaoshaUser;
    }

    public boolean updatePassword(String token, long id, String fromPass) {
        MiaoshaUser miaoshaUser = getById(id);
        if (miaoshaUser == null) {
            throw new GolbalException(CodeMsg.MOBILE_NOT_EXIST);
        }
        MiaoshaUser newMiaoshaUser = new MiaoshaUser();
        newMiaoshaUser.setId(id);
        newMiaoshaUser.setPassword(MD5Util.fromPassToDbPass(fromPass, miaoshaUser.getSalt()));
        miaoshaUserDao.update(newMiaoshaUser);

        redisService.delete(MiaoshaUserKey.getById, "" + id);
        miaoshaUser.setPassword(newMiaoshaUser.getPassword());
        redisService.set(MiaoshaUserKey.token, token, miaoshaUser);

        return true;
    }

    public MiaoshaUser getByToken(HttpServletResponse httpServletResponse, String token) {
        if (StringUtils.isEmpty(token)) {
            return null;
        }
        MiaoshaUser miaoshaUser = redisService.get(MiaoshaUserKey.token, token, MiaoshaUser.class);
        if (miaoshaUser != null) {
            addCookie(httpServletResponse, token, miaoshaUser);
        }

        return miaoshaUser;
    }

    public String login(HttpServletResponse httpServletResponse, LoginVo loginVo) {
        if (loginVo == null) {
            throw new GolbalException(CodeMsg.SERVER_ERROR);
        }
        String mobile = loginVo.getMobile();
        String formPass = loginVo.getPassword();

        if (formPass == null) {
            throw new GolbalException(CodeMsg.PASSWORD_EMPTY);
        }

        MiaoshaUser miaoshaUser = getById(Long.parseLong(mobile));

        if (miaoshaUser == null) {
            throw new GolbalException(CodeMsg.MOBILE_NOT_EXIST);
        }

        String dbPass = miaoshaUser.getPassword();
        String dbSalt = miaoshaUser.getSalt();
        String calcPass = MD5Util.fromPassToDbPass(formPass, dbSalt);
        if (!calcPass.equals(dbPass)) {
            throw new GolbalException(CodeMsg.PASSWORD_ERROR);
        }
        String token = UUidUtil.uuid();
        addCookie(httpServletResponse, token, miaoshaUser);
        return token;
    }

    private void addCookie(HttpServletResponse httpServletResponse, String token, MiaoshaUser miaoshaUser) {
        redisService.set(MiaoshaUserKey.token, token, miaoshaUser);
        Cookie cookie = new Cookie(COOKI_NAME_TOKEN, token);
        cookie.setMaxAge(MiaoshaUserKey.token.getExpireSeconds());
        cookie.setPath("/");
        httpServletResponse.addCookie(cookie);
    }
}
