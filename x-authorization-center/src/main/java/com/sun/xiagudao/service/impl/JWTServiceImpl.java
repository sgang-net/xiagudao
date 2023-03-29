package com.sun.xiagudao.service.impl;

import com.alibaba.fastjson.JSON;
import com.sun.xiagudao.constant.AuthorityConstant;
import com.sun.xiagudao.constant.CommonConstant;
import com.sun.xiagudao.dao.EcommerceUserDao;
import com.sun.xiagudao.entity.EcommerceUser;
import com.sun.xiagudao.service.JWTService;
import com.sun.xiagudao.vo.LoginUserInfo;
import com.sun.xiagudao.vo.UsernameAndPassword;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sun.misc.BASE64Decoder;

import java.io.IOException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.text.DateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.UUID;


@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class JWTServiceImpl implements JWTService {

    private final EcommerceUserDao ecommerceUserDao;

    public JWTServiceImpl(EcommerceUserDao ecommerceUserDao) {
        this.ecommerceUserDao = ecommerceUserDao;
    }

    @Override
    public String generateToken(String username, String password) throws Exception {
        return generateToken(username, password, 0);
    }

    @Override
    public String generateToken(String username, String password, int expire) throws Exception {
        EcommerceUser user = ecommerceUserDao.findByUsernameAndPassword(username, password);
        if (null == user) {
            log.error("user is not exists:[{}], [{}]", username, password);
            return null;
        }

        LoginUserInfo userInfo = new LoginUserInfo(user.getId(), user.getUsername());
        expire = expire <= 0 ? AuthorityConstant.DEFAULT_EXPIRE_DAY : expire;
        ZonedDateTime zonedDateTime = LocalDate.now().plus(expire, ChronoUnit.DAYS).atStartOfDay(ZoneId.systemDefault());
        Date expireDate = Date.from(zonedDateTime.toInstant());

        return Jwts.builder().claim(CommonConstant.JWT_USER_INFO_KEY, JSON.toJSONString(userInfo))
                .setId(UUID.randomUUID().toString()).setExpiration(expireDate)
                .signWith(getPrivateKey(), SignatureAlgorithm.RS256).compact();
    }

    @Override
    public String registerUserAndGenerateToken(UsernameAndPassword usernameAndPassword) throws Exception {
        EcommerceUser byUsername = ecommerceUserDao.findByUsername(usernameAndPassword.getUsername());
        if (null != byUsername) {
            log.error("username is registered: [{}]", byUsername.getUsername());
            return null;
        }

        EcommerceUser ecommerceUser = new EcommerceUser();
        ecommerceUser.setUsername(usernameAndPassword.getUsername());
        ecommerceUser.setPassword(usernameAndPassword.getPassword());   // MD5 编码以后
        ecommerceUser.setExtraInfo("{}");

        // 注册一个新用户, 写一条记录到数据表中
        ecommerceUser = ecommerceUserDao.save(ecommerceUser);
        log.info("register user success: [{}], [{}]", ecommerceUser.getUsername(),
                ecommerceUser.getId());

        // 生成 token 并返回
        return generateToken(ecommerceUser.getUsername(), ecommerceUser.getPassword());
    }

    private PrivateKey getPrivateKey() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        PKCS8EncodedKeySpec p8s = new PKCS8EncodedKeySpec(new BASE64Decoder().decodeBuffer(AuthorityConstant.PRIVATE_KEY));
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePrivate(p8s);
    }

    public static void main(String[] args) {
        ZonedDateTime zonedDateTime = LocalDate.now().plus(1, ChronoUnit.DAYS).atStartOfDay(ZoneId.systemDefault());
        Date expireDate = Date.from(zonedDateTime.toInstant());
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss");
        System.out.println(dateTimeFormatter.format(zonedDateTime));
    }
}
