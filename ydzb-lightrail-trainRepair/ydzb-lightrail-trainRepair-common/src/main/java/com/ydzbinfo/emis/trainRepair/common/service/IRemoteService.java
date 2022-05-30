package com.ydzbinfo.emis.trainRepair.common.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.plugins.Page;
import com.ydzbinfo.emis.trainRepair.bill.model.bill.ItemPerformance;
import com.ydzbinfo.emis.trainRepair.common.model.Role;
import com.ydzbinfo.emis.trainRepair.common.model.Temple;
import com.ydzbinfo.emis.trainRepair.common.model.Unit;
import com.ydzbinfo.emis.trainRepair.common.service.impl.RemoteServiceImpl;
import com.ydzbinfo.emis.trainRepair.mobile.model.FaultSearch;
import com.ydzbinfo.emis.trainRepair.remotemodel.fault.*;
import com.ydzbinfo.emis.trainRepair.remotemodel.item.RepairItemInfo;
import com.ydzbinfo.emis.trainRepair.remotemodel.item.RepairItemWorkQuery;
import com.ydzbinfo.emis.trainRepair.remotemodel.item.RepairItemWorkVo;
import com.ydzbinfo.emis.trainRepair.remotemodel.resume.*;
import com.ydzbinfo.emis.trainRepair.remotemodel.trainconfiguration.TreePartNodeModel;
import com.ydzbinfo.emis.trainRepair.remotemodel.trainuse.*;
import com.ydzbinfo.emis.trainRepair.remotemodel.trainuse.runRouting.LeaveBackTrainNoResult;
import com.ydzbinfo.emis.trainRepair.remotemodel.trainuse.track.ZtTrackAreaEntity;
import com.ydzbinfo.emis.trainRepair.remotemodel.fault.FaultWithKeyWork;
import com.ydzbinfo.emis.trainRepair.taskAllot.model.RepairItemVo;
import com.ydzbinfo.emis.trainRepair.taskAllot.model.RepairPacketStatu;
import com.ydzbinfo.emis.trainRepair.trainMonitor.model.PriData;
import com.ydzbinfo.emis.trainRepair.trainMonitor.model.UnitLevelRepairInfo;

import java.util.List;
import java.util.Map;

/**
 * 远程服务封装
 *
 * @author gaohan
 * @date 2021年2月24日
 */
public interface IRemoteService {

    List<ZtTrackAreaEntity> getTrackAreaByDept(String deptCode);

    List<RemainFaultNewVO> getFaultDataByIdList(List<String> trainsetIds);

    JSONObject getTrainTypeList();

    List<TrainsetBaseInfo> getTrainsetListReceived(String unitCode);

    List<TrainsetBaseInfo> getTrainsetNameListRepair(String unitCode);

    List<TrainsetBaseInfo> getTrainsetList();

    List<PriData> getRunRoutingDataByDate(String date, String trainNo, String planFlag);

    List<ShuntPlanModel> getShuntingPlanByCondition(String deptCode, String emuId, String startTime, String endTime);

    List<TrainsetHotSpareInfo> getTrainsetHotSpareInfo();

    List<HighLevelRepair> getTrainsetInHighLevelRepair();

    List<TrainsetHotSpareInfo> getTrainsetHotSpareInfoAndHighLevelRepair();

    List<PacketInfo> getPacketList();

    List<UnitLevelRepairInfo> getPgMPacketrecordList(String trainsetName, String trainsetId) throws Exception;

    TrainsetInfo getTrainsetDetialInfo(String trainsetId);

    String getTrainsetidByName(String trainsetName);

    List<String> getTraintypeListLocal(String departmentcode);

    List<String> getTrainTemplateListLocal(String departmentcode);

    List<String> getPatchListByTraintype(String trainType);

    int getMarshalCountByTrainType(String trainType);

    List<RepairItemVo> selectRepairItemListByCarNoParam(Map map);

    Page<RepairItemWorkVo> selectRepairItemListByWorkParam(RepairItemWorkQuery repairItemWorkQuery);

    TrainsetBaseInfo getTrainsetBaseinfoByID(String trainsetId);

    List<TrainsetBaseInfo> getTrainsetBaseInfoByIds(List<String> trainsetIds);

    JSONArray getFaultData(JSONObject params);

    List<ZtTaskPacketEntity> getPacketTaskByDayplanId(String dayPlanID, String deptCode);

    List<ZtTaskPacketEntity> getPacketTaskByCondition(String dayPlanId, String trainsetId, String lstPacketTypeCode, String repairDeptCode, String deptCode);

    List<String> getCarno(String trainsetId);

    int getTrainsetAccMile(String trainsetId, String querydate);

    JSONArray getPartListOnTrainset(String trainsetId);

    void addFault(JSONObject jsonObject);

    void addFaultFind(JSONObject param);

    JSONArray getBatchBomNodeListByCarNo(String trainsetId, String sCarNo);

    JSONArray partsTypeByName(String partsTypeName);

    JSONObject getFaultPacket(JSONObject params);

    void addFaultDeal(JSONObject param);

    String addFaultQa(AddQaVo addQaVo);

    RemoteServiceImpl.ResponseForFaultService<Page<JSONObject>> searchQaPage(QueryFaultQA queryFaultQA);

    JSONObject addListItemPerfPool(List<ItemPerformance> itemPerformances);

    List<TreeModel> getFunctionClass();

    List<TreePartNodeModel> getBatchBomNodeCode(String trainsetId, String carNo);

    Page<JSONObject> getFaultInfoPage(FaultSearch faultSearch);

    List<FaultWithKeyWork> getFaultInfoByPlanlessKey(FaultSearch faultSearch);

    List<ZtTaskPositionEntity> getTrackPositionByTrackCode(String trackCode, String unitCode);

    JSONObject addFaultReCheckPacketList(List<ZtTaskPacketEntity> ztTaskPacketEntityList);

    JSONObject updateRepairPacketStatus(List<RepairPacketStatu> repairPacketStatuList);

    LeaveBackTrainNoResult getLeavBackTrainNo(String trainsetId, String date);

    RecheckPacket getRecheckPacket();

    FaultPacket getFaultPacket();

    List<AxleWheel> getPartByTrainSetId(String trainSetId, String trainsetNo, String queryDate);

    List<AxleWheelDiameterEntity> getLatestAxleTurningdata(String trainSetId);

    List<Unit> getUnitList();

    RepairItemInfo getItemInfoByCode(String itemCode, String unitCode);

    List<Temple> getTramsByUnitCodeAndDeptType(String unitCode, String deptType);

    List<FaultSource> getFaultSource();

    JSONArray getFaultSourceNew();

    JSONArray getDealWith(String dealWithType);

    Page<JSONObject> selectQaRecordPage(QueryFaultQaRecord queryFaultQaRecord);

    /**
     * 获取全部角色列表
     */
    List<Role> getAllRoleList();

    JSONArray getFaultDataByFaultId(String faultId);

    JSONArray filedownload(String faultId, String fileName);

    List<FaultFile> getFaultFileByFaultId(String faultId);
}
