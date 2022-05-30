package com.ydzbinfo.emis.trainRepair.bill.fillback.utils;

import lombok.Getter;

/**
 * @Description:
 * @Data: 2021/8/12
 * @Author: 冯帅
 */
@Getter
public enum TemplateTypeNameEnum {

    RECORD_FIRST_PERSON_TEST("RECORD_FIRST_PERSON_TEST", "一级修人检试验单"),
    RECORD_FIRST_PERSON_KEEP("RECORD_FIRST_PERSON_KEEP", "一级修人检记录单"),
    RECORD_FIRST_ROBOT_KEEP("RECORD_FIRST_ROBOT_KEEP", "一级修辅助检查记录单"),
    RECORD_FIRST_EQUIPMENT_KEEP("RECORD_FIRST_EQUIPMENT_KEEP", "一级修机检记录单"),

    RECORD_SECOND_PREVENTIVE("RECORD_SECOND_PREVENTIVE", "二级修预防性记录单"),
    RECORD_SECOND_WHEEL("RECORD_SECOND_WHEEL", "二级修镟修记录单"),
    RECORD_SECOND_AXLEEDDY("RECORD_SECOND_AXLEEDDY", "二级修空心车轴探伤记录单"),
    RECORD_SECOND_LUEDDY("RECORD_SECOND_LUEDDY", "二级修轮辋轮辐探伤记录单"),

    RECORD_TEMP_CELL("RECORD_TEMP_CELL", "临时任务单据"),

    MANAGER_INTEGRATION_CELL("MANAGER_INTEGRATION_CELL","一体化作业申请单"),
    MANAGER_LIVECHECK_CELL("MANAGER_LIVECHECK_CELL","出所联检单");



    private final String value;
    private final String label;

    TemplateTypeNameEnum(String value, String label) {
        this.value = value;
        this.label = label;
    }
}
