package com.yucong.insidebuy.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.yucong.insidebuy.entity.GoodsType;

@Repository
public interface TypeRepository extends JpaRepository<GoodsType, Long> {

    @Query("select g from GoodsType g order by g.typeOrder desc")
    List<GoodsType> findAllForDropDown();
}
