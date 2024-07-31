package com.coupon.mq.message;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

@Data
@AllArgsConstructor
public class CouponExpiryTrxMsg {
    private long userId;
    private long couponId;
    private Date receiveTime;
    private Date expireTime;
}
