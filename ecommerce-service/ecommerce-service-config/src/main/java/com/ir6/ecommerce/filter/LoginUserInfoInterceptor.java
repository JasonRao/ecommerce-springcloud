package com.ir6.ecommerce.filter;

import com.ir6.ecommerce.constant.CommonConstant;
import com.ir6.ecommerce.util.TokenParseUtil;
import com.ir6.ecommerce.vo.LoginUserInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
@Component
public class LoginUserInfoInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if(isWhiteListUri(request.getRequestURI())) {
            return true;
        }
        String token = request.getHeader(CommonConstant.JWT_USER_INFO_KEY);
        LoginUserInfo loginUserInfo = null;
        try {
            loginUserInfo = TokenParseUtil.parseUserInfoFromToken(token);
        } catch (Exception ex) {
            log.error("parse user info error: [{}]", ex.getMessage(), ex);
        }
        if (loginUserInfo == null) {
            throw new RuntimeException("can not parse current login user"); //理论上不会到这, 网关层面已对用户身份做过统一拦截
        }
        AccessContext.set(loginUserInfo);
        log.info("set login user info: [{}]", request.getRequestURI());

        return true;
    }

    private boolean isWhiteListUri(String uri) {
        return StringUtils.containsAny(uri, "springfox", "swagger", "v2", "webjars", "doc.html");
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        if(AccessContext.get() != null) {
            AccessContext.clear();
        }
    }
}
