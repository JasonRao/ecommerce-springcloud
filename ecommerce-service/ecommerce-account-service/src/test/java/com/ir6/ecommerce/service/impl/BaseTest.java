package com.ir6.ecommerce.service.impl;

import com.ir6.ecommerce.filter.AccessContext;
import com.ir6.ecommerce.vo.LoginUserInfo;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
public abstract class BaseTest {
    protected final LoginUserInfo loginUserInfo = new LoginUserInfo(
            10L, "ImoocQinyi@imooc.com"
    );

    @Before
    public void init() {
        AccessContext.set(loginUserInfo);
    }

    @After
    public void destroy() {
        AccessContext.clear();
    }
}
