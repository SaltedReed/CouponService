package com.coupon.dao;

import com.coupon.entity.CouponInstance;

import java.util.Date;

public interface CouponInstanceDAO {
    int count(long userId, long couponId);
    int insert(CouponInstance instance);
    int selectStateForUpdate(long userId, long couponId);
    int updateState_UseTime_OrderId(long userId, long couponId, int state, Date useTime, long orderId);
    int updateState(long userId, long couponId, int state);
}
