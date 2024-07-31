package com.coupon.mq;

import com.coupon.annotation.LogBefore;
import com.coupon.common.EntityConstants;
import com.coupon.common.MQConstants;
import com.coupon.common.MQUtils;
import com.coupon.dao.CouponInstanceDAO;
import com.coupon.mq.message.CouponExpiryTrxMsg;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Date;

/**
 * 负责实现券自动过期
 */
@Component
@RocketMQMessageListener(topic = MQConstants.COUPON_EXPIRY_TRX_TOPIC, consumerGroup = "consumer-group-1")
public class CouponExpiryMsgListener implements RocketMQListener<String> {
    private static Logger logger = LoggerFactory.getLogger(CouponExpiryMsgListener.class);

    @Autowired
    private CouponInstanceDAO instanceDAO;
    @Autowired
    private TransactionTemplate transactionTemplate;
    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    @Override
    @LogBefore
    public void onMessage(String payload) {
        CouponExpiryTrxMsg msg = MQUtils.payload2Obj(payload, CouponExpiryTrxMsg.class);
        final long userId = msg.getUserId(), couponId = msg.getCouponId();

        Date now = new Date();
        if (now.after(msg.getExpireTime())) {
            transactionTemplate.execute(new TransactionCallbackWithoutResult() {
                @Override
                protected void doInTransactionWithoutResult(TransactionStatus status) {
                    try {
                        int state = instanceDAO.selectStateForUpdate(userId, couponId);
                        if (state != EntityConstants.STATE_UNUSED) {
                            return;
                        }

                        instanceDAO.updateState(userId, couponId, EntityConstants.STATE_EXPIRED);
                        logger.info("made coupon (userId=" + userId + ", couponId=" + couponId + ") expired");
                    } catch (Exception ex) {
                        status.setRollbackOnly();
                        ex.printStackTrace();
                    }
                }
            });
        } else {
            Message<String> newMsg = MessageBuilder.withPayload(payload).build();
            long diff = msg.getExpireTime().getTime() - now.getTime();
            rocketMQTemplate.syncSendDelayTimeMills(MQConstants.COUPON_EXPIRY_TRX_TOPIC, newMsg, diff);
            logger.info("sent coupon expiry msg for userId=" + userId + ", couponId=" + couponId + " with delay=" + diff);
        }
    }
}
