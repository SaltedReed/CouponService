package com.coupon.service.impl;

import com.coupon.annotation.Log;
import com.coupon.common.*;
import com.coupon.component.DistLockManager;
import com.coupon.dao.CouponCommodityRelationDAO;
import com.coupon.dao.CouponInstanceDAO;
import com.coupon.dao.CouponTemplateDAO;
import com.coupon.dto.CouponTemplateDTO;
import com.coupon.entity.CouponCommodityRelation;
import com.coupon.entity.CouponTemplate;
import com.coupon.mq.message.CouponExpiryTrxMsg;
import com.coupon.service.CouponService;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
@Slf4j
public class CouponServiceImpl implements CouponService {
    private static final Logger logger = LoggerFactory.getLogger(CouponServiceImpl.class);

    @Autowired
    private CouponTemplateDAO templateDAO;
    @Autowired
    private CouponCommodityRelationDAO couponCommodityRelationDAO;
    @Autowired
    private CouponInstanceDAO instanceDAO;
    @Autowired
    private TransactionTemplate transactionTemplate;
    @Autowired
    private DistLockManager distLockManager;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private RocketMQTemplate rocketMQTemplate;
    private final ExecutorService executorService = new ThreadPoolExecutor(2, 12,
            10, TimeUnit.SECONDS, new LinkedBlockingQueue<>(5000));

    @Override
    @Log
    public Long createCoupon(CouponTemplateDTO templateDTO) {
        final CouponTemplate templateEntity = EntityUtils.couponTemplateDTO2Entity(templateDTO);

        Long couponId = transactionTemplate.execute(new TransactionCallback<Long>() {
            @Override
            public Long doInTransaction(TransactionStatus status) {
                try {
                    templateDAO.insert(templateEntity);

                    long couponId = templateEntity.getId();
                    if (templateDTO.getScope() != null) {
                        List<CouponCommodityRelation> relations = new ArrayList<>(templateDTO.getScope().size());
                        for (Long commodityId: templateDTO.getScope()) {
                            CouponCommodityRelation relation = new CouponCommodityRelation();
                            relation.setCouponId(couponId);
                            relation.setCommodityId(commodityId);
                            relations.add(relation);
                        }

                        couponCommodityRelationDAO.insertAll(relations);
                    }

                    return couponId;
                } catch (Exception ex) {
                    status.setRollbackOnly();
                    ex.printStackTrace();
                    return null;
                }
            }
        });

        if (null != couponId) {
            // todo: 怎么知道操作是否成功？
            redisTemplate.opsForValue().set(CacheUtils.getCouponStockKey(couponId), templateDTO.getMaxStock());
        }

        return couponId;
    }

    @Override
    @Log
    public Integer offerCoupon(long userId, long couponId) {
        // 保证接口幂等性
        final String lockKey = "lock:" + userId + ":" + couponId;
        distLockManager.lock(lockKey); // todo: timeout?

        try {
            // 判断用户领取资格
            // 活动时间
            CouponTemplate template = templateDAO.get(couponId);
            final Date now = new Date();
            if (now.before(template.getStartTime()) || now.after(template.getEndTime())) {
                return 2;
            }
            // 限领条件
            int num = instanceDAO.count(userId, couponId);
            if (num > 0) {
                return 3;
            }

            // 处理库存
            // 返回0：成功，1：库存不足
            final String stockLuaStr =
                    """
                    local stock = redis.call('get', KEYS[1])
                    if (stock and tonumber(stock) > 0) then
                       redis.call('incrby', KEYS[1], -1)
                       return 0
                    end
                    return 1
                    """;
            RedisScript<Long> stockScript = new DefaultRedisScript<>(stockLuaStr, Long.class);
            final String stockKey = CacheUtils.getCouponStockKey(couponId);
            List<String> stockKeys = List.of(stockKey);
            Long res = redisTemplate.execute(stockScript, stockKeys);

            if (res != null && res == 0) {
                // 异步更新数据库、发送消息
                executorService.execute(() -> {
                    final CouponExpiryTrxMsg payload = new CouponExpiryTrxMsg(userId, couponId, now, template.getEndTime());
                    Message<String> msg = MessageBuilder.withPayload(MQUtils.obj2Payload(payload)).build();
                    rocketMQTemplate.sendMessageInTransaction(MQConstants.COUPON_EXPIRY_TRX_TOPIC, msg, null);
                    logger.info("sent CouponExpiryTrxMsg for userId=" + userId + ", couponId=" + couponId);
                });
                return 0;
            }
            return 1;
        } finally {
            distLockManager.unlock(lockKey);
        }
    }

    @Override
    @Log
    public Integer useCoupon(long userId, long couponId, long orderId) {
        return transactionTemplate.execute(new TransactionCallback<Integer>() {
            @Override
            public Integer doInTransaction(TransactionStatus status) {
                try {
                    int state = instanceDAO.selectStateForUpdate(userId, couponId);
                    if (state != EntityConstants.STATE_UNUSED) {
                        return 1;
                    }

                    instanceDAO.updateState_UseTime_OrderId(userId, couponId, EntityConstants.STATE_USED, new Date(), orderId);
                    return 0;
                } catch (Exception ex) {
                    status.setRollbackOnly();
                    ex.printStackTrace();
                    return 2;
                }
            }
        });
    }

}
