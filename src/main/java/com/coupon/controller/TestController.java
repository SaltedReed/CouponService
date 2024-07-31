package com.coupon.controller;

import org.apache.rocketmq.client.producer.MQProducer;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/test")
public class TestController {
    @Autowired
    private RedissonClient redissonClient;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    public void testMQ() {
        
    }

    @PostMapping("/lua")
    public void testLua() {
        String str=
                "local value=redis.call('get',KEYS[1])\n" +
                "if (value and tonumber(value)>0) then\n" +
                "   redis.call('incrby',KEYS[1],-1)\n" +
                "   return 0\n" +
                "end\n" +
                "return 1";
        RedisScript<Long> script=new DefaultRedisScript<>(str, Long.class);
        List<String> keys= Arrays.asList("k1");
        Long result=redisTemplate.execute(script, keys);
        System.out.println(result);
    }

    @PostMapping("/redisson")
    public void testRedisson() {
        String lockKey = "test-lock";
        RLock lock = redissonClient.getLock(lockKey);
        try {
            lock.lock();
            System.out.println("got lock "+lockKey);
            Thread.sleep(1000*30);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
            System.out.println("released lock "+lockKey);
        }
    }
}
