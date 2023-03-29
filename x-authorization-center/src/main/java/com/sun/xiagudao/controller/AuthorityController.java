package com.sun.xiagudao.controller;

import com.alibaba.fastjson.JSON;
import com.sun.xiagudao.service.JWTService;
import com.sun.xiagudao.vo.JwtToken;
import com.sun.xiagudao.vo.UsernameAndPassword;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/authority")
public class AuthorityController {

    private final JWTService jwtService;

    public AuthorityController(JWTService jwtService) {
        this.jwtService = jwtService;
    }

    /**
     * <h2>从授权中心获取 Token (其实就是登录功能), 且返回信息中没有统一响应的包装</h2>
     */
    @PostMapping("/token")
    public JwtToken token(@RequestBody UsernameAndPassword usernameAndPassword) throws Exception {

        log.info("request to get token with param: [{}]", JSON.toJSONString(usernameAndPassword));
        return new JwtToken(jwtService.generateToken(usernameAndPassword.getUsername(), usernameAndPassword.getPassword()));
    }

    /**
     * <h2>注册用户并返回当前注册用户的 Token, 即通过授权中心创建用户</h2>
     */
    @PostMapping("/register")
    public JwtToken register(@RequestBody UsernameAndPassword usernameAndPassword) throws Exception {

        log.info("register user with param: [{}]", JSON.toJSONString(usernameAndPassword));
        return new JwtToken(jwtService.registerUserAndGenerateToken(usernameAndPassword));
    }
}
