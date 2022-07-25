package com.ir6.ecommerce.service.communication;

import com.alibaba.fastjson.JSON;
import com.ir6.ecommerce.vo.JwtToken;
import com.ir6.ecommerce.vo.UsernameAndPassword;
import com.netflix.loadbalancer.*;
import com.netflix.loadbalancer.reactive.LoadBalancerCommand;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import rx.Observable;

import java.util.ArrayList;
import java.util.List;

import static com.ir6.ecommerce.constant.CommonConstant.AUTHORITY_CENTER_SERVICE_ID;

/**
 * <h1>使用 Ribbon 实现微服务通信</h1>
 * */
@Slf4j
@Service
public class UseRibbonService {
    @Autowired
    private RestTemplate restTemplate;
    /**
     * 注意 url 中的 ip 和端口换成了服务名称
     * <h2>通过 Ribbon 调用 Authority 服务获取 Token</h2>
     * */
    public JwtToken getTokenFromAuthorityServiceByRibbon(UsernameAndPassword usernameAndPassword) {
        String requestUrl = String.format("http://%s/ecommerce-authority-center/authority/token", AUTHORITY_CENTER_SERVICE_ID);
        log.info("login request url and body: [{}], [{}]", requestUrl, JSON.toJSONString(usernameAndPassword));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // 这里一定要使用自己注入的 RestTemplate
        return restTemplate.postForObject(
                requestUrl,
                new HttpEntity<>(JSON.toJSONString(usernameAndPassword), headers),
                JwtToken.class
        );
    }

    /**
     * <h2>使用原生的 Ribbon Api, 看看Ribbon是如何完成: 服务调用 + 负载均衡</h2>
     * */
    public JwtToken thinkingInRibbon(UsernameAndPassword usernameAndPassword) {
        BaseLoadBalancer loadBalancer = buildLoadBalancer();
        String result = LoadBalancerCommand.builder().withLoadBalancer(loadBalancer)
                .build().submit(server -> {
                    String targetUrl = String.format(
                            "http://%s/ecommerce-authority-center/authority/token",
                            String.format("%s:%s", server.getHost(), server.getPort())
                    );
                    log.info("target request url: [{}]", targetUrl);

                    HttpHeaders headers = new HttpHeaders();
                    headers.setContentType(MediaType.APPLICATION_JSON);
                    String tokenStr = new RestTemplate().postForObject( //使用原生的RestTemplate
                            targetUrl,
                            new HttpEntity<>(JSON.toJSONString(usernameAndPassword), headers),
                            String.class
                    );
                    return Observable.just(tokenStr);
                }).toBlocking().first().toString();

        return JSON.parseObject(result, JwtToken.class);
    }

    @Autowired
    private DiscoveryClient discoveryClient;

    private BaseLoadBalancer buildLoadBalancer() {
        // 1. 找到服务提供方的地址和端口号
        List<ServiceInstance> targetInstances = discoveryClient.getInstances(AUTHORITY_CENTER_SERVICE_ID);

        // 构造Ribbon服务列表
        List<Server> servers = new ArrayList<>(targetInstances.size());
        targetInstances.forEach(i -> {
            servers.add(new Server(i.getHost(), i.getPort()));
            log.info("found target instance: [{}] -> [{}]", i.getHost(), i.getPort());
        });

        // 2. 使用负载均衡策略实现远端服务调用
        BaseLoadBalancer loadBalancer = LoadBalancerBuilder.newBuilder().buildFixedServerListLoadBalancer(servers); // 构建 Ribbon负载实例
        loadBalancer.setRule(new RetryRule(new RandomRule(), 300)); // 设置负载均衡策略
        return loadBalancer;
    }

}
