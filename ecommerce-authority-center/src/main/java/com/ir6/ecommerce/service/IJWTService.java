package com.ir6.ecommerce.service;

import com.ir6.ecommerce.vo.UsernameAndPassword;

public interface IJWTService {

    /**
     * <h2>生成 JWT Token, 使用默认的超时时间</h2>
     * */
    String generateToken(String username, String password) throws Exception;

    String generateToken(String username, String password, int expire) throws Exception;

    /**
     * <h2>注册用户并生成 Token 返回</h2>
     * */
    String registerAndGenerateToken(UsernameAndPassword usernameAndPassword) throws Exception;

}
