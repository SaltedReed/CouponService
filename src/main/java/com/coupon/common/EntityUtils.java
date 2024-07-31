package com.coupon.common;

import com.coupon.dto.CouponTemplateDTO;
import com.coupon.entity.CouponTemplate;


public class EntityUtils {
    public static CouponTemplate couponTemplateDTO2Entity(CouponTemplateDTO dto) {
        if (null == dto) {
            return null;
        }

        CouponTemplate entity = new CouponTemplate();
        entity.setName(dto.getName());
        entity.setContent(dto.getContent());
        entity.setScopeType(dto.getScopeType());
        if (EntityConstants.SCOPE_TYPE_SHOP == dto.getScopeType()) {
            entity.setScope(dto.getScope().get(0));
        }
        entity.setUserType(dto.getUserType());
        entity.setDiscountType(dto.getDiscountType());
        entity.setDiscountThreshold(dto.getDiscountThreshold());
        entity.setDiscountValue(dto.getDiscountValue());
        entity.setStartTime(dto.getStartTime());
        entity.setEndTime(dto.getEndTime());
        entity.setMaxStock(dto.getMaxStock());
        entity.setStock(dto.getMaxStock());

        return entity;
    }
}
