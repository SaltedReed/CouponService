package com.coupon.mq.message;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

@Data
@AllArgsConstructor
public class CouponLowStockMsg {
    private long couponId;
    private int stock;
    private Date time;
}
