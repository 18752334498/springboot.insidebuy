package com.yucong.insidebuy;

import java.util.Date;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.yucong.insidebuy.entity.GoodsInfo;
import com.yucong.insidebuy.entity.GoodsType;

public class UtilForInside {

    public static void main(String[] args) {
        getGoodsInfo();
    }

    public static GoodsInfo getGoodsInfo() {
        GoodsInfo g = new GoodsInfo();
        GoodsType goodsType = new GoodsType();
        goodsType.setId(1l);

        // 必填
        g.setModelNum("vvvv");
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

        System.out.println(JSON.toJSONString(g, SerializerFeature.PrettyFormat));

        return g;
    }

}
