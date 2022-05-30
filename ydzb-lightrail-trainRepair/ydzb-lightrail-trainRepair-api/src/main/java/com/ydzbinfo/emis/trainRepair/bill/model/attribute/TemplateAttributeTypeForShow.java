package com.ydzbinfo.emis.trainRepair.bill.model.attribute;

import com.ydzbinfo.emis.trainRepair.bill.querymodel.attribute.TemplateAttribute;
import com.ydzbinfo.emis.trainRepair.bill.querymodel.attribute.TemplateAttributeType;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class TemplateAttributeTypeForShow extends TemplateAttributeType {
    private List<TemplateAttribute> templateAttributes;
}
