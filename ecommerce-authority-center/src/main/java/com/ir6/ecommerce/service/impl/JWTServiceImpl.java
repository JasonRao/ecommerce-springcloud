package com.ir6.ecommerce.service.impl;

import com.alibaba.fastjson.JSON;
import com.ir6.ecommerce.constant.AuthorityConstant;
import com.ir6.ecommerce.constant.CommonConstant;
import com.ir6.ecommerce.dao.EcommerceUserDao;
import com.ir6.ecommerce.entity.EcommerceUser;
import com.ir6.ecommerce.service.IJWTService;
import com.ir6.ecommerce.vo.LoginUserInfo;
import com.ir6.ecommerce.vo.UsernameAndPassword;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sun.misc.BASE64Decoder;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.UUID;


@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class JWTServiceImpl implements IJWTService {

    @Autowired
    private EcommerceUserDao dao;

    @Override
    public String generateToken(String username, String password) throws Exception {
        return generateToken(username, password, 0);
    }

    @Override
    public String generateToken(String username, String password, int expire) throws Exception {
        EcommerceUser user = dao.findByUsernameAndPassword(username, password);
        if (null == user) {
            log.error("can not find user: [{}], [{}]", username, password);
            return null;
        }
        LoginUserInfo loginUserInfo = new LoginUserInfo(user.getId(), user.getUsername());
        return Jwts.builder()
                .claim(CommonConstant.JWT_USER_INFO_KEY, JSON.toJSONString(loginUserInfo)) // jwt payload --> KV
                .setId(UUID.randomUUID().toString()) // jwt id
                .setExpiration(calExpireDate(expire)) // jwt 过期时间
                .signWith(getPrivateKey(), SignatureAlgorithm.RS256) // jwt 签名 --> 加密
                .compact();
    }

    // 计算超时时间
    private Date calExpireDate(int expire) {
        if(expire <= 0) {
            expire = AuthorityConstant.DEFAULT_EXPIRE_DAY;
        }
        ZonedDateTime zdt = LocalDate.now().plus(expire, ChronoUnit.DAYS).atStartOfDay(ZoneId.systemDefault());
        return Date.from(zdt.toInstant());
    }

    /**
     * <h2>根据本地存储的私钥获取到 PrivateKey对象</h2>
     * */
    private PrivateKey getPrivateKey() throws Exception {
        PKCS8EncodedKeySpec priPKCS8 = new PKCS8EncodedKeySpec(new BASE64Decoder().decodeBuffer(AuthorityConstant.PRIVATE_KEY));
        return KeyFactory.getInstance("RSA").generatePrivate(priPKCS8);
    }

    @Override
    public String registerAndGenerateToken(UsernameAndPassword usernameAndPassword) throws Exception {
        EcommerceUser oldUser = dao.findByUsername(usernameAndPassword.getUsername());
        if(oldUser != null) {
            log.error("The user has already been registered: [{}]", oldUser.getUsername());
            return null;
        }
        EcommerceUser user = new EcommerceUser();
        user.setUsername(usernameAndPassword.getUsername());
        user.setPassword(usernameAndPassword.getPassword());
        user.setExtraInfo("{}");
        user = dao.save(user);
        log.info("register user success: [{}], [{}]", user.getUsername(), user.getId());

        return generateToken(user.getUsername(), user.getPassword());
    }

}
