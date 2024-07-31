package com.coupon.entity;

import lombok.Data;

@Data
public class CouponCommodityRelation {
    private Long id;
    private Long couponId;
    private Long commodityId;
}
