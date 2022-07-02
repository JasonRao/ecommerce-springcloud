package com.ir6.ecommerce.dao;

import com.ir6.ecommerce.constant.BrandCategory;
import com.ir6.ecommerce.constant.GoodsCategory;
import com.ir6.ecommerce.entity.EcommerceGoods;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Optional;

public interface EcommerceGoodsDao extends PagingAndSortingRepository<EcommerceGoods, Long> {
    /**
     * <h2>根据查询条件查询商品表, 并限制返回结果</h2>
     * select * from t_ecommerce_goods where goods_category = ? and brand_category = ? and goods_name = ? limit 1;
     * */
    Optional<EcommerceGoods> findFirst1ByGoodsCategoryAndBrandCategoryAndGoodsName(
            GoodsCategory goodsCategory, BrandCategory brandCategory, String goodsName);
}
