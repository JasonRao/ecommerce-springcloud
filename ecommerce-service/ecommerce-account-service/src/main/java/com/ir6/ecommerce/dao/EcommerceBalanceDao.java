package com.ir6.ecommerce.dao;

import com.ir6.ecommerce.entity.EcommerceBalance;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EcommerceBalanceDao extends JpaRepository<EcommerceBalance, Long> {
    EcommerceBalance findByUserId(Long userId);
}
