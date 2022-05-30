package com.ydzbinfo.emis.trainRepair.bill.model.attribute;

import lombok.Data;

@Data
public class TemplateAttributeQueryParamModel {

    private Integer pageNum;

    private Integer pageSize;

    private String attributeTypeCode;

    private String attributeName;

    private String attributeCode;

    private String sysType;
}
