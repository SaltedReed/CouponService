package com.coupon.dao.mapper;

import com.coupon.entity.CouponCommodityRelation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CouponCommodityRelationMapper {
    int insertAll(@Param("relations") List<CouponCommodityRelation> relations);
}
