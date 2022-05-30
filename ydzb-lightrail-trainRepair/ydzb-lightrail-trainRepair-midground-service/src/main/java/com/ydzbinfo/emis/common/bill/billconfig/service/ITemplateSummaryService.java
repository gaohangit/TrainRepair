package com.ydzbinfo.emis.common.bill.billconfig.service;

import com.baomidou.mybatisplus.service.IService;
import com.ydzbinfo.emis.trainRepair.bill.querymodel.templatesummary.TemplateSummary;
import com.ydzbinfo.emis.trainRepair.bill.querymodel.templatesummary.TemplateSummaryForShow;
import com.ydzbinfo.emis.trainRepair.bill.model.templatesummary.TemplateSummaryInfo;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author 张天可
 * @since 2021-06-18
 */
public interface ITemplateSummaryService extends IService<TemplateSummary> {

    void insertTemplateSummaryInfo(TemplateSummaryInfo newTemplateSummary);

    void deleteTemplateSummaryByTemplateId(String templateId);

    /**
     * 更新已发布单据的状态
     */
    void updateTemplateSummaryStateById(TemplateSummary templateSummary);

    TemplateSummaryInfo transToTemplateSummaryInfo(TemplateSummary templateSummary);

}
