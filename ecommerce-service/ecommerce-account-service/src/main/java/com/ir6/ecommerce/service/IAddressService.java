package com.ir6.ecommerce.service;

import com.ir6.ecommerce.account.AddressInfo;
import com.ir6.ecommerce.common.TableId;

public interface IAddressService {
    TableId createAddressInfo(AddressInfo addressInfo);

    AddressInfo getCurrentAddressInfo();

    /**
     * <h2>通过 id 获取用户地址信息, id 是 EcommerceAddress 表的主键</h2>
     * */
    AddressInfo getAddressInfoById(Long id);

    AddressInfo getAddressInfoByTableId(TableId tableId);
}
