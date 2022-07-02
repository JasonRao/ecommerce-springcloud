package com.ir6.ecommerce.config;

import com.ir6.ecommerce.constant.GatewayConstant;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * <h1>配置登录请求转发规则</h1>
 * */
@Configuration
public class RouteLocatorConfig {

    /**
     * 使用代码定义路由规则, 在网关层面拦截登录和注册接口
     * */
    @Bean
    public RouteLocator loginRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("ecommerce_authority", r -> r.path("/gw" + GatewayConstant.LOGIN_URI, "/gw" + GatewayConstant.REGISTER_URI)
                        .uri("http://localhost:9001/"))
                .build();
    }

}
