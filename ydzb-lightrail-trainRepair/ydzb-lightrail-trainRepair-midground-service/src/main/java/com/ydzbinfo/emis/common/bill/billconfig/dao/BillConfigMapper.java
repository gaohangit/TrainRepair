package com.ydzbinfo.emis.common.bill.billconfig.dao;

import com.ydzbinfo.emis.trainRepair.bill.model.IQueryTemplateProcessModel;
import com.ydzbinfo.emis.trainRepair.bill.model.IQueryTemplateSummaryModel;
import com.ydzbinfo.emis.trainRepair.bill.querymodel.templateprocess.TemplateProcessForShow;
import com.ydzbinfo.emis.trainRepair.bill.querymodel.templatesummary.TemplateSummaryForShow;

import java.util.List;

public interface BillConfigMapper {

    /**
     * 查询未发布单据
     */
    List<TemplateProcessForShow> queryBills(IQueryTemplateProcessModel queryTemplateProcessModel);


    /**
     * 查询已发布单据
     */
    List<TemplateSummaryForShow> queryReleaseBills(IQueryTemplateSummaryModel queryTemplateSummaryModel);

}
