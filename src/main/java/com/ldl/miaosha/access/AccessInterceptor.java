package com.ldl.miaosha.access;

import com.alibaba.fastjson.JSON;
import com.ldl.miaosha.domain.MiaoshaUser;
import com.ldl.miaosha.redis.AccessKey;
import com.ldl.miaosha.redis.RedisService;
import com.ldl.miaosha.result.CodeMsg;
import com.ldl.miaosha.result.Result;
import com.ldl.miaosha.service.MiaoshaUserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;

@Service
public class AccessInterceptor extends HandlerInterceptorAdapter {

    @Autowired
    MiaoshaUserService miaoshaUserService;

    @Autowired
    RedisService redisService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (handler instanceof HandlerMethod) {
            MiaoshaUser miaoshaUser = getUser(request, response);
            UserContext.setUser(miaoshaUser);
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            AccessLimit accessLimit = handlerMethod.getMethodAnnotation(AccessLimit.class);
            if (accessLimit == null) {
                return true;
            }
            int seconds = accessLimit.seconds();
            int maxCount = accessLimit.maxCount();
            boolean needLogin = accessLimit.needLogin();
            String uri = request.getRequestURI();
            if (needLogin) {
                if (miaoshaUser == null) {
                    render(response, CodeMsg.SESSION_ERROR);
                    return false;
                }
                uri += "_" + miaoshaUser.getId();
            }
            AccessKey accessKey = AccessKey.withExpire(seconds);
            Integer count = redisService.get(accessKey, uri, Integer.class);
            if (count == null) {
                redisService.set(accessKey, uri, 1);
            } else if (count < maxCount) {
                redisService.incr(accessKey, uri);
            } else {
                render(response, CodeMsg.ACCESS_LIMIT_REACHED);
            }
        }

        return true;
    }

    private void render(HttpServletResponse response, CodeMsg codeMsg) throws Exception {
        response.setContentType("application/json;charset=UTF-8");
        OutputStream out = response.getOutputStream();
        String str  = JSON.toJSONString(Result.error(codeMsg));
        out.write(str.getBytes("UTF-8"));
        out.flush();
        out.close();
    }

    private MiaoshaUser getUser(HttpServletRequest request, HttpServletResponse response) {
        String parameterToken = request.getParameter(MiaoshaUserService.COOKI_NAME_TOKEN);
        String cookieToken = getCookieValue(request, MiaoshaUserService.COOKI_NAME_TOKEN);
        if (StringUtils.isEmpty(parameterToken) && StringUtils.isEmpty(cookieToken)) {
            return null;
        }
        String token = StringUtils.isEmpty(parameterToken) ? cookieToken : parameterToken;
        MiaoshaUser miaoshaUser = miaoshaUserService.getByToken(response, token);
        return miaoshaUser;
    }

    private String getCookieValue(HttpServletRequest request, String cookieName) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null || cookies.length <= 0) {
            return null;
        }
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(cookieName)) {
                return cookie.getValue();
            }
        }

        return null;
    }
}
