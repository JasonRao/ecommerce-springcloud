package com.ir6.ecommerce.service.impl;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.ir6.ecommerce.common.TableId;
import com.ir6.ecommerce.goods.DeductGoodsInventory;
import com.ir6.ecommerce.service.IGoodsService;
import com.ir6.ecommerce.vo.PageSimpleGoodsInfo;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@SpringBootTest
@RunWith(SpringRunner.class)
public class GoodsServiceImplTest {

    @Autowired
    private IGoodsService goodsService;

    @Test
    public void test_getGoodsInfoByTableId() {
        List<Long> ids = Lists.newArrayList(1L, 2L, 3L);
        List<TableId.Id> tIds = ids.stream() .map(TableId.Id::new).collect(Collectors.toList());
        log.info("test get goods info by table id: [{}]", JSON.toJSONString(goodsService.getGoodsInfoByTableId(new TableId(tIds))));
    }

    @Test
    public void test_getSimpleGoodsInfoByPage() {
        PageSimpleGoodsInfo pageInfo = goodsService.getSimpleGoodsInfoByPage(1);
        log.info("test get simple goods info by page: [{}]", JSON.toJSONString(pageInfo));
    }

    @Test
    public void testGetSimpleGoodsInfoByTableId() {
        List<Long> ids = Lists.newArrayList(1L, 2L, 3L);
        List<TableId.Id> tIds = ids.stream().map(TableId.Id::new).collect(Collectors.toList());
        log.info("test get simple goods info by table id: [{}]", JSON.toJSONString(
                goodsService.getSimpleGoodsInfoByTableId(new TableId(tIds))
        ));
    }

    @Test
    public void testDeductGoodsInventory() {
        List<DeductGoodsInventory> deductGoodsInventories = Lists.newArrayList(
                new DeductGoodsInventory(1L, 100),
                new DeductGoodsInventory(2L, 66));
        log.info("test deduct goods inventory: [{}]", goodsService.deductGoodsInventory(deductGoodsInventories));
    }

}