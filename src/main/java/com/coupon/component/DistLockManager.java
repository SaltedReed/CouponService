package com.coupon.component;

import java.util.concurrent.TimeUnit;

public interface DistLockManager {
    void lock(String key);
    void lock(String key, long timeout, TimeUnit unit);
    boolean tryLock(String key, long time, TimeUnit unit) throws InterruptedException;
    void unlock(String key);
}
