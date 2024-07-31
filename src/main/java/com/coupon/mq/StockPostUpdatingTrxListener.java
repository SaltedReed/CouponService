package com.coupon.mq;

import com.coupon.annotation.Log;
import com.coupon.common.CacheUtils;
import com.coupon.common.EntityConstants;
import com.coupon.common.MQUtils;
import com.coupon.dao.CouponInstanceDAO;
import com.coupon.dao.CouponTemplateDAO;
import com.coupon.entity.CouponInstance;
import com.coupon.mq.message.CouponExpiryTrxMsg;
import org.apache.rocketmq.spring.annotation.RocketMQTransactionListener;
import org.apache.rocketmq.spring.core.RocketMQLocalTransactionListener;
import org.apache.rocketmq.spring.core.RocketMQLocalTransactionState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.Message;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Date;

/***
 * 负责实现库存数据库更新和消息发送的事务原子性
 */
@RocketMQTransactionListener(rocketMQTemplateBeanName = "rocketMQTemplate")
public class StockPostUpdatingTrxListener implements RocketMQLocalTransactionListener {
    @Autowired
    private CouponTemplateDAO templateDAO;
    @Autowired
    private CouponInstanceDAO instanceDAO;
    @Autowired
    private TransactionTemplate transactionTemplate;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    @Log
    public RocketMQLocalTransactionState executeLocalTransaction(Message message, Object o) {
        String stockKey = null;
        try {
            CouponExpiryTrxMsg payload = MQUtils.payload2Obj(new String((byte[])message.getPayload()), CouponExpiryTrxMsg.class);
            final long userId = payload.getUserId(), couponId = payload.getCouponId();
            final Date now = payload.getReceiveTime();

            stockKey = CacheUtils.getCouponStockKey(couponId);
            final String finalStockKey = stockKey;

            // 更新数据库库存字段、插入实例记录
            return transactionTemplate.execute(new TransactionCallback<RocketMQLocalTransactionState>() {
                @Override
                public RocketMQLocalTransactionState doInTransaction(TransactionStatus status) {
                    try {
                        int stock = templateDAO.selectStockForUpdate(couponId);
                        if (stock <= 0) {
                            compensateStockInCache(finalStockKey);
                            return RocketMQLocalTransactionState.ROLLBACK;
                        }
                        templateDAO.incrDBStockBy(couponId, -1);

                        CouponInstance instance = new CouponInstance();
                        instance.setCouponId(couponId);
                        instance.setUserId(userId);
                        instance.setState(EntityConstants.STATE_UNUSED);
                        instance.setReceiveTime(now);
                        instanceDAO.insert(instance);

                        return RocketMQLocalTransactionState.COMMIT;
                    } catch (Exception ex) {
                        status.setRollbackOnly();
                        compensateStockInCache(finalStockKey);
                        ex.printStackTrace();
                        return RocketMQLocalTransactionState.ROLLBACK;
                    }
                }
            });
        } catch (Throwable th) {
            if (stockKey != null) {
                compensateStockInCache(stockKey);
            }
            throw th;
        }
    }

    @Override
    public RocketMQLocalTransactionState checkLocalTransaction(Message message) {
        CouponExpiryTrxMsg payload = MQUtils.payload2Obj(new String((byte[])message.getPayload()), CouponExpiryTrxMsg.class);
        return instanceDAO.count(payload.getUserId(), payload.getCouponId()) == 1
                ? RocketMQLocalTransactionState.COMMIT : RocketMQLocalTransactionState.ROLLBACK;
    }

    private void compensateStockInCache(String stockKey) {
        redisTemplate.opsForValue().increment(stockKey, 1);
    }
}
