package com.ir6.ecommerce.service;

import com.ir6.ecommerce.account.BalanceInfo;

public interface IBalanceService {
    BalanceInfo getCurrentUserBalance();

    /**
     * <h2>扣减用户余额</h2>
     * @param balanceInfo 代表想要扣减的余额
     * */
    BalanceInfo deduct(BalanceInfo balanceInfo);
}
