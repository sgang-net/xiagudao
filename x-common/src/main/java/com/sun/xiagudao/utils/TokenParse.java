package com.sun.xiagudao.utils;

import com.alibaba.fastjson2.JSON;
import com.sun.xiagudao.constant.CommonConstant;
import com.sun.xiagudao.vo.LoginUserInfo;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import org.apache.commons.lang3.StringUtils;
import sun.misc.BASE64Decoder;

import java.io.IOException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Calendar;

public class TokenParse {

    public static LoginUserInfo parseLoginUserInfoFromToken(String token) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        if (StringUtils.isBlank(token)) {
            throw new IllegalArgumentException("token error");
        }

        Jws<Claims> claimsJws = parseToken(token, getPublicKey());

        Claims body = claimsJws.getBody();

        if (body.getExpiration().before(Calendar.getInstance().getTime())) {
            throw new IllegalArgumentException("token is expire");
        }

        return JSON.parseObject(body.get(CommonConstant.JWT_USER_INFO_KEY).toString(), LoginUserInfo.class);
    }

    private static Jws<Claims> parseToken(String token, PublicKey publicKey) {
        return Jwts.parser().setSigningKey(publicKey).parseClaimsJws(token);
    }

    private static PublicKey getPublicKey() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(new BASE64Decoder().decodeBuffer(CommonConstant.PUBLIC_KEY));
        return KeyFactory.getInstance("RSA").generatePublic(x509EncodedKeySpec);
    }
}
