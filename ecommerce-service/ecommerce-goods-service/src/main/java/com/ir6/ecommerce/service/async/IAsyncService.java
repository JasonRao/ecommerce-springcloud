package com.ir6.ecommerce.service.async;

import com.ir6.ecommerce.goods.GoodsInfo;

import java.util.List;

public interface IAsyncService {

    /**
     * <h2>异步将商品信息保存下来</h2>
     * */
    void asyncImportGoods(List<GoodsInfo> goodsInfos, String taskId);
}
