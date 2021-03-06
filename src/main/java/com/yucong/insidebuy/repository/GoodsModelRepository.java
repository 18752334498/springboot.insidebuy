package com.yucong.insidebuy.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.yucong.insidebuy.entity.GoodsModel;

public interface GoodsModelRepository extends JpaRepository<GoodsModel, Long> {

    /**
     * 客户下订单，从库存减去对应数量
     * 
     */

    @Modifying
    @Query("update GoodsModel g set g.inventory = (g.inventory - :preBuyCount) where g.inventory >= :preBuyCount and g.id = :goodsModelId")
    int updateInventoryById(@Param("goodsModelId") Long goodsModelId, @Param("preBuyCount") Integer preBuyCount);

    /**
     * 客户取消订单，将购买数量返回给库存
     * 
     */
    @Modifying
    @Query("update GoodsModel m set m.inventory = (m.inventory + :goodsCount) where id = :goodsModelId")
    int updateGoodsCountById(@Param("goodsModelId") Long goodsModelId, @Param("goodsCount") Integer goodsCount);

    Optional<GoodsModel> findById(Long goodsModelId);

    @Modifying
    @Query("delete from GoodsModel g where g.goodsInfoId = :goodsInfoId")
    int deleteGoodsModelByGoodsInfoId(@Param("goodsInfoId") Long goodsInfoId);


}
