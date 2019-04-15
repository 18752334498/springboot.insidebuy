package com.yucong.insidebuy.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
 * 商品型号实体类
 * 
 * @Author: yucong
 * @Since: 2019年3月25日
 */
@Entity
@Table(name = "vwt_ins_goods_model")
public class GoodsModel implements Serializable {

    private static final long serialVersionUID = -3159244439177202138L;

    /** 主键 */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "vwt_ins_goods_model_gen")
    @SequenceGenerator(name = "vwt_ins_goods_model_gen", sequenceName = "vwt_ins_goods_model_seq", allocationSize = 1)
    private Long id;


    /** 商品信息ID */
    @Column(name = "goods_info_id")
    private Long goodsInfoId;

    /** 商品型号 */
    @Column(name = "goods_model", length = 20)
    private String goodsModel;

    /** 市场参考价 */
    @Column(name = "market_price")
    private Double marketPrice;

    /** 内购价 */
    @Column(name = "discount_price")
    private Double discountPrice;

    /** 库存 */
    @Column(name = "inventory")
    private Integer inventory;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getGoodsInfoId() {
        return goodsInfoId;
    }

    public void setGoodsInfoId(Long goodsInfoId) {
        this.goodsInfoId = goodsInfoId;
    }

    public String getGoodsModel() {
        return goodsModel;
    }

    public void setGoodsModel(String goodsModel) {
        this.goodsModel = goodsModel;
    }

    public Double getMarketPrice() {
        return marketPrice;
    }

    public void setMarketPrice(Double marketPrice) {
        this.marketPrice = marketPrice;
    }

    public Double getDiscountPrice() {
        return discountPrice;
    }

    public void setDiscountPrice(Double discountPrice) {
        this.discountPrice = discountPrice;
    }

    public Integer getInventory() {
        return inventory;
    }

    public void setInventory(Integer inventory) {
        this.inventory = inventory;
    }

}
