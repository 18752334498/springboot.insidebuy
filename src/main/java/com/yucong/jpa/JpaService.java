package com.yucong.jpa;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class JpaService {

	@Autowired
	private MyRepository repository;

	public void saveAll() {
		List<User> list = new ArrayList<>();
		User user = null;
		for (int i = 0; i < 5; i++) {
			user = new User();
			user.setName("Tom-" + i);
			user.setAge(i + new Random().nextInt(10) + 5);
			list.add(user);
		}
		for (int i = 0; i < 5; i++) {
			user = new User();
			user.setName("Jack-" + i);
			user.setAge(i + new Random().nextInt(10) + 5);
			list.add(user);
		}
		List<User> all = repository.saveAll(list);
		System.out.println(all);
	}

}
