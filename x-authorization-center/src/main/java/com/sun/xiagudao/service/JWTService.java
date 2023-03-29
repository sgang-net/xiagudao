package com.sun.xiagudao.service;

import com.sun.xiagudao.vo.UsernameAndPassword;

public interface JWTService {

    String generateToken(String username, String password) throws Exception;

    String generateToken(String username, String password, int expire) throws Exception;

    String registerUserAndGenerateToken(UsernameAndPassword usernameAndPassword) throws Exception;
}
