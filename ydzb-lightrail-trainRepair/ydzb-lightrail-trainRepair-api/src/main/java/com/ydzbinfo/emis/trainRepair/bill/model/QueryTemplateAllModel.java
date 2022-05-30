package com.ydzbinfo.emis.trainRepair.bill.model;

import com.ydzbinfo.emis.trainRepair.bill.querymodel.templatesummary.TemplateSummary;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 查询模板参数对象
 *
 * @author 张天可
 * @since 2021-06-17
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class QueryTemplateAllModel extends TemplateSummary implements Serializable, IQueryTemplateProcessModel, IQueryTemplateSummaryModel {

    private String[] templateTypeCodes;

    private String[] unitCodes;

    private String[] batches;

    private String[] trainsetTypes;

    // 是否显示系统单据
    private Boolean showSystemTemplate = true;
    // 是否显示自定义单据
    private Boolean showCustomTemplate = true;
}
