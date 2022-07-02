package com.ir6.ecommerce.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Objects;

@Getter
@AllArgsConstructor
public enum GoodsStatus {
    ONLINE(101, "上线"),
    OFFLINE(102, "下线"),
    STOCK_OUT(103, "缺货");

    private final Integer status;

    private final String description;

    public static GoodsStatus of(Integer status) {
        Objects.requireNonNull(status);

        return Arrays.stream(values())
                .filter(bean -> bean.status.equals(status))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException(status + " not exists"));
    }
}
