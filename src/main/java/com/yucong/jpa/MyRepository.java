package com.yucong.jpa;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface MyRepository extends JpaRepository<User, Long> {

	@Query("select u.id,u.name,u.age from User u where u.name = :name")
	User findByName(@Param("name") String name);

	@Modifying
	@Transactional
	@Query("delete from User u where u.id in (:ids)")
	int deleteByIds(@Param("ids") List<Long> ids);

//	@Query("select u from User u where u.name like :name%")
	@Query("select u from User u where u.name like %:name")
	List<User> findByFuzzyName(@Param("name") String name);

	@Modifying
	@Transactional
	@Query("update User u set u.age = :age where u.id = :id")
	int updateById(@Param("age") int age, @Param("id") long id);

	@Query(nativeQuery = true, value = "select count(1) from user")
	int getTotalOfUser();
}
