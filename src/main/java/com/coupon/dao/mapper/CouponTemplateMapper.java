package com.coupon.dao.mapper;

import com.coupon.dto.CouponStockDTO;
import com.coupon.entity.CouponTemplate;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CouponTemplateMapper {
    int insert(CouponTemplate template);
    CouponTemplate get(long couponId);
    List<CouponStockDTO> getAllStocks();
    int selectStockForUpdate(long couponId);
    int incrStockBy(@Param("couponId") long couponId, @Param("delta") int delta);
}
