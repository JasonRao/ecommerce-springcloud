package com.ir6.ecommerce.account;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@ApiModel(description = "用户账户余额信息")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BalanceInfo {
    @ApiModelProperty(value = "用户主键id")
    private Long userId;

    @ApiModelProperty(value = "用户账户余额")
    private Long balance;
}
