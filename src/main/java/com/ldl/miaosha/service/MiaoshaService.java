package com.ldl.miaosha.service;

import com.ldl.miaosha.domain.MiaoshaOrder;
import com.ldl.miaosha.domain.MiaoshaUser;
import com.ldl.miaosha.domain.OrderInfo;
import com.ldl.miaosha.redis.MiaoshaKey;
import com.ldl.miaosha.redis.RedisService;
import com.ldl.miaosha.util.MD5Util;
import com.ldl.miaosha.util.UUidUtil;
import com.ldl.miaosha.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Random;

@Service
public class MiaoshaService {

    @Autowired
    OrderService orderService;

    @Autowired
    GoodsService goodsService;

    @Autowired
    RedisService redisService;

    @Transactional
    public OrderInfo miaosha(MiaoshaUser miaoshaUser, GoodsVo goodsVo) {
        //减少库存,下订单，返回订单信息
        int row = goodsService.reduceStock(goodsVo);
        if (row == 0) {
            setGoodsOver(goodsVo.getId());
            return null;
        }
        return orderService.createOrder(miaoshaUser, goodsVo);
    }

    public Long getMiaoshaResult(long userId, long goodsId) {
        MiaoshaOrder miaoshaOrder = orderService.getMiaoshaOrderByUserIdAndGoodsId(userId, goodsId);
        if (miaoshaOrder == null) {
            if (getGoodsOver(goodsId)) {
                return Long.valueOf(-1);
            } else {
                return Long.valueOf(0);
            }
        } else {
            return miaoshaOrder.getOrderId();
        }
    }

    public void reset(List<GoodsVo> goodsVoList) {
        goodsService.resetStock(goodsVoList);
        orderService.deleteOrders();
    }

    private void setGoodsOver(long goodsId) {
        redisService.set(MiaoshaKey.GoodsOver, "" + goodsId, true);
    }

    private boolean getGoodsOver(long goodsId) {
        return redisService.exists(MiaoshaKey.GoodsOver, "" + goodsId);
    }

    public boolean checkPath(MiaoshaUser miaoshaUser, long goodsId, String path) {
        if (miaoshaUser == null || path == null || goodsId <= 0) {
            return false;
        }
        String pathRedis = redisService.get(MiaoshaKey.GetMiaoshaPath, "" + miaoshaUser.getId() + " " + goodsId, String.class);

        return path.equals(pathRedis);
    }

    public String createMiaoshaPath(MiaoshaUser miaoshaUser, long goodsId) {
        if (miaoshaUser == null || goodsId <= 0) {
            return null;
        }
        String path = MD5Util.MD5(UUidUtil.uuid() + "123456");
        redisService.set(MiaoshaKey.GetMiaoshaPath, "" + miaoshaUser.getId() + " " + goodsId, path);

        return path;
    }

    public BufferedImage createVerifyCode(MiaoshaUser miaoshaUser, long goodsId) {
        if (miaoshaUser == null || goodsId <= 0) {
            return null;
        }
        int width = 80;
        int height = 32;
        //create the image
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics g = image.getGraphics();
        // set the background color
        g.setColor(new Color(0xDCDCDC));
        g.fillRect(0, 0, width, height);
        // draw the border
        g.setColor(Color.black);
        g.drawRect(0, 0, width - 1, height - 1);
        // create a random instance to generate the codes
        Random rdm = new Random();
        // make some confusion
        for (int i = 0; i < 50; i++) {
            int x = rdm.nextInt(width);
            int y = rdm.nextInt(height);
            g.drawOval(x, y, 0, 0);
        }
        // generate a random code
        String verifyCode = generateVerifyCode(rdm);
        g.setColor(new Color(0, 100, 0));
        g.setFont(new Font("Candara", Font.BOLD, 24));
        g.drawString(verifyCode, 8, 24);
        g.dispose();
        //把验证码存到redis中
        int ans = calc(verifyCode);
        redisService.set(MiaoshaKey.GetMiaoshaVerifyCode, miaoshaUser.getId() + "," + goodsId, ans);
        //输出图片	
        return image;
    }

    private static char[] ops = new char[] {'+', '-', '*'};

    private String generateVerifyCode(Random rdm) {
        int num1 = rdm.nextInt(10);
        int num2 = rdm.nextInt(10);
        int num3 = rdm.nextInt(10);
        char op1 = ops[rdm.nextInt(3)];
        char op2 = ops[rdm.nextInt(3)];
        String exp = ""+ num1 + op1 + num2 + op2 + num3;
        return exp;
    }

    private int calc(String exp) {
        try {
            ScriptEngineManager manager = new ScriptEngineManager();
            ScriptEngine engine = manager.getEngineByName("JavaScript");
            return (Integer) engine.eval(exp);
        } catch (Exception exception) {
            exception.printStackTrace();
            return 0;
        }
    }

    public boolean checkVerifyCode(MiaoshaUser miaoshaUser, long goodsId, int verifyCode) {
        if (miaoshaUser == null || goodsId <= 0) {
            return false;
        }
        Integer ans = redisService.get(MiaoshaKey.GetMiaoshaVerifyCode, "" + miaoshaUser.getId() + "," + goodsId, Integer.class);
        System.out.println();
        if (ans == null || ans - verifyCode != 0) {
            return false;
        }

        redisService.delete(MiaoshaKey.GetMiaoshaVerifyCode, "" + miaoshaUser.getId() + "," + goodsId);
        return true;
    }
}
