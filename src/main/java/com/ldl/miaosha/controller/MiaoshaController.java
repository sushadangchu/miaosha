package com.ldl.miaosha.controller;

import com.ldl.miaosha.domain.MiaoshaOrder;
import com.ldl.miaosha.domain.MiaoshaUser;
import com.ldl.miaosha.domain.OrderInfo;
import com.ldl.miaosha.rabbitmq.MQSender;
import com.ldl.miaosha.rabbitmq.MiaoshaMessage;
import com.ldl.miaosha.redis.GoodsKey;
import com.ldl.miaosha.redis.MiaoshaKey;
import com.ldl.miaosha.redis.OrderKey;
import com.ldl.miaosha.redis.RedisService;
import com.ldl.miaosha.result.CodeMsg;
import com.ldl.miaosha.result.Result;
import com.ldl.miaosha.service.GoodsService;
import com.ldl.miaosha.service.MiaoshaService;
import com.ldl.miaosha.service.OrderService;
import com.ldl.miaosha.vo.GoodsVo;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.CoderMalfunctionError;
import java.util.HashMap;
import java.util.List;

@Controller
@RequestMapping("/miaosha")
public class MiaoshaController implements InitializingBean {

    @Autowired
    MiaoshaService miaoshaService;

    @Autowired
    GoodsService goodsService;

    @Autowired
    OrderService orderService;

    @Autowired
    RedisService redisService;

    @Autowired
    MQSender mqSender;

    //redis库存为负数后设置对应商品为true，并且请求直接返回，降低redis访问流量
    private HashMap<Long, Boolean> localOverMap = new HashMap<>();

    //初始化
    public void afterPropertiesSet() throws Exception {
        List<GoodsVo> goodsVoList = goodsService.listGoodsVo();
        if (goodsVoList == null) {
            return;
        }
        for (GoodsVo goodsVo : goodsVoList) {
            redisService.set(GoodsKey.GoodsStock, "" + goodsVo.getId(), goodsVo.getStockCount());
            localOverMap.put(goodsVo.getId(), false);
        }
    }

    /*
     * 5000 * 10
     * 2449 qps
     * 最终qps为5200,优化到最佳
     */
    @RequestMapping(value = "/do_miaosha", method = RequestMethod.POST)
    @ResponseBody
    public Result<Integer> doMiaosha(Model model, MiaoshaUser miaoshaUser, @RequestParam("goodsId") long goodsId) {
        model.addAttribute("user", miaoshaUser);
        if(miaoshaUser == null) {
            return Result.error(CodeMsg.SESSION_ERROR);
        }
        if (localOverMap.get(goodsId)) {
            return Result.error(CodeMsg.MIAO_SHA_OVER);
        }
        //将商品库存加入redis缓存
        Long stock = redisService.decr(GoodsKey.GoodsStock, "" + goodsId);
        if (stock < 0) {
            localOverMap.put(goodsId, true);
            return Result.error(CodeMsg.MIAO_SHA_OVER);
        }

        MiaoshaOrder miaoshaOrder = orderService.getMiaoshaOrderByUserIdAndGoodsId(miaoshaUser.getId(), goodsId);
        if (miaoshaOrder != null) {
            return Result.error(CodeMsg.REPEATE_MIAOSHA);
        }

        MiaoshaMessage miaoshaMessage = new MiaoshaMessage();
        miaoshaMessage.setMiaoshaUser(miaoshaUser);
        miaoshaMessage.setGoodsId(goodsId);
        mqSender.SendMiaoshaMessage(miaoshaMessage);
        //排队
        return Result.success(0);
    }

    /**
     * > 0 成功 orderId
     * = 0 排队中
     * - 1 秒杀结束
     */
    @RequestMapping(value = "/result", method = RequestMethod.GET)
    @ResponseBody
    public Result<Long> miaoshaResult(Model model, MiaoshaUser miaoshaUser, @RequestParam("goodsId") long goodsId) {
        model.addAttribute("user", miaoshaUser);
        if (miaoshaUser == null) {
            return Result.error(CodeMsg.SESSION_ERROR);
        }
        Long miaoshaResult = miaoshaService.getMiaoshaResult(miaoshaUser.getId(), goodsId);
        return Result.success(miaoshaResult);
    }

    @RequestMapping(value = "/reset", method = RequestMethod.GET)
    @ResponseBody
    public Result<Boolean> rest(Model model) {
        List<GoodsVo> goodsVoList = goodsService.listGoodsVo();
        for (GoodsVo goodsVo : goodsVoList) {
            goodsVo.setStockCount(10);
            redisService.set(GoodsKey.GoodsStock, "" + goodsVo.getId(), 10);
            localOverMap.put(goodsVo.getId(), false);
        }
        redisService.delete(OrderKey.getMiaoshaOrderByUserIdGoodsId);
        redisService.delete(MiaoshaKey.GoodsOver);
        miaoshaService.reset(goodsVoList);
        return Result.success(true);
    }
}
