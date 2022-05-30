package com.ydzbinfo.emis.trainRepair.repairworkflow.utils;

import lombok.Getter;

@Getter
public enum FlowTypeWithSwitchover {
    REPAIR_ONE("REPAIR_ONE,REPAIR_TWO,REPAIR_ONE_AND_TWO", "REPAIR_ONE,REPAIR_TWO,REPAIR_ONE_AND_TWO");
    private final String value;
    private final String description;

    FlowTypeWithSwitchover(String value, String description) {
        this.value = value;
        this.description = description;
    }
}
