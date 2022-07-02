package com.ir6.ecommerce.converter;

import com.ir6.ecommerce.constant.GoodsCategory;

import javax.persistence.AttributeConverter;

public class GoodsCategoryConverter implements AttributeConverter<GoodsCategory, String> {
    @Override
    public String convertToDatabaseColumn(GoodsCategory goodsCategory) {
        return goodsCategory.getCode();
    }

    @Override
    public GoodsCategory convertToEntityAttribute(String code) {
        return GoodsCategory.of(code);
    }
}
