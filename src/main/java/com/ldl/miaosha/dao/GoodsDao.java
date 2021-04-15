package com.ldl.miaosha.dao;

import com.ldl.miaosha.domain.Goods;
import com.ldl.miaosha.domain.MiaoshaGoods;
import com.ldl.miaosha.vo.GoodsVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface GoodsDao {

    @Select("select goods.*, mg.miaosha_price, mg.stock_count, mg.start_date, mg.end_date from miaosha_goods as mg left join goods on mg.goods_id = goods.id")
    public List<GoodsVo> listGoodsVo();

    @Select("select goods.*, mg.goods_id, mg.miaosha_price, mg.stock_count, mg.start_date, mg.end_date from miaosha_goods as mg left join goods on mg.goods_id = goods.id where goods.id = #{goodsId}")
    public GoodsVo getGoodsVoByGoodsId(@Param("goodsId") Long goodsId);

    @Update("update miaosha_goods set stock_count = stock_count - 1 where goods_id = #{goodsId} and stock_count > 0")
    public int reduceStock(MiaoshaGoods miaoshaGoods);

    @Update("update miaosha_goods set stock_count = #{stockCount} where goods_id = #{goodsId}")
    public void resetStock(MiaoshaGoods miaoshaGoods);
}
