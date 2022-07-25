package com.ir6.ecommerce.service.communication;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * <h1>使用Ribbon之前的配置, 增强RestTemplate</h1>
 * */
@Component
public class RibbonConfig {

    //注入 RestTemplate
    @Bean
    @LoadBalanced
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
