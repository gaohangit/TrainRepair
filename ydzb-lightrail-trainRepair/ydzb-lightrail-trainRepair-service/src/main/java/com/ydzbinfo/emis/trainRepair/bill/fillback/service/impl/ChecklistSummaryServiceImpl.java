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
 * ???????????????
 * </p>
 *
 * @author ??????
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
     * ???????????????????????????
     *
     * @return 1--??????   0--??????
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
         * 1???????????????XZY_C_CONFIG??????S_TYPE=10???S_PARAMNAME=CheckListSource
         *    ??????S_PARAMVALUE = 1 ?????????????????????????????????????????????
         *    ??????S_PARAMVALUE = 0 ???????????????????????????????????????????????????
         * 2?????????????????????????????????
         * 3??????????????????????????????????????????????????????
         * 4????????????????????????????????????????????????????????????????????????????????????????????????????????????
         * 5???????????????
         */
        List<OneTwoRepairCheckList> resultCheckList = new ArrayList<>();

        //????????????
        String checkListSource = getSourceConfig();
        List<ChecklistSummary> returnChecklist;
        Map<String, List<TaskAllotPersonEntity>> mapWorkerInfos = new HashMap<>();
       // logger.error("??????????????????   " +new Date().toString());
        if ("0".equals(checkListSource)) {
            returnChecklist = getPlan(queryCheckListSummary);
        } else {
            //????????????????????????????????????????????????????????????????????????????????????????????????????????????
            TempChecklistSummaryResult result = getTaskAllot(queryCheckListSummary);
            returnChecklist = result.checklistSummaryList;
            mapWorkerInfos = result.workerInfoList;
        }
        //?????????????????????????????????????????????????????????????????????????????????
        if(returnChecklist != null && returnChecklist.size() > 0 && !QueryCheckListTypeEnum.EQUIPMENT.getValue().equals(queryCheckListSummary.getType())){
            List<ChecklistSummary> lstSummary = returnChecklist.stream().filter(t->MainCycEnum.MAIN_CYC_1.getMainCyc().getTaskRepairCode().equals(t.getMainCyc())).filter(t->"16".equals(t.getPacketType())).collect(Collectors.toList());
            returnChecklist.removeAll(lstSummary);
        }
        //????????????????????????????????????????????????????????????????????????????????????
        if(returnChecklist != null && returnChecklist.size() > 0 && QueryCheckListTypeEnum.EQUIPMENT.getValue().equals(queryCheckListSummary.getType())){
            List<ChecklistSummary> lstSummary = returnChecklist.stream().filter(t-> !"16".equals(t.getPacketType())).collect(Collectors.toList());
            returnChecklist.removeAll(lstSummary);
        }
   //     logger.error("??????????????????   " +new Date().toString());
   //     logger.error("?????????????????????   " +new Date().toString());
        //???????????????  ????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
        List<ChecklistSummary> checklistSummaryList = getCheckList(queryCheckListSummary);
    //    logger.error("?????????????????????   " +new Date().toString());
        //???????????????
        List<ChecklistSummary> returnSummary = new ArrayList<>();
    //    logger.error("????????????????????????   " +new Date().toString());
        //??????????????????????????????????????????
        List<TemplateType> templateTypeList = billCommon.getTemplateTypeCache();
   //     logger.error("????????????????????????   " +new Date().toString());
        //????????????????????????????????????
        //??????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
        // ??????????????????????????????????????????????????????????????????????????????????????????????????????????????????
        for (ChecklistSummary summary : returnChecklist) {
            //??????????????????
            List<TemplateType> findTemplateTypes = new ArrayList<TemplateType>();
            String filterTemplateType = null;
            // ????????????????????????????????????????????????????????????????????????????????????CODE??????????????????
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
                //?????????????????????????????????????????????????????????
                findTemplateTypes = templateTypeList.stream().filter((t) -> t.getFatherTypeCode().equals(finalFilterTemplateType))
                        .filter((t) -> t.getItemType().equals(summary.getItemType()) || t.getItemType().isEmpty()).collect(Collectors.toList());
                List<TemplateType> templateTypes = findTemplateTypes.stream().filter(t->TemplateTypeNameEnum.RECORD_FIRST_EQUIPMENT_KEEP.getValue().equals(t.getTemplateTypeCode())).collect(Collectors.toList());
                if(templateTypes.size() > 0){
                    findTemplateTypes.remove(templateTypes.get(0));
                }
                //???????????????
            }else if(summary.getMainCyc() != null && queryCheckListSummary.getType().equals(QueryCheckListTypeEnum.EQUIPMENT.getValue())){
                findTemplateTypes = templateTypeList.stream().filter((t) -> TemplateTypeNameEnum.RECORD_FIRST_EQUIPMENT_KEEP.getValue().equals(t.getTemplateTypeCode())).collect(Collectors.toList());
            }

            //??????????????????????????????????????????????????????????????????????????????  ???????????????????????????????????????????????????????????????????????????????????????????????????????????????
            if (findTemplateTypes.size() > 0) {
                List<TemplateType> existItemTemplateType = new ArrayList<>();  //?????????????????????
                List<TemplateType> noExistItemTemplateType = new ArrayList<>();//????????????????????????
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
                    //?????????????????????????????????????????????????????????
                    List<ChecklistSummary> filterSummary = checklistSummaryList.stream().filter((ChecklistSummary t) -> t.getDayPlanId().equals(summary.getDayPlanId()))
                        .filter((ChecklistSummary t) -> t.getTrainsetId().equals(summary.getTrainsetId()))
                        .filter((ChecklistSummary t) -> t.getDeptCode().equals(summary.getDeptCode()))
                        .filter((ChecklistSummary t) -> t.getItemCode().equals(summary.getItemCode()))
                        .filter((ChecklistSummary t) -> t.getUnitCode().equals(summary.getUnitCode()))
                        .filter((ChecklistSummary t) -> t.getItemType().equals(summary.getItemType()))
                        .filter((ChecklistSummary t) -> t.getTemplateType().equals(type.getTemplateTypeCode())).collect(Collectors.toList());
                    if (filterSummary.size() > 0) {
                        for (ChecklistSummary summaryTemp : filterSummary) {
                            //?????????????????????????????????????????????????????????????????????????????????
                            if (StringUtils.hasText(queryCheckListSummary.getFillWorkCode())) {
                                int count = 0;
                                if(StringUtils.hasText(summaryTemp.getFillWorkCode())){
                                    count = Arrays.stream(summaryTemp.getFillWorkCode().split(",")).filter((t) -> t.equals(queryCheckListSummary.getFillWorkCode())).collect(Collectors.toList()).size();
                                }
                                if (count == 0) {
                                    continue;
                                }
                            }
                            //????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
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
                            //?????????  ?????????????????????????????????????????????
                            if (!returnCar.equals(tempCar) || !returnPart.equals(tempPart)) {
                                summaryTemp.setCar(returnCar);
                                summaryTemp.setPart(returnPart);
                                this.baseMapper.updateAllColumnById(summaryTemp);
                            }
                            //??????????????????????????????????????????????????????
                            List<ChecklistSummary> filterSummaryTemp = returnSummary.stream().filter((ChecklistSummary t) -> t.getDayPlanId().equals(summaryTemp.getDayPlanId()))
                                .filter((ChecklistSummary t) -> t.getTrainsetId().equals(summaryTemp.getTrainsetId()))
                                .filter((ChecklistSummary t) -> t.getDeptCode().equals(summaryTemp.getDeptCode()))
                                .filter((ChecklistSummary t) -> t.getItemCode().equals(summaryTemp.getItemCode()))
                                .filter((ChecklistSummary t) -> t.getUnitCode().equals(summaryTemp.getUnitCode()))
                                .filter((ChecklistSummary t) -> t.getItemType().equals(summaryTemp.getItemType())).collect(Collectors.toList());
                            if (filterSummaryTemp.size() > 0) {
                                //???????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
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
     //                   logger.error("?????????????????????   " +new Date().toString());
                        //???????????????
                        createSummaryByTask(summary, type, returnSummary, isItemCodeVaildate);
     //                   logger.error("?????????????????????   " +new Date().toString());
                    }
                }
            }
        }

        //???????????????????????????
        resultCheckList = ConvertEntityByChecklistSummary(returnSummary,mapWorkerInfos);
        //??????????????????  ????????????????????????
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
        //?????? ????????????????????????????????????????????????
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

    //??????
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
            String stateName = "???????????????";
            if (FillStateEnum.FillState_CYC_1.getFillState().getStateCode().equals(checkList.getBackFillType())) {
                stateName = "????????????";
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
            //????????????????????????????????????????????????????????????????????????????????????CODE??????????????????
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
                oneTwoRepair.setTemplateExisit("?????????");
            } else {
                oneTwoRepair.setTemplateExisit("?????????");
            }
            //??????????????????
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



    //???????????????
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
            //?????????????????????????????????????????????????????????????????????

            if (isItemCodeVaildate == false) {
                itemCode = "";
            }

            TemplateTypeInfo templateTypeInfo = billCommon.getTemplateTypeInfoCache(type.getTemplateTypeCode());
            if (templateTypeInfo.getLinkQueries() != null) {
                //??????????????????
                List<TemplateQuery> templateQueriesTrainsetType = templateTypeInfo.getLinkQueries().stream().filter((TemplateQuery t) -> "TrainsetType".equals(t.getQueryCode())).collect(Collectors.toList());
                if (templateQueriesTrainsetType.size() == 0) {
                    trainType = "";
                }
                //??????????????????
                List<TemplateQuery> templateQueriesTrainBatch = templateTypeInfo.getLinkQueries().stream().filter((TemplateQuery t) -> "Batch".equals(t.getQueryCode())).collect(Collectors.toList());
                if (templateQueriesTrainBatch.size() == 0) {
                    trainBatch = "";
                }
                //?????????????????????
                List<TemplateQuery> templateQueriesUnitCode = templateTypeInfo.getLinkQueries().stream().filter((TemplateQuery t) -> "Unit".equals(t.getQueryCode())).collect(Collectors.toList());
                if (templateQueriesUnitCode.size() == 0) {
                    unitCode = "";
                }
            }

        //    logger.error("??????????????????   " + type.getTemplateTypeCode()+"    "+new Date().toString());
            List<TemplateSummaryInfo> list = new ArrayList<TemplateSummaryInfo>();
            List<String> allCarNos = billCommon.getTrainMarshlType(info.getTrainsetid());
            if(TemplateTypeNameEnum.RECORD_FIRST_EQUIPMENT_KEEP.getValue().equals(templateTypeInfo.getTemplateTypeCode())){
                list =  billCommon.getTemplateCache("", "", type.getTemplateTypeCode(), "", "", "", "", allCarNos.size());
            }
            else{
                list =  billCommon.getTemplateCache(depotCode, unitCode, type.getTemplateTypeCode(), trainType, trainBatch, itemType, itemCode, 0);
            }
      //      logger.error("??????????????????   " + type.getTemplateTypeCode()+"    "+new Date().toString());
            List<ChecklistSummary> insertBatchSummary = new ArrayList<>();
            //????????????????????????
            if (list.size() == 0) {
                //???????????????????????????????????????????????????????????????????????????
                // ?????????????????????????????????????????????????????????????????????????????????
                //????????????????????????????????????????????????????????????????????????????????????????????????
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
            } else {    //????????????
                boolean insertFlag = true;
                if (isItemCodeVaildate) {
                    List<ChecklistSummary> filterSummary = returnSummary.stream().filter((ChecklistSummary t) -> t.getDayPlanId().equals(summary.getDayPlanId()))
                        .filter((ChecklistSummary t) -> t.getTrainsetId().equals(summary.getTrainsetId()))
                        .filter((ChecklistSummary t) -> t.getDeptCode().equals(summary.getDeptCode()))
                        .filter((ChecklistSummary t) -> t.getItemCode().equals(summary.getItemCode()))
                        .filter((ChecklistSummary t) -> t.getUnitCode().equals(summary.getUnitCode()))
                        .filter((ChecklistSummary t) -> t.getItemType().equals(summary.getItemType())).collect(Collectors.toList());
                    if (filterSummary.size() > 0) {
                        //????????????????????????????????????????????????????????????????????????????????????????????????
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
                        summary1.setItemTaskId(""); //???????????????16????????????????????????????????????????????????????????????????????????
                        returnSummary.add(summary1);
                        insertBatchSummary.add(summary1);
                    }
           //         logger.error("??????????????????   " + insertBatchSummary.size()+"    "+new Date().toString());
                    boolean flag = this.insertBatch(insertBatchSummary);
          //          logger.error("??????????????????   " + insertBatchSummary.size()+"    "+new Date().toString());
                    if (!flag) {
                        logger.info("?????????????????????! ??????:" + summary.getTrainsetId() + ",??????:" + summary.getItemName());
                    }
                }
            }
        } catch (Exception exc) {
            logger.error("?????????????????????! ??????:" + summary.getTrainsetId() + ",??????:" + summary.getItemName(), exc);
            returnSummary = new ArrayList<>();
        }
        return returnSummary;
    }



    /**
     * ??????????????????
     *
     * @return
     */
    private List<ChecklistSummary> getPlan(QueryCheckListSummary queryCheckListSummary) {
        String mainCyc = queryCheckListSummary.getMainCyc();
        if (mainCyc == null) {
            mainCyc = "";
        }
        List<ChecklistSummary> list = new ArrayList<>();

        //????????????
        List<ZtTaskPacketEntity> taskPacketEntities =
            iRemoteService.getPacketTaskByCondition(queryCheckListSummary.getDayPlanId(),
                queryCheckListSummary.getTrainsetId(), "", queryCheckListSummary.getDeptCode(),
                queryCheckListSummary.getUnitCode());
        for (ZtTaskPacketEntity packetEntity : taskPacketEntities) {
            //??????????????????????????????????????????????????????????????????????????????????????????????????????????????????
            if (mainCyc.isEmpty()) {
                mainCyc = packetEntity.getTaskRepairCode();
            }
            boolean flag = DisposeFilterTask(mainCyc, packetEntity.getPacketTypeCode(), packetEntity.getTaskRepairCode());
            if (!flag)
                continue;

            //????????????
            for (ZtTaskItemEntity taskItemEntity : packetEntity.getLstTaskItemInfo()) {
                //?????????????????????????????????????????????????????????????????????????????????????????????CODE????????????NAME??????????????????ID?????????????????????ID??????
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
                    //??????????????????????????????????????????????????????????????????????????????
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
                    String errorNote = "??????????????????????????????,??????:" + trainsetId + " ,??????:" + deptCode + " ,??????:" + itemCode + " ,????????????ID:" + taskItemId + " ,??????:" + carNo + " ,??????:" + partId;
                    logger.error(errorNote, exc);
                }
            }
        }
        return list;
    }

    //????????????????????????
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

    //??????????????????
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
        //?????????  ?????????????????????????????????????????????
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
     * ???????????????????????????????????????
     * @param checklistSummary
     * @param trainsetId
     * @param dayPlanId
     * @author ?????????
     */
    private void  checkAndInitAccuMile(ChecklistSummary checklistSummary, String trainsetId, String dayPlanId) {
        if (!StringUtils.hasText(checklistSummary.getAccuMile())) {
            //checklistSummary.setAccuMile(String.valueOf(billCommon.getAccumile(trainsetId, dayPlanId)));
        }
    }

    /**
     * ?????????????????????????????????????????????
     *
     * @param checklistSummary
     * @param unitCode
     * @param depotCode
     * @author ?????????
     */
    private void checkAndInitUnitName(ChecklistSummary checklistSummary, String unitCode, String depotCode) {
        if (!StringUtils.hasText(checklistSummary.getUnitName())) {
            checklistSummary.setUnitName(getUnitNameCache(unitCode, depotCode));
        }
    }

    /**
     * ?????????????????????????????????????????????
     *
     * @param checklistSummary
     * @param templateSummaryInfo
     * @auther ?????????
     */
    private void initByTemplateSummaryInfo(ChecklistSummary checklistSummary, TemplateSummaryInfo templateSummaryInfo) {
        checklistSummary.setTemplateId(templateSummaryInfo.getTemplateId());
        checklistSummary.setTemplateNo(templateSummaryInfo.getTemplateNo());
        checklistSummary.setTemplateType(templateSummaryInfo.getTemplateTypeCode());
        checklistSummary.setMarshallingType(templateSummaryInfo.getMarshallingType());
        checklistSummary.setVersion(templateSummaryInfo.getVersion());
    }

    /**
     * ????????????????????????????????????
     *
     * @param summary
     * @param taskPacketEntity
     * @param taskItemEntity
     * @auther ?????????
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
     * ????????????CODE??????????????????
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
            logger.error("??????????????????", ex);
            return "";
        }

    }

    @Data
    static class TempChecklistSummaryResult {
        private List<ChecklistSummary> checklistSummaryList;
        private Map<String,List<TaskAllotPersonEntity>> workerInfoList; //KEY = trainsetid + "_" + itemtype +" _" + itemcode
    }

    /**
     * ??????????????????
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
        //????????????
        List<TaskAllotPacketEntity> packetEntityList = midGroundService.getTaskAllotDataToPerson(queryCheckListSummary.getUnitCode(),
            queryCheckListSummary.getDayPlanId(), repairType, trainsetList, branchCodeList,allotWorkList, "");
        for (TaskAllotPacketEntity packetEntity : packetEntityList) {
            String mainCyc = queryCheckListSummary.getMainCyc();
            //??????????????????????????????????????????????????????????????????????????????????????????????????????????????????
            if (mainCyc == null || mainCyc.isEmpty()) {
                mainCyc = packetEntity.getMainCyc();
            }
            boolean flag = DisposeFilterTask(mainCyc, packetEntity.getPacketType(), packetEntity.getMainCyc());
            if (!flag)
                continue;
            //????????????
            for (TaskAllotItemEntity taskItemEntity : packetEntity.getTaskAllotItemEntityList()) {
                //??????????????????ID???????????????????????????????????????????????????
                if(!StringUtils.isEmpty(queryCheckListSummary.getAllotWorkCode())){
                    List<TaskAllotPersonEntity> taskAllotPersonEntities = taskItemEntity.getTaskAllotPersonEntityList().stream().filter(f->f.getWorkerId().equals(queryCheckListSummary.getAllotWorkCode())).collect(Collectors.toList());
                    //???????????????????????????????????????????????????????????????ID?????????????????????
                    if(taskAllotPersonEntities.size() == 0){
                        continue;
                    }
                }
                //?????????????????????????????????????????????????????????????????????????????????????????????CODE????????????NAME??????????????????ID?????????????????????ID??????
                String itemCode = taskItemEntity.getItemCode();
                String itemType = taskItemEntity.getItemType();
                String dayPlanId = packetEntity.getDayPlanId();
                String trainsetId = packetEntity.getTrainsetId();
                String unitCode = packetEntity.getUnitCode();
                String mapKey = trainsetId+"_"+itemType+"_"+itemCode;
                ChecklistSummary findSummary = list.stream().
                    filter((ChecklistSummary t) -> t.getTrainsetId().equals(trainsetId)).
                    filter((ChecklistSummary t) -> t.getItemCode().equals(itemCode)).findFirst().orElse(null);
                //??????????????????????????????????????????????????????????????????????????????
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
        //???????????????16?????????????????????????????????????????????????????????????????????????????????
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
    //     //????????????
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
     * ?????????????????????????????????
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
     * ????????????????????????????????????
     * @return
     */
    public QueryChecklistQueryCondition getChecklistQueryCondition(QueryCheckListSummary queryCheckListSummary,String type) {
        //????????????
        String checkListSource = getSourceConfig();
        List<WorkerInfo> workerInfos = new ArrayList<>();
        List<ChecklistSummary> returnChecklist = new ArrayList<>();

        // if (checkListSource.equals("0")) {
        //     returnChecklist = getPlan(queryCheckListSummary);
        // } else {
        //     TempChecklistSummaryResult result = getTaskAllot(queryCheckListSummary);
        //     returnChecklist = result.checklistSummaryList;
        // }
        // //????????????????????????
         QueryChecklistQueryCondition condition = new QueryChecklistQueryCondition();
        // //????????????
         List<String> trainsetStrList = new ArrayList<>();
         //??????????????????????????????????????????
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
        //????????????ID?????????????????????????????????????????????trainsetList???
        List<TrainsetBaseInfo> trainsetList = new ArrayList<>();
        if (trainsetStrList != null && trainsetStrList.size() > 0) {
            trainsetList = iRemoteService.getTrainsetBaseInfoByIds(trainsetStrList);
            trainsetList.sort(Comparator.comparing(TrainsetBaseInfo::getTrainsetname));
            if(trainsetStrList.size() != trainsetList.size()){
                String msg = "";
                if(trainsetList == null || trainsetList.size() == 0){
                    msg = "????????????";
                }else{
                    msg = JSONArray.toJSONString(trainsetList);
                }
                logger.error("???????????? iRemoteService.getTrainsetBaseInfoByIds("+ JSONArray.toJSONString(trainsetStrList) +") ????????????????????????" + msg);
            }
        }
        condition.setTrainsetList(trainsetList);
        //????????????
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
        //??????????????????
        condition.setFillState(getFillStateDict());
        return condition;
    }


    //??????????????????
    private List<MainCyc> getMainCycDict() {
        // return new ArrayList<>();
        return Arrays.stream(MainCycEnum.class.getEnumConstants()).map(MainCycEnum::getMainCyc).collect(Collectors.toList());
    }

    //????????????????????????
    private List<FillState> getFillStateDict() {
        return Arrays.stream(FillStateEnum.class.getEnumConstants()).map(FillStateEnum::getFillState).collect(Collectors.toList());
    }
}
