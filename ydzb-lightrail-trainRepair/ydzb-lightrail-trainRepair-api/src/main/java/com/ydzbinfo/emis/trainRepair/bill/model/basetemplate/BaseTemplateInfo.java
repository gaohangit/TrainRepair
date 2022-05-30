package com.ydzbinfo.emis.trainRepair.bill.model.basetemplate;

import com.ydzbinfo.emis.trainRepair.bill.model.ITemplateInfo;
import com.ydzbinfo.emis.trainRepair.bill.querymodel.basetemplate.BaseTemplate;
import com.ydzbinfo.emis.trainRepair.bill.querymodel.basetemplate.BaseTemplateArea;
import com.ydzbinfo.emis.trainRepair.bill.querymodel.basetemplate.BaseTemplateLinkContent;
import com.ydzbinfo.emis.trainRepair.bill.querymodel.basetemplate.BaseTemplateLinkControl;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 单据基础模板详细信息
 *
 * @author 张天可
 * @since 2022/2/21
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class BaseTemplateInfo extends BaseTemplate implements ITemplateInfo<BaseTemplateContentInfo, BaseTemplateLinkContent, BaseTemplateLinkControl, BaseTemplateArea> {

    /**
     * 单元格数据
     */
    private List<BaseTemplateContentInfo> contents;

    /**
     * 区域数据
     */
    private List<BaseTemplateArea> areas;
}
