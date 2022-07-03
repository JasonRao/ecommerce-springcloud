package com.ir6.ecommerce.service.async;

import com.alibaba.fastjson.JSON;
import com.ir6.ecommerce.dao.EcommerceGoodsDao;
import com.ir6.ecommerce.entity.EcommerceGoods;
import com.ir6.ecommerce.goods.GoodsInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.IterableUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.ir6.ecommerce.constant.GoodsConstant.ECOMMERCE_GOODS_DICT_KEY;

@Slf4j
@Service
@Transactional
public class AsyncServiceImpl implements IAsyncService {

    @Autowired
    private EcommerceGoodsDao goodsDao;

    @Autowired
    private StringRedisTemplate redisTemplate;

    /**
     * <h2>异步任务需要加上注解, 并指定使用的线程池</h2>
     * 异步任务处理两件事:
     *  1. 将商品信息保存到数据表
     *  2. 更新商品缓存
     * */
    @Async("getAsyncExecutor")
    @Override
    public void asyncImportGoods(List<GoodsInfo> goodsInfos, String taskId) {
        log.info("async task running taskId: [{}]", taskId);
        StopWatch watch = StopWatch.createStarted();

        List<EcommerceGoods> ecommerceGoods = goodsInfos.stream()
                .map(EcommerceGoods::to)
                .filter(goods -> {
                    EcommerceGoods duplicateGoods = goodsDao.findFirst1ByGoodsCategoryAndBrandCategoryAndGoodsName
                            (goods.getGoodsCategory(), goods.getBrandCategory(), goods.getGoodsName()).orElse(null);
                    return duplicateGoods == null;
                }).collect(Collectors.toList());

        List<EcommerceGoods> savedGoods = IterableUtils.toList(goodsDao.saveAll(ecommerceGoods));
        saveNewGoodsInfoToRedis(savedGoods);
        log.info("save goods info to db and redis: [{}]", savedGoods.size());

        watch.stop();
        log.info("check and import goods success: [{}ms]", watch.getTime(TimeUnit.MILLISECONDS));
    }

    /**
     * <h2>将保存到数据表中的数据缓存到 Redis 中</h2>
     * dict: key -> <id, SimpleGoodsInfo(json)>
     * */
    private void saveNewGoodsInfoToRedis(List<EcommerceGoods> savedGoods) {
        Map<String, String> id2JsonMap = savedGoods.stream()
                .map(EcommerceGoods::toSimple)
                .collect(Collectors.toMap(k -> k.getId().toString(), v -> JSON.toJSONString(v)));

        redisTemplate.opsForHash().putAll(ECOMMERCE_GOODS_DICT_KEY, id2JsonMap);

    }
}
