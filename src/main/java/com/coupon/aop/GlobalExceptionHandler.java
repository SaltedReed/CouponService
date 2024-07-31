package com.coupon.aop;

import com.coupon.common.Result;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.validation.ConstraintViolationException;
import java.security.InvalidParameterException;

@ControllerAdvice
@ResponseBody
public class GlobalExceptionHandler {
    @ExceptionHandler({ InvalidParameterException.class, ConstraintViolationException.class})
    public Result<Void> handleInvalidParameterException(Exception ex) {
        ex.printStackTrace();
        return Result.error("[invalid parameter found] "+ex.getMessage(), Result.INVALID_PARAMS);
    }

    @ExceptionHandler(Exception.class)
    public Result<Void> handleGeneralException(Exception ex) {
        ex.printStackTrace();
        return Result.error(ex.getMessage(), Result.GENERAL_ERROR_CODE);
    }
}
