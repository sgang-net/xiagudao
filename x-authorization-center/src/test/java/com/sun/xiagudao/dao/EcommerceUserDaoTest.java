package com.sun.xiagudao.dao;

import cn.hutool.crypto.digest.MD5;
import com.alibaba.fastjson.JSON;
import com.sun.xiagudao.entity.EcommerceUser;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest
@RunWith(SpringRunner.class)
class EcommerceUserDaoTest {

    @Autowired
    private EcommerceUserDao dao;

    @Test
    void findByUsername() {
        EcommerceUser ecommerceUser = new EcommerceUser();
        ecommerceUser.setUsername("mno");
        ecommerceUser.setPassword(MD5.create().digestHex("567890"));
        ecommerceUser.setExtraInfo("{}");
        log.info("ecommerceUser:[{}]", JSON.toJSONString(dao.save(ecommerceUser)));
    }

    @Test
    void findByUsernameAndPassword() {
    }
}