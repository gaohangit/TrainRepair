package com.ydzbinfo.emis.utils.result.base;

import com.ydzbinfo.emis.utils.Assert;
import org.slf4j.Logger;

/**
 * 接口返回对象相关的通用方法
 * 可通过继承适配各种返回对象
 *
 * @author 张天可
 */
public abstract class RestResponseUtil<T> {

    /**
     * 获取新的返回对象
     *
     * @param code 返回码
     */
    public abstract T getNewResponseObject(Integer code);

    /**
     * 设置返回对象的message
     *
     * @param responseObject
     * @param message
     */
    public abstract void setMessage(T responseObject, String message);

    /**
     * 根据异常生成返回对象，如果是意料之外的异常，会使用logger进行异常日志记录
     */
    public T getResultFromException(Exception e, ResultStyleEnum resultStyleEnum, Logger logger, String defaultErrorMessage) {
        Class<?> eClass = e.getClass();
        if (e instanceof RestRequestExceptionBase) {
            RestRequestExceptionBase restRequestException = (RestRequestExceptionBase) e;
            T restResult = this.getNewResponseObject(restRequestException.getCode());
            this.setMessage(restResult, restRequestException.getMessage());
            // 记录致命错误的error日志
            if (logger != null && resultStyleEnum.getFatalFailCode() == restRequestException.getCode()) {
                logger.error(restRequestException.getMessage(), e);
            }
            return restResult;
        } else if (eClass.equals(RuntimeException.class)) {
            RuntimeException runtimeException = (RuntimeException) e;
            T restResult = this.getNewResponseObject(resultStyleEnum.getNormalFailCode());
            this.setMessage(restResult, runtimeException.getMessage());
            if (logger != null && e.getCause() != null) {
                logger.error(runtimeException.getMessage(), e.getCause());
            }
            return restResult;
        } else if (eClass.equals(Assert.IllegalParamException.class)) {
            Assert.IllegalParamException illegalParamException = (Assert.IllegalParamException) e;
            T restResult = this.getNewResponseObject(resultStyleEnum.getNormalFailCode());
            this.setMessage(restResult, "参数错误：" + illegalParamException.getMessage());
            return restResult;
        } else {
            String msg;
            if (defaultErrorMessage == null) {
                msg = "服务器内部错误";
            } else {
                msg = defaultErrorMessage;
            }
            if (logger != null) {
                logger.error(msg, e);
            }
            T restResult = this.getNewResponseObject(resultStyleEnum.getFatalFailCode());
            this.setMessage(restResult, msg);
            return restResult;
        }
    }
}
