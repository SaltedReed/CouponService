package com.coupon.component;

import com.coupon.dao.CouponTemplateDAO;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
@Slf4j
public class InitTask {
    private final static Logger logger = LoggerFactory.getLogger(InitTask.class);

    @Autowired
    private CouponTemplateDAO templateDAO;

    @PostConstruct
    public void init() {
        templateDAO.loadAllStocks();
        logger.info("finished loading all stocks into cache");
    }
}
