package com.ydzbinfo.emis.trainRepair.common.service;

import com.ydzbinfo.emis.trainRepair.bill.model.TemplateAll;
import com.ydzbinfo.emis.trainRepair.bill.model.TemplateQueryParamModel;
import com.ydzbinfo.emis.trainRepair.bill.model.attribute.TemplateAttributeForShow;
import com.ydzbinfo.emis.trainRepair.bill.model.attribute.TemplateAttributeQueryParamModel;
import com.ydzbinfo.emis.trainRepair.bill.model.attribute.TemplateTypeInfo;
import com.ydzbinfo.emis.trainRepair.bill.querymodel.attribute.TemplateAttributeTypeInfo;
import com.ydzbinfo.emis.trainRepair.bill.querymodel.attribute.TemplateType;
import com.ydzbinfo.emis.trainRepair.bill.model.templatesummary.TemplateSummaryInfo;
import com.ydzbinfo.emis.trainRepair.common.model.ConfigParamsModel;
import com.ydzbinfo.emis.trainRepair.common.querymodel.XzyCConfig;
import com.ydzbinfo.emis.trainRepair.constant.BillTemplateStateEnum;
import com.ydzbinfo.emis.trainRepair.taskAllot.querymodel.TaskAllotPacketEntity;
import com.ydzbinfo.emis.trainRepair.trainMonitor.model.TrainsetIsConnect;
import com.ydzbinfo.emis.trainRepair.trainMonitor.model.TrainsetPostionCurWithNextTrack;
import com.ydzbinfo.emis.trainRepair.trainMonitor.querymodel.TrackPowerStateCur;
import com.ydzbinfo.emis.trainRepair.trainMonitor.querymodel.TrainsetPostionCur;
import com.ydzbinfo.emis.trainRepair.workprocess.model.ProcessPacketEntity;
import com.ydzbinfo.emis.trainRepair.workprocess.model.ProcessSummaryEntity;
import com.ydzbinfo.emis.utils.result.RestResultGeneric;

import java.util.List;

/**
 * 作业过程中台服务
 *
 * @author 张天可
 * @since 2021/7/19
 */
public interface IRepairMidGroundService {
    List<XzyCConfig> getXzyCConfigs(ConfigParamsModel configParamsModel);

    List<TrainsetPostionCurWithNextTrack> getTrainsetPosition(String unitCode, String trackCodesJsonStr, String trainsetNameStr);

    List<TaskAllotPacketEntity> getTaskAllotData(String unitCode, String dayPlanID, String repairType, List<String> trainsetIdList, List<String> branchCodeList,List<String> workIdList, String itemCode);

    List<TaskAllotPacketEntity> getTaskAllotDataToPerson(String unitCode, String dayPlanID, String repairType, List<String> trainsetIdList, List<String> branchCodeList,List<String> workIdList, String itemCode);

    List<TaskAllotPacketEntity> getTaskAllotDataToItem(String unitCode, String dayPlanID, String repairType, List<String> trainsetIdList, List<String> branchCodeList,String itemCode);

    List<String> getTaskAllotTrainsetList(String unitCode,String dayPlanId,String mainCyc,String branchCode);

    List<String> getTaskAllotEquipmentTrainsetList(String unitCode,String dayPlanId,String branchCode);

    RestResultGeneric setTaskAllotData(List<TaskAllotPacketEntity> taskAllotPacketEntityList);

    void addWorkProcess(List<ProcessPacketEntity> processPacketEntityList);

    void addSummary(List<ProcessSummaryEntity> processSummaryEntityList);

    void addOrUpdateSummary(List<ProcessSummaryEntity> processSummaryEntityList);

    String setTrainsetPostIon(TrainsetPostionCur trainsetPostionCur);

    void setTrainsetState(TrainsetIsConnect trainsetIsConnect);

    String updTrackCode(List<TrainsetPostionCur> trainsetIsConnects);

    List<TrackPowerStateCur> getTrackPowerInfo(String trackCodeList, String unitCodeList);

    void setTrackPowerInfo(List<TrackPowerStateCur> trackPowerEntityList);

    List<TemplateSummaryInfo> queryTemplateSummaryInfo(TemplateQueryParamModel templateQueryParamModel);

    List<List<TemplateSummaryInfo>> queryTemplateSummaryInfoGroups(TemplateQueryParamModel templateQueryParamModel);

    List<TemplateType> getAllChildTemplateTypeList(List<String> templateTypeCode);

    List<TemplateAll> getBillTemplateDetail(String templateId, BillTemplateStateEnum state);

    List<TemplateAttributeTypeInfo> getConfigAttr(String billTypeCode);

    TemplateTypeInfo getTemplateTypeInfo(String templateTypeCode);

    List<TemplateAttributeForShow> getTemplateAttributeList(TemplateAttributeQueryParamModel paramModel);
}
