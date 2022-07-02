package com.ir6.ecommerce.controller;

import com.alibaba.fastjson.JSON;
import com.ir6.ecommerce.annotation.IgnoreResponseAdvice;
import com.ir6.ecommerce.service.IJWTService;
import com.ir6.ecommerce.vo.JwtToken;
import com.ir6.ecommerce.vo.UsernameAndPassword;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/authority")
public class AuthorityController {

    @Autowired
    private IJWTService jwtService;

    @IgnoreResponseAdvice
    @PostMapping("/token")
    public JwtToken token(@RequestBody UsernameAndPassword userPwd) throws Exception {
        log.info("request to get token with param: [{}]", JSON.toJSONString(userPwd));
        return new JwtToken(jwtService.generateToken(userPwd.getUsername(), userPwd.getPassword()));
    }

    @IgnoreResponseAdvice
    @PostMapping("/register")
    public JwtToken register(@RequestBody UsernameAndPassword userPwd) throws Exception {
        log.info("register user with param: [{}]", JSON.toJSONString(userPwd));
        return new JwtToken(jwtService.registerAndGenerateToken(userPwd));
    }

}
