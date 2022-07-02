package com.ir6.ecommerce.filter;

import com.ir6.ecommerce.constant.GatewayConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * filter鉴权: 缓存请求 body 的全局过滤器 --> 先将post请求中用户数据(username/pwd)缓存好, 以便其它过滤器中使用
 * Spring WebFlux
 */
@Slf4j
@Component
public class GlobalCacheRequestBodyFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String uri = exchange.getRequest().getURI().getPath();
        boolean isLoginOrRegister = uri.contains(GatewayConstant.LOGIN_URI) || uri.contains(GatewayConstant.REGISTER_URI);
        if (exchange.getRequest().getHeaders().getContentType() == null || !isLoginOrRegister) {
            return chain.filter(exchange);
        }
        log.info("cache request body into data buffer");
        // DataBufferUtils.join拿到请求中的数据缓存到DataBuffer中
        return DataBufferUtils.join(exchange.getRequest().getBody()).flatMap(dataBuffer -> {
            DataBufferUtils.retain(dataBuffer); // 确保数据缓冲区不被释放

            // defer、just 都是去创建数据源, 得到当前数据的副本
            Flux<DataBuffer> cachedFlux = Flux.defer(() -> Flux.just(dataBuffer.slice(0, dataBuffer.readableByteCount())));
            ServerHttpRequest mutatedReq = new ServerHttpRequestDecorator(exchange.getRequest()) {
                @Override
                public Flux<DataBuffer> getBody() {
                    return cachedFlux;
                }
            };

            // 将包装之后的 ServerHttpRequest 向下继续传递
            return chain.filter(exchange.mutate().request(mutatedReq).build());
        });
    }

    @Override
    public int getOrder() {
        return HIGHEST_PRECEDENCE + 1; //需要优先执行, 以便后续过滤器可以使用缓存的Body数据
    }

}
