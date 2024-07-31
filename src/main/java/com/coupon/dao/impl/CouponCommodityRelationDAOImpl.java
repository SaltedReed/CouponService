package com.coupon.dao.impl;

import com.coupon.dao.CouponCommodityRelationDAO;
import com.coupon.dao.mapper.CouponCommodityRelationMapper;
import com.coupon.entity.CouponCommodityRelation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class CouponCommodityRelationDAOImpl implements CouponCommodityRelationDAO {
    @Autowired
    private CouponCommodityRelationMapper ccRelationMapper;

    @Override
    public int insertAll(List<CouponCommodityRelation> relations) {
        return ccRelationMapper.insertAll(relations);
    }
}
