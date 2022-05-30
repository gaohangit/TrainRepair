package com.ydzbinfo.emis.trainRepair.bill.model.attribute;

import lombok.Data;

@Data
public class TemplateTypeQueryParamModel {

    private Integer pageNum;

    private Integer pageSize;

    private String oneTypeCode;

    private String fatherTypeCode;

    private String templateTypeName;

    private String flag;

    private String sysType;
}
