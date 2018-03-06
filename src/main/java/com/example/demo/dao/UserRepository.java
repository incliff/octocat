package com.example.demo.dao;

import com.example.demo.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by wang on 2018/3/5.
 */
/*@Repository*/
public interface UserRepository extends JpaRepository<User, Long> {

    User findByUsername(String s);

}
