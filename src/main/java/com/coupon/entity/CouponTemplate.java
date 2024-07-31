package com.coupon.entity;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class CouponTemplate {
    private Long id;
    private String name;
    private String content;
    private Integer scopeType;
    private Long scope;
    private Integer userType;
    private Integer discountType;
    private Integer discountThreshold;
    private Float discountValue;
    private Date startTime;
    private Date endTime;
    private Integer maxStock;
    private Integer stock;
}
