package com.ydzbinfo.emis.trainRepair.bill.model.basetemplate;

import com.ydzbinfo.emis.trainRepair.bill.model.ITemplateContentInfo;
import com.ydzbinfo.emis.trainRepair.bill.querymodel.basetemplate.BaseTemplateContent;
import com.ydzbinfo.emis.trainRepair.bill.querymodel.basetemplate.BaseTemplateLinkContent;
import com.ydzbinfo.emis.trainRepair.bill.querymodel.basetemplate.BaseTemplateLinkControl;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * @author 张天可
 * @since 2022/2/21
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class BaseTemplateContentInfo extends BaseTemplateContent implements ITemplateContentInfo<BaseTemplateLinkContent, BaseTemplateLinkControl> {

    /**
     * 关联单元格数据
     */
    private List<BaseTemplateLinkContent> linkContents;


    /**
     * 控制信息
     */
    private List<BaseTemplateLinkControl> controls;
}
