package com.ldl.miaosha.service;

import com.ldl.miaosha.dao.GoodsDao;
import com.ldl.miaosha.domain.MiaoshaGoods;
import com.ldl.miaosha.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GoodsService {

    @Autowired
    GoodsDao goodsDao;

    public List<GoodsVo> listGoodsVo() {
        return goodsDao.listGoodsVo();
    }

    public GoodsVo getGoodsVoByGoodsId(Long id) {
        return goodsDao.getGoodsVoByGoodsId(id);
    }

    public int reduceStock(GoodsVo goodsVo) {
        return goodsDao.reduceStock(goodsVo.getId());
    }
}
