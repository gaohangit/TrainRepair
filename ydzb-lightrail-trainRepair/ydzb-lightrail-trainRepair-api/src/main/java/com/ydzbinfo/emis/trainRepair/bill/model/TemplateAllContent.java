package com.ydzbinfo.emis.trainRepair.bill.model;

import com.ydzbinfo.emis.trainRepair.bill.querymodel.base.TemplateContentBase;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 查询单据所有对象
 **/
@EqualsAndHashCode(callSuper = true)
@Data
public class TemplateAllContent extends TemplateContentBase implements ITemplateContentInfo<TemplateAllLinkContent, TemplateAllControl> {

    // 单据配置过程内容关联内容表
    private List<TemplateAllLinkContent> linkContents;

    private List<TemplateAllControl> controls;
}
