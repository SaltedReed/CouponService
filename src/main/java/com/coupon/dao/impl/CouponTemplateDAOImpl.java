package com.coupon.dao.impl;

import com.coupon.common.CacheUtils;
import com.coupon.dao.CouponTemplateDAO;
import com.coupon.dao.mapper.CouponTemplateMapper;
import com.coupon.dto.CouponStockDTO;
import com.coupon.entity.CouponTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class CouponTemplateDAOImpl implements CouponTemplateDAO {
    @Autowired
    private CouponTemplateMapper templateMapper;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public int insert(CouponTemplate template) {
        return templateMapper.insert(template);
    }

    @Override
    public CouponTemplate get(long couponId) {
        return templateMapper.get(couponId);
    }

    @Override
    public void loadAllStocks() {
        List<CouponStockDTO> stocks = templateMapper.getAllStocks();
        Map<String, Integer> keyStockMap = new HashMap<>(stocks.size());
        for (CouponStockDTO dto: stocks) {
            keyStockMap.put(CacheUtils.getCouponStockKey(dto.getCouponId()), dto.getStock());
        }
        redisTemplate.opsForValue().multiSet(keyStockMap);
    }

    @Override
    public int selectStockForUpdate(long couponId) {
        return templateMapper.selectStockForUpdate(couponId);
    }

    @Override
    public int incrDBStockBy(long couponId, int delta) {
        return templateMapper.incrStockBy(couponId, delta);
    }
}
