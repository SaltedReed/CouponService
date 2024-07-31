package com.coupon.service;

import com.coupon.dto.CouponTemplateDTO;
import com.coupon.vo.CouponVO;

import java.util.List;

public interface CouponService {
    /**
     * 创建优惠券
     * @param templateDTO 券信息
     * @return 如果创建成功，返回券ID，否则返回null
     */
    Long createCoupon(CouponTemplateDTO templateDTO);

    /**
     * 发放优惠券
     * @param userId 用户ID
     * @param couponId 优惠券ID
     * @return 错误码，0：成功，1：库存不足，2：不在活动时间内，3：用户不符合限领条件
     */
    Integer offerCoupon(long userId, long couponId);

    /**
     * 使用优惠券
     * @param userId 用户ID
     * @param couponId 优惠券ID
     * @param orderId 使用该券的订单的ID
     * @return 错误码，0：成功，1：优惠券状态!=未使用，2：数据库错误
     */
    Integer useCoupon(long userId, long couponId, long orderId);

    /**
     * 获取用户的所有优惠券信息
     * @param userId 用户ID
     * @return 用户的所有优惠券，包括已使用的和已过期的
     */
    List<CouponVO> getAllCoupons(long userId);
}