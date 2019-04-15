package com.yucong.insidebuy.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yucong.insidebuy.repository.OrderRepository;
import com.yucong.util.QueryUtils;

@Service
public class OrderService {

    @PersistenceContext
    private EntityManager manager;

    @Autowired
    private OrderRepository orderRepository;

    /**
     * <span>我的订单接口</span>
     * 
     * @Author: yucong
     * @Since: 2019年3月26日
     */
    public List<Map<String, Object>> findOrderList() {

        StringBuffer sql = new StringBuffer();
        sql.append("select i.description, i.goods_name, i.model_num, i.picture, i.status, ");
        sql.append("m.discount_price, m.goods_model, m.inventory, ");
        sql.append("o.address, o.business_hall, o.create_time, o.goods_count, o.order_group, o.phone_id, o.phone_num, o.username ");
        sql.append("from vwt_ins_order o ");
        sql.append("join vwt_ins_goods_info i on o.goods_info_id = i.id ");
        sql.append("join vwt_ins_goods_model m on o.goods_model_id = m.id ");
        sql.append("where o.phone_id = :phone_id order by o.create_time");

        Map<String, Object> map = new HashMap<>();
        map.put("phone_id", "18752334498");

        return QueryUtils.queryForMap(manager, sql.toString(), map);
    }

    // 查出各种限制条件
    public Map<String, Object> queryAllLimitCount(Long goodsInfoId, Long goodsModelId) {

        StringBuffer sql = new StringBuffer();

        sql.append("select ");
        sql.append("SUM(o.goods_count) as orderedCount, ");
        sql.append("(select a.limit_total as actCount from vwt_ins_activity a) as actLimitCount, ");
        sql.append("(select m.inventory from vwt_ins_goods_model m where m.id = :goodsModelId) as inventory, ");
        sql.append("(select i.limit_count from vwt_ins_goods_info i where i.id = :goodsInfoId) modelLimitCount ");
        sql.append("from vwt_ins_order o where o.phone_id = 18752334498 and o.goods_info_id = 1");

        Map<String, Object> map = new HashMap<>();
        map.put("goodsInfoId", goodsInfoId);
        map.put("goodsModelId", goodsModelId);
        Map<String, Object> singleForMap = QueryUtils.querySingleForMap(manager, sql.toString(), map);
        System.out.println(singleForMap);
        return singleForMap;
    }

    public JSONObject findOrderListForManager() {

        StringBuffer sql = new StringBuffer();
        sql.append("select o.id, o.address, o.business_hall, o.department,o.employee_num, ");
        sql.append("o.goods_count,o.order_num,o.phone_num,o.username,o.create_time, o.order_group, ");
        sql.append("i.goods_name,m.discount_price,m.goods_model ");
        sql.append("from vwt_ins_order o ");
        sql.append("join vwt_ins_goods_info i on i.id = o.goods_info_id ");
        sql.append("join vwt_ins_goods_model m on m.id = o.goods_model_id");
        Page<Map<String, Object>> page = QueryUtils.queryForMap(manager, sql.toString(), new HashMap<>(), PageRequest.of(0, 10));


        List<Map<String, Object>> list = page.getContent();
        System.out.println(JSON.toJSONString(list));

        BigDecimal discountPrice;
        BigDecimal goodsCount;
        String totalPrice;
        for (Map<String, Object> m : list) {
            discountPrice = new BigDecimal(String.valueOf(m.get("discount_price")));
            goodsCount = new BigDecimal(String.valueOf(m.get("goods_count")));
            totalPrice = discountPrice.multiply(goodsCount).toString();
            m.put("total_price", totalPrice);
            m.put("order_content", m.get("goods_name") + " " + m.get("goods_model") + " ×" + m.get("goods_count"));
        }

        // 获取所有的分组编码
        List<String> orderGroupList = new ArrayList<>();
        for (Map<String, Object> m : list) {
            String order_group = String.valueOf(m.get("order_group"));
            if (orderGroupList.contains(order_group)) {
                continue;
            } else {
                orderGroupList.add(order_group);
            }
        }

        List<Map<String, Object>> resultList = new ArrayList<>();
        List<Map<String, Object>> temList = null;
        for (String orderGroup : orderGroupList) {
            temList = new ArrayList<>();
            for (Map<String, Object> m : list) {
                String temOrderGroup = String.valueOf(m.get("order_group"));
                if (orderGroup.equals(temOrderGroup)) {
                    temList.add(m);
                }
            }
            // 此时的临时列表temList包含某一个批次的所有订单信息，并将订单信息合并成一个 <Map<String, Object>
            Map<String, Object> dd = dd(temList);
            resultList.add(dd);
        }



        JSONObject result = new JSONObject();
        result.put("data", resultList);
        result.put("nums", page.getTotalPages());
        System.out.println(JSON.toJSONString(result));
        return result;
    }

    private Map<String, Object> dd(List<Map<String, Object>> temList) {
        if (CollectionUtils.isEmpty(temList)) {
            return null;
        }
        StringBuffer sBuffer = new StringBuffer();
        double totalPrice = 0;
        for (Map<String, Object> map : temList) {
            sBuffer.append(String.valueOf(map.get("order_content"))).append(";");
            totalPrice += Double.parseDouble(String.valueOf(map.get("total_price")));
        }

        Map<String, Object> result = temList.get(0);
        result.put("order_content", sBuffer.deleteCharAt(sBuffer.length() - 1));
        result.put("total_price", totalPrice);
        return result;
    }

    public void test_new_getOrderList() {
        // 分页获取所有订单编码
        String sql = "SELECT o.order_num from vwt_ins_order o GROUP BY o.order_num ORDER BY o.order_num";
        Page<Object[]> page = QueryUtils.queryForList(manager, sql, new HashMap<>(), PageRequest.of(0, 2));
        List<String> orderNums = JSONArray.parseArray(JSON.toJSONString(page.getContent()), String.class);

        // 根据订单编码获取所有订单
        StringBuffer sql1 = new StringBuffer();
        sql1.append("select o.id, o.address, o.business_hall, o.department,o.employee_num, ");
        sql1.append("o.goods_count,o.order_num,o.phone_num,o.username,o.create_time, o.order_group, ");
        sql1.append("i.goods_name,m.discount_price,m.goods_model ");
        sql1.append("from vwt_ins_order o ");
        sql1.append("join vwt_ins_goods_info i on i.id = o.goods_info_id ");
        sql1.append("join vwt_ins_goods_model m on m.id = o.goods_model_id ");
        sql1.append("where o.order_num in (:orderNums)");
        Map<String, Object> orderNumsMap = new HashMap<>();
        orderNumsMap.put("orderNums", orderNums);
        List<Map<String, Object>> queryForMap = QueryUtils.queryForMap(manager, sql1.toString(), orderNumsMap);
        System.out.println(JSON.toJSONString(queryForMap));


        // 算出每条订单的总价，合并每条订单信息
        BigDecimal discountPrice;
        BigDecimal goodsCount;
        String totalPrice;
        for (Map<String, Object> m : queryForMap) {
            discountPrice = new BigDecimal(String.valueOf(m.get("discount_price")));
            goodsCount = new BigDecimal(String.valueOf(m.get("goods_count")));
            totalPrice = discountPrice.multiply(goodsCount).toString();
            m.put("total_price", totalPrice);
            m.put("order_content", m.get("goods_name") + " " + m.get("goods_model") + " ×" + m.get("goods_count"));
        }

        // 将相同order_num的订单分组，分组后合并每个集合中额信息，组成批次订单
        List<Map<String, Object>> resultList = new ArrayList<>();
        List<Map<String, Object>> temList = null;
        for (String orderNum : orderNums) {
            temList = new ArrayList<>();
            for (Map<String, Object> m : queryForMap) {
                String temOrderNum = String.valueOf(m.get("order_num"));
                if (orderNum.equals(temOrderNum)) {
                    temList.add(m);
                }
            }
            // 此时的临时列表temList包含某一个批次的所有订单信息，并将订单信息合并成一个 <Map<String, Object>
            Map<String, Object> dd = dd(temList);
            resultList.add(dd);
        }

        System.out.println(JSON.toJSONString(resultList));

    }

}
