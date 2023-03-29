package com.sun.xiagudao.dao;

import com.sun.xiagudao.entity.EcommerceUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EcommerceUserDao extends JpaRepository<EcommerceUser, Long> {

    EcommerceUser findByUsername(String username);

    EcommerceUser findByUsernameAndPassword(String username, String password);
}
