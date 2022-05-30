package com.ydzbinfo.emis.trainRepair.bill.model.templatesummary;

import com.ydzbinfo.emis.trainRepair.bill.model.ITemplateContentInfo;
import com.ydzbinfo.emis.trainRepair.bill.querymodel.templatesummary.TemplateSummLinkContent;
import com.ydzbinfo.emis.trainRepair.bill.querymodel.templatesummary.TemplateSummLinkControl;
import com.ydzbinfo.emis.trainRepair.bill.querymodel.templatesummary.TemplateSummaryContent;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;

/**
 * <p>
 *
 * </p>
 *
 * @author 张天可
 * @since 2021-06-17
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class TemplateSummaryContentInfo extends TemplateSummaryContent implements Serializable, ITemplateContentInfo<TemplateSummLinkContent, TemplateSummLinkControl> {

    /**
     * 关联单元格数据
     */
    private List<TemplateSummLinkContent> linkContents;


    /**
     * 控制信息
     */
    private List<TemplateSummLinkControl> controls;
}
