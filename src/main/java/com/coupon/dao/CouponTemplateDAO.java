package com.coupon.dao;

import com.coupon.entity.CouponTemplate;

public interface CouponTemplateDAO {
    int insert(CouponTemplate template);
    CouponTemplate get(long couponId);
    void loadAllStocks();
    int selectStockForUpdate(long couponId);
    int incrDBStockBy(long couponId, int delta);
}
