package com.ir6.ecommerce.advice;

import com.ir6.ecommerce.vo.CommonResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandleAdvice {

    @ExceptionHandler(value = Exception.class)
    public CommonResponse<String> handleException(HttpServletRequest req, Exception ex) {
        CommonResponse<String> response = new CommonResponse<>(-1, "business error");
        response.setBody(ex.getMessage());
        log.error("commerce service has error: [{}]", ex.getMessage(), ex);
        return response;
    }

}
