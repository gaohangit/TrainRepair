package com.ydzbinfo.emis.utils.result.base;

import lombok.Getter;

/**
 * 通用错误，利用异常机制优雅地处理接口的错误返回问题
 *
 * @author 张天可
 */
@Getter
public class RestRequestExceptionBase extends RuntimeException {

    private final int code;

    public RestRequestExceptionBase(String message, int code) {
        super(message);
        this.code = code;
    }

    public RestRequestExceptionBase(String message, Throwable cause, int code) {
        super(message, cause);
        this.code = code;
    }
}
