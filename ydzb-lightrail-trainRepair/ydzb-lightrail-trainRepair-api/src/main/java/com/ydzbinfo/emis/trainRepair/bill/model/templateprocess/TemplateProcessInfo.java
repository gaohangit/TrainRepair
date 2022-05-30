package com.ydzbinfo.emis.trainRepair.bill.model.templateprocess;

import com.ydzbinfo.emis.trainRepair.bill.model.ITemplateInfo;
import com.ydzbinfo.emis.trainRepair.bill.querymodel.templateprocess.TemplateProcLinkContent;
import com.ydzbinfo.emis.trainRepair.bill.querymodel.templateprocess.TemplateProcLinkControl;
import com.ydzbinfo.emis.trainRepair.bill.querymodel.templateprocess.TemplateProcess;
import com.ydzbinfo.emis.trainRepair.bill.querymodel.templateprocess.TemplateProcessArea;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 记录单过程模板详情
 *
 * @author 张天可
 * @date 2021/6/18
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class TemplateProcessInfo extends TemplateProcess implements ITemplateInfo<TemplateProcessContentInfo, TemplateProcLinkContent, TemplateProcLinkControl, TemplateProcessArea> {

    /**
     * 单元格数据
     */
    private List<TemplateProcessContentInfo> contents;

    /**
     * 区域数据
     */
    private List<TemplateProcessArea> areas;

}
