package com.ir6.ecommerce.controller;

import com.ir6.ecommerce.service.communication.AuthorityFeignClient;
import com.ir6.ecommerce.service.communication.UseRestTemplateService;
import com.ir6.ecommerce.service.communication.UseRibbonService;
import com.ir6.ecommerce.vo.JwtToken;
import com.ir6.ecommerce.vo.UsernameAndPassword;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <h1>微服务通信 Controller</h1>
 * */
@RestController
@RequestMapping("/communication")
public class CommunicationController {

    @Autowired
    private UseRestTemplateService restTemplateService;

    @Autowired
    private UseRibbonService ribbonService;

    @Autowired
    private AuthorityFeignClient feignClient;

    @PostMapping("/rest-template")
    public JwtToken getTokenFromAuthorityService(@RequestBody UsernameAndPassword usernameAndPassword) {
        return restTemplateService.getTokenFromAuthorityService(usernameAndPassword);
    }

    @PostMapping("/rest-template-load-balancer")
    public JwtToken getTokenFromAuthorityServiceWithLoadBalancer(@RequestBody UsernameAndPassword usernameAndPassword) {
        return restTemplateService.getTokenFromAuthorityServiceWithLoadBalancer(usernameAndPassword);
    }

    @PostMapping("/ribbon")
    public JwtToken getTokenFromAuthorityServiceByRibbon(@RequestBody UsernameAndPassword usernameAndPassword) {
        return ribbonService.getTokenFromAuthorityServiceByRibbon(usernameAndPassword);
    }

    @PostMapping("/thinking-in-ribbon")
    public JwtToken thinkingInRibbon(@RequestBody UsernameAndPassword usernameAndPassword) {
        return ribbonService.thinkingInRibbon(usernameAndPassword);
    }

    @PostMapping("/token-by-feign")
    public JwtToken getTokenByFeign(@RequestBody UsernameAndPassword usernameAndPassword) {
        return feignClient.getTokenByFeign(usernameAndPassword);
    }

}
