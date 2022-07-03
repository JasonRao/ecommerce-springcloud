package com.ir6.ecommerce.service.impl;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.ir6.ecommerce.common.TableId;
import com.ir6.ecommerce.constant.GoodsConstant;
import com.ir6.ecommerce.dao.EcommerceGoodsDao;
import com.ir6.ecommerce.entity.EcommerceGoods;
import com.ir6.ecommerce.goods.DeductGoodsInventory;
import com.ir6.ecommerce.goods.GoodsInfo;
import com.ir6.ecommerce.goods.SimpleGoodsInfo;
import com.ir6.ecommerce.service.IGoodsService;
import com.ir6.ecommerce.vo.PageSimpleGoodsInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class GoodsServiceImpl implements IGoodsService {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private EcommerceGoodsDao goodsDao;

    // 详细的商品信息, 不能从 redis cache 中去拿
    @Override
    public List<GoodsInfo> getGoodsInfoByTableId(TableId tableId) {
        List<Long> ids = tableId.getIds().stream().map(TableId.Id::getId).collect(Collectors.toList());
        log.info("get goods info by ids: [{}]", JSON.toJSONString(ids));
        List<EcommerceGoods> ecommerceGoods = IterableUtils.toList(goodsDao.findAllById(ids));
        return ecommerceGoods.stream().map(EcommerceGoods::toGoodsInfo).collect(Collectors.toList());
    }

    // 分页不能从 redis cache 中去拿
    @Override
    public PageSimpleGoodsInfo getSimpleGoodsInfoByPage(int page) {
        PageRequest pageRequest = PageRequest.of(page - 1, 10, Sort.by("id").descending());
        Page<EcommerceGoods> pageInfo = goodsDao.findAll(pageRequest);
        boolean hasMore = pageInfo.getTotalPages() > page;
        return new PageSimpleGoodsInfo(
                    pageInfo.getContent().stream().map(EcommerceGoods::toSimple).collect(Collectors.toList()),
                    hasMore);
    }

    // 获取商品的简单信息, 可以从 redis cache 中去拿, 拿不到需要从 DB 中获取并保存到 Redis
    @Override
    public List<SimpleGoodsInfo> getSimpleGoodsInfoByTableId(TableId tableId) {
        List<Object> goodIds = tableId.getIds().stream().map(i -> i.getId().toString()).collect(Collectors.toList());
        List<Object> cachedSimpleGoodsInfos = redisTemplate.opsForHash()
                .multiGet(GoodsConstant.ECOMMERCE_GOODS_DICT_KEY, goodIds)
                .stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        if(CollectionUtils.isNotEmpty(cachedSimpleGoodsInfos)) {
            // 1. 如果从缓存中查询出所有需要的 SimpleGoodsInfo
            if(cachedSimpleGoodsInfos.size() == goodIds.size()) {
                log.info("get simple goods info by ids (from cache): [{}]", JSON.toJSONString(goodIds));
                return parseCachedGoodsInfo(cachedSimpleGoodsInfos);
            } else {
                // 2. redis cache中没有的需要从DB中取
                List<SimpleGoodsInfo> cachedGoodsInfo = parseCachedGoodsInfo(cachedSimpleGoodsInfos);
                Collection<Long> subtractIds = CollectionUtils.subtract(
                        goodIds.stream().map(id -> Long.valueOf(id.toString())).collect(Collectors.toList()),
                        cachedGoodsInfo.stream().map(SimpleGoodsInfo::getId).collect(Collectors.toList()));
                List<SimpleGoodsInfo> dbGoodsInfo = queryGoodsFromDBAndCacheToRedis(new TableId(subtractIds.stream().map(TableId.Id::new).collect(Collectors.toList())));
                log.info("get simple goods info by ids (from db and cache): [{}]", JSON.toJSONString(subtractIds));
                return Lists.newArrayList(CollectionUtils.union(cachedGoodsInfo, dbGoodsInfo));
            }
        } else {
            return queryGoodsFromDBAndCacheToRedis(tableId);
        }
    }

    // Redis 中的 KV 都是字符串类型
    private List<SimpleGoodsInfo> parseCachedGoodsInfo(List<Object> cachedSimpleGoodsInfos) {
        return cachedSimpleGoodsInfos.stream().map(goods -> JSON.parseObject(goods.toString(), SimpleGoodsInfo.class)).collect(Collectors.toList());
    }

    private List<SimpleGoodsInfo> queryGoodsFromDBAndCacheToRedis(TableId tableId) {
        List<Long> ids = tableId.getIds().stream().map(TableId.Id::getId).collect(Collectors.toList());
        log.info("get goods info by ids: [{}]", JSON.toJSONString(ids));
        List<EcommerceGoods> ecommerceGoods = IterableUtils.toList(goodsDao.findAllById(ids));
        List<SimpleGoodsInfo> result = ecommerceGoods.stream().map(EcommerceGoods::toSimple).collect(Collectors.toList());

        // 将结果缓存, 下一次可以直接从 redis cache 中查询
        log.info("cache goods info: [{}]", JSON.toJSONString(ids));
        Map<Long, String> goodsMap = result.stream().collect(Collectors.toMap(SimpleGoodsInfo::getId, v -> JSON.toJSONString(v)));
        redisTemplate.opsForHash().putAll(GoodsConstant.ECOMMERCE_GOODS_DICT_KEY, goodsMap);
        return result;
    }

    @Override
    public Boolean deductGoodsInventory(List<DeductGoodsInventory> deductGoodsInventories) {
        deductGoodsInventories.forEach(d -> {
            if (d.getCount() <= 0) {
                throw new RuntimeException("purchase goods count need > 0");
            }
        });
        List<EcommerceGoods> ecommerceGoods = IterableUtils.toList(
                goodsDao.findAllById(deductGoodsInventories.stream().map(DeductGoodsInventory::getGoodsId).collect(Collectors.toList())));
        if (CollectionUtils.isEmpty(ecommerceGoods)) {
            throw new RuntimeException("can not found any goods by request");
        }
        // 查询出来的商品数量与传递的不一致, 抛异常
        if (ecommerceGoods.size() != deductGoodsInventories.size()) {
            throw new RuntimeException("request is not valid");
        }
        Map<Long, DeductGoodsInventory> deductMap = deductGoodsInventories.stream().collect(Collectors.toMap(DeductGoodsInventory::getGoodsId, v -> v));
        ecommerceGoods.forEach(g -> {
            Long currentInventory = g.getInventory();
            Integer needDeductInventory = deductMap.get(g.getId()).getCount();
            if (currentInventory < needDeductInventory) {
                throw new RuntimeException("goods inventory is not enough: " + g.getId());
            }
            g.setInventory(currentInventory - needDeductInventory);
            log.info("deduct goods inventory: [{}], [{}], [{}]", g.getId(), currentInventory, g.getInventory());
        });
        goodsDao.saveAll(ecommerceGoods);
        log.info("deduct goods inventory done");

        return true;
    }
}
