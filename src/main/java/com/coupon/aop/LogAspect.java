package com.coupon.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class LogAspect {
    private final static Logger logger = LoggerFactory.getLogger(LogAspect.class);

    @Before("@annotation(com.coupon.annotation.Log) || @annotation(com.coupon.annotation.LogBefore)")
    public void before(JoinPoint joinPoint) {
        String className = joinPoint.getTarget().getClass().getName();
        StringBuilder sb = new StringBuilder();
        String str = sb.append("\n    entering ").append(className).append(".").append(joinPoint.getSignature())
                .append("\n        with args: ").append(getArgStr(joinPoint))
                .toString();
        logger.info(str);
    }

    private String getArgStr(JoinPoint joinPoint) {
        StringBuilder sb=new StringBuilder();

        Object[] args = joinPoint.getArgs();
        if (args == null || args.length == 0) {
            sb.append("none");
        } else {
            for (int i = 0; i < args.length; ++i) {
                sb.append(args[i]);
                if (i != args.length - 1) {
                    sb.append("; ");
                }
            }
        }

        return sb.toString();
    }

    @AfterReturning(pointcut = "@annotation(com.coupon.annotation.Log) || @annotation(com.coupon.annotation.LogAfterReturning)", returning = "result")
    public void afterReturning(JoinPoint joinPoint, Object result) {
        String className = joinPoint.getTarget().getClass().getName();
        StringBuilder sb = new StringBuilder();
        String str = sb.append("\n    returning from ").append(className).append(".").append(joinPoint.getSignature())
                .append("\n        with args: ").append(getArgStr(joinPoint))
                .append("\n        with result: ").append(result)
                .toString();
        logger.info(str);
    }

    @AfterThrowing(pointcut = "@annotation(com.coupon.annotation.Log) || @annotation(com.coupon.annotation.LogAfterThrowing)", throwing = "ex")
    public void afterThrowing(JoinPoint joinPoint, Exception ex) {
        String className = joinPoint.getTarget().getClass().getName();
        StringBuilder sb = new StringBuilder();
        String str = sb.append("\n    [error occurred] returning from ").append(className).append(".").append(joinPoint.getSignature())
                .append("\n        with args: ").append(getArgStr(joinPoint))
                .append("\n        with exception: ").append(ex)
                .toString();
        logger.error(str);
    }
}