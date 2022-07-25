package com.ir6.ecommerce.service.communication;

import com.ir6.ecommerce.vo.JwtToken;
import com.ir6.ecommerce.vo.UsernameAndPassword;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import static com.ir6.ecommerce.constant.CommonConstant.AUTHORITY_CENTER_SERVICE_ID;

/**
 * <h1>与 Authority服务通信的Feign Client接口定义</h1>
 * */
@FeignClient(contextId = "AuthorityFeignClient", value = AUTHORITY_CENTER_SERVICE_ID)
public interface AuthorityFeignClient {

    /**
     * <h2>通过 OpenFeign 访问 Authority 获取 Token</h2>
     * */
    @RequestMapping(value = "/ecommerce-authority-center/authority/token",
                    method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    JwtToken getTokenByFeign(@RequestBody UsernameAndPassword usernameAndPassword);
}
