package com.coupon.dao.impl;

import com.coupon.common.CacheUtils;
import com.coupon.dao.CouponInstanceDAO;
import com.coupon.dao.mapper.CouponInstanceMapper;
import com.coupon.entity.CouponInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.concurrent.TimeUnit;

@Repository
public class CouponInstanceDAOImpl implements CouponInstanceDAO {
    @Autowired
    private CouponInstanceMapper instanceMapper;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public int count(long userId, long couponId) {
        final String countKey = CacheUtils.getCouponCountKey(userId, couponId);
        Integer count = (Integer) redisTemplate.opsForValue().get(countKey);
        if (null == count) {
            count = instanceMapper.count(userId, couponId);
            redisTemplate.opsForValue().set(countKey, count, 600, TimeUnit.SECONDS);
        } else {
            redisTemplate.expire(countKey, 600, TimeUnit.SECONDS);
        }
        return count;
    }

    @Override
    public int insert(CouponInstance instance) {
        if (null == instance) {
            return 0;
        }

        int res = instanceMapper.insert(instance);
        // 使相关缓存失效
        final String countKey = CacheUtils.getCouponCountKey(instance.getUserId(), instance.getCouponId());
        CacheUtils.doubleDelete(countKey, 500, redisTemplate);
        return res;
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
