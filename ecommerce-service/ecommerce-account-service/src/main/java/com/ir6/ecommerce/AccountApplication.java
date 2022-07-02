package com.ir6.ecommerce;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 *  127.0.0.1:8003/ecommerce-account-service/swagger-ui.html --original
 *  127.0.0.1:8003/ecommerce-account-service/doc.html        --beauty
 */
@EnableJpaAuditing
@SpringBootApplication
@EnableDiscoveryClient
public class AccountApplication {
    public static void main(String[] args) {
        SpringApplication.run(AccountApplication.class, args);
    }
}
