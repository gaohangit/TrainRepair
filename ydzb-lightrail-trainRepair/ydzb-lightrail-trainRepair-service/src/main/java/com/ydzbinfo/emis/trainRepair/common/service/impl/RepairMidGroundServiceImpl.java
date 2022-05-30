package com.ydzbinfo.emis.trainRepair.common.service.impl;

import com.alibaba.fastjson.JSONObject;
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
import com.ydzbinfo.emis.trainRepair.common.service.IRepairMidGroundService;
import com.ydzbinfo.emis.trainRepair.constant.BillTemplateStateEnum;
import com.ydzbinfo.emis.trainRepair.taskAllot.querymodel.TaskAllotPacketEntity;
import com.ydzbinfo.emis.trainRepair.trainMonitor.model.TrainsetIsConnect;
import com.ydzbinfo.emis.trainRepair.trainMonitor.model.TrainsetPostionCurWithNextTrack;
import com.ydzbinfo.emis.trainRepair.trainMonitor.querymodel.TrackPowerStateCur;
import com.ydzbinfo.emis.trainRepair.trainMonitor.querymodel.TrainsetLocationConfig;
import com.ydzbinfo.emis.trainRepair.trainMonitor.querymodel.TrainsetPostionCur;
import com.ydzbinfo.emis.trainRepair.trainMonitor.service.TrainsetLocationConfigService;
import com.ydzbinfo.emis.trainRepair.trainMonitor.utils.TrainSetLocationConfigEnum;
import com.ydzbinfo.emis.trainRepair.workprocess.model.ProcessPacketEntity;
import com.ydzbinfo.emis.trainRepair.workprocess.model.ProcessSummaryEntity;
import com.ydzbinfo.emis.utils.CustomServiceNameEnum;
import com.ydzbinfo.emis.utils.RestRequestKitUseLogger;
import com.ydzbinfo.emis.utils.result.RestRequestException;
import com.ydzbinfo.emis.utils.result.RestResultGeneric;
import com.ydzbinfo.emis.utils.result.base.RestResponseCodeEnum;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @author 张天可
 * @since 2021/7/19
 */
@Service
public class RepairMidGroundServiceImpl implements IRepairMidGroundService {

    private static final Logger logger = LoggerFactory.getLogger(RepairMidGroundServiceImpl.class);

    private final String midGroundService = CustomServiceNameEnum.MidGroundService.getId();

    @Resource
    TrainsetLocationConfigService trainsetLocationConfigService;

    @Override
    public List<XzyCConfig> getXzyCConfigs(ConfigParamsModel configParamsModel) {
        RestResultGeneric<List<XzyCConfig>> result = new RestRequestKitUseLogger<RestResultGeneric<List<XzyCConfig>>>(midGroundService, logger) {
        }.postObject("/common/getXzyCConfigs", configParamsModel);
        checkRestResult(result);
        return result.getData();
    }

    @Data
    public static class TempTrainsetPositionData {
        List<TrainsetPostionCurWithNextTrack> trainsetPostIonCurs;
        int count;
    }

    @Override
    public List<TrainsetPostionCurWithNextTrack> getTrainsetPosition(String unitCode, String trackCodesJsonStr, String trainsetNameStr) {
        TrainsetLocationConfig trainsetLocationConfig = new TrainsetLocationConfig();
        trainsetLocationConfig.setParamName(TrainSetLocationConfigEnum.ShowShuntPlan.getValue());
        TrainsetLocationConfig queryTrainsetLocationConfig = trainsetLocationConfigService.getTrainsetlocationConfig(trainsetLocationConfig);
        boolean showShuntPlan = queryTrainsetLocationConfig != null && queryTrainsetLocationConfig.getParamValue().equals("1") ? true : false;
        RestResultGeneric<TempTrainsetPositionData> result = new RestRequestKitUseLogger<RestResultGeneric<TempTrainsetPositionData>>(midGroundService, logger) {
        }.getObject("/iTrainsetPostIonCur/getTrainsetPostIon?unitCode={1}&trackCodesJsonStr={2}&trainsetNameStr={3}&showShuntPlan={4}",
            unitCode, trackCodesJsonStr, trainsetNameStr, showShuntPlan);
        checkRestResult(result);
        return result.getData().getTrainsetPostIonCurs();
    }

    @Override
    public List<TaskAllotPacketEntity> getTaskAllotData(String unitCode, String dayPlanID, String repairType, List<String> trainsetIdList, List<String> branchCodeList, List<String> workIdList, String itemCode) {
        JSONObject params = new JSONObject();
        params.put("unitCode", unitCode);
        params.put("dayPlanId", dayPlanID);
        params.put("repairType", repairType);
        params.put("trainsetIdList", trainsetIdList);
        params.put("workIdList", workIdList);
        params.put("itemCode", itemCode);
        params.put("branchCodeList", branchCodeList);
        RestResultGeneric<List<TaskAllotPacketEntity>> restResult = new RestRequestKitUseLogger<RestResultGeneric<List<TaskAllotPacketEntity>>>(midGroundService, logger) {
        }.postObject("/xzyMTaskallot/getTaskAllotData", params);
        checkRestResult(restResult);
        return restResult.getData();
    }

    @Override
    public List<TaskAllotPacketEntity> getTaskAllotDataToPerson(String unitCode, String dayPlanID, String repairType, List<String> trainsetIdList, List<String> branchCodeList, List<String> workIdList, String itemCode) {
        JSONObject params = new JSONObject();
        params.put("unitCode", unitCode);
        params.put("dayPlanId", dayPlanID);
        params.put("repairType", repairType);
        params.put("trainsetIdList", trainsetIdList);
        params.put("workIdList", workIdList);
        params.put("itemCode", itemCode);
        params.put("branchCodeList", branchCodeList);
        RestResultGeneric<List<TaskAllotPacketEntity>> restResult = new RestRequestKitUseLogger<RestResultGeneric<List<TaskAllotPacketEntity>>>(midGroundService, logger) {
        }.postObject("/xzyMTaskallot/getTaskAllotDataToPerson", params);
        checkRestResult(restResult);
        return restResult.getData();
    }

    @Override
    public List<TaskAllotPacketEntity> getTaskAllotDataToItem(String unitCode, String dayPlanID, String repairType, List<String> trainsetIdList, List<String> branchCodeList, String itemCode) {
        JSONObject params = new JSONObject();
        params.put("unitCode", unitCode);
        params.put("dayPlanId", dayPlanID);
        params.put("repairType", repairType);
        params.put("trainsetIdList", trainsetIdList);
        params.put("itemCode", itemCode);
        params.put("branchCodeList", branchCodeList);
        RestResultGeneric<List<TaskAllotPacketEntity>> restResult = new RestRequestKitUseLogger<RestResultGeneric<List<TaskAllotPacketEntity>>>(midGroundService, logger) {
        }.postObject("/xzyMTaskallot/getTaskAllotDataToItem", params);
        checkRestResult(restResult);
        return restResult.getData();
    }

    @Override
    public List<String> getTaskAllotTrainsetList(String unitCode, String dayPlanId, String mainCyc, String branchCode) {
        JSONObject params = new JSONObject();
        params.put("unitCode", unitCode);
        params.put("dayPlanId", dayPlanId);
        params.put("mainCyc", mainCyc);
        params.put("branchCode", branchCode);
        RestResultGeneric<List<String>> restResult = new RestRequestKitUseLogger<RestResultGeneric<List<String>>>(midGroundService, logger) {
        }.postObject("/xzyMTaskallot/getTaskAllotTrainsetList", params);
        checkRestResult(restResult);
        return restResult.getData();
    }

    @Override
    public List<String> getTaskAllotEquipmentTrainsetList(String unitCode, String dayPlanId, String branchCode) {
        JSONObject params = new JSONObject();
        params.put("unitCode", unitCode);
        params.put("dayPlanId", dayPlanId);
        params.put("branchCode", branchCode);
        RestResultGeneric<List<String>> restResult = new RestRequestKitUseLogger<RestResultGeneric<List<String>>>(midGroundService, logger) {
        }.postObject("/xzyMTaskallot/getTaskAllotEquipmentTrainsetList", params);
        checkRestResult(restResult);
        return restResult.getData();
    }

    @Override
    public RestResultGeneric setTaskAllotData(List<TaskAllotPacketEntity> taskAllotPacketEntityList){
        RestResultGeneric<JSONObject> res = new RestRequestKitUseLogger<RestResultGeneric<JSONObject>>(midGroundService, logger) {
        }.postObject("/xzyMTaskallot/setTaskAllotData", taskAllotPacketEntityList);
        checkRestResult(res);
        return res;
    }

    void checkRestResult(RestResultGeneric<?> restResult) {
        RestResponseCodeEnum codeEnum = restResult.getCode();
        if (codeEnum != RestResponseCodeEnum.SUCCESS) {
            String message = restResult.getMsg();
            switch (codeEnum) {
                case NORMAL_FAIL:
                    throw RestRequestException.normalFail(message);
                case FATAL_FAIL:
                    throw RestRequestException.fatalFail(message);
            }
        }
    }


    @Override
    public void addWorkProcess(List<ProcessPacketEntity> processPacketEntityList) {
        RestResultGeneric<Object> restResult = new RestRequestKitUseLogger<RestResultGeneric<Object>>(midGroundService, logger) {
        }.postObject("/workProcessMid/addWorkProcess", processPacketEntityList);
        checkRestResult(restResult);
    }

    @Override
    public void addSummary(List<ProcessSummaryEntity> processSummaryEntityList) {
        RestResultGeneric<Object> restResult = new RestRequestKitUseLogger<RestResultGeneric<Object>>(midGroundService, logger) {
        }.postObject("/workProcessMid/addSummary", processSummaryEntityList);
        checkRestResult(restResult);
    }

    @Override
    public void addOrUpdateSummary(List<ProcessSummaryEntity> processSummaryEntityList){
        RestResultGeneric<Object> restResult = new RestRequestKitUseLogger<RestResultGeneric<Object>>(midGroundService, logger) {
        }.postObject("/workProcessMid/addOrUpdateSummary", processSummaryEntityList);
        checkRestResult(restResult);
    }

    @Override
    public String setTrainsetPostIon(TrainsetPostionCur trainsetPostionCur) {
        RestResultGeneric<String> restResult = new RestRequestKitUseLogger<RestResultGeneric<String>>(midGroundService, logger) {
        }.postObject("/iTrainsetPostIonCur/setTrainsetPostIon", trainsetPostionCur);
        checkRestResult(restResult);
        return restResult.getData();
    }

    @Override
    public void setTrainsetState(TrainsetIsConnect trainsetIsConnect) {
        RestResultGeneric<Object> restResult = new RestRequestKitUseLogger<RestResultGeneric<Object>>(midGroundService, logger) {
        }.postObject("/iTrainsetPostIonCur/setTrainsetState", trainsetIsConnect);
        checkRestResult(restResult);
    }

    @Override
    public String updTrackCode(List<TrainsetPostionCur> trainsetIsConnects) {
        RestResultGeneric<String> restResult = new RestRequestKitUseLogger<RestResultGeneric<String>>(midGroundService, logger) {
        }.postObject("/iTrainsetPostIonCur/updTrackCode", trainsetIsConnects);
        checkRestResult(restResult);
        return restResult.getData();
    }

    @Override
    public List<TrackPowerStateCur> getTrackPowerInfo(String trackCodeList, String unitCodeList) {
        RestResultGeneric<List<TrackPowerStateCur>> restResult = new RestRequestKitUseLogger<RestResultGeneric<List<TrackPowerStateCur>>>(midGroundService, logger) {
        }.getObject("/iTrackPowerStateCur/getTrackPowerInfo?trackCodeList=" + trackCodeList + "&unitCodeList=" + unitCodeList);
        checkRestResult(restResult);
        return restResult.getData();
    }

    @Override
    public void setTrackPowerInfo(List<TrackPowerStateCur> trackPowerEntityList) {
        RestResultGeneric<Object> restResult = new RestRequestKitUseLogger<RestResultGeneric<Object>>(midGroundService, logger) {
        }.postObject("/iTrackPowerStateCur/setTrackPowerInfo", trackPowerEntityList);
        checkRestResult(restResult);
    }


    /**
     * 根据模板查询条件查询模板具体内容
     */
    @Override
    public List<TemplateSummaryInfo> queryTemplateSummaryInfo(TemplateQueryParamModel templateQueryParamModel) {
        RestResultGeneric<List<TemplateSummaryInfo>> restResult = new RestRequestKitUseLogger<RestResultGeneric<List<TemplateSummaryInfo>>>(midGroundService, logger) {
        }.postObject("/billConfig/queryTemplateSummaryInfo", templateQueryParamModel);
        checkRestResult(restResult);
        return restResult.getData();
    }

    /**
     * 根据模板查询条件查询多组模板具体内容
     */
    @Override
    public List<List<TemplateSummaryInfo>> queryTemplateSummaryInfoGroups(TemplateQueryParamModel templateQueryParamModel) {
        RestResultGeneric<List<List<TemplateSummaryInfo>>> restResult = new RestRequestKitUseLogger<RestResultGeneric<List<List<TemplateSummaryInfo>>>>(midGroundService, logger) {
        }.postObject("/billConfig/queryTemplateSummaryInfo", templateQueryParamModel);
        checkRestResult(restResult);
        return restResult.getData();
    }


    /**
     * 根据模板类型查询子类型
     */
    public List<TemplateType> getAllChildTemplateTypeList(List<String> templateTypeCodes) {
        RestResultGeneric<List<TemplateType>> restResult = new RestRequestKitUseLogger<RestResultGeneric<List<TemplateType>>>(midGroundService, logger) {
        }.postObject("/templateType/getAllChildTemplateTypeList", templateTypeCodes);
        checkRestResult(restResult);
        return restResult.getData();
    }

    @Override
    public List<TemplateAll> getBillTemplateDetail(String templateId, BillTemplateStateEnum state) {
        RestResultGeneric<List<TemplateAll>> result = new RestRequestKitUseLogger<RestResultGeneric<List<TemplateAll>>>(midGroundService, logger) {
        }.getObject("/billConfig/getBillTemplateDetail?templateId={1}&state={2}", templateId, state.getValue());
        checkRestResult(result);
        return result.getData();
    }

    @Override
    public List<TemplateAttributeTypeInfo> getConfigAttr(String billTypeCode) {
        RestResultGeneric<List<TemplateAttributeTypeInfo>> restResult = new RestRequestKitUseLogger<RestResultGeneric<List<TemplateAttributeTypeInfo>>>(midGroundService, logger) {
        }.getObject("/common/getConfigAttr?billTypeCode=" + billTypeCode);
        checkRestResult(restResult);
        return restResult.getData();
    }

    @Override
    public TemplateTypeInfo getTemplateTypeInfo(String templateTypeCode) {
        RestResultGeneric<TemplateTypeInfo> result = new RestRequestKitUseLogger<RestResultGeneric<TemplateTypeInfo>>(midGroundService, logger) {
        }.getObject("/templateType/getTemplateTypeInfo?templateTypeCode={1}", templateTypeCode);
        checkRestResult(result);
        return result.getData();
    }


    @Override
    public List<TemplateAttributeForShow> getTemplateAttributeList(TemplateAttributeQueryParamModel paramModel) {
        List<TemplateAttributeForShow> templateAttributeForShowList = new ArrayList<>();
        // RestResultGeneric<List<TemplateAttributeForShow>> restResult = new RestRequestKitUseLogger<RestResultGeneric<List<TemplateAttributeForShow>>>(midGroundService, logger) {
        // }.postObject("/templateAttribute/getTemplateAttributeList", paramModel);
        // checkRestResult(restResult);
        JSONObject jsonObject = new RestRequestKitUseLogger<JSONObject>(midGroundService, logger) {
        }.postObject("/templateAttribute/getTemplateAttributeList", paramModel);
        templateAttributeForShowList = jsonObject.getJSONObject("data").getJSONArray("templateAttributeList").toJavaList(TemplateAttributeForShow.class);
        return templateAttributeForShowList;
    }

}
