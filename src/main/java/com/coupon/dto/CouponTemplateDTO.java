package com.coupon.dto;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

@Data
public class CouponTemplateDTO {
    @NotBlank
    private String name;
    @NotNull
    private String content;
    @NotNull
    private Integer scopeType;
    private List<Long> scope;
    @NotNull
    private Integer userType;
    @NotNull
    private Integer discountType;
    @NotNull
    private Integer discountThreshold;
    @NotNull
    private Float discountValue;
    @NotNull
    private Date startTime;
    @NotNull
    private Date endTime;
    @NotNull
    @Min(1)
    private Integer maxStock;
}
