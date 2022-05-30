package com.ydzbinfo.emis.trainRepair.common.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.plugins.Page;
import com.jxdinfo.hussar.common.organutil.OrganUtil;
import com.ydzbinfo.emis.trainRepair.bill.model.bill.ItemPerformance;
import com.ydzbinfo.emis.trainRepair.common.model.Role;
import com.ydzbinfo.emis.trainRepair.common.model.Temple;
import com.ydzbinfo.emis.trainRepair.common.model.Unit;
import com.ydzbinfo.emis.trainRepair.common.service.IRemoteService;
import com.ydzbinfo.emis.trainRepair.constant.PacketRepairCodeEnum;
import com.ydzbinfo.emis.trainRepair.mobile.model.FaultSearch;
import com.ydzbinfo.emis.trainRepair.remotemodel.fault.*;
import com.ydzbinfo.emis.trainRepair.remotemodel.item.RepairItemInfo;
import com.ydzbinfo.emis.trainRepair.remotemodel.item.RepairItemWorkQuery;
import com.ydzbinfo.emis.trainRepair.remotemodel.item.RepairItemWorkVo;
import com.ydzbinfo.emis.trainRepair.remotemodel.resume.*;
import com.ydzbinfo.emis.trainRepair.remotemodel.trainconfiguration.BatchBomNodeEntity;
import com.ydzbinfo.emis.trainRepair.remotemodel.trainconfiguration.TreePartNodeModel;
import com.ydzbinfo.emis.trainRepair.remotemodel.trainuse.*;
import com.ydzbinfo.emis.trainRepair.remotemodel.trainuse.runRouting.LeaveBackTrainNoQuery;
import com.ydzbinfo.emis.trainRepair.remotemodel.trainuse.runRouting.LeaveBackTrainNoResult;
import com.ydzbinfo.emis.trainRepair.remotemodel.trainuse.track.ZtTrackAreaEntity;
import com.ydzbinfo.emis.trainRepair.remotemodel.fault.FaultWithKeyWork;
import com.ydzbinfo.emis.trainRepair.taskAllot.model.RepairItemVo;
import com.ydzbinfo.emis.trainRepair.taskAllot.model.RepairPacketStatu;
import com.ydzbinfo.emis.trainRepair.trainMonitor.model.PriData;
import com.ydzbinfo.emis.trainRepair.trainMonitor.model.UnitLevelRepairInfo;
import com.ydzbinfo.emis.utils.*;
import com.ydzbinfo.emis.utils.result.RestRequestException;
import com.ydzbinfo.hussar.common.treemodel.JSTreeModel;
import com.ydzbinfo.hussar.core.reqres.response.ResponseData;
import com.ydzbinfo.hussar.system.bsp.organ.SysOrgan;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.ObjectUtils;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;


@Service
public class RemoteServiceImpl implements IRemoteService {

    private static final Logger logger = LoggerFactory.getLogger(RemoteServiceImpl.class);

    private final String repairFaultServiceId = ServiceNameEnum.RepairFaultService.getId();
    private final String repairTaskId = ServiceNameEnum.RepairTaskService.getId();
    private final String resumeMidGroundServiceId = ServiceNameEnum.ResumeMidGroundService.getId();
    private final String repairItemId = ServiceNameEnum.RepairItemService.getId();
    private final String configurationService = ServiceNameEnum.configurationService.getId();
    private final String devicedataServiceId = ServiceNameEnum.DeviceDataService.getId();
    //用户信息服务
    private final String userServiceId = ServiceNameEnum.UserService.getId();

    @Override
    public List<ZtTrackAreaEntity> getTrackAreaByDept(String deptCode) {
        List<ZtTrackAreaEntity> trackAreas = new RestRequestKitUseLogger<List<ZtTrackAreaEntity>>(repairTaskId, logger) {
        }.getObject("/yard/getTrackAreaByDept?deptCode=" + deptCode);
        trackAreas.sort((o1, o2) -> {
            int value = 0;
            if (o1.getSort() > o2.getSort()) {
                value = 1;
            } else if (o1.getSort() < o2.getSort()) {
                value = -1;
            }

            o1.getLstTrackInfo().sort((a1, a2) -> {
                if (a1.getSort() > a2.getSort()) {
                    return 1;
                } else if (a1.getSort() < a2.getSort()) {
                    return -1;
                } else {
                    return 0;
                }
            });
            return value;
        });
        return trackAreas;
    }

    @Override
    public List<TrainsetHotSpareInfo> getTrainsetHotSpareInfo() {
        JSONObject jsonObject = new RestRequestKitUseLogger<JSONObject>(resumeMidGroundServiceId, logger) {
        }.getObject("/getTrainsetHotSpareInfo?deptcode=" + ContextUtils.getUnitCode());
        List<TrainsetHotSpareInfo> trainsetHotSpareInfos = JSON.parseArray(jsonObject.getString("data"), TrainsetHotSpareInfo.class);
        trainsetHotSpareInfos.forEach(v -> {
            StringBuilder stringBuilder = new StringBuilder();
            if (StringUtils.isNotBlank(v.getReBeiLine())) {
                stringBuilder.append(",热备线路:" + v.getReBeiLine());
            }
            if (StringUtils.isNotBlank(v.getReBeiPlace())) {
                stringBuilder.append(",热备地点:" + v.getReBeiPlace());
            }
            if (StringUtils.isNotBlank(v.getStuffName())) {
                stringBuilder.append(",值乘人员:" + v.getStuffName());
            }
            if (StringUtils.isNotBlank(v.getRemark())) {
                stringBuilder.append(",备注:" + v.getRemark());
            }
            v.setRemark(stringBuilder.length() == 0 ? "" : stringBuilder.substring(1, stringBuilder.length()));
        });
        return trainsetHotSpareInfos;
    }

    @Override
    public List<HighLevelRepair> getTrainsetInHighLevelRepair() {
        String unitCode = ContextUtils.getUnitCode();

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String data = simpleDateFormat.format(new Date()).replace("-", "");

        JSONObject jsonObject = new RestRequestKitUseLogger<JSONObject>(resumeMidGroundServiceId, logger) {
        }.getObject("/getTrainsetInHighLevelRepair?reportdeptcode=" + unitCode + "&date=" + data);
        List<HighLevelRepair> highLevelRepairs = JSON.parseArray(jsonObject.getString("data"), HighLevelRepair.class);
        return highLevelRepairs;
    }

    @Override
    public List<TrainsetHotSpareInfo> getTrainsetHotSpareInfoAndHighLevelRepair() {
        List<TrainsetBaseInfo> trainsetBaseInfos = CacheUtil.getDataUseThreadCache(
            "remoteService.getTrainsetList",
            () -> this.getTrainsetList()
        );
        List<TrainsetHotSpareInfo> trainsetHotSpareInfos = this.getTrainsetHotSpareInfo();
        List<HighLevelRepair> highLevelRepairs = this.getTrainsetInHighLevelRepair();
        highLevelRepairs.forEach(highLevelRepair -> {
            TrainsetHotSpareInfo trainsetHotSpareInfo = new TrainsetHotSpareInfo();
            trainsetHotSpareInfo.setRemark("修程:" + highLevelRepair.getGjxLevel() + ",承修单位:" + highLevelRepair.getStsDeptName());

            List<TrainsetBaseInfo> trainsetBaseInfoByTrainsetId = trainsetBaseInfos.stream().filter(v -> v.getTrainsetid().equals(highLevelRepair.getTrainsetid())).collect(Collectors.toList());
            if (trainsetBaseInfoByTrainsetId.size() > 0) {
                trainsetHotSpareInfo.setTrainsetName(trainsetBaseInfoByTrainsetId.get(0).getTrainsetname());
            }
            trainsetHotSpareInfos.add(trainsetHotSpareInfo);
        });
        return trainsetHotSpareInfos;
    }

    /**
     * 故障服务的返回结果封装
     *
     * @param <T>
     */
    @Data
    public static class ResponseForFaultService<T> {
        private Integer code;
        private T data;
        private String message;

        public void checkSuccess() {
            //故障服务的返回code的值为0时成功
            if (!Objects.equals(this.code, 0)) {
                throw RestRequestException.fatalFail("调用故障服务接口错误：" + message);
            }
        }
    }

    @Override
    public List<RemainFaultNewVO> getFaultDataByIdList(List<String> itemCode) {
        ResponseForFaultService<List<RemainFaultNewVO>> response = new RestRequestKitUseLogger<ResponseForFaultService<List<RemainFaultNewVO>>>(repairFaultServiceId, logger) {
        }.postObject("/fault/getFaultByIdList", itemCode);
        response.checkSuccess();
        return response.getData();
    }

    @Override
    public JSONObject getTrainTypeList() {
        JSONObject jsonObject = new RestRequestKitUseLogger<JSONObject>(resumeMidGroundServiceId, logger) {
        }.getObject("/getTraintypeList");
        return jsonObject;
    }


    @Override
    public List<TrainsetBaseInfo> getTrainsetListReceived(String unitCode) {
        List<String> unitCodeList = new ArrayList<>();
        if (StringUtils.isNotBlank(unitCode)) {
            unitCodeList.add(unitCode);
        } else {
            if (!ContextUtils.isCenter()) {
                unitCodeList.add(ContextUtils.getUnitCode());
            } else {
                unitCodeList.addAll(this.getUnitList().stream().map(Unit::getUnitCode).collect(Collectors.toList()));
            }
        }

        List<TrainsetBaseInfo> trainsetBaseInfoList = new ArrayList<>();
        for (String code : unitCodeList) {
            //获取本所所有车组
            JSONObject jsonObject = new RestRequestKitUseLogger<JSONObject>(resumeMidGroundServiceId, logger) {
            }.getObject("/getTrainsetListReceived?departmentcode=" + code);
            String resString = jsonObject.getString("data");
            List<TrainsetBaseInfo> processCarPartEntityList = JSONArray.parseArray(resString, TrainsetBaseInfo.class);
            trainsetBaseInfoList.addAll(processCarPartEntityList);
        }
        //去重
        trainsetBaseInfoList = CommonUtils.getDistinctList(trainsetBaseInfoList, item -> item.getTrainsetid());
        return trainsetBaseInfoList;
    }

    @Override
    public List<TrainsetBaseInfo> getTrainsetNameListRepair(String unitCode) {
        JSONObject jsonObject = new RestRequestKitUseLogger<JSONObject>(resumeMidGroundServiceId, logger) {
        }.getObject("/getTrainsetNameListRepair_TDB?deptcode=" + unitCode);
        String resString = jsonObject.getString("data");
        List<TrainsetBaseInfo> processCarPartEntityList = JSONArray.parseArray(resString, TrainsetBaseInfo.class);

        Map<String, TrainsetBaseInfo> trainsetToMapByName = CacheUtil.getDataUseThreadCache("remoteService.getTrainsetList", () -> {
            List<TrainsetBaseInfo> a = this.getTrainsetList();
            return CommonUtils.collectionToMap(a, TrainsetBaseInfo::getTrainsetname);
        });
        processCarPartEntityList.forEach(v -> {
            TrainsetBaseInfo trainsetBaseInfo = trainsetToMapByName.get(v.getTrainsetname());
            if (trainsetBaseInfo != null) {
                v.setTrainsetid(trainsetBaseInfo.getTrainsetid());
            }
        });
        return processCarPartEntityList;
    }

    @Override
    public List<TrainsetBaseInfo> getTrainsetList() {
        TrainsetBaseInfo trainsetBaseInfo = new TrainsetBaseInfo();
        JSONObject jsonObject = new RestRequestKitUseLogger<JSONObject>(resumeMidGroundServiceId, logger) {
        }.postObject("/getTrainsetList", trainsetBaseInfo);
        List<TrainsetBaseInfo> processCarPartEntityList = JSONArray.parseArray(jsonObject.getString("data"), TrainsetBaseInfo.class);
        return processCarPartEntityList;
    }

    @Override
    public List<PriData> getRunRoutingDataByDate(String date, String trainsetId, String planFlag) {
        Map<String, String> map = new HashMap<String, String>();
        map.put("sDate", date);
        map.put("trainNo", "");
        map.put("cPlanflag", planFlag);
        Map<String, Map<String, String>> requestMap = new HashMap<String, Map<String, String>>();
        requestMap.put("params", map);
        JSONObject jsonObject = new RestRequestKitUseLogger<JSONObject>(repairTaskId, logger) {
        }.postObject("/operate/zs/getPriDatas", requestMap);
        List<PriData> priDataList = JSONArray.parseArray(jsonObject.get("result").toString(), PriData.class);
        if (StringUtils.isNotBlank(trainsetId)) {
            priDataList = priDataList.stream().filter(v -> v.getTrainsetId().equals(trainsetId)).collect(Collectors.toList());
        }
        return priDataList;
    }

    @Override
    public List<ShuntPlanModel> getShuntingPlanByCondition(String deptCode, String emuId, String startTime, String endTime) {
        JSONObject json = new JSONObject();
        json.put("depotCode", deptCode);
        json.put("emuId", emuId);
        json.put("beginDate", startTime);
        json.put("endDate", endTime);
        JSONObject params = new JSONObject();
        params.put("params", json);
        JSONObject jsonObject = new RestRequestKitUseLogger<JSONObject>(repairTaskId, logger) {
        }.postObject("/shuntplan/getShuntingPlanByCondition", params);
        String resString = jsonObject.getString("result");
        List<ShuntPlanModel> shuntPlanModels = JSONArray.parseArray(resString, ShuntPlanModel.class);
        shuntPlanModels.sort(Comparator.comparing(ShuntPlanModel::getEmuId));
        shuntPlanModels.sort((v1, v2) -> {
            if (v1.getEmuId().equals(v2.getEmuId())) {
                if (v1.getInTime().after(v2.getInTime())) {
                    return 1;
                } else {
                    return -1;
                }
            } else {
                return 0;
            }
        });
        Map<String, TrainsetBaseInfo> trainsetBaseInfoMap = CacheUtil.getDataUseThreadCache("remoteService.getTrainsetList_toMap", () -> {
            List<TrainsetBaseInfo> trainsetBaseInfos = this.getTrainsetList();
            return CommonUtils.collectionToMap(trainsetBaseInfos, TrainsetBaseInfo::getTrainsetid);
        });
        shuntPlanModels.forEach(v -> {
            TrainsetBaseInfo trainsetBaseInfo = trainsetBaseInfoMap.get(v.getEmuId());
            if (trainsetBaseInfo != null) {
                v.setTrainsetName(trainsetBaseInfo.getTrainsetname());
            }
            TrainsetBaseInfo relateTrainsetBaseInfo = trainsetBaseInfoMap.get(v.getRelatedEmuId());
            if (relateTrainsetBaseInfo != null) {
                v.setRelateTrainsetName(relateTrainsetBaseInfo.getTrainsetname());
            }
        });
        return shuntPlanModels;
    }

    @Override
    public List<PacketInfo> getPacketList() {
        JSONObject reqJson = new JSONObject();
        reqJson.put("sPacketCode", "");
        reqJson.put("suitModel", "");
        reqJson.put("suitBatch", "");
        JSONObject jsonObject = new RestRequestKitUseLogger<JSONObject>(repairTaskId, logger) {
        }.postObject("/packet/getPacketList", reqJson);
        List<PacketInfo> packetInfoList = JSONArray.parseArray(jsonObject.getString("result"), PacketInfo.class);
        packetInfoList.forEach(v -> v.setPacketCodeName(
            EnumUtils.findValue(
                PacketRepairCodeEnum.class,
                PacketRepairCodeEnum::getValue,
                PacketRepairCodeEnum::getLabel,
                v.getRepairCode()
            )
        ));
        return packetInfoList;
    }

    @Override
    public List<UnitLevelRepairInfo> getPgMPacketrecordList(String trainsetName, String trainsetId) throws Exception {
        List<String> list = new ArrayList<>();
        list.add(trainsetName);
        JSONObject json = new JSONObject();
        json.put("trainsetNames", list);

        JSONObject reqJson = new RestRequestKitUseLogger<JSONObject>(repairTaskId, logger) {
        }.postObject("/pgMPacket/getPgMPacketrecordList", json);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        List<UnitLevelRepairInfo> unitLevelRepairInfos = JSONArray.parseArray(reqJson.getJSONArray("result").toJSONString(), UnitLevelRepairInfo.class);
        for (UnitLevelRepairInfo unitLevelRepairInfo : unitLevelRepairInfos) {
            //里程差:车组接口当前里程减最早项目检修里程
            TrainsetInfo trainsetInfo = getTrainsetDetialInfo(trainsetId);

            Integer rmileDifference = Integer.parseInt(trainsetInfo.getAccumile());
            if(StringUtils.isNotBlank(unitLevelRepairInfo.getRepairMileage())){
                rmileDifference = rmileDifference -  - Integer.parseInt(unitLevelRepairInfo.getRepairMileage());
            }
            unitLevelRepairInfo.setMileageDifference(rmileDifference.toString());
            unitLevelRepairInfo.setAccuMile(trainsetInfo.getAccumile());
            //当天差：当前日期减最早项目检修时间
            Date date = new Date();
            if(StringUtils.isNotBlank(unitLevelRepairInfo.getRepairTime())){
                date = simpleDateFormat.parse(unitLevelRepairInfo.getRepairTime());
            }
            long day = (new Date().getTime() - date.getTime()) / (24 * 60 * 60 * 1000);
            unitLevelRepairInfo.setDayDifference(day + "".trim());

        }
        return unitLevelRepairInfos;
    }

    @Override
    public TrainsetInfo getTrainsetDetialInfo(String trainsetId) {
        JSONObject jsonObject = new RestRequestKitUseLogger<JSONObject>(resumeMidGroundServiceId, logger) {
        }.getObject("/getTrainsetDetialInfo?trainsetid=" + trainsetId);
        return JSONObject.parseObject(jsonObject.get("data").toString(), TrainsetInfo.class);
    }


    @Override
    public String getTrainsetidByName(String trainsetName) {
        JSONObject jsonObject = new RestRequestKitUseLogger<JSONObject>(resumeMidGroundServiceId, logger) {
        }.getObject("/getTrainsetidByName?trainsetname=" + trainsetName);
        return jsonObject.get("data").toString();
    }


    @Override
    public List<String> getTraintypeListLocal(String departmentcode) {
        JSONObject jsonObject = new RestRequestKitUseLogger<JSONObject>(resumeMidGroundServiceId, logger) {
        }.getObject("/getTraintypeListLocal?departmentcode=" + departmentcode);
        List<String> list = JSONArray.parseArray(jsonObject.get("data").toString(), String.class);
        return list;
    }

    @Override
    public List<String> getTrainTemplateListLocal(String departmentcode) {
        JSONObject jsonObject = new RestRequestKitUseLogger<JSONObject>(resumeMidGroundServiceId, logger) {
        }.getObject("/getTrainTemplateListLocal?departmentcode=" + departmentcode);
        List<String> list = JSONArray.parseArray(jsonObject.get("data").toString(), String.class);
        return list;
    }

    @Override
    public List<String> getPatchListByTraintype(String trainType) {
        JSONObject jsonObject = new RestRequestKitUseLogger<JSONObject>(resumeMidGroundServiceId, logger) {
        }.getObject("/getPatchListByTraintype?traintype=" + trainType);
        List<String> list = JSONArray.parseArray(jsonObject.get("data").toString(), String.class);
        return list;
    }

    @Override
    public int getMarshalCountByTrainType(String trainType) {
        JSONObject jsonObject = new RestRequestKitUseLogger<JSONObject>(resumeMidGroundServiceId, logger) {
        }.getObject("/getMarshalCountByTrainType?trainSetType=" + trainType);
        return Integer.parseInt(jsonObject.getString("data"));
    }

    @Override
    public List<RepairItemVo> selectRepairItemListByCarNoParam(Map map) {
        JSONObject jsonObject = new RestRequestKitUseLogger<JSONObject>(repairItemId, logger) {
        }.postObject("/itemForeign/selectRepairItemByCarNoParam", map);
        List<RepairItemVo> repairItemVos = JSON.parseArray(jsonObject.getString("rows"), RepairItemVo.class);
        return repairItemVos;
    }


    @Data
    public static class ResponseForItemService<T> {
        List<T> rows;
        Integer code;
        Integer total;
        Integer affectcounts;
        String msg;
    }

    @Override
    public Page<RepairItemWorkVo> selectRepairItemListByWorkParam(RepairItemWorkQuery repairItemWorkQuery) {
        Page<RepairItemWorkVo> repairItemWorkVoPage = new Page<>();
        ResponseForItemService<RepairItemWorkVo> data = new RestRequestKitUseLogger<ResponseForItemService<RepairItemWorkVo>>(repairItemId, logger) {
        }.postObject("/itemForeign/selectRepairItemListByWorkParam", repairItemWorkQuery);
        if (data != null && data.getRows() != null) {
            Integer pageSize = repairItemWorkQuery.getLimit();
            if (pageSize == null) {
                pageSize = data.getRows().size();
            }
            repairItemWorkVoPage = CommonUtils.convertToPage(data.getRows(), data.getTotal(), repairItemWorkQuery.getPage(), pageSize);
        }
        return repairItemWorkVoPage;
    }

    @Override
    public TrainsetBaseInfo getTrainsetBaseinfoByID(String trainsetId) {
        List<String> list = new ArrayList<>();
        list.add(trainsetId);
        JSONObject reqJson = new RestRequestKitUseLogger<JSONObject>(resumeMidGroundServiceId, logger) {
        }.postObject("/getTrainsetBaseinfoByID", list);
        List<TrainsetBaseInfo> trainsetBaseInfos = JSON.parseArray(reqJson.getString("data"), TrainsetBaseInfo.class);
        TrainsetBaseInfo trainsetBaseInfo = trainsetBaseInfos.get(0);
        return trainsetBaseInfo;
    }

    @Override
    public List<TrainsetBaseInfo> getTrainsetBaseInfoByIds(List<String> trainsetIds) {
        JSONObject reqJson = new RestRequestKitUseLogger<JSONObject>(resumeMidGroundServiceId, logger) {
        }.postObject("/getTrainsetBaseinfoByID", trainsetIds);
        List<TrainsetBaseInfo> trainsetBaseInfos = JSON.parseArray(reqJson.getString("data"), TrainsetBaseInfo.class);
        return trainsetBaseInfos;
    }

    @Override
    public JSONArray getFaultData(JSONObject params) {
        ResponseForFaultService<JSONArray> response = new RestRequestKitUseLogger<ResponseForFaultService<JSONArray>>(repairFaultServiceId, logger) {
        }.postObject("/externalApi/getNoAssignFaultForTask", params);
        response.checkSuccess();
        return response.getData();
    }

    @Override
    public List<ZtTaskPacketEntity> getPacketTaskByDayplanId(String dayPlanID, String deptCode) {
        return new RestRequestKitUseLogger<List<ZtTaskPacketEntity>>(repairTaskId, logger) {
        }.getObject("/Task/getPacketTaskByDayplanId?dayPlanId=" + dayPlanID + "&deptCode=" + deptCode);
    }

    @Override
    public List<ZtTaskPacketEntity> getPacketTaskByCondition(String dayPlanId, String trainsetId, String lstPacketTypeCode, String repairDeptCode, String deptCode) {
        if (dayPlanId == null || dayPlanId.equals("")) {
            throw new RuntimeException("日计划不能为空");
        }
        if (deptCode == null || deptCode.equals("deptCode")) {
            throw new RuntimeException("所编码不能为空");
        }
        return new RestRequestKitUseLogger<List<ZtTaskPacketEntity>>(repairTaskId, logger) {
        }.getObject("/Task/getPacketTaskByCondition?dayPlanId={1}&deptCode={2}&trainsetId={3}&repairDeptCode={4}&lstPacketTypeCode={5}", dayPlanId, deptCode, trainsetId, repairDeptCode, lstPacketTypeCode);
    }

    @Override
    public List<String> getCarno(String trainsetId) {
        JSONObject result = new RestRequestKitUseLogger<JSONObject>(resumeMidGroundServiceId, logger) {
        }.getObject("/getCarno?trainsetid=" + trainsetId);
        return JSONArray.parseArray(result.get("data").toString(), String.class);

    }

    @Override
    public int getTrainsetAccMile(String trainsetId, String querydate) {
        if (trainsetId == null) {
            trainsetId = "";
        }
        if (querydate == null) {
            querydate = "";
        }
        // querytype 查询类型，0为查询车组在输入时间的实时走行（当前实时走行）。1为查询车组在输入时间前最后一次回所时对应的走行（回所走行）
        JSONObject result = new RestRequestKitUseLogger<JSONObject>(resumeMidGroundServiceId, logger) {
        }.getObject("/getTrainsetAccMile?trainsetid=" + trainsetId + "&querydate=" + querydate + "&querytype=0");
        String strResult = result.getString("data");
        String[] flagArray = strResult.split("'");
        if (flagArray[1].equals("false")) {
            Map<String, String> map = new HashMap<>();
            map.put("trainsetId", trainsetId);
            map.put("querydate", querydate);
            logger.error("获取走行失败，参数:{},结果{}", map, result);
            return 0;
        }
        String[] strArray = strResult.split("Data:'");
        String strAccMile = strArray[1].substring(0, strArray[1].length() - 2);
        return Integer.parseInt(strAccMile);
    }

    /**
     * TODO 此接口存在问题
     *
     * @param trainsetId
     * @return
     */
    @Override
    public JSONArray getPartListOnTrainset(String trainsetId) {
        // TODO type参数必传
        String type = "455";
        JSONObject result = new RestRequestKitUseLogger<JSONObject>(resumeMidGroundServiceId, logger) {
        }.getObject("/getPartListOnTrainset?trainsetid=" + trainsetId + "&type=" + type);
        return result.getJSONArray("data");
    }

    @Override
    public void addFault(JSONObject jsonObject) {
        ResponseForFaultService<Object> response = new RestRequestKitUseLogger<ResponseForFaultService<Object>>(repairFaultServiceId, logger) {
        }.postObject("/faultFind/addFault", jsonObject);
        response.checkSuccess();
    }

    @Override
    public JSONObject addListItemPerfPool(List<ItemPerformance> itemPerformances) {
        try {
            JSONObject jsonObject = new RestRequestKitUseLogger<JSONObject>(repairTaskId, logger) {
            }.postObject("/outRepairtask/addListItemPerfPool", itemPerformances);
            return jsonObject;
        } catch (Exception exc) {
            logger.error("调用回填实绩接口失败:" + JSONObject.toJSONString(itemPerformances));
            return null;
        }
    }

    @Override
    public void addFaultFind(JSONObject param) {
        ResponseForFaultService<Object> response = new RestRequestKitUseLogger<ResponseForFaultService<Object>>(repairFaultServiceId, logger) {
        }.postObject("/faultFind/addFaultFind", param);
        response.checkSuccess();
    }

    @Override
    public JSONArray getBatchBomNodeListByCarNo(String trainsetId, String sCarNo) {
        JSONObject jsonObject = new RestRequestKitUseLogger<JSONObject>(configurationService, logger) {
        }.getObject("/batchBom/getBatchBomNodeListByCarNo?sTransetId=" + trainsetId + "&sCarNo=" + sCarNo);
        return jsonObject.getJSONArray("rows");
    }

    @Override
    public JSONArray partsTypeByName(String partsTypeName) {
        JSONObject jsonObject = new RestRequestKitUseLogger<JSONObject>(configurationService, logger) {
        }.getObject("/partsType/PartsTypeByName?partsTypeName={1}",
            partsTypeName
        );
        return jsonObject.getJSONArray("data");
    }

    @Override
    public JSONObject getFaultPacket(JSONObject params) {
        return new RestRequestKitUseLogger<JSONObject>(repairTaskId, logger) {
        }.postObject("/packet/getFaultPacket", params);
    }

    @Override
    public void addFaultDeal(JSONObject param) {
        ResponseForFaultService<Object> response = new RestRequestKitUseLogger<ResponseForFaultService<Object>>(repairFaultServiceId, logger) {
        }.postObject("/faultDeal/addFaultDeal", param);
        response.checkSuccess();
    }

    @Override
    public String addFaultQa(AddQaVo addQaVo) {
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("faultQaList[0].faultId", addQaVo.getFaultQaList().get(0).getFaultId());
        map.add("faultQaList[0].dealWithId", addQaVo.getFaultQaList().get(0).getDealWithId());
        map.add("qaMan", addQaVo.getQaMan());
        map.add("qaManCode", addQaVo.getQaManCode());
        map.add("qaResult", addQaVo.getQaResult());
        map.add("resolveDeptCode", addQaVo.getResolveDeptCode());
        map.add("qaTime", addQaVo.getQaTime());
        map.add("qaOutComeDesc", addQaVo.getQaOutComeDesc());
        map.add("qaOutComeCode", addQaVo.getQaOutComeCode());
        map.add("qaBranchCode", addQaVo.getQaBranchCode());
        map.add("qaBranchName", addQaVo.getQaBranchName());
        map.add("qaComment", addQaVo.getQaComment());
        ResponseForFaultService<Object> response = new RestRequestKitUseLogger<ResponseForFaultService<Object>>(repairFaultServiceId, logger) {
        }.postObjectByForm("/faultQa/add", map);
        response.checkSuccess();
        return response.getMessage();
    }


    @Override
    public ResponseForFaultService<Page<JSONObject>> searchQaPage(QueryFaultQA queryFaultQA) {
        ResponseForFaultService<Page<JSONObject>> response = new RestRequestKitUseLogger<ResponseForFaultService<Page<JSONObject>>>(repairFaultServiceId, logger) {
        }.getObject("/faultQa/searchQaPage?carNo={0}&dealDate={1}&dealWithDesc={2}&dealWithId={3}&dfindFaultTime={4}&" +
                "faultDescription={5}&faultId={6}&faultSourceName={7}&locatetionNum={8}&nodeName={9}&repairMan={10}&" +
                "repairMethod={11}&resolveDeptCode={12}&serialNum={13}&sysFunctionName={14}&trainSetName={15}&trainSetNo={16}",
            queryFaultQA.getCarNo(), queryFaultQA.getDealDate(), queryFaultQA.getDealWithDesc(), queryFaultQA.getDealWithId(),
            queryFaultQA.getDfindFaultTime(), queryFaultQA.getFaultDescription(), queryFaultQA.getFaultId(), queryFaultQA.getFaultSourceName(),
            queryFaultQA.getLocatetionNum(), queryFaultQA.getNodeName(), queryFaultQA.getRepairMan(), queryFaultQA.getRepairMethod(),
            queryFaultQA.getResolveDeptCode(), queryFaultQA.getSerialNum(), queryFaultQA.getSysFunctionName(), queryFaultQA.getTrainSetName(),
            queryFaultQA.getTrainSetNo());
        response.checkSuccess();
        return response;
    }


    @Override
    public List<TreeModel> getFunctionClass() {
        ResponseForFaultService<List<TreeModel>> response = new RestRequestKitUseLogger<ResponseForFaultService<List<TreeModel>>>(repairFaultServiceId, logger) {
        }.getObject("/faultPart/getFunctionClassTree");
        response.checkSuccess();
        List<TreeModel> allTreeModels = response.getData();
        List<TreeModel> level = allTreeModels.stream().filter(o -> o.getOrderNum() == 1).collect(Collectors.toList());
        List<TreeModel> treeModels = level.stream().map(o -> {
            TreeModel treeModel = new TreeModel();
            treeModel.setId(o.getId());
            treeModel.setName(o.getName());
            treeModel.setCode(o.getCode());
            return treeModel;
        }).collect(Collectors.toList());
        for (TreeModel treeModel : treeModels) {
            setChild(treeModel, allTreeModels);
        }
        return treeModels;
    }

    private void setChild(TreeModel treeModel, List<TreeModel> dataList) {
        treeModel.setChildren(dataList.stream().filter(o -> o.getsNodeobjectid().equals(treeModel.getId())).map(o -> {
            TreeModel child = new TreeModel();
            child.setId(o.getId());
            child.setName(o.getName());
            child.setCode(o.getCode());
            setChild(child, dataList);
            if (child.getChildren() == null || child.getChildren().size() == 0) {
                child.setChildren(null);
            }
            return child;
        }).collect(Collectors.toList()));
    }

    @Override
    public List<TreePartNodeModel> getBatchBomNodeCode(String trainsetId, String carNoListStr) {
        List<String> carNos = new ArrayList<>();
        if (StringUtils.isBlank(trainsetId)) {
            trainsetId = "";
        }
        if (StringUtils.isNotBlank(carNoListStr)) {
            carNos = Arrays.asList(carNoListStr.split(","));
        }
        List<TreePartNodeModel> allTreePartNodeModels = new ArrayList<>();
        if(StringUtils.isNotBlank(carNoListStr) && !carNoListStr.equals("全列")){
            for (String no : carNos) {
                JSONObject jsonObject = new RestRequestKitUseLogger<JSONObject>(configurationService, logger) {
                }.getObject("/batchBom/getBatchBomNodeListByCarNo?sTransetId=" + trainsetId + "&sCarNo=" + no);
                List<BatchBomNodeEntity> dataList = JSONObject.parseArray(jsonObject.getString("rows"), BatchBomNodeEntity.class);
                if (null == dataList || dataList.size() == 0) {
                    return null;
                }
                List<BatchBomNodeEntity> level1 = dataList.stream().filter(o -> Objects.equals(2L, o.getILevel())).collect(Collectors.toList());
                List<TreePartNodeModel> tree = level1.stream().map(o -> {
                    TreePartNodeModel treeModel = new TreePartNodeModel();
                    treeModel.setName(o.getSNodeName());
                    treeModel.setCode(o.getSNodeCode());
                    treeModel.setKeyFlag(o.getPartsTypeEntity().getCKeyFlag());
                    treeModel.setPartsTypeId(o.getPartsTypeEntity().getSPartsTypeId());
                    treeModel.setPartsTypeName(o.getPartsTypeEntity().getSPartsTypeName());
                    treeModel.setLocationNum(o.getSCarPosition());
                    treeModel.setFunctionClassCode(o.getSFunctionClassCode());
                    treeModel.setLevel(o.getILevel().toString());
                    return treeModel;
                }).collect(Collectors.toList());

                for (TreePartNodeModel c : tree) {
                    setPartNodeChild(c, dataList);
                }
                allTreePartNodeModels.addAll(tree);
            }
        }
        return allTreePartNodeModels;
    }

    private void setPartNodeChild(TreePartNodeModel treeModel, List<BatchBomNodeEntity> dataList) {
        treeModel.setChildren(dataList.stream().filter(o -> o.getSFatherNodeCode().equals(treeModel.getCode())).map(o -> {
            TreePartNodeModel child = new TreePartNodeModel();
            child.setName(o.getSNodeName());
            child.setCode(o.getSNodeCode());
            child.setKeyFlag(o.getPartsTypeEntity().getCKeyFlag());
            child.setPartsTypeId(o.getPartsTypeEntity().getSPartsTypeId());
            child.setPartsTypeName(o.getPartsTypeEntity().getSPartsTypeName());
            child.setLevel(o.getILevel().toString());
            child.setLocationNum(o.getSCarPosition());
            child.setFunctionClassCode(o.getSFunctionClassCode());
            setPartNodeChild(child, dataList);
            if (child.getChildren() == null || child.getChildren().size() == 0) {
                child.setChildren(null);
            }
            return child;
        }).collect(Collectors.toList()));
    }

    @Override
    public Page<JSONObject> getFaultInfoPage(FaultSearch faultSearch) {
        ResponseForFaultService<Page<JSONObject>> response = new RestRequestKitUseLogger<ResponseForFaultService<Page<JSONObject>>>(repairFaultServiceId, logger) {
        }.getObject("/remainFault/getFaultInfoPage?trainSetId={1}&startTime={2}&endTime={3}" +
                "&faultGrade={4}&carNo={5}&faultSourceCode={6}&subFunctionClassId={7}" +
                "&faultPartId={8}&locatetionNum={9}&serialNum={10}&partTypeId={11}" +
                "&faultFindUnitCode={12}&orgCode={13}&faultFindBranchCode={14}&dealWithDescCode={15}" +
                "&dealMethodCode={16}&repairManCode={17}&dealBranchCode={18}&dealStartTime={19}" +
                "&dealEndTime={20}&faultTreeId={21}&dealDeptName={22}&pageNum={23}&pageSize={24}",
            faultSearch.getTrainsetId(),
            faultSearch.getStartTime(),
            faultSearch.getEndTime(),
            faultSearch.getFaultGrade(),
            faultSearch.getCarNo(),
            faultSearch.getFaultSourceCode(),
            faultSearch.getSubFunctionClassId(),
            faultSearch.getFaultPartId(),
            faultSearch.getLocatetionNum(),
            faultSearch.getSerialNum(),
            faultSearch.getPartTypeId(),
            faultSearch.getFaultFindUnitCode(),
            faultSearch.getOrgCode(),
            faultSearch.getFaultFindBranchCode(),
            faultSearch.getDealWithDescCode(),
            faultSearch.getDealMethodCode(),
            faultSearch.getRepairManCode(),
            faultSearch.getDealBranchCode(),
            faultSearch.getDealStartTime(),
            faultSearch.getDealEndTime(),
            faultSearch.getFaultTreeId(),
            faultSearch.getDealDeptName(),
            faultSearch.getPageNum(),
            faultSearch.getPageSize()
        );
        response.checkSuccess();
        return response.getData();
    }

    @Override
    public List<FaultWithKeyWork> getFaultInfoByPlanlessKey(FaultSearch faultSearch) {
        ResponseForFaultService<Page<FaultWithKeyWork>> response = new RestRequestKitUseLogger<ResponseForFaultService<Page<FaultWithKeyWork>>>(repairFaultServiceId, logger) {
        }.getObject("/remainFault/getFaultInfoPage?trainSetId={1}&startTime={2}&endTime={3}" +
                "&faultGrade={4}&carNo={5}&faultSourceCode={6}&subFunctionClassId={7}" +
                "&faultPartId={8}&locatetionNum={9}&serialNum={10}&partTypeId={11}" +
                "&faultFindUnitCode={12}&orgCode={13}&faultFindBranchCode={14}&dealWithDescCode={15}" +
                "&dealMethodCode={16}&repairManCode={17}&dealBranchCode={18}&dealStartTime={19}" +
                "&dealEndTime={20}&faultTreeId={21}&dealDeptName={22}&pageNum={23}&pageSize={24}",
            faultSearch.getTrainsetId(),
            faultSearch.getStartTime(),
            faultSearch.getEndTime(),
            faultSearch.getFaultGrade(),
            faultSearch.getCarNo(),
            faultSearch.getFaultSourceCode(),
            faultSearch.getSubFunctionClassId(),
            faultSearch.getFaultPartId(),
            faultSearch.getLocatetionNum(),
            faultSearch.getSerialNum(),
            faultSearch.getPartTypeId(),
            faultSearch.getFaultFindUnitCode(),
            faultSearch.getOrgCode(),
            faultSearch.getFaultFindBranchCode(),
            faultSearch.getDealWithDescCode(),
            faultSearch.getDealMethodCode(),
            faultSearch.getRepairManCode(),
            faultSearch.getDealBranchCode(),
            faultSearch.getDealStartTime(),
            faultSearch.getDealEndTime(),
            faultSearch.getFaultTreeId(),
            faultSearch.getDealDeptName(),
            "1",
            "10000"
        );
        response.checkSuccess();
        return response.getData().getRecords();
    }

    @Override
    public List<ZtTaskPositionEntity> getTrackPositionByTrackCode(String trackCode, String unitCode) {
        List<ZtTaskPositionEntity> ztTaskPositionEntities = new RestRequestKitUseLogger<List<ZtTaskPositionEntity>>(repairTaskId, logger) {
        }.getObject("/yard/getTrackPositionByTrackCode?trackCode=" + trackCode + "&deptCode=" + unitCode);
        return ztTaskPositionEntities;
    }


    /***
     * @author:冯帅
     * @desc: 批量添加故障复核任务
     * @date: 2021/8/30
     * @param: [repairPacketStatuList]
     * @return: com.alibaba.fastjson.JSONObject
     */
    @Override
    public JSONObject addFaultReCheckPacketList(List<ZtTaskPacketEntity> ztTaskPacketEntityList) {
        JSONObject res = new JSONObject();
        res = new RestRequestKitUseLogger<JSONObject>(repairTaskId, logger) {
        }.postObject("/Task/addFaultReCheckPacketList", ztTaskPacketEntityList);
        return res;
    }

    @Override
    public JSONObject updateRepairPacketStatus(List<RepairPacketStatu> repairPacketStatuList) {
        try {
            JSONObject res = new JSONObject();
            res = new RestRequestKitUseLogger<JSONObject>(repairTaskId, logger) {
            }.postObject("/Task/updateRepairTaskStatus", repairPacketStatuList);
            //错误写日志
            if (res.get("result").equals("false")) {
                String errorLogContent = "";
                for (RepairPacketStatu repairPacketStatu :
                    repairPacketStatuList) {
                    errorLogContent = repairPacketStatu.getSDayplanid() + "," + repairPacketStatu.getSTrainsetid() + "," + repairPacketStatu.getSItemcode() + "," + repairPacketStatu.getCStatue() + ";";
                }
                logger.error("调用计划接口updateRepairTaskStatus失败:" + errorLogContent);
            }
            return res;
        } catch (Exception exc) {
            throw exc;
        }
    }

    @Override
    public LeaveBackTrainNoResult getLeavBackTrainNo(String trainsetId, String date) {
        try {
            LeaveBackTrainNoQuery leaveBackTrainNoQuery = new LeaveBackTrainNoQuery();
            leaveBackTrainNoQuery.setVDate(date);
            leaveBackTrainNoQuery.setVTrainsetid(trainsetId);

            JSONObject param = new JSONObject();
            param.put("params", leaveBackTrainNoQuery);
            JSONObject result = new JSONObject();
            result = new RestRequestKitUseLogger<JSONObject>(repairTaskId, logger) {
            }.postObject("/operate/out/getLeaveBackTrainNo", param.toJSONString());
            if (!ObjectUtils.isEmpty(result) && "200".equals(result.getString("code"))) {
                LeaveBackTrainNoResult leaveBackTrainNoResult = new LeaveBackTrainNoResult();
                JSONArray jsonArray = result.getJSONArray("result");
                if (!CollectionUtils.isEmpty(jsonArray)) {
                    JSONObject jsonObject = (JSONObject) jsonArray.get(0);
                    if (!ObjectUtils.isEmpty(jsonObject)) {
                        String backTrainNo = jsonObject.getString("backTrainNo");
                        String depTrainNo = jsonObject.getString("depTrainNo");
                        leaveBackTrainNoResult.setBackTrainNo(backTrainNo);
                        leaveBackTrainNoResult.setDepTrainNo(depTrainNo);
                        return leaveBackTrainNoResult;
                    } else {
                        return null;
                    }
                } else {
                    return null;
                }
            } else {
                return null;
            }
        } catch (Exception exc) {
            logger.error("getLeavBackTrainNo调用计划获取出入组车次接口报错：", exc);
            return null;
        }
    }

    @Override
    public RecheckPacket getRecheckPacket() {
        JSONObject jsonObject = new RestRequestKitUseLogger<JSONObject>(repairTaskId, logger) {
        }.getObject("/packet/getRecheckPacket");
        RecheckPacket recheckPacket = JSON.parseObject(jsonObject.getString("result"), RecheckPacket.class);
        return recheckPacket;
    }

    @Override
    public FaultPacket getFaultPacket() {
        JSONObject jsonObject = new RestRequestKitUseLogger<JSONObject>(repairTaskId, logger) {
        }.postObject("/packet/getFaultPacket", new HashMap<>());
        List<FaultPacket> faultPacket = JSONArray.parseArray(jsonObject.getString("result"), FaultPacket.class);
        if (faultPacket.size() > 0) {
            return faultPacket.get(0);
        }
        return null;
    }

    /**
     * 根据车组号日期获取车轴车轮信息
     *
     * @param trainSetId 车组ID
     * @param trainsetNo 辆序（两位）
     * @param queryDate  日期（20211020）
     * @return 轮轴数据集合
     */
    @Override
    public List<AxleWheel> getPartByTrainSetId(String trainSetId, String trainsetNo, String queryDate) {
        JSONObject result = new RestRequestKitUseLogger<JSONObject>(resumeMidGroundServiceId, logger) {
        }.getObject("/getPartByTrainSetId?trainSetId=" + trainSetId + "&trainsetNo=" + trainsetNo + "&queryDate=" + queryDate);
        String code = result.getString("code");
        if (code.equals("0")) {
            List<AxleWheel> lstAxleWheel = JSONArray.parseArray(result.getString("data"), AxleWheel.class);
            if (lstAxleWheel != null && lstAxleWheel.size() > 0) {
                return lstAxleWheel;
            } else {
                logger.warn(String.format("根据车组ID[%s]、车号[%s]和日期[%s]获取轮轴数据时接口返回成功，但没有获取到轮轴数据", trainSetId, trainsetNo, queryDate));
                return null;
            }
        } else {
            String msg = result.getString("msg");
            logger.warn(String.format("根据车组ID[%s]、车号[%s]和日期[%s]获取轮轴数据时接口返回失败，接口返回信息：%s", trainSetId, trainsetNo, queryDate, msg));
            return null;
        }
    }

    /**
     * 根据车组ID获取该车组所有车轮的最新镟轮数据
     *
     * @param trainSetId 车组ID
     * @return 所有车轮最新的镟轮数据集合
     */
    @Override
    public List<AxleWheelDiameterEntity> getLatestAxleTurningdata(String trainSetId) {
        try {
            JSONObject result = new RestRequestKitUseLogger<JSONObject>(resumeMidGroundServiceId, logger) {
            }.getObject("/getLatestAxleTurningdata?trainSetID=" + trainSetId);
            String data = result.getString("data");
            if (data == null || data == "") {
                logger.warn(String.format("根据车组ID[%s]获取该车组所有车轮的最新镟轮数据时接口返回成功，但没有获取到镟轮数据", trainSetId));
                return null;
            }
            List<AxleWheelDiameterEntity> lathingDatas = JSONArray.parseArray(data, AxleWheelDiameterEntity.class);
            if (lathingDatas != null && lathingDatas.size() > 0) {
                return lathingDatas;
            } else {
                logger.warn(String.format("根据车组ID[%s]获取该车组所有车轮的最新镟轮数据时接口返回成功，但没有获取到镟轮数据", trainSetId));
                return null;
            }
        } catch (Exception ex) {
            logger.error("根据车组ID获取该车组所有车轮的最新镟轮数据", ex);
            return null;
        }
    }

    /**
     * 根据项目编码获取项目详细信息
     *
     * @param itemCode 项目编码
     * @return 项目的详细信息
     */
    @Override
    public RepairItemInfo getItemInfoByCode(String itemCode, String unitCode) {
        try {
            Map<String, Object> params = new HashMap<>();
            List<String> lstItemCodes = new ArrayList<String>();
            lstItemCodes.add(itemCode);
            params.put("repairItemCodeList", lstItemCodes);
            params.put("depotCode", "");
            JSONObject result = new RestRequestKitUseLogger<JSONObject>(repairItemId, logger) {
            }.postObject("/itemForeign/selectRepairItemListByItemCodeList", params);
            String code = result.getString("code");
            if (code.equals("1")) {
                logger.warn(String.format("调用ITEM接口selectRepairItemListByItemCodeList(%s)时，返回失败", itemCode));
                return null;
            }
            String total = result.getString("total");
            if (total.equals("0")) {
                logger.warn(String.format("调用ITEM接口selectRepairItemListByItemCodeList(%s)时，返回0条数据", itemCode));
                return null;
            }
            String rows = result.getString("rows");
            List<RepairItemInfo> items = JSONArray.parseArray(rows, RepairItemInfo.class);
            if (items == null && items.size() == 0) {
                logger.warn(String.format("调用ITEM接口selectRepairItemListByItemCodeList(%s)时，返回数据集为空或长度为0", itemCode));
                return null;
            }
            return items.get(0);
        } catch (Exception ex) {
            logger.error("根据项目编码获取项目详细信息", ex);
            return null;
        }
    }

    @Override
    public List<Unit> getUnitList() {
        List<Unit> unitList = new ArrayList<>();
        String depotCode = ContextUtils.getDepotCode();
        if (StringUtils.isNotBlank(depotCode)) {
            boolean isCenter = ContextUtils.isCenter();
            List<SysOrgan> sysOrganList = OrganUtil.getOranListByParent(depotCode, "08");// 08动车段
            if (sysOrganList != null) {
                unitList.addAll(
                    sysOrganList.stream().filter(
                        v -> isCenter || Objects.equals(v.getOrganCode(), ContextUtils.getUnitCode())
                    ).map(
                        item -> {
                            Unit unit = new Unit();
                            unit.setUnitCode(item.getOrganCode());
                            unit.setUnitName(item.getOrganName());
                            unit.setUnitAbbr(item.getShortName());
                            return unit;
                        }
                    ).collect(Collectors.toList())
                );
            }
        }
        return unitList;
    }

    @Override
    public List<Temple> getTramsByUnitCodeAndDeptType(String unitCode, String deptType) {
        List<Temple> workTeamList = new ArrayList<>();
        List<SysOrgan> list = OrganUtil.getOranListStruByParent(unitCode, deptType);
        for (SysOrgan sysUnit : list) {
            Temple t1 = new Temple();
            t1.setId(sysUnit.getOrganCode());
            t1.setName(sysUnit.getOrganName());
            workTeamList.add(t1);
        }
        workTeamList.sort(Comparator.comparing(Temple::getName));
        return workTeamList;
    }

    @Override
    public List<FaultSource> getFaultSource() {
        ResponseForFaultService<List<FaultSource>> result = new RestRequestKitUseLogger<ResponseForFaultService<List<FaultSource>>>(repairFaultServiceId, logger) {
        }.getObject("/FaultSourceDict/getFaultSource");
        result.checkSuccess();
        return result.getData();
    }

    @Override
    public JSONArray getFaultSourceNew() {
        ResponseForFaultService<JSONArray> result = new RestRequestKitUseLogger<ResponseForFaultService<JSONArray>>(repairFaultServiceId, logger) {
        }.getObject("/FaultSourceDict/getFaultSourceNew");
        result.checkSuccess();
        return result.getData();
    }

    @Override
    public JSONArray getDealWith(String dealWithType) {
        ResponseForFaultService<JSONArray> response = new RestRequestKitUseLogger<ResponseForFaultService<JSONArray>>(repairFaultServiceId, logger) {
        }.getObject("/faultPart/getDealWith?dealWithType=" + dealWithType);
        response.checkSuccess();
        return response.getData();
    }

    @Override
    public Page<JSONObject> selectQaRecordPage(QueryFaultQaRecord queryFaultQaRecord) {
        ResponseForFaultService<Page<JSONObject>> response = new RestRequestKitUseLogger<ResponseForFaultService<Page<JSONObject>>>(repairFaultServiceId, logger) {
        }.getObject("/faultQa/selectQaRecordPage?deptCode={1}&trainSetNo={2}&findStartDate={3}&findEndDate={4}" +
                "&dealStartDate={5}&dealEndDate={6}&qaStartDate={7}&qaEndDate={8}&qaManCode={9}&qaBranchCode={10}" +
                "&qaOutComeCode={11}&pageNum={12}&pageSize={13}&faultId={14}", queryFaultQaRecord.getDeptCode(), queryFaultQaRecord.getTrainSetNo(),
            queryFaultQaRecord.getFindStartDate(), queryFaultQaRecord.getFindEndDate(), queryFaultQaRecord.getDealStartDate(),
            queryFaultQaRecord.getDealEndDate(), queryFaultQaRecord.getQaStartDate(), queryFaultQaRecord.getQaEndDate(),
            queryFaultQaRecord.getQaManCode(), queryFaultQaRecord.getQaBranchCode(),
            queryFaultQaRecord.getQaOutComeCode(), queryFaultQaRecord.getPageNum(), queryFaultQaRecord.getPageSize(), queryFaultQaRecord.getFaultId());
        response.checkSuccess();
        return response.getData();
    }

    @Override
    public List<Role> getAllRoleList() {
        ResponseData<List<JSTreeModel>> responseData = new RestRequestKitUseLogger<ResponseData<List<JSTreeModel>>>(userServiceId, logger) {
        }.getObject("/ydzbinfoRest/role/getRoleList?deptId={1}", ContextUtils.getDepotId());
        return responseData.getData().stream().filter(
            v -> "ROLE".equals(v.getType())
        ).map(
            v -> {
                Role role = new Role();
                role.setCode(v.getId());
                role.setName(v.getText());
                return role;
            }
        ).collect(Collectors.toList());
    }

    @Override
    public JSONArray getFaultDataByFaultId(String faultId) {
        ResponseForFaultService<JSONArray> response = new RestRequestKitUseLogger<ResponseForFaultService<JSONArray>>(repairFaultServiceId, logger) {
        }.getObject("/fault/getFaultDataByFaultId?faultId=" + faultId);
        response.checkSuccess();
        return response.getData();
    }

    @Override
    public JSONArray filedownload(String faultId, String fileName) {
        ResponseForFaultService<JSONArray> response = new RestRequestKitUseLogger<ResponseForFaultService<JSONArray>>(repairFaultServiceId, logger) {
        }.getObject("/faultFind/findFileByFileIdAndFileName?faultId=" + faultId + "&fileName=" + fileName);
        response.checkSuccess();
        return response.getData();
    }

    @Override
    public List<FaultFile> getFaultFileByFaultId(String faultId) {
        ResponseForFaultService<List<FaultFile>> response = new RestRequestKitUseLogger<ResponseForFaultService<List<FaultFile>>>(repairFaultServiceId, logger) {
        }.getObject("/fault/getFaultDataByFaultId?faultId=" + faultId);
        response.checkSuccess();
        return response.getData();
    }
}

