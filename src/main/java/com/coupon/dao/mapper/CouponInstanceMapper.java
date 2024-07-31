package com.coupon.dao.mapper;

import com.coupon.entity.CouponInstance;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;

@Mapper
public interface CouponInstanceMapper {
    int count(@Param("userId") long userId, @Param("couponId") long couponId);
    int insert(CouponInstance instance);
    int selectStateForUpdate(@Param("userId") long userId, @Param("couponId") long couponId);
    int updateState_UseTime_OrderId(@Param("userId") long userId, @Param("couponId") long couponId,
                                    @Param("state") int state, @Param("useTime") Date useTime, @Param("orderId") long orderId);
    int updateState(@Param("userId") long userId, @Param("couponId") long couponId, @Param("state") int state);
}
