package com.ydzbinfo.emis.trainRepair.bill.model.attribute;

import com.ydzbinfo.emis.trainRepair.bill.querymodel.attribute.TemplateAttribute;
import com.ydzbinfo.emis.trainRepair.bill.querymodel.attribute.TemplateQuery;
import com.ydzbinfo.emis.trainRepair.bill.querymodel.attribute.TemplateType;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 单据类型详情信息
 *
 * @author 张天可
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class TemplateTypeInfo extends TemplateType {

    /**
     * 关联属性列表
     */
    private List<TemplateAttribute> linkAttributes;

    /**
     * 关联条件列表
     */
    private List<TemplateQuery> linkQueries;


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

}

