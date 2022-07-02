package com.ir6.ecommerce.entity;

import cn.hutool.crypto.digest.MD5;
import com.alibaba.fastjson.JSON;
import com.ir6.ecommerce.dao.EcommerceUserDao;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@Slf4j
@SpringBootTest
@RunWith(SpringRunner.class)
public class EcommerceUserTest {

    @Autowired
    private EcommerceUserDao dao;

    @Test
    public void createUser() {
        EcommerceUser user = new EcommerceUser();
        user.setUsername("ImoocQinyi@imooc.com");
        user.setPassword(MD5.create().digestHex("12345678"));
        user.setExtraInfo("{}");
        log.info("save user: [{}]", JSON.toJSON(dao.save(user)));
    }

}