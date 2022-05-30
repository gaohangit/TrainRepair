package com.ydzbinfo.emis.trainRepair.repairworkflow.utils;

import lombok.Getter;

/**
 * 关键作业额外属性
 */
@Getter
public enum KeyWorkExtraColumnEnum {
    // 功能分类
    FunctionClass("FUNCTION_CLASS", "功能分类", "functionClass", ParamType.SPECIAL_OPTIONS, null, false, false),
    // 部件(构型)节点编码
    BatchBomNodeCode("BATCH_BOM_NODE_CODE", "部件", "batchBomNodeCode", ParamType.SPECIAL_OPTIONS, null, false, false),
    // RcdSysType("RCD_SYS_TYPE", "系统分类", "rcdSysType", ParamType.OPTIONS, null, false),
    KeyWorkType("KEY_WORK_TYPE", "类型", "keyWorkType", ParamType.OPTIONS, null, false, false),
    CAR("CAR", "辆序", "carNoList", ParamType.MULTIPLE_CHOICE, new String[]{"trainModel"}, true, true),
    Position("POSITION", "位置", "position", ParamType.TEXT, null, false, false),
    WorkEnv("WORK_ENV", "作业条件", "workEnv", ParamType.OPTIONS, null, false, false);
    String key;
    String label;
    String property;
    String type;
    String[] optionParams;
    Boolean simpleOption;
    Boolean required;

    public interface ParamType {
        String OPTIONS = "options";
        String SPECIAL_OPTIONS = "specialOptions";
        String TEXT = "text";
        String MULTIPLE_CHOICE = "multipleChoice";
    }

    KeyWorkExtraColumnEnum(String key, String label, String property, String type, String[] optionParams, boolean simpleOption, boolean required) {
        this.key = key;
        this.label = label;
        this.property = property;
        this.type = type;
        this.optionParams = optionParams;
        this.simpleOption = simpleOption;
        this.required = required;
    }

}
