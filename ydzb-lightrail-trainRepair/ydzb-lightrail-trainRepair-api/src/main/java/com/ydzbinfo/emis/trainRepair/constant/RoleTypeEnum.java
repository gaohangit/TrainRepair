package com.ydzbinfo.emis.trainRepair.constant;

import com.ydzbinfo.emis.utils.entity.IJsonEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.context.annotation.Configuration;

/**
 * 角色类型枚举
 *
 * @author 张天可
 * @since 2021/6/29
 */
@Getter
@AllArgsConstructor
public enum RoleTypeEnum implements IJsonEnum<String> {
    SYS_ROLE("2", "系统角色"),
    POST_ROLE("1", "岗位角色");
    private final String value;
    private final String label;

    @Configuration
    public static class Config {
        static {
            IJsonEnum.register(RoleTypeEnum.class);
        }
    }
}
