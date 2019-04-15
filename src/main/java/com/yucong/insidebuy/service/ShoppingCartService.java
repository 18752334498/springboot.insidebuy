package com.yucong.insidebuy.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yucong.util.QueryUtils;

@Service
public class ShoppingCartService {

    @PersistenceContext
    private EntityManager manager;

    /**
     * <span>购物车接口</span>
     * 
     * @Author: yucong
     * @Since: 2019年3月26日
     */
    public List<Map<String, Object>> findShoppingCartList() {

        StringBuffer sql = new StringBuffer();
		sql.append("select cart.goods_count, cart.id as cart_id, ");
        sql.append("info.description, info.goods_name, info.model_num, info.picture, info.status, ");
        sql.append("model.inventory, model.discount_price, model.goods_model ");
        sql.append("from vwt_ins_shopping_cart cart ");
        sql.append("join vwt_ins_goods_info info on cart.goods_info_id = info.id ");
        sql.append("join vwt_ins_goods_model model on cart.goods_model_id = model.id ");
        sql.append("where cart.phone_id = :phone_id");

        Map<String, Object> map = new HashMap<>();
        map.put("phone_id", "18752334498");

        return QueryUtils.queryForMap(manager, sql.toString(), map);
    }

    // 此方法需要插入ID
    @Transactional
    public void saveShoppingCart() {
        StringBuffer sql = new StringBuffer();
        sql.append("insert into vwt_ins_shopping_cart(goods_count, phone_id, goods_info_id, goods_model_id) ");
        sql.append(" values(:goodsCount, :phoneId, :goodsInfoId, :goodsModelId)");

        Map<String, Object> map = new HashMap<>();
        map.put("goodsCount", 22);
        map.put("phoneId", 18752334499l);
        map.put("goodsInfoId", 22);
        map.put("goodsModelId", 22);
        int count = QueryUtils.executeUpdate(manager, sql.toString(), map);
        System.out.println("插入了： " + count);
    }

}
