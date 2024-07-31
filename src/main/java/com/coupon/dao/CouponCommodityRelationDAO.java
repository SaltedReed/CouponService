package com.coupon.dao;

import com.coupon.entity.CouponCommodityRelation;
import java.util.List;

public interface CouponCommodityRelationDAO {
    int insertAll(List<CouponCommodityRelation> relations);
}
