package com.yucong.insidebuy.repository;

import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.yucong.insidebuy.entity.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {


    @Modifying
    @Transactional
    @Query(nativeQuery = true, value = "delete from vwt_ins_order where order_group = :orderGroup")
    int deleteByOrderGroup(@Param("orderGroup") String orderGroup);

    @Query("select count(o) from Order o where o.goodsInfo.id = :goodsInfoId")
    public int findAllByGoodsInfoId(@Param("goodsInfoId") Long goodsInfoId);

    @Query("select o.id,o.username,o.goodsCount,o.phoneNum,o.businessHall,o.address,o.createTime,o.employeeNum," + "o.department,o.orderNum,"
            + "o.goodsInfo.goodsName," + "o.goodsModel.discountPrice," + "o.goodsModel.goodsModel " + "from Order o")
    Page<Map<String, Object>> findByCondition(Pageable pageable);

}
