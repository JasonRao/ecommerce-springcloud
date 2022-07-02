package com.ir6.ecommerce.filter;

import com.ir6.ecommerce.vo.LoginUserInfo;

public class AccessContext {

    private static final ThreadLocal<LoginUserInfo> loginUserInfo = new ThreadLocal<>();

    public static LoginUserInfo get() {
        return loginUserInfo.get();
    }

    public static void set(LoginUserInfo userInfo) {
        loginUserInfo.set(userInfo);
    }

    public static void clear() {
        loginUserInfo.remove();
    }

}
