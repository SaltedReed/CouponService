package com.coupon.common;

import org.springframework.data.redis.core.RedisTemplate;

import java.security.InvalidParameterException;

public class CacheUtils {
    public static String getCouponInstanceStateKey(long userId, long couponId) {
        return "coupon:state:" + userId + ":" + couponId;
    }

    public static String getCouponListKey(long userId) {
        return "coupon:list:" + userId;
    }

    public static String getCouponTemplateKey(long couponId) {
        return "coupon:template:" + couponId;
    }

    public static String getCouponStockKey(long couponId) {
        return "coupon:stock:" + couponId;
    }

    public static void doubleDelete(String key, long delayMs, RedisTemplate<String,?> redisTemplate) {
        if (null == key || key.length() == 0) {
            throw new InvalidParameterException("key cannot be blank");
        }
        if (delayMs < 0) {
            throw new InvalidParameterException("delayMs cannot be less than zero");
        }
        redisTemplate.delete(key);

        // todo: thread pool
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(Math.max(0, delayMs));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                redisTemplate.delete(key);
            }
        });
        thread.start();
    }
}
