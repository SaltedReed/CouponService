package com.coupon.entity;

import lombok.Data;

import java.util.Date;

@Data
public class CouponInstance {
    private Long id;
    private Long couponId;
    private Long userId;
    private Integer state;
    private Date receiveTime;
    private Date useTime;
    private Long orderId;
}
