package com.ydzbinfo.emis.utils;


import org.springframework.util.ObjectUtils;

/**
 * @author 张天可
 * @since 2021/7/2
 */
public class Assert {

    public static void notEmpty(Object o, String message) {
        if (ObjectUtils.isEmpty(o)) {
            throw new IllegalParamException(message);
        }
    }

    public static void notEmptyInnerFatal(Object o, String message) {
        if (ObjectUtils.isEmpty(o)) {
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * 参数错误异常
     */
    public static class IllegalParamException extends RuntimeException {

        public IllegalParamException(String message) {
            super(message);
        }

    }
}
