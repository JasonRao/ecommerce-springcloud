package com.ir6.ecommerce.filter;

import com.alibaba.fastjson.JSON;
import com.ir6.ecommerce.constant.CommonConstant;
import com.ir6.ecommerce.constant.GatewayConstant;
import com.ir6.ecommerce.util.TokenParseUtil;
import com.ir6.ecommerce.vo.JwtToken;
import com.ir6.ecommerce.vo.LoginUserInfo;
import com.ir6.ecommerce.vo.UsernameAndPassword;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.CharBuffer;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
@Component
public class GlobalLoginRegisterFilter implements GlobalFilter, Ordered {
    @Autowired
    private LoadBalancerClient lbClient; /** 注册中心客户端, 可以从注册中心中获取服务实例信息 */

    @Autowired
    private RestTemplate restTemplate;

    /**
     * <h2>登录、注册、鉴权</h2>
     * 1. 如果是登录或注册, 则去授权中心拿到 Token 并返回给客户端
     * 2. 如果是访问其他的服务, 则鉴权, 没有权限返回 401
     * */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();
        String uri = request.getURI().getPath();
        if (uri.contains(GatewayConstant.LOGIN_URI)) {
            return processLoginRequest(request, response);
        }
        if (uri.contains(GatewayConstant.REGISTER_URI)) {
            return processRegisterRequest(request, response);
        }
        LoginUserInfo loginUserInfo = getLoginUserInfo(request);
        if(loginUserInfo == null) {
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return response.setComplete();
        }
        return chain.filter(exchange);  // 解析通过, 则放行
    }

    private Mono<Void> processLoginRequest(ServerHttpRequest request, ServerHttpResponse response) {
        String token = getTokenFromAuthorityCenter(request, GatewayConstant.AUTHORITY_CENTER_TOKEN_URL_FORMAT);
        response.getHeaders().add(CommonConstant.JWT_USER_INFO_KEY, token); // header中不能设置 null
        response.setStatusCode(HttpStatus.OK);
        return response.setComplete();
    }

    private Mono<Void> processRegisterRequest(ServerHttpRequest request, ServerHttpResponse response) {
        String token = getTokenFromAuthorityCenter(request, GatewayConstant.AUTHORITY_CENTER_REGISTER_URL_FORMAT);
        response.getHeaders().add(CommonConstant.JWT_USER_INFO_KEY, token);
        response.setStatusCode(HttpStatus.OK);
        return response.setComplete();
    }

    private String getTokenFromAuthorityCenter(ServerHttpRequest request, String uriFormat) {
        ServiceInstance serviceInstance = lbClient.choose(CommonConstant.AUTHORITY_CENTER_SERVICE_ID);
        log.info("Nacos Client Info: [{}], [{}], [{}]",
                serviceInstance.getServiceId(), serviceInstance.getInstanceId(), JSON.toJSONString(serviceInstance.getMetadata()));

        String requestUrl = String.format(uriFormat, serviceInstance.getHost(), serviceInstance.getPort());
        UsernameAndPassword requestBody = JSON.parseObject(parseBodyFromRequest(request), UsernameAndPassword.class);
        log.info("login request url and body: [{}], [{}]", requestUrl, JSON.toJSONString(requestBody));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        JwtToken token = restTemplate.postForObject(
                requestUrl,
                new HttpEntity<>(JSON.toJSONString(requestBody), headers),
                JwtToken.class
        );
        if (token != null) {
            return token.getToken();
        }
        return "";
    }

    private String parseBodyFromRequest(ServerHttpRequest request) {
        AtomicReference<String> bodyRef = new AtomicReference<>();
        // 订阅缓冲区去消费请求体中的数据
        request.getBody().subscribe(dataBuffer -> {
            CharBuffer charBuffer = StandardCharsets.UTF_8.decode(dataBuffer.asByteBuffer());
            DataBufferUtils.release(dataBuffer); // 一定要释放掉dataBuffer, 防止内存泄露
            bodyRef.set(charBuffer.toString());
        });
        return bodyRef.get();
    }

    private LoginUserInfo getLoginUserInfo(ServerHttpRequest request) {
        String token = request.getHeaders().getFirst(CommonConstant.JWT_USER_INFO_KEY); //登录/注册请求中已将Token放入Header
        LoginUserInfo loginUserInfo = null;
        try {
            loginUserInfo = TokenParseUtil.parseUserInfoFromToken(token);
        } catch (Exception ex) {
            log.error("parse user info from token error: [{}]", ex.getMessage(), ex);
        }
        return loginUserInfo;
    }

    @Override
    public int getOrder() {
        return HIGHEST_PRECEDENCE + 2; //比GlobalCacheRequestBodyFilter优先级低, 后执行
    }
}
