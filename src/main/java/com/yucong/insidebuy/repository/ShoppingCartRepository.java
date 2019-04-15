package com.yucong.insidebuy.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.yucong.insidebuy.entity.ShoppingCart;

@Repository
public interface ShoppingCartRepository extends JpaRepository<ShoppingCart, Long> {

    // @Query(nativeQuery = true, value = "select count(*) from vwt_ins_shopping_cart")
    @Query("select count(c) from ShoppingCart c where c.phoneId = :phoneId")
    public Integer findNumOfCartByPhoneId(@Param("phoneId") Long phoneId);

    @Modifying
    @Transactional
    @Query("delete from ShoppingCart cart where cart.id = :cartId")
    int deleteByCartId(@Param("cartId") Long cartId);

    @Query("select c from ShoppingCart c where c.phoneId=:phoneId and c.goodsInfo.id=:goodsInfoId and c.goodsModel.id=:goodsModelId")
    public ShoppingCart findByPhoneInfoModelId(@Param("phoneId") Long phoneId, @Param("goodsInfoId") Long goodsInfoId,
            @Param("goodsModelId") Long goodsModelId);

    @Modifying
    @Query("delete from ShoppingCart cart where cart.id in (:ids)")
    public int deleteShoppingCartByIds(@Param("ids") List<Long> ids);
}
