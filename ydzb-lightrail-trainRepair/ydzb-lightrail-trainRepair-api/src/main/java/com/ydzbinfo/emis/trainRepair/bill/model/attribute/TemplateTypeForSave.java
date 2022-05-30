package com.ydzbinfo.emis.trainRepair.bill.model.attribute;

import com.ydzbinfo.emis.trainRepair.bill.querymodel.attribute.TemplateType;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class TemplateTypeForSave extends TemplateType {

    /**
     * 关联属性编码（多个，以逗号分隔）
     */
    private String templateLinkAttrs;

    /**
     * 操作类型（0-删除  1-新增）
     */
    private String operationType;

    /**
     * 关联条件编码（多个，以逗号分隔）
     */
    private String queryCodes;
}

