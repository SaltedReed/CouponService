package com.coupon;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class CouponServiceApp {
    public static void main(String[] args) {
        SpringApplication.run(CouponServiceApp.class);
    }
}
