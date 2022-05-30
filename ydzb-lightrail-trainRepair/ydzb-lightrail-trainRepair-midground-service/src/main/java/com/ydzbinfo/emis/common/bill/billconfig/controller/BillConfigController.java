package com.ydzbinfo.emis.common.bill.billconfig.controller;

import com.baomidou.mybatisplus.plugins.Page;
import com.ydzbinfo.emis.common.bill.basetemplate.service.IBaseTemplateConfigService;
import com.ydzbinfo.emis.common.bill.basetemplate.service.IBaseTemplateService;
import com.ydzbinfo.emis.common.bill.billconfig.service.BillConfigService;
import com.ydzbinfo.emis.common.bill.billconfig.service.ITemplateProcessService;
import com.ydzbinfo.emis.common.bill.billconfig.service.ITemplateSummaryService;
import com.ydzbinfo.emis.common.bill.constant.TemplateQueryEnum;
import com.ydzbinfo.emis.common.bill.typeconfig.service.ITemplateTypeService;
import com.ydzbinfo.emis.common.bill.utils.BillUtil;
import com.ydzbinfo.emis.common.bill.utils.SsjsonFileUtils;
import com.ydzbinfo.emis.trainRepair.bill.model.*;
import com.ydzbinfo.emis.trainRepair.bill.model.basetemplate.BaseTemplateInfo;
import com.ydzbinfo.emis.trainRepair.bill.model.templateprocess.TemplateProcessForSave;
import com.ydzbinfo.emis.trainRepair.bill.model.templateprocess.TemplateProcessInfo;
import com.ydzbinfo.emis.trainRepair.bill.model.templatesummary.TemplateSummaryInfo;
import com.ydzbinfo.emis.trainRepair.bill.querymodel.attribute.TemplateType;
import com.ydzbinfo.emis.trainRepair.bill.querymodel.base.ConfigTemplateBase;
import com.ydzbinfo.emis.trainRepair.bill.querymodel.base.ITemplateForShow;
import com.ydzbinfo.emis.trainRepair.bill.querymodel.base.key.BaseTemplateUniqueKey;
import com.ydzbinfo.emis.trainRepair.bill.querymodel.base.key.TemplateUniqueKey;
import com.ydzbinfo.emis.trainRepair.bill.querymodel.basetemplate.BaseTemplate;
import com.ydzbinfo.emis.trainRepair.bill.querymodel.basetemplate.BaseTemplateConfig;
import com.ydzbinfo.emis.trainRepair.bill.querymodel.templateprocess.TemplateProcess;
import com.ydzbinfo.emis.trainRepair.bill.querymodel.templateprocess.TemplateProcessForShow;
import com.ydzbinfo.emis.trainRepair.bill.querymodel.templatesummary.TemplateSummary;
import com.ydzbinfo.emis.trainRepair.bill.querymodel.templatesummary.TemplateSummaryForShow;
import com.ydzbinfo.emis.trainRepair.common.service.IRemoteService;
import com.ydzbinfo.emis.trainRepair.constant.BillTemplateStateEnum;
import com.ydzbinfo.emis.trainRepair.constant.MarshallingTypeEnum;
import com.ydzbinfo.emis.utils.Assert;
import com.ydzbinfo.emis.utils.BaseController;
import com.ydzbinfo.emis.utils.CommonUtils;
import com.ydzbinfo.emis.utils.EnumUtils;
import com.ydzbinfo.emis.utils.entity.ReflectUtil;
import com.ydzbinfo.emis.utils.mybatisplus.MybatisPlusUtils;
import com.ydzbinfo.emis.utils.mybatisplus.param.ColumnParam;
import com.ydzbinfo.emis.utils.mybatisplus.param.ColumnParamUtil;
import com.ydzbinfo.emis.utils.mybatisplus.param.LogicalLinkable;
import com.ydzbinfo.emis.utils.result.RestRequestException;
import com.ydzbinfo.emis.utils.result.RestResult;
import com.ydzbinfo.emis.utils.result.RestResultGeneric;
import com.ydzbinfo.hussar.common.annotion.BussinessLog;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static com.ydzbinfo.emis.utils.mybatisplus.MybatisPlusUtils.*;

/**
 * 单据配置
 */
@Controller
@RequestMapping(BillConfigController.BASE_MAPPING)
public class BillConfigController extends BaseController {

    final public static String BASE_MAPPING = "/billConfig";

    @Autowired
    private BillConfigService billConfigService;

    @Autowired
    private ITemplateSummaryService templateSummaryService;

    @Autowired
    private ITemplateProcessService templateProcessService;

    @Autowired
    private ITemplateTypeService templateTypeService;

    // protected static final Logger logger = LoggerFactory.getLogger(BillConfigController.class);

    /**
     * 记录单据配置首页查询
     */
    @GetMapping(value = "/queryBillConfigs")
    @ResponseBody
    public RestResultGeneric<Page<TemplateGroupForShow>> queryBillConfigs(QueryBillConfigsModel queryModel) {
        QueryTemplateAllModel queryTemplateModel = new QueryTemplateAllModel();
        BeanUtils.copyProperties(queryModel, queryTemplateModel);

        try {
            // 单据配置保存的时候 出现两种情况 这里查询 需要将
            // 第一种情况 是 只有单据的情况
            // 第二种情况是多个单据，他们只有编组形式不同，单据模版id不同，这里
            // 展示的时候 要按 S_TEMPLATENO 模版编号 分组 合并展示 编组形式 和 单据模版id 逗号分割合并 过程 和 发布 他俩逻辑是一样的
            BillTemplateStateEnum state = EnumUtils.findEnum(BillTemplateStateEnum.class, BillTemplateStateEnum::getValue, queryModel.getState());
            // 这里需要判断单据类型有没有
            String templateTypeCode = queryModel.getTemplateTypeCode();
            if (templateTypeCode != null && !templateTypeCode.equals("")) {
                List<TemplateType> templateTypes = templateTypeService.getAllTemplateTypesIncludeSelf(
                    templateTypeCode,
                    queryModel.getShowSystemTemplate(),
                    queryModel.getShowCustomTemplate()
                );
                if (templateTypes.size() > 0) {
                    queryTemplateModel.setTemplateTypeCode(null);
                    queryTemplateModel.setTemplateTypeCodes(templateTypes.stream().map(TemplateType::getTemplateTypeCode).toArray(String[]::new));
                }
            }
            // 根据指定状态对单据模板进行查询
            List<TemplateGroupForShow> templateList = new ArrayList<>();

            if (state == null) {
                // 查询全部
                List<TemplateGroupForShow> TemplateProcesses = getTemplateGroupItemFromTemplateProcess(queryTemplateModel);
                List<TemplateGroupForShow> templateSummaries = getTemplateGroupItemFromTemplateSummary(queryTemplateModel);
                templateList.addAll(templateSummaries);
                templateList.addAll(TemplateProcesses);
            } else {
                switch (state) {
                    case RELEASED: {// 查询已发布
                        List<TemplateGroupForShow> templateSummaries = getTemplateGroupItemFromTemplateSummary(queryTemplateModel);
                        templateList.addAll(templateSummaries);
                        break;
                    }
                    case UNRELEASED: {// 查询过程
                        List<TemplateGroupForShow> TemplateProcesses = getTemplateGroupItemFromTemplateProcess(queryTemplateModel);
                        templateList.addAll(TemplateProcesses);
                        break;
                    }
                }
            }


            Function<List<TemplateGroupForShow>, TemplateGroupForShow> mergeTemplates = (templates) -> {
                TemplateGroupForShow mergedTemplate = new TemplateGroupForShow();
                TemplateGroupForShow firstTemplate = templates.get(0);
                BeanUtils.copyProperties(firstTemplate, mergedTemplate);
                List<String> templateIds = new ArrayList<>();
                List<String> marshallingTypeNames = new ArrayList<>();
                for (TemplateGroupForShow template : templates) {
                    String templateId = template.getTemplateId();
                    templateIds.add(templateId);
                    String marshallingTypeName = template.getMarshallingTypeName();
                    if (!marshallingTypeNames.contains(marshallingTypeName)) {
                        marshallingTypeNames.add(marshallingTypeName);
                    }
                }
                mergedTemplate.setTemplateId(String.join(",", templateIds));
                mergedTemplate.setMarshallingType(null);
                mergedTemplate.setMarshallingTypeName(String.join(",", marshallingTypeNames));
                return mergedTemplate;
            };

            // 将单据按照templateNo和isUpdate分组
            Map<String, List<TemplateGroupForShow>> templateAllGroup = templateList.stream()
                .collect(Collectors.groupingBy((templateAll) -> templateAll.getTemplateNo() + "," + templateAll.getIsUpdate()));
            // 合并同组单据
            List<TemplateGroupForShow> mergedTemplates = templateAllGroup.values().stream().map(mergeTemplates).collect(Collectors.toList());

            List<List<TemplateGroupForShow>> groupedMergedTemplates = new ArrayList<>(mergedTemplates.stream().collect(Collectors.groupingBy(TemplateGroupForShow::getTemplateNo)).values());
            // 组内发布固定在前
            groupedMergedTemplates.forEach(v -> v.sort((a, b) -> {
                boolean canUpdateA = a.getIsUpdate().equals("1");
                boolean canUpdateB = b.getIsUpdate().equals("1");
                if (canUpdateA == canUpdateB) {
                    return 0;
                } else if (!canUpdateA) {// 如果a不可更新，那么a为发布模板，那么a提前展示
                    return -1;
                } else {
                    return 1;
                }
            }));

            // 按照每组最近更新的时间进行降序排列
            groupedMergedTemplates.sort((v1, v2) -> {
                TemplateGroupForShow newest1 = v1.stream().max(Comparator.comparing(TemplateGroupForShow::getCreateTime)).orElse(null);
                TemplateGroupForShow newest2 = v2.stream().max(Comparator.comparing(TemplateGroupForShow::getCreateTime)).orElse(null);
                return Comparator.comparing(TemplateGroupForShow::getCreateTime).reversed().compare(newest1, newest2);
            });

            // 如果存在排序信息，则按指定字段进行排序
            if (!StringUtils.isEmpty(queryModel.getOrderAscOrDesc()) && !StringUtils.isEmpty(queryModel.getOrderInfo())) {
                @SuppressWarnings({"unchecked", "rawtypes"})
                Comparator<TemplateGroupForShow> comparator = Comparator.comparing(
                    v -> (Comparable) ReflectUtil.getValue(v, queryModel.getOrderInfo())
                );
                if (queryModel.getOrderAscOrDesc().equals("desc")) {
                    comparator = comparator.reversed();
                }
                Comparator<TemplateGroupForShow> finalComparator = comparator;
                groupedMergedTemplates.sort((v1, v2) -> finalComparator.compare(v1.get(0), v2.get(0)));
            }

            // 扁平化
            List<TemplateGroupForShow> resultTemplateList = groupedMergedTemplates.stream().reduce(new ArrayList<>(), (v1, v2) -> {
                List<TemplateGroupForShow> mergedList = new ArrayList<>();
                mergedList.addAll(v1);
                mergedList.addAll(v2);
                return mergedList;
            });
            int pageSize = queryModel.getLimit();
            int pageNum = queryModel.getPage();
            // 分页返回
            return RestResultGeneric.success(CommonUtils.getPage(resultTemplateList, pageNum, pageSize));
        } catch (Exception ex) {
            return RestResultGeneric.fromException(ex, logger, null);
        }
    }

    /**
     * 获取发布(仅主表)
     **/
    private List<TemplateGroupForShow> getTemplateGroupItemFromTemplateSummary(IQueryTemplateSummaryModel queryTemplateSummaryModel) {
        List<TemplateGroupForShow> resultList = new ArrayList<>();
        List<TemplateSummaryForShow> templateSummaryList = billConfigService.queryReleaseBills(queryTemplateSummaryModel);
        for (TemplateSummaryForShow templateSummary : templateSummaryList) {
            TemplateGroupForShow templateGroupItem = transToTemplateGroupForShow(templateSummary);
            templateGroupItem.setIsUpdate("0");// 是否可以修改 1— 可以，0—不可以，已发布状态的不可以，编辑状态的可以
            templateGroupItem.setState(BillTemplateStateEnum.RELEASED);// 状态0—编辑，1—已发布
            resultList.add(templateGroupItem);
        }
        return resultList;
    }

    /**
     * 获取过程(仅主表)
     **/
    private List<TemplateGroupForShow> getTemplateGroupItemFromTemplateProcess(IQueryTemplateProcessModel queryTemplateProcessModel) {
        List<TemplateGroupForShow> resultList = new ArrayList<>();
        List<TemplateProcessForShow> list = billConfigService.queryBills(queryTemplateProcessModel);
        for (TemplateProcessForShow summary : list) {
            TemplateGroupForShow templateGroupItem = transToTemplateGroupForShow(summary);
            templateGroupItem.setIsUpdate("1");
            templateGroupItem.setState(BillTemplateStateEnum.UNRELEASED);
            resultList.add(templateGroupItem);
        }
        return resultList;
    }

    private TemplateGroupForShow transToTemplateGroupForShow(ITemplateForShow templateForShow) {
        TemplateGroupForShow templateGroupForShow = new TemplateGroupForShow();
        BeanUtils.copyProperties(templateForShow, templateGroupForShow);
        Integer marshallingType = templateForShow.getMarshallingType();
        templateGroupForShow.setMarshallingType(marshallingType);
        templateGroupForShow.setMarshallingTypeName(MarshallingTypeEnum.getLabelByKey(marshallingType));
        return templateGroupForShow;
    }

    /**
     * 校验是否能够新增
     */
    @RequestMapping(value = "/verifyBill")
    @ResponseBody
    public RestResult verifyBill(@RequestBody TemplateUniqueKey templateUnique) {
        try {
            CommonUtils.emptyStringToNull(templateUnique);
            if (templateUnique.getMarshalCount() != null && (templateUnique.getBatch() != null || templateUnique.getTrainsetType() != null)) {
                throw RestRequestException.normalFail("车型（批次）与编组数量不能共存！");
            }
            boolean exist = MybatisPlusUtils.selectExist(
                templateProcessService,
                getEqColumnParamsFromEntity(templateUnique, true, TemplateUniqueKey.class)
            );
            if (exist) {
                throw RestRequestException.normalFail("当前单据已存在，不能新增！");
            } else {
                return RestResult.success();
            }
        } catch (Exception ex) {
            return RestResult.fromException(ex, logger, null);
        }
    }

    /**
     * 记录单配置保存
     */
    @RequestMapping(value = "/saveBillConfig")
    @ResponseBody
    public RestResult saveBillConfig(@RequestBody List<TemplateProcessForSave> list) {
        try {
            return RestResult.success().setData(billConfigService.saveTemplateProcesses(list));
        } catch (Exception ex) {
            return RestResult.fromException(ex, logger, "保存记录单模板失败");
        }
    }

    /**
     * @author 冯帅
     * @date 2021/4/19
     * @description 记录单配置发布  代码迁移
     * @modifier 张天可 2021/6/23
     */
    @BussinessLog(value = "记录单配置-发布", key = "/billConfig/billConfigRelease", type = "04")
    @ApiOperation(value = "记录单配置-发布", notes = "记录单配置-发布")
    @GetMapping(value = "/billConfigRelease")
    @ResponseBody
    public RestResult billConfigRelease(String templateId) {
        try {
            billConfigService.billConfigRelease(templateId);
            return RestResult.success();
        } catch (Exception ex) {
            return RestResult.fromException(ex, logger, "发布记录单模板失败");
        }
    }

    /**
     * 记录单配置-单条记录单查看功能 可以查 过程 和发布的 templateId 逗号分割的
     * <p>
     * templateId 模版id
     */
    @GetMapping("/getBillTemplateDetail")
    @ApiOperation(value = "记录单配置-单条记录单查看功能 可以查 过程 和发布的 templateId 逗号分割的", notes = "记录单配置-单条记录单查看功能 可以查 过程 和发布的 templateId 逗号分割的")
    @ResponseBody
    public RestResultGeneric<List<TemplateAll>> getBillTemplateDetail(@RequestParam("templateId") String templateId, @RequestParam("state") String state) {
        try {
            Set<String> templateIds = new HashSet<>(Arrays.asList(templateId.split(",")));
            BillTemplateStateEnum stateEnum = EnumUtils.findEnum(BillTemplateStateEnum.class, BillTemplateStateEnum::getValue, state);
            if (BillTemplateStateEnum.UNRELEASED.equals(stateEnum)) {// 未发布
                List<TemplateProcessInfo> templateProcessInfos = billConfigService.queryBillsWithDetailsByTemplateIds(templateIds);
                return RestResultGeneric.success(templateProcessInfos.stream().map(BillUtil::transToTemplateAll).collect(Collectors.toList()));
            } else if (BillTemplateStateEnum.RELEASED.equals(stateEnum)) {// 已发布
                List<TemplateSummaryInfo> templateSummaryInfos = billConfigService.queryReleaseBillsWithDetailsByTemplateIds(templateIds);
                return RestResultGeneric.success(templateSummaryInfos.stream().map(BillUtil::transToTemplateAll).collect(Collectors.toList()));
            } else {
                throw new RuntimeException("state参数错误，" + BillTemplateStateEnum.getTip());
            }
        } catch (Exception ex) {
            return RestResultGeneric.fromException(ex, logger, null);
        }
    }

    /**
     * 真删除单据
     */
    @GetMapping("/realDeleteBillTemplate")
    @ResponseBody
    public RestResult realDeleteBillTemplate(@RequestParam("templateId") String templateId) {
        try {
            RestResult restResult = RestResult.success();
            for (String templateId_ : templateId.split(",")) {
                templateProcessService.deleteTemplateProcessByTemplateId(templateId_);
                templateSummaryService.deleteTemplateSummaryByTemplateId(templateId_);
            }
            return restResult;
        } catch (Exception ex) {
            return RestResult.fromException(ex, logger, null);
        }
    }

    /**
     * 查询已发布的记录单配置模板（单组）
     *
     * @author 张天可
     */
    @RequestMapping("/queryTemplateSummaryInfo")
    @ResponseBody
    @ApiOperation(value = "查询已发布的记录单配置模板", notes = "查询已发布的记录单配置模板")
    @BussinessLog(value = "查询已发布的记录单配置模板", key = BASE_MAPPING + "/queryTemplateSummaryInfo", type = "04")
    public RestResult queryTemplateSummaryInfo(@RequestBody TemplateQueryParamModel templateQueryParamModel) {
        try {
            List<List<TemplateSummaryInfo>> templateSummaryInfoGroups = billConfigService.queryTemplateSummaryInfoGroups(templateQueryParamModel);
            if (templateSummaryInfoGroups.size() > 1) {
                throw RestRequestException.fatalFail("查询到多组单据，请检查条件");
            }
            return RestResult.success().setData(templateSummaryInfoGroups.size() == 1 ? templateSummaryInfoGroups.get(0) : new ArrayList<>());
        } catch (Exception e) {
            return RestResult.fromException(e, logger, "查询已发布的记录单配置模板失败");
        }
    }

    @RequestMapping("/queryTemplateSummaryInfoGroups")
    @ResponseBody
    @ApiOperation(value = "查询多组已发布的记录单配置模板", notes = "查询多组已发布的记录单配置模板")
    @BussinessLog(value = "查询多组已发布的记录单配置模板", key = BASE_MAPPING + "/queryTemplateSummaryInfoGroups", type = "04")
    public RestResult queryTemplateSummaryInfoGroups(@RequestBody TemplateQueryParamModel templateQueryParamModel) {
        try {
            List<List<TemplateSummaryInfo>> templateSummaryInfoGroups = billConfigService.queryTemplateSummaryInfoGroups(templateQueryParamModel);
            return RestResult.success().setData(templateSummaryInfoGroups);
        } catch (Exception e) {
            return RestResult.fromException(e, logger, "查询已发布的记录单配置模板失败");
        }
    }

    @Autowired
    private IBaseTemplateService baseTemplateService;
    @Autowired
    private IBaseTemplateConfigService baseTemplateConfigService;
    @Autowired
    private IRemoteService remoteService;

    /**
     * 记录单新增获取模板（直接查基础模板表）
     */
    @GetMapping(value = "/queryBaseTemplate")
    @ResponseBody
    public RestResultGeneric<List<BaseTemplateInfo>> queryBaseTemplate(BaseTemplateUniqueKey baseTemplateUniqueKey) {
        try {
            String templateTypeCode = baseTemplateUniqueKey.getTemplateTypeCode();
            Assert.notEmpty(templateTypeCode, "查询基础模板异常：单据名称不能为空");
            List<BaseTemplateConfig> baseTemplateConfigs = selectList(baseTemplateConfigService, eqParam(BaseTemplateConfig::getTemplateTypeCode, templateTypeCode));

            List<TemplateQueryEnum> queryEnumList = baseTemplateConfigs.stream().map(v -> EnumUtils.findEnum(
                TemplateQueryEnum.class,
                Enum::toString,
                v.getQueryCondition()
            )).filter(Objects::nonNull).collect(Collectors.toList());
            if (queryEnumList.size() == 0) {
                return RestResultGeneric.success(new ArrayList<>());
            }
            if (queryEnumList.stream().anyMatch(v -> v != TemplateQueryEnum.MarshalCount)) {
                throw RestRequestException.fatalFail("基础模板暂不支持编组数量以外的条件，请检查数据库配置");
            }
            Supplier<ColumnParam<BaseTemplate>> marshalCountParamGetter = () -> {
                if (queryEnumList.stream().anyMatch(v -> v == TemplateQueryEnum.MarshalCount)) {
                    if (baseTemplateUniqueKey.getMarshalCount() != null) {
                        return eqParam(BaseTemplate::getMarshalCount, baseTemplateUniqueKey.getMarshalCount());
                    } else {
                        Assert.notEmptyInnerFatal(
                            baseTemplateUniqueKey.getTrainsetType(),
                            "查询基础模板异常：当前单据车型不能为空"
                        );
                        int marshalCount = remoteService.getMarshalCountByTrainType(baseTemplateUniqueKey.getTrainsetType());
                        return eqParam(BaseTemplate::getMarshalCount, marshalCount);
                    }
                } else {
                    return null;
                }
            };

            LogicalLinkable<BaseTemplate> criteria = allMatch(
                allMatch(ColumnParamUtil.filterBlankParams(
                    eqParam(BaseTemplate::getTemplateTypeCode, templateTypeCode),
                    marshalCountParamGetter.get()
                ))
            );
            List<BaseTemplate> baseTemplates = selectList(baseTemplateService, criteria);
            List<BaseTemplateInfo> baseTemplateInfoList = baseTemplates.stream().map(
                v -> baseTemplateService.transToBaseTemplateInfo(v)
            ).collect(Collectors.toList());
            return RestResultGeneric.success(baseTemplateInfoList);
        } catch (Exception e) {
            return RestResultGeneric.fromException(e, logger, "获取基础模板失败");
        }
    }

    /**
     * 获取SSJsonFile 文件的内容
     */
    @PostMapping(value = "/getSSJsonFileContent")
    @ResponseBody
    public RestResult getSSJsonFileContent(@RequestParam String templatePath) {
        try {
            if (templatePath == null || templatePath.equals("")) {
                throw new RuntimeException("路径为空");
            }
            return RestResult.success().setData(SsjsonFileUtils.getSsjsonFileContent(templatePath));
        } catch (Exception ex) {
            return RestResult.fromException(ex, logger, "获取ssjson文件内容失败");
        }
    }

    @GetMapping("/transAllData")
    @ResponseBody
    public RestResult transAllData() {
        try {
            for (TemplateSummary templateSummary : templateSummaryService.selectList(null)) {
                String templateId = templateSummary.getTemplateId();
                String newTemplateNo = newNo(templateSummary);
                TemplateSummary update = new TemplateSummary();
                update.setTemplateId(templateId);
                update.setTemplateNo(newTemplateNo);
                templateSummaryService.updateById(update);
            }
            for (TemplateProcess templateProcess : templateProcessService.selectList(null)) {
                String templateId = templateProcess.getTemplateId();
                String newTemplateNo = newNo(templateProcess);
                TemplateProcess update = new TemplateProcess();
                update.setTemplateId(templateId);
                update.setTemplateNo(newTemplateNo);
                templateProcessService.updateById(update);
            }
            return RestResult.success();
        } catch (Exception e) {
            return RestResult.fromException(e, logger, null);
        }
    }

    private String newNo(ConfigTemplateBase template) {
        return BillUtil.generateTemplateNo(template);
    }


}
