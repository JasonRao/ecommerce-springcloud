package com.ir6.ecommerce.util;

import com.alibaba.fastjson.JSON;
import com.ir6.ecommerce.constant.CommonConstant;
import com.ir6.ecommerce.vo.LoginUserInfo;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import org.apache.commons.lang3.StringUtils;
import sun.misc.BASE64Decoder;

import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Calendar;

import static com.ir6.ecommerce.constant.CommonConstant.JWT_USER_INFO_KEY;

/**
 * Jwt Token解析工具类
 */
public class TokenParseUtil {

    /**
     * <h2>从 JWT Token 中解析 LoginUserInfo 对象</h2>
     * */
    public static LoginUserInfo parseUserInfoFromToken(String token) throws Exception {
        if(StringUtils.isEmpty(token)) {
            return null;
        }
        Jws<Claims> jws = parseToken(token, getPublicKey());
        Claims body = jws.getBody();
        if(body.getExpiration().before(Calendar.getInstance().getTime())) {
            return null; //Token过期
        }
        return JSON.parseObject(body.get(JWT_USER_INFO_KEY).toString(), LoginUserInfo.class);
    }

    private static Jws<Claims> parseToken(String token, PublicKey publicKey) {
        return Jwts.parser().setSigningKey(publicKey).parseClaimsJws(token);
    }

    /**
     * <h2>根据本地存储的公钥获取到 PublicKey</h2>
     * */
    private static PublicKey getPublicKey() throws Exception {
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(new BASE64Decoder().decodeBuffer(CommonConstant.PUBLIC_KEY));
        return KeyFactory.getInstance("RSA").generatePublic(keySpec);
    }

}
