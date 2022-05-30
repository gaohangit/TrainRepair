package com.ydzbinfo.emis.trainRepair.repairworkflow.utils;

import lombok.Getter;

@Getter
public enum FlowRunSwitchTypeEnum {
    FORCE("FORCE", "强制切换"),
    NORMAL("NORMAL", "普通切换");

    private final String value;
    private final String description;

    FlowRunSwitchTypeEnum(String value, String description) {
        this.value = value;
        this.description = description;
    }
}
