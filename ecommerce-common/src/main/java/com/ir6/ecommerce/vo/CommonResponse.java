package com.ir6.ecommerce.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommonResponse<T> implements Serializable {
    private int code;
    private T body;
    private String message;

    public CommonResponse(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
