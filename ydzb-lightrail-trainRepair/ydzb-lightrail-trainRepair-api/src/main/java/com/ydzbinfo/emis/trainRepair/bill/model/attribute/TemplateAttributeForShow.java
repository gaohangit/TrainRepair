package com.ydzbinfo.emis.trainRepair.bill.model.attribute;

import com.ydzbinfo.emis.trainRepair.bill.querymodel.attribute.TemplateAttribute;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class TemplateAttributeForShow extends TemplateAttribute {

    /**
     * 属性类型名称
     */
    private String attributeTypeName;

    /**
     * 属性模式名称
     */
    private String attributeModeName;

    /**
     * 属性模式说明
     */
    private String attributeModeNote;

    /**
     * 取值范围字符串拼接
     */
    private String templateValues;

    // /**
    //  * 取值范围集合
    //  */
    // private List<TemplateValue> templateValueList;
}
