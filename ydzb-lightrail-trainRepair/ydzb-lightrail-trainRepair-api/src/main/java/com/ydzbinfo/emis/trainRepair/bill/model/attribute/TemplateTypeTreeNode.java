package com.ydzbinfo.emis.trainRepair.bill.model.attribute;

import com.ydzbinfo.emis.trainRepair.bill.querymodel.attribute.TemplateType;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 单据类型树节点
 *
 * @author 张天可
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class TemplateTypeTreeNode extends TemplateType {

    /**
     * 子类型列表
     */
    private List<TemplateTypeTreeNode> children;
}

