package com.ydzbinfo.emis.common.bill.billconfig.service.impl;

import com.ydzbinfo.emis.common.bill.billconfig.dao.BillConfigMapper;
import com.ydzbinfo.emis.common.bill.billconfig.service.BillConfigService;
import com.ydzbinfo.emis.common.bill.billconfig.service.ITemplateProcessService;
import com.ydzbinfo.emis.common.bill.billconfig.service.ITemplateSummaryService;
import com.ydzbinfo.emis.common.bill.utils.BillUtil;
import com.ydzbinfo.emis.common.bill.utils.SsjsonFileUtils;
import com.ydzbinfo.emis.configs.kafka.SpringCloudStreamModuleEnum;
import com.ydzbinfo.emis.configs.kafka.billconfig.BillConfigMqSink;
import com.ydzbinfo.emis.configs.kafka.billconfig.BillConfigMqSource;
import com.ydzbinfo.emis.handlers.kafka.StreamCloudTransData;
import com.ydzbinfo.emis.trainRepair.bill.model.IQueryTemplateProcessModel;
import com.ydzbinfo.emis.trainRepair.bill.model.IQueryTemplateSummaryModel;
import com.ydzbinfo.emis.trainRepair.bill.model.TemplateQueryParamModel;
import com.ydzbinfo.emis.trainRepair.bill.model.templateprocess.TemplateProcessContentInfo;
import com.ydzbinfo.emis.trainRepair.bill.model.templateprocess.TemplateProcessForSave;
import com.ydzbinfo.emis.trainRepair.bill.model.templateprocess.TemplateProcessInfo;
import com.ydzbinfo.emis.trainRepair.bill.model.templatesummary.TemplateSummaryContentInfo;
import com.ydzbinfo.emis.trainRepair.bill.model.templatesummary.TemplateSummaryInfo;
import com.ydzbinfo.emis.trainRepair.bill.querymodel.base.ConfigTemplateBase;
import com.ydzbinfo.emis.trainRepair.bill.querymodel.base.key.TemplateUniqueKey;
import com.ydzbinfo.emis.trainRepair.bill.querymodel.templateprocess.*;
import com.ydzbinfo.emis.trainRepair.bill.querymodel.templatesummary.*;
import com.ydzbinfo.emis.utils.Assert;
import com.ydzbinfo.emis.utils.CommonUtils;
import com.ydzbinfo.emis.utils.UserUtil;
import com.ydzbinfo.emis.utils.mybatisplus.MybatisPlusUtils;
import com.ydzbinfo.emis.utils.mybatisplus.param.ColumnParam;
import com.ydzbinfo.emis.utils.mybatisplus.param.ColumnParamUtil;
import com.ydzbinfo.emis.utils.mybatisplus.param.Criteria;
import com.ydzbinfo.emis.utils.result.RestRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.ydzbinfo.emis.utils.mybatisplus.MybatisPlusUtils.eqParam;
import static com.ydzbinfo.emis.utils.mybatisplus.MybatisPlusUtils.inParam;

@Service
public class BillConfigServiceImpl implements BillConfigService {

    @Autowired
    BillConfigMapper billConfigMapper;

    @Autowired
    ITemplateProcessService templateProcessService;

    @Autowired
    ITemplateSummaryService templateSummaryService;


    @Override
    @Transactional
    synchronized public List<String> saveTemplateProcesses(List<TemplateProcessForSave> templateProcesses) {
        if (templateProcesses.size() == 0) {
            throw RestRequestException.normalFail("???????????????");
        }

        templateProcesses.forEach(BillUtil::validTemplateBasicInfo);

        for (TemplateProcessForSave process : templateProcesses) {
            process.setTemplatePath(SsjsonFileUtils.saveTemplateSsjsonFile(process));
        }
        return BillUtil.saveTemplateInfoGroup(
            new ArrayList<TemplateProcessInfo>(templateProcesses),
            TemplateUniqueKey.class,
            TemplateUniqueKey::new,
            templateProcessService,
            TemplateProcess::getTemplateId,
            v -> v,
            templateProcessInfo -> BillUtil.getIdResetCopyNew(
                templateProcessInfo,
                TemplateProcessInfo::new,
                TemplateProcessContentInfo::new,
                TemplateProcLinkContent::new,
                TemplateProcLinkControl::new,
                TemplateProcessArea::new
            ),
            newTemplateProcess -> {
                // ??????????????????
                newTemplateProcess.setCreateTime(new Date());
                // ???????????????
                newTemplateProcess.setCreateUser(UserUtil.getUserInfo().getName());
            },
            (deleteTemplateIds, newTemplateProcesses) ->
                CommonUtils.getBeanByThis(this)
                    .doSaveTemplateProcessWithData(deleteTemplateIds, newTemplateProcesses)
        );
    }


    @Override
    @Transactional
    @StreamCloudTransData(
        module = SpringCloudStreamModuleEnum.BILL_CONFIG,
        outputChannel = BillConfigMqSource.TEMPLATEPROCESS_OUTPUT,
        inputChannel = BillConfigMqSink.TEMPLATEPROCESS_INPUT
    )
    public void doSaveTemplateProcessWithData(List<String> deleteTemplateIds, List<TemplateProcessInfo> insertTemplateProcesses) {
        for (String deleteTemplateId : deleteTemplateIds) {
            templateProcessService.deleteTemplateProcessByTemplateId(deleteTemplateId);
        }
        for (TemplateProcessInfo insertTemplateProcess : insertTemplateProcesses) {
            templateProcessService.insertTemplateProcessInfo(insertTemplateProcess);
        }
    }

    private TemplateSummaryInfo getIdResetCopyNew(TemplateSummaryInfo source) {
        return BillUtil.getIdResetCopyNew(
            source,
            TemplateSummaryInfo::new,
            TemplateSummaryContentInfo::new,
            TemplateSummLinkContent::new,
            TemplateSummLinkControl::new,
            TemplateSummaryArea::new
        );
    }

    @Override
    public List<TemplateProcessForShow> queryBills(IQueryTemplateProcessModel queryTemplateProcessModel) {
        return billConfigMapper.queryBills(queryTemplateProcessModel);
    }

    @Override
    public List<TemplateSummaryForShow> queryReleaseBills(IQueryTemplateSummaryModel queryTemplateSummaryModel) {
        return billConfigMapper.queryReleaseBills(queryTemplateSummaryModel);
    }

    @Override
    public List<TemplateProcessInfo> queryBillsWithDetailsByTemplateIds(Set<String> templateIds) {
        Assert.notEmptyInnerFatal(templateIds, "templateIds????????????");
        List<TemplateProcess> templateProcesses = MybatisPlusUtils.selectList(
            templateProcessService,
            inParam(ConfigTemplateBase::getTemplateId, templateIds)
        );
        return templateProcesses.stream().map(
            v -> templateProcessService.transToTemplateProcessInfo(v)
        ).collect(Collectors.toList());
    }

    @Override
    public List<TemplateProcessInfo> queryBillsWithDetailsByTemplateNos(Set<String> templateNos) {
        Assert.notEmptyInnerFatal(templateNos, "templateNos????????????");
        List<TemplateProcess> templateProcesses = MybatisPlusUtils.selectList(
            templateProcessService,
            inParam(ConfigTemplateBase::getTemplateNo, templateNos)
        );
        return templateProcesses.stream().map(
            v -> templateProcessService.transToTemplateProcessInfo(v)
        ).collect(Collectors.toList());
    }

    @Override
    public List<TemplateSummaryInfo> queryReleaseBillsWithDetailsByTemplateIds(Set<String> templateIds) {
        Assert.notEmptyInnerFatal(templateIds, "templateIds????????????");
        List<TemplateSummary> templateSummaries = MybatisPlusUtils.selectList(
            templateSummaryService,
            inParam(ConfigTemplateBase::getTemplateId, templateIds)
        );
        return templateSummaries.stream().map(
            v -> templateSummaryService.transToTemplateSummaryInfo(v)
        ).collect(Collectors.toList());
    }


    private Criteria<TemplateSummary> toCriteria(TemplateQueryParamModel templateQueryParamModel) {
        return MybatisPlusUtils.allMatch(
            eqParam(TemplateSummary::getValidFlag, "1"),// ??????????????????????????????????????????????????????
            MybatisPlusUtils.allMatch(ColumnParamUtil.filterBlankParams(
                eqParam(TemplateSummary::getTemplateTypeCode, templateQueryParamModel.getTemplateTypeCode()),
                eqParam(TemplateSummary::getTrainsetType, templateQueryParamModel.getTrainsetType()),
                eqParam(TemplateSummary::getMarshalCount, templateQueryParamModel.getMarshalCount()),
                eqParam(TemplateSummary::getItemCode, templateQueryParamModel.getItemCode()),
                eqParam(TemplateSummary::getDepotCode, templateQueryParamModel.getDepotCode())
            )),
            StringUtils.hasText(templateQueryParamModel.getBatch()) ?
                // ??????????????????????????????????????????????????????null
                MybatisPlusUtils.anyMatch(
                    eqParam(TemplateSummary::getBatch, templateQueryParamModel.getBatch()),
                    eqParam(TemplateSummary::getBatch, null)
                ) :
                // ??????????????????
                null,
            StringUtils.hasText(templateQueryParamModel.getUnitCode()) ?
                // ????????????????????????????????????????????????????????????????????????null
                MybatisPlusUtils.anyMatch(
                    eqParam(TemplateSummary::getUnitCode, templateQueryParamModel.getUnitCode()),
                    eqParam(TemplateSummary::getUnitCode, null)
                ) :
                // ???????????????????????????
                null
        );
    }

    @Override
    public List<List<TemplateSummaryInfo>> queryTemplateSummaryInfoGroups(TemplateQueryParamModel templateQueryParamModel) {
        String batch = templateQueryParamModel.getBatch();
        String trainsetType = templateQueryParamModel.getTrainsetType();
        boolean hasBatch = StringUtils.hasText(batch);
        if (hasBatch && !StringUtils.hasText(trainsetType)) {
            throw new RuntimeException("???????????????????????????????????????");
        }
        String unitCode = templateQueryParamModel.getUnitCode();
        String depotCode = templateQueryParamModel.getDepotCode();
        boolean hasUnitCode = StringUtils.hasText(unitCode);
        if (hasUnitCode && !StringUtils.hasText(depotCode)) {
            throw new RuntimeException("???????????????????????????????????????????????????");
        }
        List<TemplateSummaryInfo> list = MybatisPlusUtils.selectList(templateSummaryService, toCriteria(templateQueryParamModel)).stream().map(
            v -> templateSummaryService.transToTemplateSummaryInfo(v)
        ).collect(Collectors.toList());

        Function<List<TemplateSummaryInfo>, List<List<TemplateSummaryInfo>>> toGroups = (listForCheck) -> {
            Map<String, List<TemplateSummaryInfo>> groupedList = listForCheck.stream().collect(Collectors.groupingBy(ConfigTemplateBase::getTemplateNo));
            return new ArrayList<>(groupedList.values());
        };
        Set<String> batches = list.stream().map(TemplateUniqueKey::getBatch).collect(Collectors.toSet());
        Set<String> unitCodes = list.stream().map(TemplateUniqueKey::getUnitCode).collect(Collectors.toSet());
        // ???????????????????????????????????????
        if (batches.size() > 1 || unitCodes.size() > 1) {
            // ??????????????????(??????????????????1)?????????????????????????????????
            Predicate<String> testBatch = batches.size() > 1 && hasBatch ? batch::equals : v -> true;
            Predicate<String> testUnitCode = unitCodes.size() > 1 && hasUnitCode ? unitCode::equals : v -> true;
            List<TemplateSummaryInfo> filteredList = list.stream()
                .filter(v -> testBatch.test(v.getBatch()))
                .filter(v -> testUnitCode.test(v.getUnitCode()))
                .collect(Collectors.toList());
            return toGroups.apply(filteredList);
        } else {
            return toGroups.apply(list);
        }
    }

    @Override
    synchronized public void billConfigRelease(String templateId) {
        List<String> templateIds = Arrays.asList(templateId.split(","));
        // ??????templateId?????????????????????
        List<TemplateProcess> matchedTemplateProcessList = MybatisPlusUtils.selectList(
            templateProcessService,
            inParam(TemplateProcess::getTemplateId, templateIds)
        );
        if (matchedTemplateProcessList.size() == 0) {
            throw RestRequestException.normalFail("???????????????!");
        }
        // ???????????????templateNo
        Set<String> allTemplateNos = matchedTemplateProcessList.stream().map(ConfigTemplateBase::getTemplateNo).collect(Collectors.toSet());
        // ??????templateNo?????? ?????????????????????
        List<TemplateProcessInfo> templateProcesses = this.queryBillsWithDetailsByTemplateNos(allTemplateNos);

        // ??????templateNo??????
        Map<String, List<TemplateProcessInfo>> groupedTemplateProcesses = templateProcesses.stream().collect(Collectors.groupingBy(TemplateProcess::getTemplateNo));

        // ????????????????????????templateNo??????????????????
        List<TemplateSummary> templateSummaries = MybatisPlusUtils.selectList(
            templateSummaryService,
            eqParam(TemplateSummary::getValidFlag, "1"),
            inParam(TemplateSummary::getTemplateNo, allTemplateNos)
        );
        // ??????templateNo??????
        Map<String, List<TemplateSummary>> groupedTemplateSummaries = templateSummaries.stream().collect(Collectors.groupingBy(TemplateSummary::getTemplateNo));

        List<TemplateSummaryInfo> inserts = new ArrayList<>();
        List<TemplateSummary> updates = new ArrayList<>();
        for (String templateNo : allTemplateNos) {
            // ???????????????
            List<TemplateProcessInfo> needReleaseTemplates = groupedTemplateProcesses.get(templateNo);
            // ???????????????
            List<TemplateSummary> releasedTemplates = groupedTemplateSummaries.get(templateNo);

            // ?????????????????????
            String version;
            if (releasedTemplates != null && releasedTemplates.size() > 0) {
                int oldSubVersion = Integer.parseInt(releasedTemplates.get(0).getVersion().split("\\.")[1]);
                version = "V1." + (oldSubVersion + 1);
            } else {
                version = "V1.0";
            }

            // ?????????????????????
            for (TemplateProcessInfo needReleaseTemplate : needReleaseTemplates) {
                // ?????????????????????????????????
                TemplateSummaryInfo newTemplateSummary = BillUtil.transTemplateTo(
                    needReleaseTemplate,
                    TemplateSummaryInfo::new,
                    TemplateSummaryContentInfo::new,
                    TemplateSummLinkContent::new,
                    TemplateSummLinkControl::new,
                    TemplateSummaryArea::new
                );

                // ??????????????????
                newTemplateSummary.setVersion(version);

                // ????????????????????????
                newTemplateSummary.setTemplateName(BillUtil.generateTemplateName(newTemplateSummary));

                // ?????????ssjson???????????????????????????ssjson?????????????????????
                newTemplateSummary.setTemplatePath(
                    SsjsonFileUtils.saveTemplateSsjsonFile(
                        newTemplateSummary,
                        SsjsonFileUtils.getSsjsonFileContent(needReleaseTemplate.getTemplatePath())
                    )
                );

                // ??????????????????
                newTemplateSummary.setValidDate(new Date());
                newTemplateSummary.setValidFlag("1");

                newTemplateSummary.setDelFlag("0");// ?????????????????? 1--??????0--???
                newTemplateSummary.setPublish("1");// ???????????? 1--?????????0--?????????

                TemplateSummaryInfo newTemplateSummaryInfo = getIdResetCopyNew(newTemplateSummary);
                newTemplateSummaryInfo.setCreateTime(new Date());
                inserts.add(newTemplateSummaryInfo);
            }

            // ?????????????????????????????????
            if (releasedTemplates != null && releasedTemplates.size() > 0) {
                for (TemplateSummary releasedTemplateSummary : releasedTemplates) {
                    TemplateSummary templateSummary = new TemplateSummary();

                    templateSummary.setTemplateId(releasedTemplateSummary.getTemplateId());

                    templateSummary.setUnvalidDate(new Date());
                    templateSummary.setValidFlag("0"); // ????????????0

                    updates.add(templateSummary);
                }
            }
        }
        CommonUtils.getBeanByThis(this).doBillConfigReleaseWithData(inserts, updates);
    }

    @Override
    @Transactional
    @StreamCloudTransData(
        module = SpringCloudStreamModuleEnum.BILL_CONFIG,
        outputChannel = BillConfigMqSource.TEMPLATESUMMARY_OUTPUT,
        inputChannel = BillConfigMqSink.TEMPLATESUMMARY_INPUT
    )
    public void doBillConfigReleaseWithData(List<TemplateSummaryInfo> inserts, List<TemplateSummary> updates) {
        for (TemplateSummaryInfo insert : inserts) {
            templateSummaryService.insertTemplateSummaryInfo(insert);
        }
        for (TemplateSummary update : updates) {
            templateSummaryService.updateTemplateSummaryStateById(update);
        }
    }
}
