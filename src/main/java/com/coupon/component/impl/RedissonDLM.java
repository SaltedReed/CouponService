package com.coupon.component.impl;

import com.coupon.component.DistLockManager;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class RedissonDLM implements DistLockManager {
    @Autowired
    private RedissonClient redissonClient;

    @Override
    public void lock(String key) {
        redissonClient.getLock(key).lock();
    }

    @Override
    public void lock(String key, long time, TimeUnit unit) {
        redissonClient.getLock(key).lock(time, unit);
    }

    @Override
    public boolean tryLock(String key, long time, TimeUnit unit) throws InterruptedException {
        return redissonClient.getLock(key).tryLock(time, unit);
    }

    @Override
    public void unlock(String key) {
        redissonClient.getLock(key).unlock();
    }
}
