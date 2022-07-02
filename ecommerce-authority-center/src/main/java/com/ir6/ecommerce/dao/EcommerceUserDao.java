package com.ir6.ecommerce.dao;

import com.ir6.ecommerce.entity.EcommerceUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EcommerceUserDao extends JpaRepository<EcommerceUser, Long> {
    EcommerceUser findByUsername(String username);
    EcommerceUser findByUsernameAndPassword(String username, String pwd);
}
