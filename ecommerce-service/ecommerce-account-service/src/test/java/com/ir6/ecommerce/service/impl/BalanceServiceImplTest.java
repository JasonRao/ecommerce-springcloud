package com.ir6.ecommerce.service.impl;

import com.alibaba.fastjson.JSON;
import com.ir6.ecommerce.account.BalanceInfo;
import com.ir6.ecommerce.service.IBalanceService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
public class BalanceServiceImplTest extends BaseTest {
    @Autowired
    private IBalanceService balanceService;

    /**
     * <h2>测试获取当前用户的余额信息</h2>
     * */
    @Test
    public void testGetCurrentUserBalanceInfo() {
        log.info("test get current user balance info: [{}]", JSON.toJSONString(
                balanceService.getCurrentUserBalance()
        ));
    }

    @Test
    public void testDeductBalance() {
        BalanceInfo balanceInfo = new BalanceInfo();
        balanceInfo.setUserId(loginUserInfo.getId());
        balanceInfo.setBalance(1000L);

        log.info("test deduct balance: [{}]", JSON.toJSONString(
                balanceService.deduct(balanceInfo)
        ));
    }
}