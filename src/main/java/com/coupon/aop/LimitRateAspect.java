package com.coupon.aop;

import com.coupon.annotation.LimitRate;
import com.coupon.exception.RateLimitException;
import com.google.common.util.concurrent.RateLimiter;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Aspect
@Component
public class LimitRateAspect {
    private final Map<String, RateLimiter> rateLimiterMap = new ConcurrentHashMap<>();

    @Pointcut("@annotation(com.coupon.annotation.LimitRate)")
    public void pointcut() {}

    @Before("@annotation(limitRateAnnotation)")
    public void before(LimitRate limitRateAnnotation) {
        final String key = limitRateAnnotation.key();
        RateLimiter rateLimiter = rateLimiterMap.getOrDefault(key, null);
        if (null == rateLimiter) {
            rateLimiter = RateLimiter.create(limitRateAnnotation.permitsPerSec());
            rateLimiterMap.put(key, rateLimiter);
        }

        boolean success = rateLimiter.tryAcquire(limitRateAnnotation.timeout(), limitRateAnnotation.unit());
        if (!success) {
            throw new RateLimitException("key="+key);
        }
    }
}
