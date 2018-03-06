package com.example.demo.dao;

import com.example.demo.domain.User;

import java.util.Map;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by wang on 2018/3/5.
 */
public interface UserRepository extends JpaRepository<User, Long> {

    User findByUsername(String s);

	void saveUser(Map<String, Object> map);
}
