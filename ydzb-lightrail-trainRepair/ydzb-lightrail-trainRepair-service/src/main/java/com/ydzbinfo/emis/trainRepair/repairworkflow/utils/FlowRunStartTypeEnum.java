package com.ydzbinfo.emis.trainRepair.repairworkflow.utils;

import lombok.Getter;

/**
 * 流程运行启动方式类型
 *
 * @author 张天可
 * @since 2021/7/3
 */
@Getter
public enum FlowRunStartTypeEnum {
    AUTO("AUTO", "系统自动开始"), PERSON("PERSON", "人员操作开始");

    private final String value;
    private final String description;

    FlowRunStartTypeEnum(String value, String description) {
        this.value = value;
        this.description = description;
    }
}
