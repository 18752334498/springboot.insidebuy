package com.yucong.insidebuy.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.yucong.insidebuy.entity.GoodsInfo;

public interface GoodsInfoRepository extends JpaRepository<GoodsInfo, Long> {


    @Query("select g from GoodsInfo g order by g.goodsType.typeOrder, g.goodsNum, g.updateTime")
    Page<GoodsInfo> findAllGoodsInfo(Pageable pageable);

    /*******************/

    GoodsInfo findByModelNum(String modelNum);

    @Modifying
    @Query(nativeQuery = true, value = "update vwt_ins_goods_info g set g.limit_count = :limitCount where g.model_num = :modelNum")
    public int updateLimitCountByModelNum(@Param("limitCount") Integer limitCount, @Param("modelNum") String modelNum);


    /**
     * <li>根据ID修改机型编码和限购数量</li>
     */
    @Modifying
    @Query(nativeQuery = true, value = "update vwt_ins_goods_info g set g.limit_count = :limitCount, g.model_num = :modelNum where g.id = :id")
    public int updateById(@Param("id") Long id, @Param("limitCount") Integer limitCount, @Param("modelNum") String modelNum);

    @Modifying
    @Query(nativeQuery = true, value = "update vwt_ins_goods_info i set i.limit_count = null")
    int modifyLimitCountToNull();
}
