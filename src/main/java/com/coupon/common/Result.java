package com.coupon.common;

import java.util.HashMap;
import java.util.Map;

public class Result<T> {
    public static final int SUCCESS_CODE = 0;
    public static final int GENERAL_ERROR_CODE = 1;
    public static final int INVALID_PARAMS = 2;
    public static final int RATE_LIMIT = 3;
    public static final int LOCK_FAILURE = 4;

    private int code;
    private T data;
    private String msg;
    private final Map<String, Object> extras = new HashMap<>();

    public static <T> Result<T> success() {
        return success(null);
    }

    public static <T> Result<T> success(T data) {
        Result<T> res = new Result<>();
        res.setCode(SUCCESS_CODE);
        res.setData(data);
        return res;
    }

    public static <T> Result<T> error(String msg) {
        return error(msg, GENERAL_ERROR_CODE);
    }

    public static <T> Result<T> error(String msg, int errCode) {
        Result<T> res = new Result<>();
        res.setCode(errCode);
        res.setMsg(msg);
        return res;
    }

    public boolean isSuccessful() {
        return code == SUCCESS_CODE;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Map<String, Object> getExtras() {
        return extras;
    }

    public void addExtras(String key, Object val) {
        extras.put(key, val);
    }
}
