package com.ydzbinfo.emis.trainRepair.repairworkflow.utils;

import lombok.Getter;

/**
 * 流程运行状态枚举类
 *
 * @author 张天可
 * @since 2021/7/3
 */
@Getter
public enum FlowRunStateEnum {
    NOT_STARTED("-1", "未开始"),
    RUNNING("0", "运行中"),
    ENDED("1", "已结束");

    private final String value;
    private final String description;

    FlowRunStateEnum(String value, String description) {
        this.value = value;
        this.description = description;
    }

    public boolean is(String state) {
        return this.value.equals(state);
    }
}
