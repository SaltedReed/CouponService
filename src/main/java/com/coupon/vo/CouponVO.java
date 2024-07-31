package com.coupon.vo;

import lombok.Data;

import java.util.Date;

@Data
public class CouponVO {
    private String name;
    private String content;
    private Date startTime;
    private Date endTime;
    private Integer state;
}
