package com.coupon.dao.impl;

import com.coupon.dao.CouponInstanceDAO;
import com.coupon.dao.mapper.CouponInstanceMapper;
import com.coupon.entity.CouponInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Date;

@Repository
public class CouponInstanceDAOImpl implements CouponInstanceDAO {
    @Autowired
    private CouponInstanceMapper instanceMapper;

    @Override
    public int count(long userId, long couponId) {
        return instanceMapper.count(userId, couponId);
    }

    @Override
    public int insert(CouponInstance instance) {
        return instanceMapper.insert(instance);
    }

    @Override
    public int selectStateForUpdate(long userId, long couponId) {
        return instanceMapper.selectStateForUpdate(userId, couponId);
    }

    @Override
    public int updateState_UseTime_OrderId(long userId, long couponId, int state, Date useTime, long orderId) {
        return instanceMapper.updateState_UseTime_OrderId(userId, couponId, state, useTime, orderId);
    }

    @Override
    public int updateState(long userId, long couponId, int state) {
        return instanceMapper.updateState(userId, couponId, state);
    }
}
