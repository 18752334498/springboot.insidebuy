package com.yucong.insidebuy.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Service;

import com.yucong.util.QueryUtils;

@Service
public class GoodsDetailService {

    @PersistenceContext
    private EntityManager manager;

    /**
     * <h3>在列表页面点击某个商品产看详情</h3>
     * <h3>根据页面传过来的参数：机型编码（model_num） 查询信息</h3>
     * 
     * @Author: yucong
     * @Since: 2019年3月26日
     */
    public List<Map<String, Object>> findGoodsDetail() {
        StringBuffer sql = new StringBuffer();
		sql.append("select i.goods_name,i.description, i.picture, i.brand, i.id as goods_info_id, i.limit_count, ");
		sql.append("m.discount_price, m.market_price, m.goods_model, m.inventory, m.id as goods_model_id ");
        sql.append("from vwt_ins_goods_info i ");
        sql.append("join vwt_ins_goods_model m on m.goods_info_id = i.id ");
        sql.append("where i.status = 1 and i.model_num = :model_num");

        Map<String, Object> map = new HashMap<>();
		map.put("model_num", "aaa");

        return QueryUtils.queryForMap(manager, sql.toString(), map);
    }

    public void countshoppingCart() {

    }

}
