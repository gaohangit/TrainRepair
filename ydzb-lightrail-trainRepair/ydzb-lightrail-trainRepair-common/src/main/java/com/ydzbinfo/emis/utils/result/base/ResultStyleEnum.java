package com.ydzbinfo.emis.utils.result.base;

import lombok.Getter;

/**
 * 由于不同开发人员对接口成功调用的code值的定义不同，这里定义了目前发现的所有类型
 *
 * @author 张天可
 */
@Getter
public enum ResultStyleEnum {
    // STYLE_ENUM_01(0, 1, -1),
    STYLE_ENUM_10(1, 0, -1);

    /**
     * 成功码
     */
    private final int successCode;
    /**
     * 通常错误码
     */
    private final int normalFailCode;
    /**
     * 致命错误码
     */
    private final int fatalFailCode;

    ResultStyleEnum(int successCode, int normalFailCode, int fatalFailCode) {
        this.successCode = successCode;
        this.normalFailCode = normalFailCode;
        this.fatalFailCode = fatalFailCode;
    }
}
