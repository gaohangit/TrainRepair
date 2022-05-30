package com.ydzbinfo.emis.trainRepair.bill.querymodel.attribute;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class TemplateAttributeTypeInfo extends TemplateAttributeType {
    private List<TemplateAttribute> templateAttributes;
}
