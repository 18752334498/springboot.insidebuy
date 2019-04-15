package com.yucong.insidebuy.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.yucong.insidebuy.entity.Activity;

@Repository
public interface ActivityRepository extends JpaRepository<Activity, Long> {

	@Query("select a from Activity a where a.endTime > :nowTime")
	public List<Activity> findActivityByEndTime(@Param("nowTime") Date nowTime);

}
