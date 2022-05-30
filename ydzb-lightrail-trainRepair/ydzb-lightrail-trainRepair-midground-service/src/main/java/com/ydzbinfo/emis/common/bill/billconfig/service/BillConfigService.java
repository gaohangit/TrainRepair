package com.ydzbinfo.emis.common.bill.billconfig.service;

import com.ydzbinfo.emis.configs.kafka.SpringCloudStreamModuleEnum;
import com.ydzbinfo.emis.configs.kafka.billconfig.BillConfigMqSink;
import com.ydzbinfo.emis.configs.kafka.billconfig.BillConfigMqSource;
import com.ydzbinfo.emis.handlers.kafka.StreamCloudTransData;
import com.ydzbinfo.emis.trainRepair.bill.model.IQueryTemplateProcessModel;
import com.ydzbinfo.emis.trainRepair.bill.model.IQueryTemplateSummaryModel;
import com.ydzbinfo.emis.trainRepair.bill.model.TemplateQueryParamModel;
import com.ydzbinfo.emis.trainRepair.bill.model.templateprocess.TemplateProcessForSave;
import com.ydzbinfo.emis.trainRepair.bill.model.templateprocess.TemplateProcessInfo;
import com.ydzbinfo.emis.trainRepair.bill.model.templatesummary.TemplateSummaryInfo;
import com.ydzbinfo.emis.trainRepair.bill.querymodel.templateprocess.TemplateProcessForShow;
import com.ydzbinfo.emis.trainRepair.bill.querymodel.templatesummary.TemplateSummary;
import com.ydzbinfo.emis.trainRepair.bill.querymodel.templatesummary.TemplateSummaryForShow;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

public interface BillConfigService {

    /**
     * 保存模板过程，根据id是否存在判断是更新还是新增
     *
     * @param templateProcessInfos
     * @return 新id列表
     */
    List<String> saveTemplateProcesses(List<TemplateProcessForSave> templateProcessInfos);


    @Transactional
    void doSaveTemplateProcessWithData(List<String> deleteTemplateId, List<TemplateProcessInfo> insertTemplateProcess);

    /**
     * 没有分页功能的记录单查询
     *
     * @return
     */
    List<TemplateProcessForShow> queryBills(IQueryTemplateProcessModel queryTemplateProcessModel);


    /**
     * 没有分页功能的记录单查询
     *
     * @param queryTemplateSummaryModel
     * @return
     */
    List<TemplateSummaryForShow> queryReleaseBills(IQueryTemplateSummaryModel queryTemplateSummaryModel);

    /**
     * 根据templateId列表查询过程记录单，一并查询单元格等关联表信息
     */
    List<TemplateProcessInfo> queryBillsWithDetailsByTemplateIds(Set<String> templateIds);

    /**
     * 根据templateNo列表查询过程记录单，一并查询单元格等关联表信息
     */
    List<TemplateProcessInfo> queryBillsWithDetailsByTemplateNos(Set<String> templateNos);

    /**
     * 根据templateId列表查询已发布记录单，一并查询单元格等关联表信息
     */
    List<TemplateSummaryInfo> queryReleaseBillsWithDetailsByTemplateIds(Set<String> templateIds);

    /**
     * 查询已发布记录单模板信息
     *
     * @param templateQueryParamModel
     * @return
     */
    List<List<TemplateSummaryInfo>> queryTemplateSummaryInfoGroups(TemplateQueryParamModel templateQueryParamModel);


    void billConfigRelease(String templateId);

    @Transactional
    @StreamCloudTransData(
        module = SpringCloudStreamModuleEnum.BILL_CONFIG,
        outputChannel = BillConfigMqSource.TEMPLATESUMMARY_OUTPUT,
        inputChannel = BillConfigMqSink.TEMPLATESUMMARY_INPUT
    )
    void doBillConfigReleaseWithData(List<TemplateSummaryInfo> inserts, List<TemplateSummary> updates);
}
