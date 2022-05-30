package com.ydzbinfo.emis.utils.result;

import com.ydzbinfo.emis.utils.result.base.RestRequestExceptionBase;
import com.ydzbinfo.emis.utils.result.base.RestResponseCodeEnum;
import lombok.Getter;

/**
 * 通用错误，利用异常机制优雅地处理接口的错误返回问题
 *
 * @author 张天可
 */
@Getter
public class RestRequestException extends RestRequestExceptionBase {

    private final RestResponseCodeEnum codeEnum;

    RestRequestException(String message, RestResponseCodeEnum codeEnum) {
        super(message, codeEnum.getValue());
        this.codeEnum = codeEnum;
    }

    public RestRequestException(String message, Throwable cause, RestResponseCodeEnum codeEnum) {
        super(message, cause, codeEnum.getValue());
        this.codeEnum = codeEnum;
    }

    /**
     * 获取普通接口异常，目的是返回普通错误
     */
    public static RestRequestException normalFail(String message) {
        return new RestRequestException(message, RestResponseCodeEnum.NORMAL_FAIL);
    }

    /**
     * 获取致命接口异常，目的是返回致命错误
     */
    public static RestRequestException fatalFail(String message) {
        return new RestRequestException(message, RestResponseCodeEnum.FATAL_FAIL);
    }

    /**
     * 获取服务器内部错误的异常（致命异常的一种），目的是返回服务器内部错误
     */
    public static RestRequestException serviceInnerFatalFail(String message) {
        return new RestRequestException("服务器内部错误：" + message, RestResponseCodeEnum.FATAL_FAIL);
    }

    /**
     * 获取服务器内部错误的异常（致命异常的一种），目的是返回服务器内部错误
     */
    public static RestRequestException serviceInnerFatalFail(String message, Throwable cause) {
        return new RestRequestException("服务器内部错误：" + message, cause, RestResponseCodeEnum.FATAL_FAIL);
    }

}
