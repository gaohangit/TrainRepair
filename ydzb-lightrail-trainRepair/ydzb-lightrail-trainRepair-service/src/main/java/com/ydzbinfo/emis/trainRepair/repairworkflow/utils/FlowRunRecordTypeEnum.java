package com.ydzbinfo.emis.trainRepair.repairworkflow.utils;

import lombok.Getter;

/**
 * 流程运行记录类型
 *
 * @author 张天可
 * @since 2021/7/7
 */
@Getter
public enum FlowRunRecordTypeEnum {
    NORMAL_END("NORMAL_END", "正常结束"),
    REJECT_END("REJECT_END", "驳回结束"),
    REVOKE_END("REVOKE_END", "撤销结束"),
    FORCE_END("FORCE_END", "强制结束");

    private final String value;
    private final String description;

    FlowRunRecordTypeEnum(String value, String description) {
        this.value = value;
        this.description = description;
    }
}
