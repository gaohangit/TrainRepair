package com.ydzbinfo.emis.trainRepair.bill.model.attribute;

import lombok.Data;

@Data
public class TemplateTypeParamModel {
    private String type;
    private String fatherTypeCode;
    // private String itemType;
    // 是否显示系统单据
    private Boolean showSystemTemplate = true;
    // 是否显示自定义单据
    private Boolean showCustomTemplate = true;
}
