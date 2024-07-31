package com.coupon.common;

import com.alibaba.fastjson.JSON;

public class MQUtils {
    public static <T> String obj2Payload(T obj) {
        return JSON.toJSONString(obj);
    }

    public static <T> T payload2Obj(String payload, Class<T> clazz) {
        return JSON.parseObject(payload, clazz);
    }
}
