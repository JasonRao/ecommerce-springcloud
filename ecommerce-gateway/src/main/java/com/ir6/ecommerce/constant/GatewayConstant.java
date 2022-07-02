package com.ir6.ecommerce.constant;

public class GatewayConstant {
    public static final String LOGIN_URI = "/ecommerce/login";

    public static final String REGISTER_URI = "/ecommerce/register";

    /** 去授权中心拿到登录 token 的 uri 格式化接口 */
    public static final String AUTHORITY_CENTER_TOKEN_URL_FORMAT = "http://%s:%s/ecommerce-authority-center/authority/token";

    /** 去授权中心注册并拿到 token 的 uri 格式化接口 */
    public static final String AUTHORITY_CENTER_REGISTER_URL_FORMAT = "http://%s:%s/ecommerce-authority-center/authority/register";

}
