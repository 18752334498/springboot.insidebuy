package com.yucong.jpa;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.test.context.junit4.SpringRunner;

import com.yucong.App;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = App.class)
public class TestJpa {

	@Autowired
	private JpaService service;

	@Autowired
	private MyRepository repo;

	@Test
	public void test_saveAll() {
		service.saveAll();
	}

	@Test
	public void test_deleteById() {
		repo.deleteById(5l);
	}

	@Test
	public void test_findByName() {
		User user = repo.findByName("Jack-2");
		System.out.println(user);
	}

	@Test
	public void test_deleteByIds() {
		List<Long> ids = new ArrayList<>();
		ids.add(1l);
		ids.add(52l);
		int count = repo.deleteByIds(ids);
		System.out.println("删除了： " + count);
	}

	@Test
	public void test_findByFuzzyName() {
//		List<User> list = repo.findByFuzzyName("Tom");
		List<User> list = repo.findByFuzzyName("-2");
		System.out.println(list);
	}

	@Test
	public void test_updateById() {
		int count = repo.updateById(100, 56);
		System.out.println("更新了： " + count);
	}

	@Test
	public void test_getTotalOfUser() {
		int count = repo.getTotalOfUser();
		System.out.println("总共： " + count);
	}

	@Test
	public void test_sort() {
		Sort sort = new Sort(Direction.ASC, "id");
		List<User> list = repo.findAll(sort);
		list.forEach(System.out::println);
	}

	@Test
	public void test_page() {
		// zero-based page index
		PageRequest pageRequest = PageRequest.of(2, 4);
		Page<User> page = repo.findAll(pageRequest);
		page.getContent().forEach(System.out::println);
		System.out.println("此页多少条：	" + page.getNumberOfElements());
		System.out.println("数据总数：	" + page.getTotalElements());
		System.out.println("一共几页：	" + page.getTotalPages());
	}

	@Test
	public void test_page_sort() {
		Sort sort = new Sort(Direction.DESC, "age");
		PageRequest pageRequest = PageRequest.of(2, 4, sort);
		Page<User> page = repo.findAll(pageRequest);
		page.getContent().forEach(System.out::println);
		System.out.println("此页多少条：	" + page.getNumber());
		System.out.println("数据总数：	" + page.getTotalElements());
		System.out.println("一共几页：	" + page.getTotalPages());
	}

	@PersistenceContext
	private EntityManager entitymanager;
	@Test
	@Transactional
	public void test_entityManager() {

		Query query = entitymanager.createNativeQuery("update user set name = 'Zhangsan' where id = 1");
		int i = query.executeUpdate();
		System.out.println("dddddddd:  " + i);
	}

}
