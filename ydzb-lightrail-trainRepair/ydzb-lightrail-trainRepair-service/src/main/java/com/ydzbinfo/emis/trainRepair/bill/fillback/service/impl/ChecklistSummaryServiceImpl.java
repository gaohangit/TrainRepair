package com.ydzbinfo.emis.trainRepair.bill.fillback.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.jxdinfo.hussar.common.organutil.OrganUtil;
import com.ydzbinfo.emis.trainRepair.bill.fillback.dao.ChecklistSummaryMapper;
import com.ydzbinfo.emis.trainRepair.bill.fillback.service.IChecklistSummaryService;
import com.ydzbinfo.emis.trainRepair.bill.fillback.utils.*;
import com.ydzbinfo.emis.trainRepair.bill.model.attribute.TemplateTypeInfo;
import com.ydzbinfo.emis.trainRepair.bill.model.bill.*;
import com.ydzbinfo.emis.trainRepair.bill.querymodel.attribute.TemplateQuery;
import com.ydzbinfo.emis.trainRepair.bill.querymodel.attribute.TemplateType;
import com.ydzbinfo.emis.trainRepair.bill.querymodel.checklist.ChecklistSummary;
import com.ydzbinfo.emis.trainRepair.bill.model.templatesummary.TemplateSummaryInfo;
import com.ydzbinfo.emis.trainRepair.common.controller.CommonController;
import com.ydzbinfo.emis.trainRepair.common.model.ConfigParamsModel;
import com.ydzbinfo.emis.trainRepair.common.model.Role;
import com.ydzbinfo.emis.trainRepair.common.model.Unit;
import com.ydzbinfo.emis.trainRepair.common.querymodel.ITaskItemEntityBase;
import com.ydzbinfo.emis.trainRepair.common.querymodel.ITaskPacketEntityBase;
import com.ydzbinfo.emis.trainRepair.common.querymodel.XzyCConfig;
import com.ydzbinfo.emis.trainRepair.common.service.IRemoteService;
import com.ydzbinfo.emis.trainRepair.common.service.IRepairMidGroundService;
import com.ydzbinfo.emis.trainRepair.constant.MarshallingTypeEnum;
import com.ydzbinfo.emis.trainRepair.remotemodel.resume.TrainsetBaseInfo;
import com.ydzbinfo.emis.trainRepair.remotemodel.trainuse.ZtTaskItemEntity;
import com.ydzbinfo.emis.trainRepair.remotemodel.trainuse.ZtTaskPacketEntity;
import com.ydzbinfo.emis.trainRepair.repairworkflow.model.param.WorkerInfo;
import com.ydzbinfo.emis.trainRepair.taskAllot.querymodel.TaskAllotItemEntity;
import com.ydzbinfo.emis.trainRepair.taskAllot.querymodel.TaskAllotPacketEntity;
import com.ydzbinfo.emis.trainRepair.taskAllot.querymodel.TaskAllotPersonEntity;
import com.ydzbinfo.emis.utils.*;
import com.ydzbinfo.emis.utils.mybatisplus.MybatisPlusUtils;
import com.ydzbinfo.emis.utils.mybatisplus.param.ColumnParam;
import com.ydzbinfo.emis.utils.mybatisplus.param.ColumnParamUtil;
import com.ydzbinfo.hussar.system.bsp.organ.SysOrgan;
import lombok.Data;
import org.apache.poi.util.StringUtil;
import org.slf4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static com.ydzbinfo.emis.utils.mybatisplus.MybatisPlusUtils.eqParam;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author 韩旭
 * @since 2021-07-29
 */
@Service
public class ChecklistSummaryServiceImpl extends ServiceImpl<ChecklistSummaryMapper, ChecklistSummary> implements IChecklistSummaryService {
    private ChecklistSummaryMapper checklistSummaryMapper;
    @Autowired
    private IRepairMidGroundService midGroundService;
    @Autowired
    private IRemoteService iRemoteService;
    protected static final Logger logger = getLogger(ChecklistSummaryServiceImpl.class);
    @Autowired
    BillCommon billCommon;
    @Autowired
    private CommonController commonController;


    @Override
    public String getChecklistSource() {
        return getSourceConfig();
    }

    /**
     * 获取记录单来源配置
     *
     * @return 1--计划   0--派工
     */
    private String getSourceConfig() {
        ConfigParamsModel configParamsModel = new ConfigParamsModel();
        configParamsModel.setType("10");
        configParamsModel.setName("CheckListSource");
        List<XzyCConfig> configs = midGroundService.getXzyCConfigs(configParamsModel);
        String checkListSource = "0";
        if (configs.size() > 0) {
            checkListSource = configs.get(0).getParamValue();
        }
        return checkListSource;
    }

    @Override
    public List<OneTwoRepairCheckList> getChecklistSummaryList(QueryCheckListSummary queryCheckListSummary) {
        /**
         * 1、根据配置XZY_C_CONFIG表中S_TYPE=10，S_PARAMNAME=CheckListSource
         *    如果S_PARAMVALUE = 1 根据日计划、班组、获取计划数据
         *    如果S_PARAMVALUE = 0 根据日计划、班组、派工人员获取派工
         * 2、根据日计划获取记录单
         * 3、将日计划和记录单结合，以日计划为准
         * 4、找到日计划中对应不到记录单的数据，获取模板，组织数据，并保存到数据库中
         * 5、返回数据
         */
        List<OneTwoRepairCheckList> resultCheckList = new ArrayList<>();

        //获取配置
        String checkListSource = getSourceConfig();
        List<ChecklistSummary> returnChecklist;
        Map<String, List<TaskAllotPersonEntity>> mapWorkerInfos = new HashMap<>();
       // logger.error("开始获取派工   " +new Date().toString());
        if ("0".equals(checkListSource)) {
            returnChecklist = getPlan(queryCheckListSummary);
        } else {
            //如果当前登录人是工长或者质检，则不过滤派工人员，能够看到所有派工人员数据
            TempChecklistSummaryResult result = getTaskAllot(queryCheckListSummary);
            returnChecklist = result.checklistSummaryList;
            mapWorkerInfos = result.workerInfoList;
        }
        //不是机检记录单管理传进来的参数，去除机检记录单派工数据
        if(returnChecklist != null && returnChecklist.size() > 0 && !QueryCheckListTypeEnum.EQUIPMENT.getValue().equals(queryCheckListSummary.getType())){
            List<ChecklistSummary> lstSummary = returnChecklist.stream().filter(t->MainCycEnum.MAIN_CYC_1.getMainCyc().getTaskRepairCode().equals(t.getMainCyc())).filter(t->"16".equals(t.getPacketType())).collect(Collectors.toList());
            returnChecklist.removeAll(lstSummary);
        }
        //如果是机检一级修，则去除所有非机检一级修的记录单派工数据
        if(returnChecklist != null && returnChecklist.size() > 0 && QueryCheckListTypeEnum.EQUIPMENT.getValue().equals(queryCheckListSummary.getType())){
            List<ChecklistSummary> lstSummary = returnChecklist.stream().filter(t-> !"16".equals(t.getPacketType())).collect(Collectors.toList());
            returnChecklist.removeAll(lstSummary);
        }
   //     logger.error("结束获取派工   " +new Date().toString());
   //     logger.error("开始获取记录单   " +new Date().toString());
        //获取记录单  记录单获取时，不进行状态过滤和人员过滤，因为是以计划或者派工为准，需要把数据组织完成后，统一过滤
        List<ChecklistSummary> checklistSummaryList = getCheckList(queryCheckListSummary);
    //    logger.error("结束获取记录单   " +new Date().toString());
        //返回的单据
        List<ChecklistSummary> returnSummary = new ArrayList<>();
    //    logger.error("开始获取模板类型   " +new Date().toString());
        //根据单据模板父类型获取子类型
        List<TemplateType> templateTypeList = billCommon.getTemplateTypeCache();
   //     logger.error("结束获取模板类型   " +new Date().toString());
        //根据计划或者派工找记录单
        //如果存在，比较辆序或者部件，如果不一致，更新记录表中的辆序或者部件信息，然后将结果直接放到返回结果中
        // 如果不存在记录单，则获取模板并且插入到数据库回填总表中，并且增加到返回结果中
        for (ChecklistSummary summary : returnChecklist) {
            //循环模板类型
            List<TemplateType> findTemplateTypes = new ArrayList<TemplateType>();
            String filterTemplateType = null;
            // 根据段编码、所编码、模板类型、车型、批次、项目类型、项目CODE创建模板字典
            if (summary.getMainCyc() != null && !QueryCheckListTypeEnum.EQUIPMENT.getValue().equals(queryCheckListSummary.getType())) {
                MainCycEnum mainCycEnum = EnumUtils.findEnum(MainCycEnum.class, (v) -> {
                    return v.getMainCyc().getTaskRepairCode();
                }, summary.getMainCyc());
                if (mainCycEnum != null) {
                    SummaryTemplateTypeEnum summaryTemplateTypeEnum = EnumUtils.findEnum(SummaryTemplateTypeEnum.class, (v) -> {
                        return v.getMainCycEnum();
                    }, mainCycEnum);
                    filterTemplateType = summaryTemplateTypeEnum.getValue();
                }
                String finalFilterTemplateType = filterTemplateType;
                //查找单据子类型下所有符合条件的单据名称
                findTemplateTypes = templateTypeList.stream().filter((t) -> t.getFatherTypeCode().equals(finalFilterTemplateType))
                        .filter((t) -> t.getItemType().equals(summary.getItemType()) || t.getItemType().isEmpty()).collect(Collectors.toList());
                List<TemplateType> templateTypes = findTemplateTypes.stream().filter(t->TemplateTypeNameEnum.RECORD_FIRST_EQUIPMENT_KEEP.getValue().equals(t.getTemplateTypeCode())).collect(Collectors.toList());
                if(templateTypes.size() > 0){
                    findTemplateTypes.remove(templateTypes.get(0));
                }
                //机检一级修
            }else if(summary.getMainCyc() != null && queryCheckListSummary.getType().equals(QueryCheckListTypeEnum.EQUIPMENT.getValue())){
                findTemplateTypes = templateTypeList.stream().filter((t) -> TemplateTypeNameEnum.RECORD_FIRST_EQUIPMENT_KEEP.getValue().equals(t.getTemplateTypeCode())).collect(Collectors.toList());
            }

            //所有单据子类型下的单据名称必须统一到项目或者不到项目  避免像二级修，如果单据部分到项目，部分不到项目，则无法分辨应该属于哪钟单据
            if (findTemplateTypes.size() > 0) {
                List<TemplateType> existItemTemplateType = new ArrayList<>();  //存在项目的类型
                List<TemplateType> noExistItemTemplateType = new ArrayList<>();//不存在项目的类型
                for (TemplateType type : findTemplateTypes) {
                    TemplateTypeInfo templateTypeInfo = billCommon.getTemplateTypeInfoCache(type.getTemplateTypeCode());
                    if (templateTypeInfo.getLinkQueries() != null) {
                        List<TemplateQuery> templateQueries = templateTypeInfo.getLinkQueries().stream().filter((TemplateQuery t) -> "Item".equals(t.getQueryCode())).collect(Collectors.toList());
                        if (templateQueries.size() > 0) {
                            existItemTemplateType.add(templateTypeInfo);
                        } else {
                            noExistItemTemplateType.add(templateTypeInfo);
                        }
                    } else {
                        noExistItemTemplateType.add(templateTypeInfo);
                    }
                }
                boolean isItemCodeVaildate = false;
                if (existItemTemplateType.size() > 0) {
                    findTemplateTypes = existItemTemplateType;
                    isItemCodeVaildate = true;
                } else {
                    findTemplateTypes = noExistItemTemplateType;
                    isItemCodeVaildate = false;
                }

                for (TemplateType type : findTemplateTypes) {
                    //查找每个任务对应的记录单类型，是否存在
                    List<ChecklistSummary> filterSummary = checklistSummaryList.stream().filter((ChecklistSummary t) -> t.getDayPlanId().equals(summary.getDayPlanId()))
                        .filter((ChecklistSummary t) -> t.getTrainsetId().equals(summary.getTrainsetId()))
                        .filter((ChecklistSummary t) -> t.getDeptCode().equals(summary.getDeptCode()))
                        .filter((ChecklistSummary t) -> t.getItemCode().equals(summary.getItemCode()))
                        .filter((ChecklistSummary t) -> t.getUnitCode().equals(summary.getUnitCode()))
                        .filter((ChecklistSummary t) -> t.getItemType().equals(summary.getItemType()))
                        .filter((ChecklistSummary t) -> t.getTemplateType().equals(type.getTemplateTypeCode())).collect(Collectors.toList());
                    if (filterSummary.size() > 0) {
                        for (ChecklistSummary summaryTemp : filterSummary) {
                            //如果回填人员条件不为空，则只显示满足回填人员条件的数据
                            if (StringUtils.hasText(queryCheckListSummary.getFillWorkCode())) {
                                int count = 0;
                                if(StringUtils.hasText(summaryTemp.getFillWorkCode())){
                                    count = Arrays.stream(summaryTemp.getFillWorkCode().split(",")).filter((t) -> t.equals(queryCheckListSummary.getFillWorkCode())).collect(Collectors.toList()).size();
                                }
                                if (count == 0) {
                                    continue;
                                }
                            }
                            //对辆序和部件进行排序，然后进行字符串比较，比较是否相同，如果不一致，更新记录表中的辆序或者部件信息，然后将结果直接放到返回结果中
                            String returnCar = "";
                            String tempCar = "";
                            String returnPart = "";
                            String tempPart = "";
                            if (!summary.getCar().isEmpty()) {
                                String[] returnFilterStrList = arraySort(summary.getCar().split(","));
                                String[] tempFilterStrList = arraySort(summaryTemp.getCar().split(","));
                                returnCar = StringUtil.join(returnFilterStrList, ",");
                                tempCar = StringUtil.join(tempFilterStrList, ",");
                            }
                            if (!summary.getPart().isEmpty()) {
                                String[] returnFilterPartList = arraySort(summary.getPart().split(","));
                                String[] tempFilterPartList = arraySort(summaryTemp.getPart().split(","));
                                returnPart = StringUtil.join(returnFilterPartList, ",");
                                tempPart = StringUtil.join(tempFilterPartList, ",");
                            }
                            //判断空  同一个项目一定有辆序或者有部件
                            if (!returnCar.equals(tempCar) || !returnPart.equals(tempPart)) {
                                summaryTemp.setCar(returnCar);
                                summaryTemp.setPart(returnPart);
                                this.baseMapper.updateAllColumnById(summaryTemp);
                            }
                            //判断当前项目是否存在，如果存在，移除
                            List<ChecklistSummary> filterSummaryTemp = returnSummary.stream().filter((ChecklistSummary t) -> t.getDayPlanId().equals(summaryTemp.getDayPlanId()))
                                .filter((ChecklistSummary t) -> t.getTrainsetId().equals(summaryTemp.getTrainsetId()))
                                .filter((ChecklistSummary t) -> t.getDeptCode().equals(summaryTemp.getDeptCode()))
                                .filter((ChecklistSummary t) -> t.getItemCode().equals(summaryTemp.getItemCode()))
                                .filter((ChecklistSummary t) -> t.getUnitCode().equals(summaryTemp.getUnitCode()))
                                .filter((ChecklistSummary t) -> t.getItemType().equals(summaryTemp.getItemType())).collect(Collectors.toList());
                            if (filterSummaryTemp.size() > 0) {
                                //如果存在类型为空的该项目，则移除该项目，如果类型不为空，并且类型已经存在，直接忽略，否则增加此项目
                                if (filterSummaryTemp.get(0) == null || StringUtils.hasText(filterSummaryTemp.get(0).getTemplateType()) == false) {
                                    returnSummary.remove(filterSummaryTemp.get(0));
                                    returnSummary.addAll(filterSummary);
                                } else {
                                    int size = filterSummaryTemp.stream().filter(t -> t.getTemplateType().equals(type.getTemplateTypeCode())).collect(Collectors.toList()).size();
                                    if (size == 0) {
                                        returnSummary.addAll(filterSummary);
                                    }
                                }
                            } else {
                                returnSummary.addAll(filterSummary);
                            }
                        }
                    } else {
     //                   logger.error("开始创建记录单   " +new Date().toString());
                        //创建记录单
                        createSummaryByTask(summary, type, returnSummary, isItemCodeVaildate);
     //                   logger.error("结束创建记录单   " +new Date().toString());
                    }
                }
            }
        }

        //将结果集合转换实体
        resultCheckList = ConvertEntityByChecklistSummary(returnSummary,mapWorkerInfos);
        //过滤查询条件  回填人、回填状态
        String fillState = queryCheckListSummary.getBackState();
        String fillWorks = queryCheckListSummary.getFillWorkCode();
        if (StringUtils.hasText(fillState)) {
            if (FillStateEnum.FillState_CYC_0.getFillState().getStateCode().equals(fillState) || FillStateEnum.FillState_CYC_1.getFillState().getStateCode().equals(fillState)) {
                resultCheckList = resultCheckList.stream().filter((OneTwoRepairCheckList t) -> t.getFillStateCode().equals(fillState)==true).collect(Collectors.toList());
            } else if (FillStateEnum.FillState_CYC_2.getFillState().getStateCode().equals(fillState)) {
                resultCheckList = resultCheckList.stream().filter((OneTwoRepairCheckList t) ->  StringUtils.hasText(t.getForemanNames())==true).collect(Collectors.toList());
            } else if (FillStateEnum.FillState_CYC_3.getFillState().getStateCode().equals(fillState)) {
                resultCheckList = resultCheckList.stream().filter((OneTwoRepairCheckList t) ->  StringUtils.hasText(t.getQualityNames())==true).collect(Collectors.toList());
            }
        }
        if (StringUtils.hasText(fillWorks)) {
            resultCheckList = resultCheckList.stream().filter((t) -> StringUtils.hasText(t.getFillWorkerCodes()) && t.getFillWorkerCodes().contains(fillWorks)).collect(Collectors.toList());
        }
        Comparator<OneTwoRepairCheckList> configuredComparator = (checkListA, checkListB) -> {
            boolean isAConfigured = StringUtils.hasText(checkListA.getTemplateId());
            boolean isBConfigured = StringUtils.hasText(checkListB.getTemplateId());
            if (isAConfigured == isBConfigured) {
                return 0;
            } else {
                return isAConfigured ? -1 : 1;
            }
        };

        Comparator<OneTwoRepairCheckList> itemNameComparator = Comparator.comparing(OneTwoRepairCheckList::getItemName);
        Comparator<OneTwoRepairCheckList> trainsetNameComparator = Comparator.comparing(OneTwoRepairCheckList::getTrainsetName);
        //排序 按照已配置、未配置，项目名称排序
        resultCheckList.sort((checkListA, checkListB) -> {
            int r1 = configuredComparator.compare(checkListA, checkListB);
            if (r1 != 0) {
                return r1;
            }
            int r2 = trainsetNameComparator.compare(checkListA, checkListB);
            if (r2 != 0) {
                return r2;
            }
            return itemNameComparator.compare(checkListA, checkListB);
        });
        return resultCheckList;
    }

    //排序
    private String[] arraySort(String[] input) {
        for (int i = 0; i < input.length - 1; i++) {
            for (int j = 0; j < input.length - i - 1; j++) {
                if (input[j].compareTo(input[j + 1]) > 0) {
                    String temp = input[j];
                    input[j] = input[j + 1];
                    input[j + 1] = temp;
                }
            }
        }
        return input;
    }

    private List<OneTwoRepairCheckList> ConvertEntityByChecklistSummary(List<ChecklistSummary> summayList,Map<String, List<TaskAllotPersonEntity>> mapWorkerInfos) {
        List<OneTwoRepairCheckList> returnCheckList = new ArrayList<>();
        for (ChecklistSummary checkList : summayList) {
            OneTwoRepairCheckList oneTwoRepair = new OneTwoRepairCheckList();
            oneTwoRepair.setFillStateCode(checkList.getBackFillType());
            oneTwoRepair.setChecklistSummaryId(checkList.getChecklistSummaryId());
            oneTwoRepair.setDayPlanId(checkList.getDayPlanId());
            oneTwoRepair.setDeptCode(checkList.getDeptCode());
            oneTwoRepair.setDeptName(checkList.getDeptName());
            String stateName = "未完全回填";
            if (FillStateEnum.FillState_CYC_1.getFillState().getStateCode().equals(checkList.getBackFillType())) {
                stateName = "完全回填";
            }
            oneTwoRepair.setFillStateName(stateName);
            oneTwoRepair.setItemCode(checkList.getItemCode());
            oneTwoRepair.setItemName(checkList.getItemName());
            oneTwoRepair.setItemType(checkList.getItemType());
            oneTwoRepair.setFillWorkerCodes(checkList.getFillWorkCode());
            oneTwoRepair.setFillWorkers(checkList.getFillWorkName());
            oneTwoRepair.setForemanNames(checkList.getForemanSignName());
            // String mainCycName = EnumUtils.findEnum("");
            String mainCycName = "";
            //根据段编码、所编码、模板类型、车型、批次、项目类型、项目CODE创建模板字典
            if (checkList.getMainCyc() != null) {
                MainCycEnum mainCycEnum = EnumUtils.findEnum(MainCycEnum.class, (v) -> {
                    return v.getMainCyc().getTaskRepairCode();
                }, checkList.getMainCyc());
                if (mainCycEnum != null) {
                    mainCycName = mainCycEnum.getMainCyc().getTaskRepairName();
                }
            }
            oneTwoRepair.setMainCyc(mainCycName);
            oneTwoRepair.setUnitCode(checkList.getUnitCode());
            oneTwoRepair.setUnitName(checkList.getUnitName());
            Integer marshallingType = checkList.getMarshallingType();
            if (marshallingType == null) {
                marshallingType = MarshallingTypeEnum.ALL.getValue();
            }
            oneTwoRepair.setMarshallingType(MarshallingTypeEnum.getLabelByKey(marshallingType));
            oneTwoRepair.setQualityNames(checkList.getQualitySignName());
            String templateId = checkList.getTemplateId();
            oneTwoRepair.setTemplateId(templateId);
            oneTwoRepair.setTemplateNo(checkList.getTemplateNo());
            String templateTypeCode = checkList.getTemplateType();
            oneTwoRepair.setTemplateType(templateTypeCode);
            String templateTypeName = "";
            List<TemplateType> templateTypeList = billCommon.getTemplateTypeCache();
            List<TemplateType> filter = templateTypeList.stream().filter((t) -> t.getTemplateTypeCode().equals(templateTypeCode)).collect(Collectors.toList());
            if (filter.size() > 0) {
                templateTypeName = filter.get(0).getTemplateTypeName();
            }
            oneTwoRepair.setTemplateTypeName(templateTypeName);
            String trainsetId = checkList.getTrainsetId();
            oneTwoRepair.setTrainsetId(trainsetId);
            TrainsetBaseInfo info = billCommon.getTrainsetInfo(trainsetId);
            if (info != null) {
                oneTwoRepair.setTrainsetName(info.getTrainsetname());
            }
            if (!StringUtils.hasText(templateId)) {
                oneTwoRepair.setTemplateExisit("未配置");
            } else {
                oneTwoRepair.setTemplateExisit("已配置");
            }
            //增加派工人员
            String key = trainsetId + "_" + checkList.getItemType() + "_" + checkList.getItemCode();
            if (mapWorkerInfos.containsKey(key)) {
                List<TaskAllotPersonEntity> personEntities = mapWorkerInfos.get(key);
                /**
                 * String taskPerson = "";
                 *                 for (TaskAllotPersonEntity entity :personEntities) {
                 *                     if (taskPerson.length() == 0) {
                 *                         taskPerson = entity.getWorkerName();
                 *                     } else
                 *                     {
                 *                         taskPerson = taskPerson + "," + entity.getWorkerName();
                 *                     }
                 *                 }
                 */
                String taskPerson = "";
                List<String> lstPersons = new ArrayList<String>();
                for (TaskAllotPersonEntity entity :personEntities){
                    if(!StringUtils.isEmpty(entity.getWorkerName()) && !lstPersons.contains(entity.getWorkerName())){
                        lstPersons.add(entity.getWorkerName());
                    }
                }
                taskPerson = String.join(",", lstPersons);
                oneTwoRepair.setTaskAllotWorkers(taskPerson);
            }
            returnCheckList.add(oneTwoRepair);
        }
        return returnCheckList;
    }



    //创建记录单
    synchronized private List<ChecklistSummary> createSummaryByTask(ChecklistSummary summary,TemplateType type,List<ChecklistSummary> returnSummary,boolean isItemCodeVaildate) {
        try {
            String dayPlanId = summary.getDayPlanId();
            String depotCode = UserUtil.getUserInfo().getDepot().getCode();
            String deptCode = summary.getDeptCode();
            String unitCode = summary.getUnitCode();
            String trainsetId = summary.getTrainsetId();
            TrainsetBaseInfo info = billCommon.getTrainsetInfo(trainsetId);
            String trainType = "";
            String trainBatch = "";
            if (info != null) {
                trainType = info.getTraintype();
                trainBatch = info.getTraintempid();
            }
            String itemType = summary.getItemType();
            String itemCode = summary.getItemCode();

            Supplier<ChecklistSummary> getSummaryCopy = () -> {
                ChecklistSummary summary1 = new ChecklistSummary();
                BeanUtils.copyProperties(summary, summary1);
                summary1.setDayPlanId(dayPlanId);
                summary1.setTrainsetId(trainsetId);
                summary1.setDeptCode(deptCode);
                summary1.setChecklistSummaryId(UUID.randomUUID().toString());
                checkAndInitAccuMile(summary1, trainsetId, dayPlanId);
                checkAndInitUnitName(summary1, summary.getUnitCode(), depotCode);
                return summary1;
            };
            //根据项目类型，查找该项目的关联条件是否存在项目

            if (isItemCodeVaildate == false) {
                itemCode = "";
            }

            TemplateTypeInfo templateTypeInfo = billCommon.getTemplateTypeInfoCache(type.getTemplateTypeCode());
            if (templateTypeInfo.getLinkQueries() != null) {
                //处理车型条件
                List<TemplateQuery> templateQueriesTrainsetType = templateTypeInfo.getLinkQueries().stream().filter((TemplateQuery t) -> "TrainsetType".equals(t.getQueryCode())).collect(Collectors.toList());
                if (templateQueriesTrainsetType.size() == 0) {
                    trainType = "";
                }
                //处理批次条件
                List<TemplateQuery> templateQueriesTrainBatch = templateTypeInfo.getLinkQueries().stream().filter((TemplateQuery t) -> "Batch".equals(t.getQueryCode())).collect(Collectors.toList());
                if (templateQueriesTrainBatch.size() == 0) {
                    trainBatch = "";
                }
                //处理运用所条件
                List<TemplateQuery> templateQueriesUnitCode = templateTypeInfo.getLinkQueries().stream().filter((TemplateQuery t) -> "Unit".equals(t.getQueryCode())).collect(Collectors.toList());
                if (templateQueriesUnitCode.size() == 0) {
                    unitCode = "";
                }
            }

        //    logger.error("开始获取模板   " + type.getTemplateTypeCode()+"    "+new Date().toString());
            List<TemplateSummaryInfo> list = new ArrayList<TemplateSummaryInfo>();
            List<String> allCarNos = billCommon.getTrainMarshlType(info.getTrainsetid());
            if(TemplateTypeNameEnum.RECORD_FIRST_EQUIPMENT_KEEP.getValue().equals(templateTypeInfo.getTemplateTypeCode())){
                list =  billCommon.getTemplateCache("", "", type.getTemplateTypeCode(), "", "", "", "", allCarNos.size());
            }
            else{
                list =  billCommon.getTemplateCache(depotCode, unitCode, type.getTemplateTypeCode(), trainType, trainBatch, itemType, itemCode, 0);
            }
      //      logger.error("结束获取模板   " + type.getTemplateTypeCode()+"    "+new Date().toString());
            List<ChecklistSummary> insertBatchSummary = new ArrayList<>();
            //没有找到相关模板
            if (list.size() == 0) {
                //因为项目可能存在没有配置单据，并且有多种类型的情况
                // 如果是关联项目的，则每个项目只能对应其中一种类型的单据
                //如果结果中不存在数据，则增加一条，如果结果中已经存在数据，则忽略
                List<ChecklistSummary> filterSummary = returnSummary.stream().filter((ChecklistSummary t) -> t.getDayPlanId().equals(summary.getDayPlanId()))
                    .filter((ChecklistSummary t) -> t.getTrainsetId().equals(summary.getTrainsetId()))
                    .filter((ChecklistSummary t) -> t.getDeptCode().equals(summary.getDeptCode()))
                    .filter((ChecklistSummary t) -> t.getItemCode().equals(summary.getItemCode()))
                    .filter((ChecklistSummary t) -> t.getUnitCode().equals(summary.getUnitCode()))
                    .filter((ChecklistSummary t) -> t.getItemType().equals(summary.getItemType())).collect(Collectors.toList());
                if (filterSummary.size() == 0) {
                    ChecklistSummary summary1 = getSummaryCopy.get();
                    returnSummary.add(summary1);
                }
            } else {    //存在模板
                boolean insertFlag = true;
                if (isItemCodeVaildate) {
                    List<ChecklistSummary> filterSummary = returnSummary.stream().filter((ChecklistSummary t) -> t.getDayPlanId().equals(summary.getDayPlanId()))
                        .filter((ChecklistSummary t) -> t.getTrainsetId().equals(summary.getTrainsetId()))
                        .filter((ChecklistSummary t) -> t.getDeptCode().equals(summary.getDeptCode()))
                        .filter((ChecklistSummary t) -> t.getItemCode().equals(summary.getItemCode()))
                        .filter((ChecklistSummary t) -> t.getUnitCode().equals(summary.getUnitCode()))
                        .filter((ChecklistSummary t) -> t.getItemType().equals(summary.getItemType())).collect(Collectors.toList());
                    if (filterSummary.size() > 0) {
                        //如果存在类型为空的该项目，则移除该项目，如果类型不为空，直接忽略
                        if (filterSummary.get(0) == null || StringUtils.hasText(filterSummary.get(0).getTemplateType())==false) {
                            returnSummary.remove(filterSummary.get(0));
                        } else {
                            insertFlag = false;
                        }
                    }
                }
                if (insertFlag) {
                    for (TemplateSummaryInfo templateSummaryInfo : list) {
                        ChecklistSummary summary1 = getSummaryCopy.get();
                        initByTemplateSummaryInfo(summary1, templateSummaryInfo);
                        checkAndInitAccuMile(summary1, summary1.getTrainsetId(), summary1.getDayPlanId());
                        summary1.setCreateTime(new Date());
                        summary1.setCreateStaffCode(UserUtil.getUserInfo().getStaffId());
                        summary1.setCreateStaffName(UserUtil.getUserInfo().getName());
                        summary1.setItemTaskId(""); //因镟修单据16编组数据过多，导致超出字段长度，所以置为空字符串
                        returnSummary.add(summary1);
                        insertBatchSummary.add(summary1);
                    }
           //         logger.error("开始插入数据   " + insertBatchSummary.size()+"    "+new Date().toString());
                    boolean flag = this.insertBatch(insertBatchSummary);
          //          logger.error("结束插入数据   " + insertBatchSummary.size()+"    "+new Date().toString());
                    if (!flag) {
                        logger.info("创建记录单失败! 车组:" + summary.getTrainsetId() + ",项目:" + summary.getItemName());
                    }
                }
            }
        } catch (Exception exc) {
            logger.error("创建记录单失败! 车组:" + summary.getTrainsetId() + ",项目:" + summary.getItemName(), exc);
            returnSummary = new ArrayList<>();
        }
        return returnSummary;
    }



    /**
     * 组织计划数据
     *
     * @return
     */
    private List<ChecklistSummary> getPlan(QueryCheckListSummary queryCheckListSummary) {
        String mainCyc = queryCheckListSummary.getMainCyc();
        if (mainCyc == null) {
            mainCyc = "";
        }
        List<ChecklistSummary> list = new ArrayList<>();

        //获取计划
        List<ZtTaskPacketEntity> taskPacketEntities =
            iRemoteService.getPacketTaskByCondition(queryCheckListSummary.getDayPlanId(),
                queryCheckListSummary.getTrainsetId(), "", queryCheckListSummary.getDeptCode(),
                queryCheckListSummary.getUnitCode());
        for (ZtTaskPacketEntity packetEntity : taskPacketEntities) {
            //过滤作业包不满足条件的，处理过滤任务修程，判定该修程下的任务是否能够继续执行
            if (mainCyc.isEmpty()) {
                mainCyc = packetEntity.getTaskRepairCode();
            }
            boolean flag = DisposeFilterTask(mainCyc, packetEntity.getPacketTypeCode(), packetEntity.getTaskRepairCode());
            if (!flag)
                continue;

            //组织数据
            for (ZtTaskItemEntity taskItemEntity : packetEntity.getLstTaskItemInfo()) {
                //如果一个项目在多个作业包中出现，则进行项目合并，该项目的作业包CODE，作业包NAME，作业包任务ID，作业项目任务ID合并
                String itemCode = taskItemEntity.getItemCode();
                String taskItemId = taskItemEntity.getTaskItemId();
                String carNo = taskItemEntity.getCarNo();
                String partId = taskItemEntity.getPartId();
                String dayPlanId = packetEntity.getDayplanId();
                String trainsetId = packetEntity.getTrainsetId();
                String deptCode = packetEntity.getRepairDeptCode();
                String unitCode = packetEntity.getDepotCode();
                try {
                    String depotCode = UserUtil.getUserInfo().getDepot().getCode();
                    ChecklistSummary findSummary = list.stream().
                        filter((ChecklistSummary t) -> t.getTrainsetId().equals(trainsetId)).
                        filter((ChecklistSummary t) -> t.getItemCode().equals(itemCode)).findFirst().orElse(null);
                    //如果车组和项目组合条件不存在，证明该项目是第一次添加
                    if (findSummary == null) {
                        ChecklistSummary summary = new ChecklistSummary();
                        initByTaskInfo(summary, packetEntity, taskItemEntity);
                        checkAndInitAccuMile(summary, trainsetId, dayPlanId);
                        checkAndInitUnitName(summary, unitCode, depotCode);
                        summary.setMainCyc(mainCyc);
                        list.add(summary);
                    } else {
                         DisposeFilterSummary(findSummary, packetEntity.getTaskId(), taskItemEntity.getTaskItemId(), packetEntity.getPacketCode(),
                            packetEntity.getPacketName(), taskItemEntity.getCarNo(), taskItemEntity.getPartId());
                    }
                } catch (Exception exc) {
                    String errorNote = "组织计划数据出现问题,车组:" + trainsetId + " ,班组:" + deptCode + " ,项目:" + itemCode + " ,项目任务ID:" + taskItemId + " ,辆序:" + carNo + " ,部件:" + partId;
                    logger.error(errorNote, exc);
                }
            }
        }
        return list;
    }

    //处理过滤任务修程
    private boolean DisposeFilterTask(String mainCyc, String packetTypeCode, String packetMainCyc) {
        if (MainCycEnum.MAIN_CYC_1.getMainCyc().getTaskRepairCode().equals(mainCyc)) {
            if (!"1".equals(packetMainCyc))
                return false;
        } else if (MainCycEnum.MAIN_CYC_2.getMainCyc().getTaskRepairCode().equals(mainCyc)) {
            if (!"1".equals(packetTypeCode) || !"2".equals(packetMainCyc))
                return false;
        } else if (MainCycEnum.MAIN_CYC_TEMP.getMainCyc().getTaskRepairCode().equals(mainCyc)) {
            if (!"3".equals(packetTypeCode))
                return false;
        } else if (StringUtils.hasText(mainCyc) == false) {
            return false;
        }
        return true;
    }

    //处理合并数据
    private ChecklistSummary DisposeFilterSummary(ChecklistSummary findSummary, String taskId, String taskItemId, String packetCode, String packetName, String carNo, String partId) {
        String summaryTaskId = findSummary.getTaskId();
        String summaryTaskItemId = findSummary.getItemTaskId();
        String summaryPacketCode = findSummary.getPacketCode();
        String summaryPacketName = findSummary.getPacketName();
        String summaryCarNo = findSummary.getCar();
        String summarypartId = findSummary.getPart();
        if(StringUtils.hasText(summaryTaskId))
        {
            List<String> filter = Arrays.stream(summaryTaskId.split(",")).filter((t)->t.equals(taskId)).collect(Collectors.toList());
            if (filter.size()==0) {
                findSummary.setTaskId(summaryTaskId + "," + taskId);
            }
        }else {
            findSummary.setTaskId(taskId);
        }
        if(StringUtils.hasText(summaryTaskItemId)) {
            List<String> filter = Arrays.stream(summaryTaskItemId.split(",")).filter((t)->t.equals(taskItemId)).collect(Collectors.toList());
            if (filter.size()==0) {
                findSummary.setItemTaskId(summaryTaskItemId + "," + taskItemId);
            }
        }else
        {
            findSummary.setItemTaskId(taskItemId);
        }
        if(StringUtils.hasText(summaryPacketCode)) {
            List<String> filter = Arrays.stream(summaryPacketCode.split(",")).filter((t)->t.equals(packetCode)).collect(Collectors.toList());
            if (filter.size()==0) {
                findSummary.setPacketCode(summaryPacketCode + "," + packetCode);
                findSummary.setPacketName(summaryPacketName + "," + packetName);
            }
        }else
        {
            findSummary.setPacketCode(packetCode );
            findSummary.setPacketName(packetName);
        }
        //判断空  同一个项目一定有辆序或者有部件
        if(StringUtils.hasText(summaryPacketCode)) {
            List<String> filter = Arrays.stream(summaryCarNo.split(",")).filter((t)->t.equals(carNo)).collect(Collectors.toList());
            if (filter.size()==0) {
                findSummary.setCar(summaryCarNo + "," + carNo);
            }
        }else
        {
            findSummary.setCar(summaryCarNo);
        }
        if (!summarypartId.isEmpty() && !summarypartId.equals(partId)) {
            findSummary.setPart(summarypartId + ","+ partId);
        }
        return findSummary;
    }

    /**
     * 检查并初始化记录单走行公里
     * @param checklistSummary
     * @param trainsetId
     * @param dayPlanId
     * @author 张天可
     */
    private void  checkAndInitAccuMile(ChecklistSummary checklistSummary, String trainsetId, String dayPlanId) {
        if (!StringUtils.hasText(checklistSummary.getAccuMile())) {
            //checklistSummary.setAccuMile(String.valueOf(billCommon.getAccumile(trainsetId, dayPlanId)));
        }
    }

    /**
     * 检查并初始化记录单的运用所名称
     *
     * @param checklistSummary
     * @param unitCode
     * @param depotCode
     * @author 张天可
     */
    private void checkAndInitUnitName(ChecklistSummary checklistSummary, String unitCode, String depotCode) {
        if (!StringUtils.hasText(checklistSummary.getUnitName())) {
            checklistSummary.setUnitName(getUnitNameCache(unitCode, depotCode));
        }
    }

    /**
     * 根据记录单模板信息初始化记录单
     *
     * @param checklistSummary
     * @param templateSummaryInfo
     * @auther 张天可
     */
    private void initByTemplateSummaryInfo(ChecklistSummary checklistSummary, TemplateSummaryInfo templateSummaryInfo) {
        checklistSummary.setTemplateId(templateSummaryInfo.getTemplateId());
        checklistSummary.setTemplateNo(templateSummaryInfo.getTemplateNo());
        checklistSummary.setTemplateType(templateSummaryInfo.getTemplateTypeCode());
        checklistSummary.setMarshallingType(templateSummaryInfo.getMarshallingType());
        checklistSummary.setVersion(templateSummaryInfo.getVersion());
    }

    /**
     * 根据任务信息初始化记录单
     *
     * @param summary
     * @param taskPacketEntity
     * @param taskItemEntity
     * @auther 张天可
     */
    private void initByTaskInfo(ChecklistSummary summary, ITaskPacketEntityBase taskPacketEntity, ITaskItemEntityBase taskItemEntity) {
        String dayPlanId = taskPacketEntity.getDayPlanId();
        String trainsetId = taskPacketEntity.getTrainsetId();
        String taskId = taskPacketEntity.getTaskId();
        String packetCode = taskPacketEntity.getPacketCode();
        String packetName = taskPacketEntity.getPacketName();
        String packetTypeCode = taskPacketEntity.getPacketTypeCode();

        String taskItemId = taskItemEntity.getTaskItemId();
        String itemCode = taskItemEntity.getItemCode();
        String itemName = taskItemEntity.getItemName();
        String itemType = taskItemEntity.getItemType();

        String partId = taskItemEntity.getPartId();
        String carNo = taskItemEntity.getCarNo();

        String deptCode = taskPacketEntity.getRepairDeptCode();
        String deptName = taskPacketEntity.getRepairDeptName();
        String unitCode = taskPacketEntity.getDepotCode();

        summary.setDayPlanId(dayPlanId);
        summary.setTrainsetId(trainsetId);
        summary.setTaskId(taskId);
        summary.setPacketCode(packetCode);
        summary.setPacketName(packetName);
        summary.setPacketType(packetTypeCode);

        summary.setItemTaskId(taskItemId);
        summary.setItemCode(itemCode);
        summary.setItemName(itemName);
        summary.setItemType(itemType);

        summary.setPart(partId);
        summary.setCar(carNo);

        summary.setDeptCode(deptCode);
        summary.setDeptName(deptName);
        summary.setUnitCode(unitCode);

        summary.setBackFillType(FillStateEnum.FillState_CYC_0.getFillState().getStateCode());
        summary.setMarshallingType(MarshallingTypeEnum.ALL.getValue());
    }





    /**
     * 根据单位CODE获取单位名称
     *
     * @return
     */
    private String getUnitNameCache(String unitCode, String depotCode) {
        try {
            Map<String, Unit> unitMap = CacheUtil.getDataUseThreadCache("OrganUtil.getOranListByParentMap_" + depotCode + "_08", () -> {
                List<Unit> unitList = new ArrayList<>();
                List<SysOrgan> sysOrgans = OrganUtil.getOranListByParent(depotCode, "08");
                if (sysOrgans != null) {
                    for (SysOrgan item : sysOrgans) {
                        Unit unit = new Unit();
                        unit.setUnitCode(item.getOrganCode());
                        unit.setUnitName(item.getOrganName());
                        unit.setUnitAbbr(item.getShortName());
                        unitList.add(unit);
                    }
                }
                return CommonUtils.collectionToMap(unitList, Unit::getUnitCode);

            });
            if (unitMap.containsKey(unitCode)) {
                return unitMap.get(unitCode).getUnitName();
            } else {
                return "";
            }

        } catch (Exception ex) {
            logger.error("获取单位名称", ex);
            return "";
        }

    }

    @Data
    static class TempChecklistSummaryResult {
        private List<ChecklistSummary> checklistSummaryList;
        private Map<String,List<TaskAllotPersonEntity>> workerInfoList; //KEY = trainsetid + "_" + itemtype +" _" + itemcode
    }

    /**
     * 组织派工数据
     *
     * @return
     */
    private TempChecklistSummaryResult getTaskAllot(QueryCheckListSummary queryCheckListSummary) {

        Map<String,List<TaskAllotPersonEntity>> workInfoMap =new HashMap<>();
        List<ChecklistSummary> list = new ArrayList<>();
        List<String> trainsetList = new ArrayList<>();
        if (queryCheckListSummary.getTrainsetId() != null && !queryCheckListSummary.getTrainsetId().isEmpty()) {
            trainsetList.add(queryCheckListSummary.getTrainsetId());
        }
        List<String> allotWorkList = new ArrayList<>();
        if (queryCheckListSummary.getAllotWorkCode() != null && !queryCheckListSummary.getAllotWorkCode().isEmpty()) {
            allotWorkList.add(queryCheckListSummary.getAllotWorkCode());
        }
        String repairType = null;

       List<String> branchCodeList = new ArrayList<>();
        if (queryCheckListSummary.getDeptCode() != null && !queryCheckListSummary.getDeptCode().isEmpty()) {
            branchCodeList.add(queryCheckListSummary.getDeptCode());
        }

        List<String> allotPersons = new ArrayList<>();
        //获取派工
        List<TaskAllotPacketEntity> packetEntityList = midGroundService.getTaskAllotDataToPerson(queryCheckListSummary.getUnitCode(),
            queryCheckListSummary.getDayPlanId(), repairType, trainsetList, branchCodeList,allotWorkList, "");
        for (TaskAllotPacketEntity packetEntity : packetEntityList) {
            String mainCyc = queryCheckListSummary.getMainCyc();
            //过滤作业包不满足条件的，处理过滤任务修程，判定该修程下的任务是否能够继续执行
            if (mainCyc == null || mainCyc.isEmpty()) {
                mainCyc = packetEntity.getMainCyc();
            }
            boolean flag = DisposeFilterTask(mainCyc, packetEntity.getPacketType(), packetEntity.getMainCyc());
            if (!flag)
                continue;
            //组织数据
            for (TaskAllotItemEntity taskItemEntity : packetEntity.getTaskAllotItemEntityList()) {
                //如果派工人员ID不为空，则需要增加对派工人员的校验
                if(!StringUtils.isEmpty(queryCheckListSummary.getAllotWorkCode())){
                    List<TaskAllotPersonEntity> taskAllotPersonEntities = taskItemEntity.getTaskAllotPersonEntityList().stream().filter(f->f.getWorkerId().equals(queryCheckListSummary.getAllotWorkCode())).collect(Collectors.toList());
                    //没有在派工人员集合内查询到查询条件中的人员ID，则跳过该任务
                    if(taskAllotPersonEntities.size() == 0){
                        continue;
                    }
                }
                //如果一个项目在多个作业包中出现，则进行项目合并，该项目的作业包CODE，作业包NAME，作业包任务ID，作业项目任务ID合并
                String itemCode = taskItemEntity.getItemCode();
                String itemType = taskItemEntity.getItemType();
                String dayPlanId = packetEntity.getDayPlanId();
                String trainsetId = packetEntity.getTrainsetId();
                String unitCode = packetEntity.getUnitCode();
                String mapKey = trainsetId+"_"+itemType+"_"+itemCode;
                ChecklistSummary findSummary = list.stream().
                    filter((ChecklistSummary t) -> t.getTrainsetId().equals(trainsetId)).
                    filter((ChecklistSummary t) -> t.getItemCode().equals(itemCode)).findFirst().orElse(null);
                //如果车组和项目组合条件不存在，证明该项目是第一次添加
                if (findSummary == null) {
                    String depotCode = UserUtil.getUserInfo().getDepot().getCode();
                    ChecklistSummary summary = new ChecklistSummary();
                    initByTaskInfo(summary, packetEntity, taskItemEntity);
//                    checkAndInitAccuMile(summary, trainsetId, dayPlanId);
                    checkAndInitUnitName(summary, unitCode, depotCode);
                    summary.setMainCyc(mainCyc);
                    list.add(summary);
                    workInfoMap.put(mapKey,taskItemEntity.getTaskAllotPersonEntityList());

                } else {
                    DisposeFilterSummary(findSummary, packetEntity.getTaskId(), taskItemEntity.getTaskItemId(), packetEntity.getPacketCode(),
                        packetEntity.getPacketName(), taskItemEntity.getCarNo(), taskItemEntity.getPartName());
                    if (workInfoMap.containsKey(mapKey)) {
                        List<TaskAllotPersonEntity> personEntities = workInfoMap.get(mapKey);
                        for (TaskAllotPersonEntity personEntity:
                            taskItemEntity.getTaskAllotPersonEntityList()) {
                            TaskAllotPersonEntity pe = personEntities.stream().filter(m -> m.getWorkerId().equals(personEntity.getWorkerId())).findFirst().orElse(null);
                            if (pe == null) {
                                workInfoMap.get(mapKey).add(personEntity);
                            }
                        }
                    } else
                    {
                        workInfoMap.put(mapKey, taskItemEntity.getTaskAllotPersonEntityList());
                    }
                }
            }
        }
        //因镟修探伤16编组数据过多，超出字段长度，导致报错，所以置为空字符串
        for(ChecklistSummary summary : list){
            summary.setItemTaskId("");
        }
        TempChecklistSummaryResult result = new TempChecklistSummaryResult();
        result.setWorkerInfoList(workInfoMap);
        result.setChecklistSummaryList(list);
        return result;
    }

    /**
     *
     * @param personEntities
     * @param workerInfos
     */
    // private void  DisposeWorkerInfo(List<TaskAllotPersonEntity> personEntities,List<WorkerInfo> workerInfos)
    // {
    //     //组织人员
    //     for (TaskAllotPersonEntity person : personEntities) {
    //         WorkerInfo workerInfo = workerInfos.stream().filter((WorkerInfo t) -> t.getWorkerId().equals(person.getWorkerId())).findFirst().orElse(null);
    //         if (workerInfo == null) {
    //             workerInfo = new WorkerInfo();
    //             workerInfo.setWorkerId(person.getWorkerId());
    //             workerInfo.setWorkerName(person.getWorkerName());
    //             workerInfos.add(workerInfo);
    //         }
    //     }
    // }


    /**
     * 根据条件获取记录单数据
     */
    private List<ChecklistSummary> getCheckList(QueryCheckListSummary queryCheckListSummary) {
        List<ChecklistSummary> summaryList;
        List<ColumnParam<ChecklistSummary>> columnParamList = ColumnParamUtil.filterBlankParamList(
            eqParam(ChecklistSummary::getDayPlanId, queryCheckListSummary.getDayPlanId()),
            eqParam(ChecklistSummary::getTrainsetId, queryCheckListSummary.getTrainsetId()),
            eqParam(ChecklistSummary::getDeptCode, queryCheckListSummary.getDeptCode()),
            eqParam(ChecklistSummary::getUnitCode, queryCheckListSummary.getUnitCode())
        );
        if (StringUtils.hasText(queryCheckListSummary.getMainCyc())) {
            columnParamList.add(eqParam(ChecklistSummary::getMainCyc, queryCheckListSummary.getMainCyc()));
        }
        summaryList = MybatisPlusUtils.selectList(this, columnParamList);
        return summaryList;
    }

    @Override
    /**
     * 获取一二级修回填查询条件
     * @return
     */
    public QueryChecklistQueryCondition getChecklistQueryCondition(QueryCheckListSummary queryCheckListSummary,String type) {
        //获取配置
        String checkListSource = getSourceConfig();
        List<WorkerInfo> workerInfos = new ArrayList<>();
        List<ChecklistSummary> returnChecklist = new ArrayList<>();

        // if (checkListSource.equals("0")) {
        //     returnChecklist = getPlan(queryCheckListSummary);
        // } else {
        //     TempChecklistSummaryResult result = getTaskAllot(queryCheckListSummary);
        //     returnChecklist = result.checklistSummaryList;
        // }
        // //循环组织查询条件
         QueryChecklistQueryCondition condition = new QueryChecklistQueryCondition();
        // //车组集合
         List<String> trainsetStrList = new ArrayList<>();
         //区分机检一级修和非机检一级修
         if("equipment".equals(type)) {
             trainsetStrList = midGroundService.getTaskAllotEquipmentTrainsetList(queryCheckListSummary.getUnitCode(),
                     queryCheckListSummary.getDayPlanId(), queryCheckListSummary.getDeptCode());
         }else
         {
             trainsetStrList = midGroundService.getTaskAllotTrainsetList(queryCheckListSummary.getUnitCode(),
                     queryCheckListSummary.getDayPlanId(), queryCheckListSummary.getMainCyc(), queryCheckListSummary.getDeptCode());
         }
        // for (ChecklistSummary summary : returnChecklist) {
        //     if (!trainsetStrList.contains(summary.getTrainsetId())) {
        //         trainsetStrList.add(summary.getTrainsetId());
        //     }
        // }
        //根据车组ID集合获取车组名字集合并且赋值到trainsetList中
        List<TrainsetBaseInfo> trainsetList = new ArrayList<>();
        if (trainsetStrList != null && trainsetStrList.size() > 0) {
            trainsetList = iRemoteService.getTrainsetBaseInfoByIds(trainsetStrList);
            trainsetList.sort(Comparator.comparing(TrainsetBaseInfo::getTrainsetname));
            if(trainsetStrList.size() != trainsetList.size()){
                String msg = "";
                if(trainsetList == null || trainsetList.size() == 0){
                    msg = "集合为空";
                }else{
                    msg = JSONArray.toJSONString(trainsetList);
                }
                logger.error("调用接口 iRemoteService.getTrainsetBaseInfoByIds("+ JSONArray.toJSONString(trainsetStrList) +") 获取到车组信息：" + msg);
            }
        }
        condition.setTrainsetList(trainsetList);
        //修程集合
        List<MainCyc> mainCycs = getMainCycDict();
        List<TemplateType> templateTypeList = billCommon.getTemplateTypeCache();
        List<MainCycWithTypes> withTypes = new ArrayList<>();
        for (MainCyc cyc : mainCycs) {
            MainCycWithTypes withtype = new MainCycWithTypes();
            withtype.setTaskRepairCode(cyc.getTaskRepairCode());
            withtype.setTaskRepairName(cyc.getTaskRepairName());
            List<TemplateType> filtertemplateTypeList = new ArrayList<>();
            if (MainCycEnum.MAIN_CYC_1.getMainCyc().getTaskRepairCode().equals(cyc.getTaskRepairCode())) {
                filtertemplateTypeList = templateTypeList.stream().filter((t) -> "RECORD_FIRST".equals(t.getFatherTypeCode())).collect(Collectors.toList());

            } else if (MainCycEnum.MAIN_CYC_2.getMainCyc().getTaskRepairCode().equals(cyc.getTaskRepairCode())) {
                filtertemplateTypeList = templateTypeList.stream().filter((t) -> "RECORD_SECOND".equals(t.getFatherTypeCode())).collect(Collectors.toList());
            } else if (MainCycEnum.MAIN_CYC_TEMP.getMainCyc().getTaskRepairCode().equals(cyc.getTaskRepairCode())) {
                filtertemplateTypeList = templateTypeList.stream().filter((t) -> "RECORD_TEMP".equals(t.getFatherTypeCode())).collect(Collectors.toList());
            }
            if (filtertemplateTypeList.size() == 0) {
                filtertemplateTypeList = new ArrayList<>();
            }
            withtype.setTemplateTypeList(filtertemplateTypeList);
            withTypes.add(withtype);
        }
        condition.setMainCycList(withTypes);
        //设置回填状态
        condition.setFillState(getFillStateDict());
        return condition;
    }


    //获取修程字典
    private List<MainCyc> getMainCycDict() {
        // return new ArrayList<>();
        return Arrays.stream(MainCycEnum.class.getEnumConstants()).map(MainCycEnum::getMainCyc).collect(Collectors.toList());
    }

    //获取回填状态字典
    private List<FillState> getFillStateDict() {
        return Arrays.stream(FillStateEnum.class.getEnumConstants()).map(FillStateEnum::getFillState).collect(Collectors.toList());
    }
}
