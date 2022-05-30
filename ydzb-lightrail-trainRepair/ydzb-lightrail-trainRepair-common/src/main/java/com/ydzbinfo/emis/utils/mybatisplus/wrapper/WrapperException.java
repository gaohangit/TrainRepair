package com.ydzbinfo.emis.utils.mybatisplus.wrapper;

/**
 * @author 张天可
 * @since 2021/12/27
 */
class WrapperException extends RuntimeException {

    public WrapperException(String message) {
        super(message);
    }

    public WrapperException(String message, Throwable cause) {
        super(message, cause);
    }

    public WrapperException(Throwable cause) {
        super(cause);
    }

    public WrapperException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public WrapperException() {
    }
}
