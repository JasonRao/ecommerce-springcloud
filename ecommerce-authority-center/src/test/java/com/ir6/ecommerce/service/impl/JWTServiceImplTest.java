package com.ir6.ecommerce.service.impl;

import com.alibaba.fastjson.JSON;
import com.ir6.ecommerce.util.TokenParseUtil;
import com.ir6.ecommerce.vo.LoginUserInfo;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest
@RunWith(SpringRunner.class)
public class JWTServiceImplTest {

    @Autowired
    private JWTServiceImpl jwtService;

    @Test
    public void testGenerateAndParseToken() throws Exception {
        String jwtToken = jwtService.generateToken("ImoocQinyi@imooc.com", "25d55ad283aa400af464c76d713c07ad");
        log.info("Jwt jwtToken is [{}]: ", jwtToken);

        LoginUserInfo userInfo = TokenParseUtil.parseUserInfoFromToken(jwtToken);
        log.info("parse token: [{}]", JSON.toJSON(userInfo));

        Assert.assertEquals("ImoocQinyi@imooc.com", userInfo.getUsername());
    }

}