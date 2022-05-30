package com.ydzbinfo.emis.trainRepair.repairworkflow.utils;

import lombok.Getter;

/**
 * 节点记录类型
 *
 * @author gaohan
 * @description
 * @createDate 2021/6/15 17:28
 * @modify 张天可 2021年7月5日
 **/
@Getter
public enum NodeRecordTypeEnum {
    NODE_START("NODE_START", "节点启动"),
    NODE_FINISH("NODE_FINISH", "节点完成"),
    PERSON("PERSON", "人员操作");

    String value;
    String description;

    NodeRecordTypeEnum(String value, String description) {
        this.value = value;
        this.description = description;
    }

    public boolean is(String value) {
        return this.getValue().equals(value);
    }
}
