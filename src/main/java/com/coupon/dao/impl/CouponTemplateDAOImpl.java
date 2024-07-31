package com.coupon.dao.impl;

import com.coupon.common.CacheUtils;
import com.coupon.dao.CouponTemplateDAO;
import com.coupon.dao.mapper.CouponTemplateMapper;
import com.coupon.dto.CouponStockDTO;
import com.coupon.entity.CouponTemplate;
import jodd.cache.Cache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

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
        final String templateKey = CacheUtils.getCouponTemplateKey(couponId);
        Map<Object, Object> entries = redisTemplate.opsForHash().entries(templateKey);

        CouponTemplate res;
        if (entries.size() == 0) {
            res = templateMapper.get(couponId);

            entries = new HashMap<>(11);
            entries.put("name", res.getName());
            entries.put("content", res.getContent());
            entries.put("scopeType", res.getScopeType());
            entries.put("scope", res.getScope());
            entries.put("userType", res.getUserType());
            entries.put("discountType", res.getDiscountType());
            entries.put("discountThreshold", res.getDiscountThreshold());
            entries.put("discountValue", res.getDiscountValue());
            entries.put("startTime", res.getStartTime());
            entries.put("endTime", res.getEndTime());
            entries.put("maxStock", res.getMaxStock());

            redisTemplate.opsForHash().putAll(templateKey, entries);
        } else {
            // 暂时只放用到的数据
            res = new CouponTemplate();
            res.setId(couponId);
            res.setName((String) entries.get("name"));
            res.setContent((String) entries.get("content"));
            res.setStartTime((Date) entries.get("startTime"));
            res.setEndTime((Date) entries.get("endTime"));
        }
        redisTemplate.expire(templateKey, 600, TimeUnit.SECONDS);
        return res;
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
