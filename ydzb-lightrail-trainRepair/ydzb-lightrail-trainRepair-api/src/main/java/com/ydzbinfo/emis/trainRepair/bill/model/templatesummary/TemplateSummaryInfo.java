package com.ydzbinfo.emis.trainRepair.bill.model.templatesummary;

import com.ydzbinfo.emis.trainRepair.bill.model.ITemplateInfo;
import com.ydzbinfo.emis.trainRepair.bill.querymodel.templatesummary.TemplateSummLinkContent;
import com.ydzbinfo.emis.trainRepair.bill.querymodel.templatesummary.TemplateSummLinkControl;
import com.ydzbinfo.emis.trainRepair.bill.querymodel.templatesummary.TemplateSummary;
import com.ydzbinfo.emis.trainRepair.bill.querymodel.templatesummary.TemplateSummaryArea;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 记录单发布模板详情
 *
 * @author 张天可
 * @date 2021/6/18
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class TemplateSummaryInfo extends TemplateSummary implements ITemplateInfo<TemplateSummaryContentInfo, TemplateSummLinkContent, TemplateSummLinkControl, TemplateSummaryArea> {

    /**
     * 单元格数据
     */
    private List<TemplateSummaryContentInfo> contents;

    /**
     * 区域数据
     */
    private List<TemplateSummaryArea> areas;

}
