package com.deji.demo.bean.exception;

/**
 * 用户级别的需要传递到前端的异常，一般用于逻辑错误等
 *
 * @author
 */
public class UserBusinessException extends RuntimeException {

    private int resultCode;

    public UserBusinessException() {
    }

    public UserBusinessException(int resultCode, String message) {
        super(message);
        this.resultCode = resultCode;
    }

    public UserBusinessException(String message) {
        super(message);
    }

    public UserBusinessException(String message, Throwable cause) {
        super(message, cause);
    }

    public UserBusinessException(Throwable cause) {
        super(cause);
    }

    public UserBusinessException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public int getResultCode() {
        return resultCode;
    }
}
