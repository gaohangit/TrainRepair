package com.ydzbinfo.emis.trainRepair.repairworkflow.utils;

import lombok.Getter;

/**
 * 节点记录操作类型枚举
 * @author 张天可
 * @date 2022年4月26日
 */
@Getter
public enum NodeRecordOperationTypeEnum {
    DISPOSE("DISPOSE", "节点处理") ,
    SWITCH("SWITCH", "流程切换");

    String value;
    String description;

    NodeRecordOperationTypeEnum(String value, String description) {
        this.value = value;
        this.description = description;
    }

    public boolean is(String value) {
        return this.getValue().equals(value);
    }
}
