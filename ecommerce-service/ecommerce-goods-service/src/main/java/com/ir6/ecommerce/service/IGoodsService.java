package com.ir6.ecommerce.service;

import com.ir6.ecommerce.common.TableId;
import com.ir6.ecommerce.goods.DeductGoodsInventory;
import com.ir6.ecommerce.goods.GoodsInfo;
import com.ir6.ecommerce.goods.SimpleGoodsInfo;
import com.ir6.ecommerce.vo.PageSimpleGoodsInfo;

import java.util.List;

public interface IGoodsService {
    /**
     * <h2>根据 TableId 查询商品详细信息</h2>
     * */
    List<GoodsInfo> getGoodsInfoByTableId(TableId tableId);

    /**
     * <h2>获取分页的商品信息</h2>
     * */
    PageSimpleGoodsInfo getSimpleGoodsInfoByPage(int page);

    /**
     * <h2>根据 TableId 查询简单商品信息</h2>
     * */
    List<SimpleGoodsInfo> getSimpleGoodsInfoByTableId(TableId tableId);

    /**
     * <h2>扣减商品库存</h2>
     * */
    Boolean deductGoodsInventory(List<DeductGoodsInventory> deductGoodsInventories);
}
