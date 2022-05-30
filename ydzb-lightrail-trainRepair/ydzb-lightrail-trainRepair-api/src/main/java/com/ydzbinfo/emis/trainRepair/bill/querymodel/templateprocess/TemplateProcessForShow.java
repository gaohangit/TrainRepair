package com.ydzbinfo.emis.trainRepair.bill.querymodel.templateprocess;

import com.ydzbinfo.emis.trainRepair.bill.querymodel.base.ITemplateForShow;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author 张天可
 * @date 2021/6/18
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class TemplateProcessForShow extends TemplateProcess implements ITemplateForShow {

    private String moreCellFlag;

    /**
     * 模板类型名称
     */
    private String templateTypeName;

}
