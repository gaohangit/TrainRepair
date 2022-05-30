package com.ydzbinfo.emis.trainRepair.bill.model.attribute;

import com.ydzbinfo.emis.trainRepair.bill.querymodel.attribute.TemplateType;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class TemplateTypeForShow extends TemplateType {

    /**
     * 关联属性编码（多个，以逗号分隔）
     */
    private String templateLinkAttrs;

    /**
     * 关联属性名称（多个，以逗号分隔）
     */
    private String templateLinkAttrNames;

    /**
     * 父类型名称
     */
    private String fatherTypeName;

    /**
     * 第一层单据类型编码
     */
    private String oneTypeCode;

    /**
     * 第一层单据类型名称
     */
    private String oneTypeName;

    /**
     * 关联条件编码（多个，以逗号分隔）
     */
    private String queryCodes;

    /**
     * 关联条件名称（多个，以逗号分隔）
     */
    private String queryNames;
}

