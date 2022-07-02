package com.ir6.ecommerce.dao;

import com.ir6.ecommerce.entity.EcommerceAddress;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EcommerceAddressDao extends JpaRepository<EcommerceAddress, Long> {
    List<EcommerceAddress> findAllByUserId(Long userId);
}
