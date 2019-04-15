package com.yucong.jpa;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

@Service
public class EntityManagerService {

	@PersistenceContext
	private EntityManager entitymanager;

	public void findByAll() {
		Query query = entitymanager.createQuery("select u from User u");
		@SuppressWarnings("unchecked")
		List<User> list = query.getResultList();
		list.forEach(System.out::println);
	}

	@Transactional
	public void updateById() {
		Query query = entitymanager.createNativeQuery("update user set name = :name where id = :id");
		query.setParameter("name", "Lisi");
		query.setParameter("id", 2);
		int i = query.executeUpdate();
		System.out.println("更新了： " + i);

	}

}
