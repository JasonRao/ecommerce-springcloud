package com.ir6.ecommerce.service.impl;

import com.ir6.ecommerce.account.BalanceInfo;
import com.ir6.ecommerce.dao.EcommerceBalanceDao;
import com.ir6.ecommerce.entity.EcommerceBalance;
import com.ir6.ecommerce.filter.AccessContext;
import com.ir6.ecommerce.service.IBalanceService;
import com.ir6.ecommerce.vo.LoginUserInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class BalanceServiceImpl implements IBalanceService {
    @Autowired
    private EcommerceBalanceDao balanceDao;

    @Override
    public BalanceInfo getCurrentUserBalance() {
        LoginUserInfo userInfo = AccessContext.get();
        BalanceInfo balanceInfo = new BalanceInfo(userInfo.getId(), 0L);
        EcommerceBalance balance = balanceDao.findByUserId(userInfo.getId());
        if(balance != null) {
            balanceInfo.setBalance(balance.getBalance());
        } else {
            EcommerceBalance newBalance = new EcommerceBalance();
            newBalance.setBalance(0L);
            newBalance.setUserId(userInfo.getId());
            log.info("init user balance record: [{}]",
                    balanceDao.save(newBalance).getId());
        }
        return balanceInfo;
    }

    @Override
    public BalanceInfo deduct(BalanceInfo balanceInfo) {
        LoginUserInfo userInfo = AccessContext.get();
        EcommerceBalance balance = balanceDao.findByUserId(userInfo.getId());
        long newBalance = balance.getBalance() - balanceInfo.getBalance();
        if(balance == null || newBalance < 0) {
            throw new RuntimeException("user balance is not enough!");
        }
        long oldBalance = balance.getBalance();
        balance.setBalance(newBalance);
        log.info("deduct balance: [{}], [{}], [{}]",
                balanceDao.save(balance).getId(), oldBalance, newBalance);
        return new BalanceInfo(userInfo.getId(), balance.getBalance());
    }
}
