package com.coupon.controller;

import com.coupon.common.Result;
import com.coupon.dto.CouponTemplateDTO;
import com.coupon.service.CouponService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/coupon")
public class CouponController {
    @Autowired
    private CouponService couponService;

    @PostMapping("/create")
    public Result<Long> createCoupon(@Valid CouponTemplateDTO templateDTO) {
        Long couponId = couponService.createCoupon(templateDTO);
        return (null == couponId ? Result.error("") : Result.success(couponId));
    }

    @PostMapping("/offer")
    public Result<Void> offerCoupon(long userId, long couponId) {
        Integer res = couponService.offerCoupon(userId, couponId);
        switch (res) {
            case 0:
                return Result.success();
            case 1:
                return Result.error("库存不足");
            case 2:
                return Result.error("不在活动时间内");
            case 3:
                return Result.error("用户不符合限领条件");
            default:
                return Result.error(res.toString());
        }
    }

    @PostMapping("/use")
    public Result<Void> useCoupon(long userId, long couponId, long orderId) {
        Integer res = couponService.useCoupon(userId, couponId, orderId);
        switch (res) {
            case 0:
                return Result.success();
            case 1:
                return Result.error("优惠券已使用或已过期");
            case 2:
                return Result.error("服务器内部错误");
            default:
                return Result.error(res.toString());
        }
    }
}
