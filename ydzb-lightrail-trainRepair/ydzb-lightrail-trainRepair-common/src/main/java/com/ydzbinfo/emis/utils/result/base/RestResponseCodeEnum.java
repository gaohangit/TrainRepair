package com.ydzbinfo.emis.utils.result.base;

import com.ydzbinfo.emis.utils.entity.IJsonEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.context.annotation.Configuration;

/**
 * 返回码枚举类
 *
 * @author 张天可
 * @since 2022/1/18
 */
@Getter
@AllArgsConstructor
public enum RestResponseCodeEnum implements IJsonEnum<Integer> {
    SUCCESS(1),
    NORMAL_FAIL(0),
    FATAL_FAIL(-1),
    ;
    private final int code;

    @Override
    public Integer getValue() {
        return code;
    }

    @Configuration
    public static class Config {
        static {
            IJsonEnum.register(RestResponseCodeEnum.class);
        }
    }

    public static RestResponseCodeEnum from(Integer code) {
        return IJsonEnum.from(code, RestResponseCodeEnum.class);
    }

}
