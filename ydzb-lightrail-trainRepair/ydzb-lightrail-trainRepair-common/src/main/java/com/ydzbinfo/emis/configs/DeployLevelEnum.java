package com.ydzbinfo.emis.configs;

import lombok.Getter;

/**
 * 部署等级枚举定义
 */
@Getter
public enum DeployLevelEnum {
    DEPARTMENT("department", "所级"),
    CENTER("center", "段级");

    String value;
    String label;

    DeployLevelEnum(String value, String label) {
        this.value = value;
        this.label = label;
    }

}
