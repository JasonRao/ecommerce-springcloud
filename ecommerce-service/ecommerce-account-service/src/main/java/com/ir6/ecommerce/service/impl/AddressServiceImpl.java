package com.ir6.ecommerce.service.impl;

import com.alibaba.fastjson.JSON;
import com.ir6.ecommerce.account.AddressInfo;
import com.ir6.ecommerce.common.TableId;
import com.ir6.ecommerce.dao.EcommerceAddressDao;
import com.ir6.ecommerce.entity.EcommerceAddress;
import com.ir6.ecommerce.filter.AccessContext;
import com.ir6.ecommerce.service.IAddressService;
import com.ir6.ecommerce.vo.LoginUserInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class AddressServiceImpl implements IAddressService {

    @Autowired
    private EcommerceAddressDao addressDao;

    @Override
    public TableId createAddressInfo(AddressInfo addressInfo) {
        LoginUserInfo userInfo = AccessContext.get();

        List<EcommerceAddress> ecommerceAddresses = addressInfo.getAddressItems().stream()
                .map(addr -> EcommerceAddress.to(userInfo.getId(), addr))
                .collect(Collectors.toList());
        List<EcommerceAddress> saveRecords = addressDao.saveAll(ecommerceAddresses);

        List<Long> ids = saveRecords.stream().map(EcommerceAddress::getId).collect(Collectors.toList());
        return new TableId(ids.stream().map(TableId.Id::new).collect(Collectors.toList()));
    }

    @Override
    public AddressInfo getCurrentAddressInfo() {
        LoginUserInfo userInfo = AccessContext.get();
        List<EcommerceAddress> ecommerceAddresses = addressDao.findAllByUserId(userInfo.getId());
        List<AddressInfo.AddressItem> addressItems = ecommerceAddresses.stream()
                .map(EcommerceAddress::toAddressItem)
                .collect(Collectors.toList());
        return new AddressInfo(userInfo.getId(), addressItems);
    }

    @Override
    public AddressInfo getAddressInfoById(Long id) {
        EcommerceAddress address = addressDao.findById(id).orElse(null);
        if(address == null) {
            throw new RuntimeException("address is not exist: " + id);
        }
        return new AddressInfo(address.getUserId(), Collections.singletonList(address.toAddressItem()));
    }

    @Override
    public AddressInfo getAddressInfoByTableId(TableId tableId) {
        List<Long> ids = tableId.getIds().stream().map(TableId.Id::getId).collect(Collectors.toList());
        log.info("get address info by table id: [{}]", JSON.toJSONString(ids));

        List<EcommerceAddress> ecommerceAddresses = addressDao.findAllById(ids);
        if (CollectionUtils.isEmpty(ecommerceAddresses)) {
            return new AddressInfo(-1L, Collections.emptyList());
        }
        List<AddressInfo.AddressItem> addressItems = ecommerceAddresses.stream()
                .map(EcommerceAddress::toAddressItem)
                .collect(Collectors.toList());

        return new AddressInfo(ecommerceAddresses.get(0).getUserId(), addressItems);
    }
}
