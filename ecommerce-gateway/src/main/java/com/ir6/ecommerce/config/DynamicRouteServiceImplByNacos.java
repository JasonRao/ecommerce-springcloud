package com.ir6.ecommerce.config;

import com.alibaba.fastjson.JSON;
import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.listener.Listener;
import com.alibaba.nacos.api.exception.NacosException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.Executor;

@Slf4j
@Component
@DependsOn({"gatewayConfig"})
public class DynamicRouteServiceImplByNacos {

    @Autowired
    private DynamicRouteService routeService;

    @PostConstruct
    public void init() {
        ConfigService configService = initConfigService();
        if(configService == null) {
            log.error("init config service fail");
            return;
        }
        try {
            String configInfo = configService.getConfig(GatewayConfig.NACOS_ROUTE_DATA_ID, GatewayConfig.NACOS_ROUTE_GROUP, GatewayConfig.DEFAULT_TIMEOUT);
            log.info("get current gateway config: [{}]", configInfo);
            List<RouteDefinition> routeDefinitions = JSON.parseArray(configInfo, RouteDefinition.class);
            routeDefinitions.forEach(routeDefinition -> {
                log.info("init gateway config: [{}]", routeDefinition.toString());
                routeService.addRoute(routeDefinition);
            });
        } catch (Exception ex) {
            log.error("gateway route init has some error: [{}]", ex.getMessage(), ex);
        }
        addNacosListener4RouteConfig(configService);
    }

    private ConfigService initConfigService() {
        log.info("gateway route init....");
        try {
            Properties properties = new Properties();
            properties.setProperty("serverAddr", GatewayConfig.NACOS_SERVER_ADDR);
            properties.setProperty("namespace", GatewayConfig.NACOS_NAMESPACE);
            return NacosFactory.createConfigService(properties);
        } catch (NacosException ex) {
            log.error("init gateway nacos config error: [{}]", ex.getMessage(), ex);
            return null;
        }
    }

    /**
     * <h2>监听 Nacos下的动态路由配置</h2>
     * */
    private void addNacosListener4RouteConfig(ConfigService configService) {
        try {
            configService.addListener(GatewayConfig.NACOS_ROUTE_DATA_ID, GatewayConfig.NACOS_ROUTE_GROUP, new Listener() {
                @Override
                public Executor getExecutor() {
                    return null;
                }

                @Override
                public void receiveConfigInfo(String configInfo) {
                    log.info("start to update config: [{}]", configInfo);
                    routeService.updateList(JSON.parseArray(configInfo, RouteDefinition.class));
                }
            });
        } catch (Exception ex) {
            log.error("dynamic update gateway config error: [{}]", ex.getMessage(), ex);
        }
    }

}
