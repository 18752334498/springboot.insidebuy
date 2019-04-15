package com.yucong.insidebuy.service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.yucong.insidebuy.entity.GoodsInfo;
import com.yucong.insidebuy.entity.GoodsType;
import com.yucong.insidebuy.repository.GoodsInfoRepository;
import com.yucong.util.QueryUtils;

@Service
public class GoodsInfoService {

	@PersistenceContext
	private EntityManager manager;

	public List<Map<String, Object>> findGoodsList() {
		StringBuffer sql = new StringBuffer();
		sql.append("select type.goods_type, ");
		sql.append(
				"info.goods_name,info.description, info.picture, info.model_num, info.id as goods_info_id, info.limit_count, ");
		sql.append("model.discount_price, model.market_price ");
		sql.append("from vwt_ins_goods_info info ");
		sql.append("join vwt_ins_goods_type type on info.goods_type_id = type.id ");
		sql.append("join vwt_ins_goods_model model on model.goods_info_id = info.id ");
		sql.append("where info.status = 1 order by type.type_order, info.goods_num, info.update_time");

		return QueryUtils.queryForMap(manager, sql.toString(), new HashMap<>());
	}

	public List<Map<String, Object>> findModelNumAndLimitCount() {
		StringBuffer sql = new StringBuffer();
		sql.append(
				"select i.id as goods_info_id, i.model_num, i.limit_count from vwt_ins_goods_info i where i.limit_count > 0");
		return QueryUtils.queryForMap(manager, sql.toString(), new HashMap<>());
	}

	@Autowired
	private GoodsInfoRepository goodsInfoRepository;

	@Transactional
	public void updateGoodsForManager() {
		GoodsInfo goodsInfo = getGoodsInfo();
		goodsInfo.setId(52l);
		goodsInfo.setGoodsNum(44440l);
		goodsInfoRepository.save(goodsInfo);

		throw new RuntimeException("自定义异常");

	}

	public static GoodsInfo getGoodsInfo() {
		GoodsInfo g = new GoodsInfo();
		GoodsType goodsType = new GoodsType();
		goodsType.setId(1l);

		// 必填
		g.setLimitCount(3);
		g.setGoodsType(goodsType);
		g.setDescription("bigger than bigger");
		g.setGoodsName("honor10");
		g.setPicture("/aa/ss/magic.png");

		// 默认上架，即 1
		g.setStatus(1);

		// 选填
		g.setBrand("honor");
		g.setGoodsNum(1l);

		// 自定义
		g.setUpdateTime(new Date());

		System.out.println(JSON.toJSONString(g));

		return g;
	}

}
