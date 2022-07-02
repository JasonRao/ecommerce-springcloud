package com.ir6.ecommerce.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.event.RefreshRoutesEvent;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionLocator;
import org.springframework.cloud.gateway.route.RouteDefinitionWriter;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * 事件推送 Aware: 动态更新路由网关
 * */
@Slf4j
@Service
public class DynamicRouteService implements ApplicationEventPublisherAware {
    private final RouteDefinitionWriter writer;  //写路由定义
    private final RouteDefinitionLocator locator;//获取路由定义
    private ApplicationEventPublisher publisher;

    public DynamicRouteService(RouteDefinitionWriter writer, RouteDefinitionLocator locator) {
        this.writer = writer;
        this.locator = locator;
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.publisher = applicationEventPublisher;
    }

    public String addRoute(RouteDefinition definition) {
        writer.save(Mono.just(definition)).subscribe();       // 保存路由配置并发布
        publisher.publishEvent(new RefreshRoutesEvent(this)); // 发布事件通知给 Gateway, 同步新增的路由定义

        return "success";
    }

    public String updateList(List<RouteDefinition> definitions) {
        List<RouteDefinition> existedDefinitions = locator.getRouteDefinitions().buffer().blockFirst();
        existedDefinitions.forEach(definition -> deleteRoute(definition.getId()));

        definitions.forEach(definition -> updateRoute(definition));
        return "success";
    }

    private String deleteRoute(String id) {
        log.info("gateway delete route id: [{}]", id);
        try {
            writer.delete(Mono.just(id)).subscribe();
            publisher.publishEvent(new RefreshRoutesEvent(this));
            return "delete success";
        } catch (Exception ex) {
            log.error("gateway delete route fail: [{}]", ex.getMessage(), ex);
            return "delete fail";
        }
    }

    private String updateRoute(RouteDefinition definition) {
        log.info("gateway update route: [{}]", definition);
        try {
            writer.delete(Mono.just(definition.getId()));
        } catch (Exception ex) {
            return "update fail, not find route routeId: " + definition.getId();
        }
        try {
            writer.save(Mono.just(definition)).subscribe();
            publisher.publishEvent(new RefreshRoutesEvent(this));
            return "success";
        } catch (Exception ex) {
            return "update route fail";
        }
    }
}
