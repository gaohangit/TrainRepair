package com.ydzbinfo.emis.trainRepair.mobile.taskallot.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.jxdinfo.hussar.core.shiro.ShiroKit;
import com.jxdinfo.hussar.core.shiro.ShiroUser;
import com.ydzbinfo.emis.configs.kafka.taskallot.TaskAllotConfigMqSource;
import com.ydzbinfo.emis.guns.config.RecheckTaskProperties;
import com.ydzbinfo.emis.trainRepair.common.service.IRemoteService;
import com.ydzbinfo.emis.trainRepair.common.service.IRepairMidGroundService;
import com.ydzbinfo.emis.trainRepair.mobile.model.*;
import com.ydzbinfo.emis.trainRepair.remotemodel.resume.TrainsetBaseInfo;
import com.ydzbinfo.emis.trainRepair.remotemodel.resume.TrainsetInfo;
import com.ydzbinfo.emis.trainRepair.remotemodel.trainuse.RecheckPacket;
import com.ydzbinfo.emis.trainRepair.remotemodel.trainuse.ZtTaskItemEntity;
import com.ydzbinfo.emis.trainRepair.remotemodel.trainuse.ZtTaskPacketEntity;
import com.ydzbinfo.emis.trainRepair.taskAllot.controller.ConeAllotConfigController;
import com.ydzbinfo.emis.trainRepair.taskAllot.model.*;
import com.ydzbinfo.emis.trainRepair.taskAllot.querymodel.*;
import com.ydzbinfo.emis.trainRepair.taskAllot.service.*;
import com.ydzbinfo.emis.trainRepair.mobile.taskallot.service.IPhoneTaskAllotPacketService;
import com.ydzbinfo.emis.trainRepair.taskAllot.model.pojo.EntitySJOverRunRecord;
import com.ydzbinfo.emis.trainRepair.taskAllot.model.pojo.JsonRootBean;
import com.ydzbinfo.emis.trainRepair.taskAllot.model.pojo.PacketData;
import com.ydzbinfo.emis.utils.*;
import com.ydzbinfo.hussar.core.util.ToolUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Description: ??????????????????????????????
 * <p>
 * Author: wuyuechang
 * Create Date Time: 2021/4/23 14:02
 * Update Date Time: 2021/4/23 14:02
 *
 * @see
 */
@Service
@Slf4j
public class PhoneTaskAllotPacketServiceImpl implements IPhoneTaskAllotPacketService {

    protected static final Logger logger = LoggerFactory.getLogger(PhoneTaskAllotPacketServiceImpl.class);

    @Autowired
    private IAllotBranchConfigService cAllotbranchConfigService;

    @Autowired
    private IRemoteService remoteService;

    @Autowired
    private RecheckTaskProperties recheckTaskProperties;

    @Autowired
    private XzyMTaskAllotDeptService taskAllotDeptService;

    //PC????????????????????????
    @Autowired
    private XzyMTaskallotpacketService xzyMTaskallotpacketService;

    @Autowired
    IReCheckTaskService reCheckTaskService;

    @Autowired
    IRepairMidGroundService midGroundService;

    @Autowired
    XzyMTaskcarpartService taskcarpartService;

    @Autowired
    IXzyCOneallotConfigService xzyCOneallotConfigService;

    @Autowired
    IXzyCOneallotTemplateService oneallotTemplateService;

    @Autowired(required = false)
    private TaskAllotConfigMqSource taskAllotConfigSource;

    @Autowired
    private ConeAllotConfigController coneAllotConfigController;

    @Autowired
    IXzyBTaskallotshowDictService xzyBTaskallotshowDictService;

    @Override
    public JSONObject getPhoneTaskAllot(String unitCode, String dayPlanId, String deptCode) {
        JSONObject result = new JSONObject();
        JSONObject repairTask = new JSONObject();
        try {
            //1.??????PC????????????????????????
            repairTask = xzyMTaskallotpacketService.getRepairTask(unitCode, dayPlanId, deptCode, null, "1");
        } catch (Exception e) {
            logger.error("???????????????PC??????????????????????????????", e);
        }
        JSONArray data = repairTask.getJSONArray("data");
        JSONArray queryList = repairTask.getJSONArray("queryList");
        JSONArray allotTypeDict = repairTask.getJSONArray("allotTypeDict");
        JSONArray taskAllotTypeDict = repairTask.getJSONArray("taskAllotTypeDict");
        //2.??????????????????AllotData??????????????????
        List<AllotData> allotDataList = JSONArray.parseArray(data.toJSONString(), AllotData.class);
        if (!CollectionUtils.isEmpty(allotDataList)) {
            //?????????????????????
            List<PacketType> packetTypeArrayList = new ArrayList<>();
            //??????????????????
            List<String> trainsetNames = allotDataList.stream().map(AllotData::getTrainsetName).distinct().collect(Collectors.toList());
            Set<String> packetTypes = new HashSet<>();
            List<String> iMarshalcounts = new ArrayList<>();
            //????????????
            List<String> trainsetIdList = allotDataList.stream().map(AllotData::getTrainsetId).distinct().collect(Collectors.toList());
            //???????????????????????????????????????
            List<XzyBTaskallotshowDict> xzyBTaskallotshowDictList = xzyBTaskallotshowDictService.getShowDictByTaskAllotType("");
            //3.???????????????????????????????????????
            JSONArray faultData = new JSONArray();
            try {
                if (!CollectionUtils.isEmpty(trainsetIdList)) {
                    JSONObject faultParams = new JSONObject();
                    faultParams.put("findFaultTime", dayPlanId.substring(0, dayPlanId.length() - 3));
                    faultParams.put("trainsetIdList", trainsetIdList);
                    faultData = remoteService.getFaultData(faultParams);
                }
            } catch (Exception e) {
                logger.error("???????????????????????????????????????", e);
            }

            //4.?????????????????????????????????????????????
            List<EntitySJOverRunRecord> entitySJOverRunRecordList = new ArrayList<>();
            try {
                Map<String, Object> overRunParams = new HashMap<>();
                overRunParams.put("TrainsetNameList", trainsetNames);
                entitySJOverRunRecordList = reCheckTaskService.getReCheckTaskList(overRunParams);
            } catch (Exception ex) {
                logger.error("???????????????????????????????????????", ex);
            }

            List<PhoneAllotData> phoneAllotDataArrayList = new ArrayList<>();
            Map<String, List<AllotData>> allotDataMap = allotDataList.stream().collect(Collectors.groupingBy(AllotData::getTrainsetId));
            for (String trainsetId : allotDataMap.keySet()) {
                //???????????????
                String iMarshalcount = allotDataMap.get(trainsetId).get(0).getMarshal();
                String trainsetName = allotDataMap.get(trainsetId).get(0).getTrainsetName();
                String trainsetType = allotDataMap.get(trainsetId).get(0).getTrainsetTypeCode();
                logger.info("?????????????????????????????????{}", iMarshalcount);
                iMarshalcounts.add(iMarshalcount);
                PhoneAllotData phoneAllotData = new PhoneAllotData();
                BeanUtils.copyProperties(allotDataMap.get(trainsetId).get(0), phoneAllotData);
                List<PacketData> packetDataArrayList = new ArrayList<>();
                for (AllotData allotData : allotDataMap.get(trainsetId)) {
                    PacketData packetData = new PacketData();
                    packetData.setWorkTypeCode(allotData.getWorkTypeCode());
                    packetData.setWorkTypeName(allotData.getWorkTypeName());
                    packetData.setWorkTaskCode(allotData.getWorkTaskCode());
                    packetData.setWorkTaskName(allotData.getWorkTaskName());
                    packetData.setWorkTaskDisplayName(allotData.getWorkTaskDisplayName());
                    packetData.setAllotStateCode(allotData.getAllotStateCode());
                    packetData.setAllotStateName(allotData.getAllotStateName());
                    packetData.setWorkModeTypeCode(allotData.getWorkModeTypeCode());
                    packetData.setWorkModeTypeName(allotData.getWorkModeTypeName());
                    XzyMTaskallotpacket packet = allotData.getPacket();
                    if (!ObjectUtils.isEmpty(packet)) {
                        packetData.setPacket(packet);
                        //?????????????????????
                        String packetType = packet.getPacketType();
                        packetTypes.add(packetType);
                    }
                    packetData.setShowMode(allotData.getShowMode());
                    packetDataArrayList.add(packetData);
                }
                /** ?????????????????????????????????????????????????????????????????????
                 //???????????????????????????
                 List<AllotData> currentAllotDataList = allotDataMap.get(trainsetId);
                 //?????????????????????
                 long wCount = currentAllotDataList.stream().filter(t -> "0".equals(t.getAllotStateCode())).count();
                 //?????????????????????
                 long yCount = currentAllotDataList.stream().filter(t -> "1".equals(t.getAllotStateCode())).count();
                 //????????????????????????
                 long bCount = currentAllotDataList.stream().filter(t -> "2".equals(t.getAllotStateCode())).count();
                 if (bCount > 0) {
                 phoneAllotData.setAllotStateCode("2");
                 phoneAllotData.setAllotStateName("????????????");
                 } else if (wCount > 0) {
                 if (yCount > 0) {
                 phoneAllotData.setAllotStateCode("2");
                 phoneAllotData.setAllotStateName("????????????");
                 } else {
                 phoneAllotData.setAllotStateCode("0");
                 phoneAllotData.setAllotStateName("?????????");
                 }
                 } else if (yCount > 0) {
                 phoneAllotData.setAllotStateCode("1");
                 phoneAllotData.setAllotStateName("?????????");
                 } else {
                 phoneAllotData.setAllotStateCode("0");
                 phoneAllotData.setAllotStateName("?????????");
                 }**/

                //5.??????????????????????????????????????????????????????????????????
                if (faultData != null && faultData.size() > 0) {
                    //5.1??????????????????????????????????????????
                    PacketData faultPacketData = packetDataArrayList.stream().filter(t -> !ObjectUtils.isEmpty(t.getPacket()) && "5".equals(t.getPacket().getPacketType())).findFirst().orElse(null);
                    if (ObjectUtils.isEmpty(faultPacketData)) {//5.2 new????????????????????????????????????????????????????????????
                        faultPacketData = new PacketData();
                        JSONObject faultPacketInfo = this.getFaultPacketInfo();
                        if (!ObjectUtils.isEmpty(faultPacketInfo)) {
                            faultPacketData.setWorkTaskDisplayName(faultPacketInfo.getString("S_PACKET_NAME"));
                            faultPacketData.setWorkTypeCode(faultPacketInfo.getString("S_PACKET_TYPE"));
                            faultPacketData.setWorkTaskCode(faultPacketInfo.getString("S_PACKET_CODE"));
                            faultPacketData.setWorkTaskName(faultPacketInfo.getString("S_PACKET_NAME"));
                            faultPacketData.setNewPacket("1");
                            faultPacketData.setAllotStateCode("0");
                            faultPacketData.setAllotStateName("?????????");

                            XzyMTaskallotpacket taskAllotPacket = new XzyMTaskallotpacket();
                            taskAllotPacket.setPacketName(faultPacketInfo.getString("S_PACKET_NAME"));
                            taskAllotPacket.setPacketType(faultPacketInfo.getString("S_PACKET_TYPE"));
                            taskAllotPacket.setPacketCode(faultPacketInfo.getString("S_PACKET_CODE"));
                            taskAllotPacket.setStaskId(UUID.randomUUID().toString());
                            taskAllotPacket.setMainCyc("-1");
                            taskAllotPacket.setTrainsetId(trainsetId);


                            faultPacketData.setPacket(taskAllotPacket);
                        } else {
                            logger.info("?????????????????????????????????????????????");
                        }
                    }
                    packetDataArrayList.remove(faultPacketData);
                    //5.2?????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
                    //   ??????????????????????????????????????????????????????????????????????????????????????????
                    if (!ObjectUtils.isEmpty(faultPacketData.getPacket())) {//?????????????????????????????????????????????????????????????????????????????????????????????????????????
                        List<XzyMTaskallotItem> faultItemList = faultPacketData.getPacket().getTaskallotItemList();
                        if (CollectionUtils.isEmpty(faultItemList)) {
                            faultItemList = new ArrayList<>();
                        }
                        faultPacketData.getPacket().setTaskallotItemList(new ArrayList<>());
                        for (Object o : faultData) {
                            JSONObject fault = (JSONObject) o;
                            String faultId = fault.getString("faultId");
                            String faultTrainsetId = fault.getString("trainSetId");
                            if (trainsetId.equals(faultTrainsetId)) {//??????????????????????????????
                                XzyMTaskcarpart carPart = new XzyMTaskcarpart();
                                carPart = this.getTaskAllotCarPart(unitCode, "", dayPlanId, trainsetId, trainsetName, fault.getString("carNo"), "5", fault);
                                String carNo = carPart.getCarNo();
                                XzyMTaskallotItem xzyMTaskallotItem = faultItemList.stream().filter(t -> !ObjectUtils.isEmpty(faultId) && faultId.equals(t.getItemCode())).findFirst().orElse(null);
                                List<XzyMTaskcarpart> xzyMTaskcarpartList = new ArrayList<>();
                                if (!ObjectUtils.isEmpty(xzyMTaskallotItem)) {
                                    xzyMTaskcarpartList = xzyMTaskallotItem.getXzyMTaskcarpartList();
                                    if (!CollectionUtils.isEmpty(xzyMTaskcarpartList)) {
                                        if (!xzyMTaskcarpartList.stream().anyMatch(t -> t.getCarNo().equals(carNo))) {
                                            xzyMTaskcarpartList.add(carPart);
                                            faultPacketData.setAllotStateCode("2");
                                            faultPacketData.setAllotStateName("????????????");
                                        }
                                    } else {
                                        xzyMTaskcarpartList.add(carPart);
                                        faultPacketData.setAllotStateCode("2");
                                        faultPacketData.setAllotStateName("????????????");
                                    }
                                    xzyMTaskallotItem.setXzyMTaskcarpartList(xzyMTaskcarpartList);
                                } else {
                                    xzyMTaskallotItem = new XzyMTaskallotItem();
                                    xzyMTaskallotItem.setItemCode(carPart.getItemCode());
                                    xzyMTaskallotItem.setItemName(carPart.getItemName());
                                    xzyMTaskallotItem.setDisplayItemName(carPart.getDisplayCarPartName());
                                    xzyMTaskallotItem.setNewItem("1");
                                    xzyMTaskcarpartList.add(carPart);
                                    xzyMTaskallotItem.setXzyMTaskcarpartList(xzyMTaskcarpartList);
                                    faultItemList.add(xzyMTaskallotItem);
                                }
                            }
                        }
                        faultPacketData.getPacket().setTaskallotItemList(faultItemList);
                        if (!CollectionUtils.isEmpty(faultItemList)) {
                            if (faultItemList.stream().anyMatch(t -> "1".equals(t.getNewItem()))) {
                                if (faultItemList.stream().anyMatch(t -> !CollectionUtils.isEmpty(t.getWorkerList()))) {
                                    faultPacketData.setAllotStateCode("2");
                                    faultPacketData.setAllotStateName("????????????");
                                } else {
                                    faultPacketData.setAllotStateCode("0");
                                    faultPacketData.setAllotStateName("?????????");
                                }
                            }
                            //??????????????????
                            if(!CollectionUtils.isEmpty(xzyBTaskallotshowDictList)){
                                XzyBTaskallotshowDict filterShowDict = xzyBTaskallotshowDictList.stream().filter(t -> "5".equals(t.getItemTypeCode())).findFirst().orElse(null);
                                if(!ObjectUtils.isEmpty(filterShowDict)){
                                    faultPacketData.setShowMode(filterShowDict.getsTaskallotshowcode());
                                }
                            }
                            packetDataArrayList.add(faultPacketData);
                        }
                    }
                }

                //6.????????????????????????????????????????????????????????????????????????
                if (!CollectionUtils.isEmpty(entitySJOverRunRecordList)) {
                    PacketData overRunPacketData = packetDataArrayList.stream().filter(t -> !ObjectUtils.isEmpty(t.getPacket()) && "12".equals(t.getPacket().getPacketType())).findFirst().orElse(null);
                    if (ObjectUtils.isEmpty(overRunPacketData)) {//6.2 new????????????????????????????????????????????????????????????
                        overRunPacketData = new PacketData();
                        RecheckPacket overRunPacketInfo = this.getOverRunPacketInfo();
                        if (!ObjectUtils.isEmpty(overRunPacketInfo)) {
                            overRunPacketData.setWorkTaskDisplayName(overRunPacketInfo.getPacketName());
                            overRunPacketData.setWorkTypeCode(overRunPacketInfo.getPacketType());
                            overRunPacketData.setWorkTaskCode(overRunPacketInfo.getPacketCode());
                            overRunPacketData.setWorkTaskName(overRunPacketInfo.getPacketName());
                            overRunPacketData.setNewPacket("1");
                            overRunPacketData.setAllotStateCode("0");
                            overRunPacketData.setAllotStateName("?????????");

                            XzyMTaskallotpacket taskAllotPacket = new XzyMTaskallotpacket();
                            taskAllotPacket.setPacketName(overRunPacketInfo.getPacketName());
                            taskAllotPacket.setPacketType(overRunPacketInfo.getPacketType());
                            taskAllotPacket.setPacketCode(overRunPacketInfo.getPacketCode());
                            taskAllotPacket.setStaskId(UUID.randomUUID().toString());
                            taskAllotPacket.setMainCyc("-1");
                            taskAllotPacket.setTrainsetId(trainsetId);

                            overRunPacketData.setPacket(taskAllotPacket);
                        } else {
                            logger.info("?????????????????????????????????????????????");
                        }
                    }
                    packetDataArrayList.remove(overRunPacketData);
                    //6.3?????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
                    //   ??????????????????????????????????????????????????????????????????????????????????????????
                    if (!ObjectUtils.isEmpty(overRunPacketData.getPacket())) {//?????????????????????????????????????????????????????????????????????????????????????????????????????????
                        List<XzyMTaskallotItem> overRunItemList = overRunPacketData.getPacket().getTaskallotItemList();
                        if (CollectionUtils.isEmpty(overRunItemList)) {
                            overRunItemList = new ArrayList<>();
                        }
                        overRunPacketData.getPacket().setTaskallotItemList(new ArrayList<>());
                        for (EntitySJOverRunRecord overRunRecord : entitySJOverRunRecordList) {
                            String overRunId = overRunRecord.getId();
                            String overRunTrainsetId = overRunRecord.getTrainsetId();
                            if (trainsetId.equals(overRunTrainsetId)) {//????????????????????????????????????
                                XzyMTaskcarpart carPart = new XzyMTaskcarpart();
                                carPart = this.getTaskAllotCarPart(unitCode, "", dayPlanId, trainsetId, trainsetName, overRunRecord.getCarNo(), "12", overRunRecord);
                                String carNo = carPart.getCarNo();
                                XzyMTaskallotItem xzyMTaskallotItem = overRunItemList.stream().filter(t -> !ObjectUtils.isEmpty(overRunId) && overRunId.equals(t.getItemCode())).findFirst().orElse(null);
                                List<XzyMTaskcarpart> xzyMTaskcarpartList = new ArrayList<>();
                                if (!ObjectUtils.isEmpty(xzyMTaskallotItem)) {
                                    xzyMTaskcarpartList = xzyMTaskallotItem.getXzyMTaskcarpartList();
                                    if (!CollectionUtils.isEmpty(xzyMTaskcarpartList)) {
                                        if (!xzyMTaskcarpartList.stream().anyMatch(t -> t.getCarNo().equals(carNo))) {
                                            xzyMTaskcarpartList.add(carPart);
                                            overRunPacketData.setAllotStateCode("2");
                                            overRunPacketData.setAllotStateName("????????????");
                                        }
                                    } else {
                                        xzyMTaskcarpartList.add(carPart);
                                        overRunPacketData.setAllotStateCode("2");
                                        overRunPacketData.setAllotStateName("????????????");
                                    }
                                } else {
                                    xzyMTaskallotItem = new XzyMTaskallotItem();
                                    xzyMTaskallotItem.setItemCode(carPart.getItemCode());
                                    xzyMTaskallotItem.setItemName(carPart.getItemName());
                                    xzyMTaskallotItem.setDisplayItemName(carPart.getDisplayCarPartName());
                                    xzyMTaskallotItem.setNewItem("1");
                                    xzyMTaskcarpartList.add(carPart);
                                    xzyMTaskallotItem.setXzyMTaskcarpartList(xzyMTaskcarpartList);
                                    overRunItemList.add(xzyMTaskallotItem);
                                }
                            }
                        }
                        overRunPacketData.getPacket().setTaskallotItemList(overRunItemList);
                        if (!CollectionUtils.isEmpty(overRunItemList)) {
                            if (overRunItemList.stream().anyMatch(t -> "1".equals(t.getNewItem()))) {
                                if (overRunItemList.stream().anyMatch(t -> !CollectionUtils.isEmpty(t.getWorkerList()))) {
                                    overRunPacketData.setAllotStateCode("2");
                                    overRunPacketData.setAllotStateName("????????????");
                                } else {
                                    overRunPacketData.setAllotStateCode("0");
                                    overRunPacketData.setAllotStateName("?????????");
                                }
                            }
                            //??????????????????
                            if(!CollectionUtils.isEmpty(xzyBTaskallotshowDictList)){
                                XzyBTaskallotshowDict filterShowDict = xzyBTaskallotshowDictList.stream().filter(t -> "12".equals(t.getItemTypeCode())).findFirst().orElse(null);
                                if(!ObjectUtils.isEmpty(filterShowDict)){
                                    overRunPacketData.setShowMode(filterShowDict.getsTaskallotshowcode());
                                }
                            }
                            packetDataArrayList.add(overRunPacketData);
                        }
                    }
                }

                /** ??????????????????????????????????????????????????? ???????????????
                 //????????????
                 List<XzyMTaskallotItem> taskallotItems = new ArrayList<>();
                 //????????????
                 List<XzyMTaskcarpart> xzyMTaskcarparts = new ArrayList<>();
                 if (!CollectionUtils.isEmpty(entitySJOverRunRecordList)) {
                 for (EntitySJOverRunRecord entitySJOverRunRecord : entitySJOverRunRecordList) {
                 if (trainsetId.equals(entitySJOverRunRecord.getTrainsetId())) {
                 //???????????????
                 XzyMTaskcarpart xzyMTaskcarpart = getXzyMTaskallotItem(unitCode, dayPlanId, allotDataMap, trainsetId, entitySJOverRunRecord.getTrainsetName(), entitySJOverRunRecord.getCheckItemName(), entitySJOverRunRecord.getCheckItem(), entitySJOverRunRecord.getCarNo());
                 xzyMTaskcarparts.add(xzyMTaskcarpart);
                 }
                 }
                 }
                 //????????????
                 if (!CollectionUtils.isEmpty(faultDataJSONArray)) {
                 for (Object o : faultDataJSONArray) {
                 JSONObject fault = (JSONObject) o;
                 if (trainsetId.equals(fault.getString("trainsetId"))) {
                 //???????????????
                 XzyMTaskcarpart xzyMTaskcarpart = getXzyMTaskallotItem(unitCode, dayPlanId, allotDataMap, trainsetId, fault.getString("trainsetID"), fault.getString("faultDescription"), fault.getString("faultID"), fault.getString("carNo"));
                 xzyMTaskcarparts.add(xzyMTaskcarpart);
                 }
                 }
                 }
                 List<PacketData> packetDataList = packetDataArrayList.stream().filter(t -> t.getPacket().getPacketType().equals("5")).collect(Collectors.toList());
                 if (CollectionUtils.isEmpty(packetDataList) && !CollectionUtils.isEmpty(xzyMTaskcarparts)) {
                 JSONObject faultPacket = null;
                 try {
                 JSONObject param = new JSONObject();
                 param.put("params", new JSONObject());
                 faultPacket = remoteService.getFaultPacket(param);
                 } catch (Exception e) {
                 logger.error("???????????????????????????????????????", e);
                 }

                 JSONArray faultResult = faultPacket.getJSONArray("result");
                 for (Object o : faultResult) {
                 JSONObject json = (JSONObject) o;
                 //????????????????????????????????????????????????????????????
                 PacketData packetData = new PacketData();
                 packetData.setWorkTaskDisplayName(json.getString("S_PACKET_NAME"));
                 packetData.setWorkTypeCode(json.getString("S_PACKET_TYPE"));
                 packetData.setNewPacket("1");
                 packetData.setAllotStateCode("1");
                 packetData.setAllotStateName("?????????");
                 //???????????????
                 XzyMTaskallotpacket taskAllotPacket = new XzyMTaskallotpacket();
                 taskAllotPacket.setPacketName(json.getString("S_PACKET_NAME"));
                 taskAllotPacket.setPacketType(json.getString("S_PACKET_TYPE"));
                 taskAllotPacket.setPacketCode(json.getString("S_PACKET_CODE"));
                 taskAllotPacket.setStaskId(UUID.randomUUID().toString());
                 //??????????????????
                 setXzyMTaskallotItem(taskallotItems, xzyMTaskcarparts);
                 taskAllotPacket.setTaskallotItemList(taskallotItems);
                 packetData.setPacket(taskAllotPacket);
                 packetDataArrayList.add(packetData);
                 }
                 }

                 if (!CollectionUtils.isEmpty(packetDataList)) {
                 List<XzyMTaskallotItem> taskAllotItemList = packetDataList.get(0).getPacket().getTaskallotItemList();
                 setXzyMTaskallotItem(taskallotItems, xzyMTaskcarparts);
                 for (XzyMTaskallotItem taskAllotItem : taskallotItems) {
                 if (taskAllotItemList.stream().noneMatch(t -> t.getItemCode().equals(taskAllotItem.getItemCode()))) {
                 taskAllotItemList.add(taskAllotItem);
                 }
                 }
                 packetDataList.get(0).getPacket().setTaskallotItemList(taskAllotItemList);
                 }*/

                //????????????????????????????????????
                packetDataArrayList = Optional.ofNullable(packetDataArrayList).orElseGet(ArrayList::new).stream().sorted(Comparator.comparing((PacketData t) -> t.getWorkTypeCode()).thenComparing((PacketData v) -> v.getPacket().getPacketName())).collect(Collectors.toList());
                phoneAllotData.setPacketData(packetDataArrayList);
                phoneAllotDataArrayList.add(phoneAllotData);
            }

            //???????????????????????????
            Optional.ofNullable(phoneAllotDataArrayList).orElseGet(ArrayList::new).forEach(phoneAllotData -> {
                List<PacketData> packetDataList = phoneAllotData.getPacketData();
                if (!CollectionUtils.isEmpty(packetDataList)) {
                    //?????????????????????
                    long wCount = packetDataList.stream().filter(t -> "0".equals(t.getAllotStateCode())).count();
                    //?????????????????????
                    long yCount = packetDataList.stream().filter(t -> "1".equals(t.getAllotStateCode())).count();
                    //????????????????????????
                    long bCount = packetDataList.stream().filter(t -> "2".equals(t.getAllotStateCode())).count();
                    if (bCount > 0) {
                        phoneAllotData.setAllotStateCode("2");
                        phoneAllotData.setAllotStateName("????????????");
                    } else if (wCount > 0) {
                        if (yCount > 0) {
                            phoneAllotData.setAllotStateCode("2");
                            phoneAllotData.setAllotStateName("????????????");
                        } else {
                            phoneAllotData.setAllotStateCode("0");
                            phoneAllotData.setAllotStateName("?????????");
                        }
                    } else if (yCount > 0) {
                        phoneAllotData.setAllotStateCode("1");
                        phoneAllotData.setAllotStateName("?????????");
                    } else {
                        phoneAllotData.setAllotStateCode("0");
                        phoneAllotData.setAllotStateName("?????????");
                    }
                }
            });

            //?????????????????????
            logger.info("????????????????????????:{}", iMarshalcounts);
            String maxMarshal = iMarshalcounts.stream().distinct().max(String::compareTo).get();
            int marshalType;
            switch (maxMarshal) {
                case "16":
                    marshalType = 2;
                    break;
                case "17":
                    marshalType = 3;
                    break;
                default:
                    marshalType = 1;
            }

            //???????????????????????????
            for (String packetType : packetTypes) {
                PacketType type = new PacketType();
                type.setPacketTypeCode(packetType);
                packetTypeArrayList.add(type);
            }

            JSONArray jsonArray = new JSONArray();
            List<PhoneAllotData> resData = new ArrayList<>();
            if (!CollectionUtils.isEmpty(phoneAllotDataArrayList)) {
                jsonArray = JSONArray.parseArray(JSON.toJSONString(phoneAllotDataArrayList, SerializerFeature.DisableCircularReferenceDetect));
                resData = jsonArray.toJavaList(PhoneAllotData.class);
            }
            result.put("data", resData);
            result.put("queryList", queryList);
            result.put("maxMarshal", maxMarshal);
            result.put("marshalType", marshalType);
            result.put("allotTypeDict", allotTypeDict);
            result.put("taskAllotTypeDict", taskAllotTypeDict);
            result.put("packetTypeList", packetTypeArrayList);
        }
        return result;
    }

    /***
     * @author:??????
     * @desc: ?????????????????????????????????
     * @date: 2021/9/3
     * @param: []
     * @return: com.alibaba.fastjson.JSONObject
     */
    @Override
    public List<TaskAllotPhoneTrainSetState> getPhoneTaskTrainSetState(String unitCode, String deptCode, String dayPlanId) {
        List<TaskAllotPhoneTrainSetState> result = new ArrayList<>();
        //1.??????PC??????????????????????????????
        JSONObject repairTask = xzyMTaskallotpacketService.getRepairTask(unitCode, dayPlanId, deptCode, "", "1");
        //2.??????????????????
        JSONArray jsonArray = repairTask.getJSONArray("data");
        if (jsonArray != null && jsonArray.size() > 0) {
            List<AllotData> allotDataList = jsonArray.toJavaList(AllotData.class);
            if (allotDataList != null && allotDataList.size() > 0) {
                List<String> trainsetIdList = allotDataList.stream().map(t -> t.getTrainsetId()).distinct().collect(Collectors.toList());
                if (trainsetIdList != null && trainsetIdList.size() > 0) {
                    for (String trainsetId : trainsetIdList) {
                        //?????????????????????
                        long wCount = allotDataList.stream().filter(t -> trainsetId.equals(t.getTrainsetId()) && "0".equals(t.getAllotStateCode())).count();
                        //?????????????????????
                        long yCount = allotDataList.stream().filter(t -> trainsetId.equals(t.getTrainsetId()) && "1".equals(t.getAllotStateCode())).count();
                        //????????????????????????
                        long bCount = allotDataList.stream().filter(t -> trainsetId.equals(t.getTrainsetId()) && "2".equals(t.getAllotStateCode())).count();
                        TaskAllotPhoneTrainSetState taskAllotPhoneTrainSetState = new TaskAllotPhoneTrainSetState();
                        taskAllotPhoneTrainSetState.setTrainSetId(trainsetId);
                        if (bCount > 0) {
                            taskAllotPhoneTrainSetState.setState("????????????");
                        } else if (wCount > 0) {
                            if (yCount > 0) {
                                taskAllotPhoneTrainSetState.setState("????????????");
                            } else {
                                taskAllotPhoneTrainSetState.setState("?????????");
                            }
                        } else if (yCount > 0) {
                            taskAllotPhoneTrainSetState.setState("?????????");
                        } else {
                            taskAllotPhoneTrainSetState.setState("??????");
                        }
                        result.add(taskAllotPhoneTrainSetState);
                    }
                }
            }
        }
        return result;
    }

    private void setXzyMTaskallotItem(List<XzyMTaskallotItem> taskallotItems, List<XzyMTaskcarpart> xzyMTaskcarparts) {
        Map<String, List<XzyMTaskcarpart>> collect = xzyMTaskcarparts.stream().collect(Collectors.groupingBy(XzyMTaskcarpart::getItemCode));
        for (String key : collect.keySet()) {
            XzyMTaskallotItem xzyMTaskallotItem = new XzyMTaskallotItem();
            xzyMTaskallotItem.setItemName(collect.get(key).get(0).getItemName());
            xzyMTaskallotItem.setDisplayItemName(collect.get(key).get(0).getDisplayCarPartName());
            xzyMTaskallotItem.setItemCode(collect.get(key).get(0).getItemCode());
            xzyMTaskallotItem.setNewItem("1");
            xzyMTaskallotItem.setXzyMTaskcarpartList(collect.get(key));
            taskallotItems.add(xzyMTaskallotItem);
        }
    }

    private XzyMTaskcarpart getXzyMTaskallotItem(String unitCode, String dayPlanID, Map<String, List<AllotData>> allotDataMap, String trainsetId, String trainsetName, String checkItemName, String checkItem, String carNo) {
        XzyMTaskcarpart xzyMTaskcarpart = new XzyMTaskcarpart();
        xzyMTaskcarpart.setDayPlanID(dayPlanID);
        xzyMTaskcarpart.setItemName(checkItemName);
        xzyMTaskcarpart.setItemCode(checkItem);
        xzyMTaskcarpart.setRepairType("5");
        xzyMTaskcarpart.setTrainsetId(trainsetId);
        xzyMTaskcarpart.setUnitCode(unitCode);
        xzyMTaskcarpart.setTrainsetName(trainsetId);
        xzyMTaskcarpart.setCarNo(carNo);
        xzyMTaskcarpart.setDisplayCarPartName(carNo);
        xzyMTaskcarpart.setTaskItemId(UUID.randomUUID().toString());
        xzyMTaskcarpart.setTrainsetName(allotDataMap.get(trainsetId).get(0).getTrainsetName());
        return xzyMTaskcarpart;
    }

    /***
     * @author: ??????
     * @desc: ????????????????????????
     */
    private XzyMTaskallotItem getTaskAllotItem() {
        return null;
    }

    /***
     * @author: ??????
     * @desc: ????????????????????????
     */
    private XzyMTaskcarpart getTaskAllotCarPart(String unitCode, String unitName, String dayPlanId, String trainsetId, String trainsetName, String carNo, String packetTypeCode, Object faultOrOverRun) {
        XzyMTaskcarpart carPart = new XzyMTaskcarpart();
        carPart.setUnitCode(unitCode);
        carPart.setUnitName(unitName);
        carPart.setCarNo(carNo);
        carPart.setArrageType("0");
        carPart.setDayPlanID(dayPlanId);
        carPart.setTrainsetId(trainsetId);
        carPart.setTrainsetName(trainsetName);
        carPart.setTrainsetType("");
        carPart.setMainCyc("-1");
        if ("5".equals(packetTypeCode)) {
            JSONObject jsonObject = (JSONObject) faultOrOverRun;
            JSONObject faultPacketInfo = this.getFaultPacketInfo();
            carPart.setItemCode(jsonObject.getString("faultId"));
            carPart.setItemName(jsonObject.getString("faultDescription"));
            carPart.setDisplayCarPartName("[?????????" + carPart.getCarNo() + "]" + jsonObject.getString("faultDescription"));
            carPart.setRepairType("5");
            if (!ObjectUtils.isEmpty(faultPacketInfo)) {
                carPart.setPacketCode(faultPacketInfo.getString("S_PACKET_CODE"));
                carPart.setPacketName(faultPacketInfo.getString("S_PACKET_NAME"));
                carPart.setPacketType(faultPacketInfo.getString("S_PACKET_TYPE"));
            }
        } else if ("12".equals(packetTypeCode)) {
            EntitySJOverRunRecord overRunRecord = (EntitySJOverRunRecord) faultOrOverRun;
            carPart.setItemCode(overRunRecord.getId());
            carPart.setItemName(overRunRecord.getCheckItemName());
            carPart.setDisplayCarPartName(overRunRecord.getCheckItemName());
            carPart.setRepairType("12");
            RecheckPacket overRunPacketInfo = this.getOverRunPacketInfo();
            if (!ObjectUtils.isEmpty(overRunPacketInfo)) {
                carPart.setPacketCode(overRunPacketInfo.getPacketCode());
                carPart.setPacketName(overRunPacketInfo.getPacketName());
                carPart.setPacketType(overRunPacketInfo.getPacketType());
            }
        }
        return carPart;
    }

    /***
     * @author: ??????
     * @desc: ???????????????????????????
     */
    private JSONObject getFaultPacketInfo() {
        JSONObject res = null;
        try {
            JSONObject param = new JSONObject();
            param.put("params", new JSONObject());
            JSONObject remoteResult = CacheUtil.getDataUseThreadCache(
                    "remoteService.getFaultPacketInfo",
                    () -> remoteService.getFaultPacket(param)
            );
            if (!ObjectUtils.isEmpty(remoteResult) && remoteResult.getInteger("code") == 200) {
                res = (JSONObject) remoteResult.getJSONArray("result").get(0);
            }
        } catch (Exception ex) {
            logger.error("???????????????????????????getFaultPacketInfo", ex);
        }
        return res;
    }

    /***
     * @author: ??????
     * @desc: ???????????????????????????
     */
    private RecheckPacket getOverRunPacketInfo() {
        RecheckPacket res = null;
        try {
            res = CacheUtil.getDataUseThreadCache(
                    "remoteService.getRecheckPacket",
                    () -> remoteService.getRecheckPacket()
            );
        } catch (Exception ex) {
            logger.error("???????????????????????????getOverRunPacketInfo", ex);
        }
        return res;
    }

    private List<EntitySJOverRunRecord> getLyGetOverRunRecord(List<String> trainsetNames, String dayPlanId) {
        String substring = dayPlanId.substring(dayPlanId.length() - 2);
        String endDate;
        if ("00".equals(substring)) {
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, 7);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            endDate = DateUtils.dateTime24ToStr(calendar.getTime());
        } else {
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_MONTH, 1);
            calendar.set(Calendar.HOUR_OF_DAY, 8);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            endDate = DateUtils.dateTime24ToStr(calendar.getTime());
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(DateUtils.strToDateTime(endDate));
        calendar.add(Calendar.DAY_OF_MONTH, -20);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        String startDate = DateUtils.dateTime24ToStr(calendar.getTime());

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("StartDate", startDate);
        jsonObject.put("EndDate", endDate);
        jsonObject.put("TrainsetNameList", trainsetNames);

        Map<String, Object> req = new HashMap<>();
        req.put("QueryPar", jsonObject.toString());


        JSONObject lyJson = null;
        JSONObject sjJson = null;
        try {
            JsonRootBean jsonRootBean = HttpUtil.getToken(recheckTaskProperties);
            lyJson = HttpUtil.doPost(recheckTaskProperties.getLyGetOverRunRecordUrl(), req, recheckTaskProperties, jsonRootBean);
            log.info("??????????????????ly{}", lyJson);
            sjJson = HttpUtil.doPost(recheckTaskProperties.getSjGetOverRunRecordUrl(), req, recheckTaskProperties, jsonRootBean);
            log.info("??????????????????sj{}", lyJson);
        } catch (Exception e) {
            log.error("??????????????????????????????", e);
        }
        List<EntitySJOverRunRecord> entitySJOverRunRecords = new ArrayList<>();
        log.info("??????????????????{}", lyJson);
        //??????ly????????????
        if (!CollectionUtils.isEmpty(lyJson)) {
            List<EntitySJOverRunRecord> data = JSON.parseArray(lyJson.getString("Data"), EntitySJOverRunRecord.class);
            if (!CollectionUtils.isEmpty(data)) {
                entitySJOverRunRecords.addAll(data);
            }
        } else if (!CollectionUtils.isEmpty(sjJson)) {
            //??????sj????????????
            List<EntitySJOverRunRecord> data = JSON.parseArray(sjJson.getString("Data"), EntitySJOverRunRecord.class);
            if (!CollectionUtils.isEmpty(data)) {
                entitySJOverRunRecords.addAll(data);
            }
        }
        return entitySJOverRunRecords;
    }

    @Override
    public Map getMobileGroup(String deptCode) {
        return cAllotbranchConfigService.getGroup(deptCode);
    }

    @Override
    public void setMobileAllotTask(JSONObject params) {
        JSONArray data = params.getJSONArray("data");
        String unitCode = params.getString("unitCode");
        String deptCode = params.getString("deptCode");
        List<PhoneAllotData> phoneAllotDataList = JSONArray.parseArray(data.toJSONString(), PhoneAllotData.class);

        Map<String, String> deptParam = new HashMap<>();
        deptParam.put("deptCode", deptCode);
        List<XzyMTaskallotdept> taskallotdeptList = taskAllotDeptService.getTaskAllotDepts(deptParam);
        XzyMTaskallotdept taskallotdept = new XzyMTaskallotdept();
        if (taskallotdeptList != null && taskallotdeptList.size() > 0) {
            taskallotdept = taskallotdeptList.get(0);
        }
        //??????????????????
        List<PacketData> packetDataList = phoneAllotDataList.stream().map(PhoneAllotData::getPacketData).flatMap(Collection::stream).collect(Collectors.toList());
        List<XzyMTaskallotpacket> packets = packetDataList.stream().map(PacketData::getPacket).collect(Collectors.toList());
        for (XzyMTaskallotpacket taskallotpacket : packets) {
            List<XzyMTaskallotItem> taskallotItemList = taskallotpacket.getTaskallotItemList();
            taskallotpacket.setMainCyc(taskallotItemList.get(0).getXzyMTaskcarpartList().get(0).getMainCyc());
            for (XzyMTaskallotItem taskallotItem : taskallotItemList) {
                List<XzyMTaskcarpart> xzyMTaskcarpartList = taskallotItem.getXzyMTaskcarpartList();
                for (XzyMTaskcarpart taskcarpart : xzyMTaskcarpartList) {
                    if (taskcarpart.getXzyMTaskallotdept() == null || ((taskcarpart.getXzyMTaskallotdept() != null && StringUtils.isEmpty(taskcarpart.getXzyMTaskallotdept().getDeptCode())) && StringUtils.isEmpty(taskcarpart.getTaskAllotDeptId()))) {
                        taskcarpart.setTaskAllotDeptId(taskallotdept.getTaskAllotDeptId());
                        taskcarpart.setXzyMTaskallotdept(taskallotdept);
                    }
                }
            }
        }
        xzyMTaskallotpacketService.setTaskAllotPackets(packets);

        //??????PC??????  ????????????????????????????????????????????????+?????????????????????????????????????????????
        if (phoneAllotDataList != null && phoneAllotDataList.size() > 0) {
            this.phoneSetTaskDataAndStatus(phoneAllotDataList);
        }
    }


    public boolean phoneSetTaskDataAndStatus(List<PhoneAllotData> phoneAllotDataList) {
        String unitCode = "";//???????????????
        String deptCode = "";//????????????
        String deptName = "";//????????????
        String dayPlanId = "";//?????????id
        List<ZtTaskPacketEntity> ztTaskPacketEntityList = new ArrayList<>();
        List<RepairPacketStatu> repairPacketStatuList = new ArrayList<>();//???????????????????????????????????????
        for (PhoneAllotData allotData : phoneAllotDataList) {
            List<PacketData> packetList = allotData.getPacketData();
            for (PacketData packetData : packetList) {
                XzyMTaskallotpacket packet = packetData.getPacket();
                List<XzyMTaskallotItem> taskallotItemList = new ArrayList<>();//???????????????????????????
                if (packetData != null) {
                    taskallotItemList = packet.getTaskallotItemList();
                }
                if (!CollectionUtils.isEmpty(taskallotItemList)) {
                    ZtTaskPacketEntity ztTaskPacketEntity = new ZtTaskPacketEntity();
                    ztTaskPacketEntity.setTaskRepairCode(packet.getMainCyc());
                    ztTaskPacketEntity.setTrainsetId(allotData.getTrainsetId());
                    ztTaskPacketEntity.setTrainsetName(allotData.getTrainsetName());
                    ztTaskPacketEntity.setPacketCode(packet.getPacketCode());
                    ztTaskPacketEntity.setPacketName(packet.getPacketName());
                    ztTaskPacketEntity.setPacketTypeCode(packet.getPacketType());
                    ztTaskPacketEntity.setRemark("????????????");
                    ztTaskPacketEntity.setStatus("4");
                    ztTaskPacketEntity.setDepotCode(unitCode);
                    ztTaskPacketEntity.setTaskId(packet.getStaskId());
                    List<ZtTaskItemEntity> lstTaskItemInfo = new ArrayList<>();
                    for (XzyMTaskallotItem taskallotItem : taskallotItemList) {
                        List<XzyMTaskcarpart> xzyMTaskcarpartList = taskallotItem.getXzyMTaskcarpartList();
                        if (!CollectionUtils.isEmpty(xzyMTaskcarpartList)) {
                            unitCode = xzyMTaskcarpartList.get(0).getUnitCode();
                            deptCode = xzyMTaskcarpartList.get(0).getXzyMTaskallotdept().getDeptCode();
                            deptName = xzyMTaskcarpartList.get(0).getXzyMTaskallotdept().getDeptName();
                            dayPlanId = xzyMTaskcarpartList.get(0).getDayPlanID();
                        }
                        if (("5".equals(packetData.getWorkTypeCode()) || "11".equals(packetData.getWorkTypeCode())) &&
                                "1".equals(taskallotItem.getNewItem()) && !CollectionUtils.isEmpty(xzyMTaskcarpartList)) {
                            for (XzyMTaskcarpart xzyMTaskcarpart : xzyMTaskcarpartList) {
                                ZtTaskItemEntity ztTaskItemEntity = new ZtTaskItemEntity();
                                ztTaskItemEntity.setTrainsetId(xzyMTaskcarpart.getTrainsetId());
                                ztTaskItemEntity.setTrainsetName(xzyMTaskcarpart.getTrainsetName());
                                ztTaskItemEntity.setDayplanId(xzyMTaskcarpart.getDayPlanID());
                                ztTaskPacketEntity.setDayplanId(xzyMTaskcarpart.getDayPlanID());//????????????????????????????????????
                                ztTaskItemEntity.setItemCode(xzyMTaskcarpart.getItemCode());
                                ztTaskItemEntity.setItemName(xzyMTaskcarpart.getItemName());
                                ztTaskItemEntity.setItemType(xzyMTaskcarpart.getRepairType());
                                ztTaskItemEntity.setArrangeType(xzyMTaskcarpart.getArrageType());
                                ztTaskItemEntity.setCarNo(xzyMTaskcarpart.getCarNo());
                                ztTaskItemEntity.setRemark("????????????");
                                ztTaskItemEntity.setDepotCode(unitCode);
                                ztTaskItemEntity.setTaskId(packet.getStaskId());
                                ztTaskItemEntity.setStatus("4");
                                lstTaskItemInfo.add(ztTaskItemEntity);
                            }
                        } else {
                            RepairPacketStatu repairPacketStatu = new RepairPacketStatu();
                            repairPacketStatu.setSTrainsetid(allotData.getTrainsetId());
                            repairPacketStatu.setSDayplanid(dayPlanId);
                            repairPacketStatu.setSItemcode(taskallotItem.getItemCode());
                            repairPacketStatu.setSDeptcode(unitCode);
                            repairPacketStatu.setSPacketcode(packet.getPacketCode());
                            repairPacketStatu.setCStatue("4");//???????????????
                            repairPacketStatuList.add(repairPacketStatu);
                        }
                    }
                    ztTaskPacketEntity.setDepotCode(unitCode);
                    ztTaskPacketEntity.setRepairDeptCode(deptCode);
                    ztTaskPacketEntity.setRepairDeptName(deptName);
                    ztTaskPacketEntity.setDayplanId(dayPlanId);
                    ztTaskPacketEntity.setLstTaskItemInfo(lstTaskItemInfo);
                    ztTaskPacketEntityList.add(ztTaskPacketEntity);
                }
            }
        }
        //??????????????????-????????????????????????????????????
        try {
            //????????????????????????????????????
            if (!CollectionUtils.isEmpty(ztTaskPacketEntityList)) {
                ztTaskPacketEntityList = ztTaskPacketEntityList.stream().filter(t -> !CollectionUtils.isEmpty(t.getLstTaskItemInfo())).collect(Collectors.toList());
                if (!CollectionUtils.isEmpty(ztTaskPacketEntityList)) {
                    JSONObject addRes = remoteService.addFaultReCheckPacketList(ztTaskPacketEntityList);
                }
            }
        } catch (Exception ex) {
            logger.error("???????????????????????????????????????????????????:" + ex.getMessage());
        }
        //??????????????????-????????????????????????
        try {
            if (!CollectionUtils.isEmpty(repairPacketStatuList)) {
                JSONObject updateRes = remoteService.updateRepairPacketStatus(repairPacketStatuList);
            }
        } catch (Exception ex) {
            logger.error("???????????????????????????????????????????????????????????????:" + ex.getMessage());
        }
        return true;
    }

    /**
     * ?????????????????????????????????
     *
     * @param
     * @return
     */
    @Override
    public List<PlanTrainset> getPlanTrainset(String dayPlanId, String unitCode, String deptCode) {
        List<String> deptCodeList = Arrays.asList(deptCode);
        Date beginTime = new Date();
        //1.????????????????????????
        List<ZtTaskPacketEntity> taskPacketEntityList = remoteService.getPacketTaskByCondition(dayPlanId, null, null, deptCode, unitCode);
        Date time1 = new Date();
        logger.info("getPlanTrainset??????????????????????????????????????????????????????" + (time1.getTime() - beginTime.getTime()));
        //2.????????????????????????
        List<TaskAllotPacketEntity> getTaskAllotData = midGroundService.getTaskAllotDataToItem(unitCode, dayPlanId, null, null, deptCodeList, null);
        Date time2 = new Date();
        logger.info("getPlanTrainset????????????????????????????????????????????????????????????" + (time2.getTime() - time1.getTime()));
        //3.??????????????????
        List<PlanTrainset> planTrainsetList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(taskPacketEntityList)) {
            //3.1?????????????????????????????????id??????
            Map<String, List<ZtTaskPacketEntity>> ztTaskPacketEntityMap = taskPacketEntityList.stream().collect(Collectors.groupingBy(ZtTaskPacketEntity::getTrainsetId));
            ztTaskPacketEntityMap.forEach((currentTrainsetId,ztTaskPacketEntityList)->{
                PlanTrainset planTrainset = new PlanTrainset();
                planTrainset.setTrainsetId(currentTrainsetId);
                planTrainset.setTrainsetName(ztTaskPacketEntityList.get(0).getTrainsetName());
                //3.2
                for (ZtTaskPacketEntity ztTaskPacketEntity : ztTaskPacketEntityList) {
                    String currentPacketCode = ztTaskPacketEntity.getPacketCode();//???????????????
                }
                planTrainsetList.add(planTrainset);
            });
        }
        Date time3 = new Date();
        logger.info("getPlanTrainset????????????????????????????????????" + (time3.getTime() - time2.getTime()));
        Date endTime = new Date();
        logger.info("getPlanTrainset?????????????????????" + (endTime.getTime() - beginTime.getTime()));
        return planTrainsetList;
    }

    @Override
    public List<PlanPacket> getPlanPacket(String dayPlanId, String unitCode, String deptCode, String trainsetId) {
        List<String> trainsetIdList = Arrays.asList(trainsetId);
        List<String> deptCodeList = Arrays.asList(deptCode);
        //????????????????????????
        List<ZtTaskPacketEntity> taskPacketEntityList = remoteService.getPacketTaskByCondition(dayPlanId, trainsetId, null, deptCode, unitCode);
        //????????????????????????
        List<TaskAllotPacketEntity> getTaskAllotData = midGroundService.getTaskAllotData(unitCode, dayPlanId, null, trainsetIdList, deptCodeList, null, null);
        List<PlanPacketWorkType> planPacketWorkTypeList = new ArrayList<>();
        //?????????????????????????????????????????????
        List<ZtTaskPacketEntity> taskPacketEntityList1 = taskPacketEntityList.stream().filter(t -> !t.getPacketTypeCode().equals("5") && !t.getPacketTypeCode().equals("12")).collect(Collectors.toList());
        for (ZtTaskPacketEntity ztTaskPacketEntity : taskPacketEntityList1) {
            String packetCode = ztTaskPacketEntity.getPacketCode();
            PlanPacketWorkType planPacketWorkType = new PlanPacketWorkType();
            planPacketWorkType.setPacketCode(packetCode);
            planPacketWorkType.setPacketName(ztTaskPacketEntity.getPacketName());
            //????????????????????? ???????????????workModeTypeCode???0??????????????????1???????????????
            String packetTypeCode = ztTaskPacketEntity.getPacketTypeCode();
            planPacketWorkType.setWorkTypeCode(packetTypeCode);
            if ("6".equals(packetTypeCode)) {
                //????????????????????????
                planPacketWorkType.setWorkModeTypeCode("0");
                //??????????????????????????????
                List<TaskAllotPacketEntity> taskAllotPacketEntityList = getTaskAllotData.stream().filter(t -> t.getPacketCode().equals(packetCode)).collect(Collectors.toList());
                if (taskAllotPacketEntityList.size() > 0) {
                    //???????????????????????????
                    planPacketWorkType.setIsExistPerson("1");
                } else {
                    //????????????????????????
                    planPacketWorkType.setIsExistPerson("0");
                }
            } else {
                //???????????????????????????
                planPacketWorkType.setWorkModeTypeCode("1");
            }
            planPacketWorkTypeList.add(planPacketWorkType);

        }
        //?????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
        //????????????????????????????????????
        //????????????????????????
        //???????????????????????????????????????,?????????????????????????????????
        JSONArray faultData = new JSONArray();
        try {
            JSONObject faultParams = new JSONObject();
            faultParams.put("findFaultTime", dayPlanId.substring(0, dayPlanId.length() - 3));
            faultParams.put("trainsetIdList", trainsetId);
            faultData = remoteService.getFaultData(faultParams);
        } catch (Exception e) {
            logger.error("???????????????????????????????????????", e);
        }
        //List<Map<String, Object>> mapList = (List) faultDataJSONArray;
        //mapList = mapList.stream().filter(t -> t.get("trainSetId").equals(trainsetId)).collect(Collectors.toList());
        TrainsetBaseInfo trainsetBaseInfo = remoteService.getTrainsetBaseinfoByID(trainsetId);
        String trainsetName = trainsetBaseInfo.getTrainsetname();
        //????????????????????????
//        List<String> trainsetNames = Arrays.asList(trainsetName);
//        Map<String, Object> overRunParams = new HashMap<>();
//        overRunParams.put("TrainsetNameList", trainsetNames);
//        List<EntitySJOverRunRecord> entitySJOverRunRecordList = reCheckTaskService.getReCheckTaskList(overRunParams);
//        entitySJOverRunRecordList = entitySJOverRunRecordList.stream().filter(t -> t.getTrainsetId().equals(trainsetId)).collect(Collectors.toList());
        List<ZtTaskPacketEntity> ztTaskPacketEntities = taskPacketEntityList.stream().filter(t -> t.getPacketTypeCode().equals("5")).collect(Collectors.toList());
        if (ztTaskPacketEntities.size() > 0) {
            ZtTaskPacketEntity ztTaskPacketEntity = ztTaskPacketEntities.get(0);
            //???????????????????????????????????????
            PlanPacketWorkType planPacketWorkType = new PlanPacketWorkType();
            planPacketWorkType.setPacketCode(ztTaskPacketEntity.getPacketCode());
            planPacketWorkType.setPacketName(ztTaskPacketEntity.getPacketName());
            planPacketWorkType.setNewPacket("0");
            planPacketWorkType.setWorkModeTypeCode("1");
            planPacketWorkType.setWorkTypeCode(ztTaskPacketEntity.getPacketTypeCode());
            planPacketWorkTypeList.add(planPacketWorkType);
        } else {
            //?????????????????????
            if (faultData.size() > 0) {
                //??????????????????????????????,???????????????
                //new???????????????????????????
                JSONObject faultPacketInfo = this.getFaultPacketInfo();
                if (!ObjectUtils.isEmpty(faultPacketInfo)) {
                    PlanPacketWorkType planPacketWorkType = new PlanPacketWorkType();
                    planPacketWorkType.setPacketCode(faultPacketInfo.getString("S_PACKET_CODE"));
                    planPacketWorkType.setPacketName(faultPacketInfo.getString("S_PACKET_NAME"));
                    planPacketWorkType.setNewPacket("1");
                    planPacketWorkType.setWorkModeTypeCode("1");
                    planPacketWorkType.setWorkTypeCode(faultPacketInfo.getString("S_PACKET_TYPE"));
                    planPacketWorkTypeList.add(planPacketWorkType);
                }
            }
        }
        List<ZtTaskPacketEntity> ztTaskPacketEntities1 = taskPacketEntityList.stream().filter(t -> t.getPacketTypeCode().equals("12")).collect(Collectors.toList());
//        if (ztTaskPacketEntities1.size() > 0) {
//            ZtTaskPacketEntity ztTaskPacketEntity = ztTaskPacketEntities1.get(0);
//            //??????????????????????????????????????????
//            PlanPacket planPacket = new PlanPacket();
//            planPacket.setPacketCode(ztTaskPacketEntity.getPacketCode());
//            planPacket.setPacketName(ztTaskPacketEntity.getPacketName());
//            planPacket.setNewPacket("0");
//            planPacket.setWorkModeTypeCode("1");
//            planPacketList.add(planPacket);
//        } else {
//            //?????????????????????
//            if (entitySJOverRunRecordList.size() > 0) {
//                //??????????????????????????????,???????????????
//                //new???????????????????????????
//                RecheckPacket overRunPacketInfo = this.getOverRunPacketInfo();
//                if (!ObjectUtils.isEmpty(overRunPacketInfo)) {
//                    PlanPacket planPacket = new PlanPacket();
//                    planPacket.setPacketCode(overRunPacketInfo.getPacketCode());
//                    planPacket.setPacketName(overRunPacketInfo.getPacketName());
//                    planPacket.setNewPacket("1");
//                    planPacket.setWorkModeTypeCode("1");
//                    planPacketList.add(planPacket);
//                }
//            }
//
//        }
        //????????????????????????????????????
        if (planPacketWorkTypeList.size() > 0) {
            planPacketWorkTypeList = planPacketWorkTypeList.stream().sorted(Comparator.comparing(PlanPacketWorkType::getWorkTypeCode).thenComparing(PlanPacketWorkType::getPacketName)).collect(Collectors.toList());
        }
        List<PlanPacket> planPacketList = new ArrayList<>();
        for (PlanPacketWorkType planPacketWorkType : planPacketWorkTypeList) {
            PlanPacket planPacket = new PlanPacket();
            planPacket.setPacketCode(planPacketWorkType.getPacketCode());
            planPacket.setPacketName(planPacketWorkType.getPacketName());
            planPacket.setWorkModeTypeCode(planPacketWorkType.getWorkModeTypeCode());
            planPacket.setNewPacket(planPacketWorkType.getNewPacket());
            planPacket.setIsExistPerson(planPacketWorkType.getIsExistPerson());
            planPacketList.add(planPacket);
        }
        return planPacketList;
    }

    @Override
    public List<PlanItem> getPlanItem(String dayPlanId, String unitCode, String deptCode, String trainsetId, String
            packetCode) {
        List<String> deptCodeList = Arrays.asList(deptCode);
        List<String> trainsetIdList = Arrays.asList(trainsetId);
        //????????????????????????
        List<ZtTaskPacketEntity> taskPacketEntityList = remoteService.getPacketTaskByCondition(dayPlanId, trainsetId, null, deptCode, unitCode);
        //????????????????????????
        List<TaskAllotPacketEntity> getTaskAllotData = midGroundService.getTaskAllotData(unitCode, dayPlanId, null, trainsetIdList, deptCodeList, null, null);
        //?????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
        //??????????????????????????????????????????????????????
        JSONObject faultPacketInfo = this.getFaultPacketInfo();
        String faultPacketCode = "";
        List<PlanItem> planItemList = new ArrayList<>();
        //????????????????????????
        List<ZtTaskPacketEntity> taskPacketEntityList1 = taskPacketEntityList.stream().filter(t -> t.getPacketTypeCode().equals("5")).collect(Collectors.toList());
        if (!ObjectUtils.isEmpty(faultPacketInfo)) {
            faultPacketCode = faultPacketInfo.getString("S_PACKET_CODE");
            if (faultPacketCode.equals(packetCode)) {
                //????????????????????????
                JSONObject faultParams = new JSONObject();
                faultParams.put("findFaultTime", dayPlanId.substring(0, dayPlanId.length() - 3));
                faultParams.put("trainsetIdList", trainsetIdList);
                JSONArray faultData = remoteService.getFaultData(faultParams);
                if (faultData.size() > 0) {
                    List<TaskAllotPacketEntity> taskAllotPacketEntityList = getTaskAllotData.stream().filter(t -> t.getPacketCode().equals(packetCode)).collect(Collectors.toList());
                    List<String> itemCodeList = new ArrayList<>();
                    if (taskAllotPacketEntityList.size() > 0) {
                        //?????????????????????????????????
                        TaskAllotPacketEntity taskAllotPacketEntity = taskAllotPacketEntityList.get(0);
                        List<TaskAllotItemEntity> taskAllotItemEntities = taskAllotPacketEntity.getTaskAllotItemEntityList();
                        //?????????????????????itemCode
                        itemCodeList = taskAllotItemEntities.stream().map(TaskAllotItemEntity::getItemCode).collect(Collectors.toList());
                    }
                    for (int i = 0; i < faultData.size(); i++) {
                        JSONObject jsonObject = faultData.getJSONObject(i);
                        PlanItem planItem = new PlanItem();
                        String faultId = jsonObject.getString("faultId");
                        planItem.setItemCode(faultId);
                        //????????????????????????
                        String carNo = jsonObject.getString("carNo");
                        String faultDescription = jsonObject.getString("faultDescription");
                        String itemName = "[?????????" + carNo + "]" + faultDescription;
                        planItem.setItemName(itemName);
                        if (itemCodeList.size() > 0) {
                            if (itemCodeList.contains(faultId)) {
                                //?????????????????????
                                planItem.setIsExistPerson("1");
                                //?????????
                                planItem.setNewItem("0");
                            } else {
                                //?????????????????????
                                planItem.setIsExistPerson("0");
                                planItem.setNewItem("1");
                            }
                        } else {
                            //?????????????????????????????????
                            planItem.setIsExistPerson("0");
                            planItem.setNewItem("1");
                        }
                        planItem.setArrangeType("");
                        planItem.setFillType("");
                        planItemList.add(planItem);
                    }
                }
            }
        }
        //???????????????????????????????????????????????????????????????
        if (!ObjectUtils.isEmpty(faultPacketInfo)) {
            faultPacketCode = faultPacketInfo.getString("S_PACKET_CODE");
            if (faultPacketCode.equals(packetCode)) {
                if (taskPacketEntityList1.size() > 0) {
                    ZtTaskPacketEntity ztTaskPacketEntity = taskPacketEntityList1.get(0);
                    List<ZtTaskItemEntity> ztTaskItemEntityList = ztTaskPacketEntity.getLstTaskItemInfo();
                    //???????????????itemCode
                    List<String> itemCodeList = ztTaskItemEntityList.stream().map(m -> m.getItemCode()).distinct().collect(Collectors.toList());
                    List<String> itemCodeList1 = planItemList.stream().map(m -> m.getItemCode()).distinct().collect(Collectors.toList());
                    for (String itemCode : itemCodeList) {
                        if (!itemCodeList1.contains(itemCode)) {
                            //??????????????? ???????????????????????????????????????
                            List<ZtTaskItemEntity> ztTaskItemEntity = ztTaskItemEntityList.stream().filter(t -> t.getItemCode().equals(itemCode)).collect(Collectors.toList());
                            ZtTaskItemEntity ztTaskItemEntity1 = ztTaskItemEntity.get(0);
                            List<TaskAllotPacketEntity> taskAllotPacketEntityList = getTaskAllotData.stream().filter(t -> t.getPacketCode().equals(packetCode)).collect(Collectors.toList());
                            List<String> itemCodeList2 = new ArrayList<>();
                            if (taskAllotPacketEntityList.size() > 0) {
                                //?????????????????????????????????
                                TaskAllotPacketEntity taskAllotPacketEntity = taskAllotPacketEntityList.get(0);
                                List<TaskAllotItemEntity> taskAllotItemEntities = taskAllotPacketEntity.getTaskAllotItemEntityList();
                                //?????????????????????itemCode
                                itemCodeList2 = taskAllotItemEntities.stream().map(TaskAllotItemEntity::getItemCode).collect(Collectors.toList());
                                PlanItem planItem = new PlanItem();
                                planItem.setItemCode(itemCode);
                                planItem.setItemName(ztTaskItemEntity1.getItemName());
                                if (itemCodeList2.size() > 0) {
                                    if (itemCodeList2.contains(itemCode)) {
                                        //?????????????????????
                                        planItem.setIsExistPerson("1");
                                        //?????????
                                        planItem.setNewItem("0");
                                    } else {
                                        //?????????????????????
                                        planItem.setIsExistPerson("0");
                                        planItem.setNewItem("1");
                                    }
                                } else {
                                    //?????????????????????????????????
                                    planItem.setIsExistPerson("0");
                                    planItem.setNewItem("1");
                                }
                                planItem.setArrangeType("");
                                planItem.setFillType("");
                                planItemList.add(planItem);
                            }
                        }

                    }
                }
            }
        }
        //RecheckPacket overRunPacketInfo = this.getOverRunPacketInfo();
        String faultPacketCode1 = "";
        //????????????????????????
        List<ZtTaskPacketEntity> taskPacketEntityList2 = taskPacketEntityList.stream().filter(t -> t.getPacketTypeCode().equals("12")).collect(Collectors.toList());
//        if (!ObjectUtils.isEmpty(overRunPacketInfo)) {
//            faultPacketCode1 = overRunPacketInfo.getPacketCode();
//            if (faultPacketCode1.equals(packetCode)) {
//                TrainsetBaseInfo trainsetBaseInfo = remoteService.getTrainsetBaseinfoByID(trainsetId);
//                String trainsetName = trainsetBaseInfo.getTrainsetname();
        //????????????????????????
//                List<String> trainsetNames = Arrays.asList(trainsetName);
//                Map<String, Object> overRunParams = new HashMap<>();
//                overRunParams.put("TrainsetNameList", trainsetNames);
//                List<EntitySJOverRunRecord> entitySJOverRunRecordList = reCheckTaskService.getReCheckTaskList(overRunParams);
//                if (entitySJOverRunRecordList.size() > 0) {
//                    List<TaskAllotPacketEntity> taskAllotPacketEntityList = getTaskAllotData.stream().filter(t -> t.getPacketCode().equals(packetCode)).collect(Collectors.toList());
//                    List<String> itemCodeList = new ArrayList<>();
//                    if (taskAllotPacketEntityList.size() > 0) {
//                        //?????????????????????????????????
//                        TaskAllotPacketEntity taskAllotPacketEntity = taskAllotPacketEntityList.get(0);
//                        List<TaskAllotItemEntity> taskAllotItemEntities = taskAllotPacketEntity.getTaskAllotItemEntityList();
//                        //?????????????????????itemCode
//                        itemCodeList = taskAllotItemEntities.stream().map(TaskAllotItemEntity::getItemCode).collect(Collectors.toList());
//                    }
//                    for (int i = 0; i < entitySJOverRunRecordList.size(); i++) {
//                        EntitySJOverRunRecord entitySJOverRunRecord = entitySJOverRunRecordList.get(i);
//                        PlanItem planItem = new PlanItem();
//                        String faultId = entitySJOverRunRecord.getId();
//                        planItem.setItemCode(faultId);
//                        planItem.setItemName(entitySJOverRunRecord.getCheckItemName());
//                        if (itemCodeList.size() > 0) {
//                            if (itemCodeList.contains(faultId)) {
//                                //?????????????????????
//                                planItem.setIsExistPerson("1");
//                                //?????????
//                                planItem.setNewItem("0");
//                            } else {
//                                //?????????????????????
//                                planItem.setIsExistPerson("0");
//                                planItem.setNewItem("1");
//                            }
//                        } else {
//                            //?????????????????????????????????
//                            planItem.setIsExistPerson("0");
//                            planItem.setNewItem("1");
//                        }
//                        planItem.setArrangeType("");
//                        planItem.setFillType("");
//                        planItemList.add(planItem);
//                    }
//                }
//            }
//        }
        //???????????????????????????????????????????????????????????????
//        if (taskPacketEntityList2.size() > 0) {
//            ZtTaskPacketEntity ztTaskPacketEntity = taskPacketEntityList2.get(0);
//            List<ZtTaskItemEntity> ztTaskItemEntityList = ztTaskPacketEntity.getLstTaskItemInfo();
//            //???????????????itemCode
//            List<String> itemCodeList = ztTaskItemEntityList.stream().map(m -> m.getItemCode()).distinct().collect(Collectors.toList());
//            List<String> itemCodeList1 = planItemList.stream().map(m -> m.getItemCode()).distinct().collect(Collectors.toList());
//            for (String itemCode : itemCodeList) {
//                if (!itemCodeList1.contains(itemCode)) {
//                    //??????????????? ???????????????????????????????????????
//                    List<ZtTaskItemEntity> ztTaskItemEntity = ztTaskItemEntityList.stream().filter(t -> t.getItemCode().equals(itemCode)).collect(Collectors.toList());
//                    ZtTaskItemEntity ztTaskItemEntity1 = ztTaskItemEntity.get(0);
//                    List<TaskAllotPacketEntity> taskAllotPacketEntityList = getTaskAllotData.stream().filter(t -> t.getPacketCode().equals(packetCode)).collect(Collectors.toList());
//                    List<String> itemCodeList2 = new ArrayList<>();
//                    if (taskAllotPacketEntityList.size() > 0) {
//                        //?????????????????????????????????
//                        TaskAllotPacketEntity taskAllotPacketEntity = taskAllotPacketEntityList.get(0);
//                        List<TaskAllotItemEntity> taskAllotItemEntities = taskAllotPacketEntity.getTaskAllotItemEntityList();
//                        //?????????????????????itemCode
//                        itemCodeList2 = taskAllotItemEntities.stream().map(TaskAllotItemEntity::getItemCode).collect(Collectors.toList());
//                        PlanItem planItem = new PlanItem();
//                        planItem.setItemCode(itemCode);
//                        planItem.setItemName(ztTaskItemEntity1.getItemName());
//                        if (itemCodeList2.size() > 0) {
//                            if (itemCodeList2.contains(itemCode)) {
//                                //?????????????????????
//                                planItem.setIsExistPerson("1");
//                                //?????????
//                                planItem.setNewItem("0");
//                            } else {
//                                //?????????????????????
//                                planItem.setIsExistPerson("0");
//                                planItem.setNewItem("1");
//                            }
//                        } else {
//                            //?????????????????????????????????
//                            planItem.setIsExistPerson("0");
//                            planItem.setNewItem("1");
//                        }
//                        planItem.setArrangeType("");
//                        planItem.setFillType("");
//                        planItemList.add(planItem);
//                    }
//                }
//
//            }
//        }

        if ((!faultPacketCode.equals(packetCode)) && (!faultPacketCode1.equals(packetCode))) {
            //??????????????????????????????????????????
            //?????????code??????????????????
            List<ZtTaskPacketEntity> ztTaskPacketEntityList = taskPacketEntityList.stream().filter(t -> t.getPacketCode().equals(packetCode)).collect(Collectors.toList());
            String isExistPerson = "";
            List<TaskAllotPacketEntity> taskAllotPacketEntityList = new ArrayList<>();
            if (getTaskAllotData.size() == 0) {
                //?????????????????????,??????????????????
                isExistPerson = "0";
            } else {
                //?????????code????????????????????????
                taskAllotPacketEntityList = getTaskAllotData.stream().filter(t -> t.getPacketCode().equals(packetCode)).collect(Collectors.toList());
                if (taskAllotPacketEntityList.size() == 0) {
                    //????????????????????????????????????
                    isExistPerson = "0";
                }
            }

            if (ztTaskPacketEntityList.size() > 0) {
                ZtTaskPacketEntity ztTaskPacketEntity = ztTaskPacketEntityList.get(0);
                List<ZtTaskItemEntity> ztTaskItemEntityList = ztTaskPacketEntity.getLstTaskItemInfo();
                String repairCode = ztTaskPacketEntity.getTaskRepairCode();
                if ("1".equals(repairCode)) {
                    String arrangeType = ztTaskItemEntityList.get(0).getArrangeType();
                    //???????????????????????????????????????
                    //?????????????????????????????????
                    TrainsetInfo trainsetInfo = remoteService.getTrainsetDetialInfo(trainsetId);
                    String marshalcount = trainsetInfo.getIMarshalcount();
                    String carNos = "";
                    Map oneAllotConfig = coneAllotConfigController.getOneAllotConfig(unitCode, deptCode);
                    List<XzyCOneallotConfig> xzyCOneallotConfigList = (List<XzyCOneallotConfig>) oneAllotConfig.get("data");
                    List<XzyCOneallotConfig> xzyCOneallotConfigs = xzyCOneallotConfigList.stream().filter(t -> t.getsMarshalnum().equals(marshalcount)).collect(Collectors.toList());
                    if (xzyCOneallotConfigs.size() > 0) {
                        List<XzyCOneallotTemplate> xzyCOneallotTemplateList = xzyCOneallotConfigs.get(0).getOneallotTemplateList();
                        if (xzyCOneallotTemplateList.size() > 0) {
                            carNos = xzyCOneallotTemplateList.get(0).getsCarnolist();
                        }
                    }
                    //??????fillType????????????????????????
                    //??????????????????????????????????????????????????????
                    // List<ColumnParam<ChecklistSummary, ?>> columnParamList = new ArrayList<>();
                    // columnParamList.add(eqParam(ChecklistSummary::getDayPlanId, "2022-02-09-00"));
                    // columnParamList.add(eqParam(ChecklistSummary::getUnitCode, unitCode));
                    // columnParamList.add(eqParam(ChecklistSummary::getDeptCode, deptCode));
                    // columnParamList.add(eqParam(ChecklistSummary::getTrainsetId,trainsetId));
                    // columnParamList.add(eqParam(ChecklistSummary::getItemCode, packetCode));
                    // List<ChecklistSummary> checklistSummaryList = MybatisPlusUtils.selectList(
                    //     checklistSummaryService,
                    //     columnParamList
                    // );

                    //???????????????????????????
                    List<String> carNoList = Arrays.asList(carNos.split(","));
                    if (carNoList.size() > 0) {
                        for (String carNo : carNoList) {
                            PlanItem planItem = new PlanItem();
                            String itemName = ztTaskPacketEntity.getPacketName();
                            planItem.setItemCode(packetCode);
                            planItem.setItemName(itemName);
                            planItem.setArrangeType(arrangeType);
                            //???carNo?????????????????????01-04?????????01???02???03???04
                            List<String> carNumbers = new ArrayList<>();
                            int carNoListSize = carNoList.size();
                            List<String> carNoSplit = Arrays.asList(carNo.split("-"));
                            int number = Integer.parseInt(marshalcount) / carNoListSize;
                            int car = Integer.parseInt(carNoSplit.get(0));
                            for (int i = 0; i < number; i++) {
                                carNumbers.add("0" + car);
                                car++;
                                if (car == Integer.parseInt(marshalcount)) {
                                    carNumbers.add("00");
                                    break;
                                }
                            }
                            String packetName = ztTaskPacketEntity.getPacketName();
                            String displayItemName = packetName + "(" + carNo + ")";
                            planItem.setDisplayItemName(displayItemName);
                            //???????????????????????????
                            if (ToolUtil.isEmpty(taskAllotPacketEntityList)) {
                                //??????????????????
                                planItem.setIsExistPerson(isExistPerson);
                            } else {
                                //????????????
                                //??????carnumber????????????????????????
                                List<TaskAllotItemEntity> taskAllotItemEntityList = taskAllotPacketEntityList.get(0).getTaskAllotItemEntityList();
                                List<String> carNosList = taskAllotItemEntityList.stream().map(m -> m.getCarNo()).distinct().collect(Collectors.toList());
                                if (Collections.disjoint(carNosList, carNumbers)) {
                                    //????????????????????????
                                    planItem.setIsExistPerson("0");
                                } else {
                                    //????????????????????????
                                    planItem.setIsExistPerson("1");
                                }
                            }
                            planItemList.add(planItem);
                        }
                    }
                } else {
                    //???????????????
                    if (ztTaskItemEntityList.size() > 0) {
                        Map<String, List<ZtTaskItemEntity>> ztTaskItemEntityMap = ztTaskItemEntityList.stream().collect(Collectors.groupingBy(ZtTaskItemEntity::getItemCode));
                        for (String itemCode : ztTaskItemEntityMap.keySet()) {
                            PlanItem planItem = new PlanItem();
                            ZtTaskItemEntity ztTaskItemEntity = ztTaskItemEntityMap.get(itemCode).get(0);
                            String itemName = ztTaskItemEntity.getItemName();
                            String arrangeType = ztTaskItemEntity.getArrangeType();
                            planItem.setItemCode(itemCode);
                            planItem.setItemName(itemName);
                            planItem.setArrangeType(arrangeType);
                            if (ToolUtil.isNotEmpty(taskAllotPacketEntityList)) {
                                List<TaskAllotItemEntity> taskAllotItemEntityList = taskAllotPacketEntityList.get(0).getTaskAllotItemEntityList();
                                //????????????code????????????????????????
                                List<TaskAllotItemEntity> itemEntityList = taskAllotItemEntityList.stream().filter(t -> t.getItemCode().equals(itemCode)).collect(Collectors.toList());
                                boolean hasWorkerList = itemEntityList.stream().anyMatch(t -> t.getTaskAllotPersonEntityList() != null && t.getTaskAllotPersonEntityList().size() > 0);
                                if (hasWorkerList) {
                                    //???????????????
                                    planItem.setIsExistPerson("1");
                                } else {
                                    //???????????????
                                    planItem.setIsExistPerson("0");
                                }
                            } else {
                                planItem.setIsExistPerson(isExistPerson);
                            }
                            planItemList.add(planItem);
                        }
                    }
                }
            }

        }
        return planItemList;
    }

    @Override
    public List<PlanCarNoOrPart> getPlanCarNoOrPart(String dayPlanId, String unitCode, String deptCode, String
            trainsetId, String packetCode, String itemCode, String displayItemName) {
        //????????????????????????
        List<ZtTaskPacketEntity> taskPacketEntityList = remoteService.getPacketTaskByCondition(dayPlanId, trainsetId, null, deptCode, unitCode);
        //??????packetCode????????????
        List<ZtTaskPacketEntity> ztTaskPacketEntityList = taskPacketEntityList.stream().filter(t -> t.getPacketCode().equals(packetCode)).collect(Collectors.toList());
        //????????????????????????
        List<String> deptCodeList = Arrays.asList(deptCode);
        List<String> trainsetIdList = Arrays.asList(trainsetId);
        List<TaskAllotPacketEntity> getTaskAllotData = midGroundService.getTaskAllotData(unitCode, dayPlanId, null, trainsetIdList, deptCodeList, null, null);
        List<String> carNoOrPartFinish = new ArrayList<>();
        if (getTaskAllotData.size() > 0) {
            //??????packetCode????????????
            List<TaskAllotPacketEntity> taskAllotPacketEntityList = getTaskAllotData.stream().filter(t -> t.getPacketCode().equals(packetCode)).collect(Collectors.toList());
            if (taskAllotPacketEntityList.size() > 0) {
                List<TaskAllotItemEntity> taskAllotItemEntityList = taskAllotPacketEntityList.get(0).getTaskAllotItemEntityList();
                //??????itemCode????????????
                List<TaskAllotItemEntity> taskAllotItemEntities = taskAllotItemEntityList.stream().filter(t -> t.getItemCode().equals(itemCode)).collect(Collectors.toList());
                if (taskAllotItemEntities.size() > 0) {
                    //??????????????????????????????????????????????????????????????????????????????????????????????????????id
                    String arrageType = taskAllotItemEntities.get(0).getArrageType();
                    if ("0".equals(arrageType)) {
                        //??????
                        carNoOrPartFinish = taskAllotItemEntities.stream().map(m -> m.getCarNo()).distinct().collect(Collectors.toList());
                    } else {
                        //??????
                        carNoOrPartFinish = taskAllotItemEntities.stream().map(m -> m.getPartPosition()).distinct().collect(Collectors.toList());
                    }
                }

            }
        }
        List<PlanCarNoOrPart> planCarNoOrPartList = new ArrayList<>();
        if (ToolUtil.isNotEmpty(displayItemName)) {
            //??????????????????
            //???displayItemName???????????????????????????????????????01-04????????????01???02???03???04
            int index1 = displayItemName.indexOf('(');
            int index2 = displayItemName.indexOf(')');
            String carNo = displayItemName.substring(index1 + 1, index2);
            List<String> carNoSplit = Arrays.asList(carNo.split("-"));
            //?????????????????????
            TrainsetInfo trainsetInfo = remoteService.getTrainsetDetialInfo(trainsetId);
            String marshalcount = trainsetInfo.getIMarshalcount();
            String carNos = "";
            Map oneAllotConfig = coneAllotConfigController.getOneAllotConfig(unitCode, deptCode);
            List<XzyCOneallotConfig> xzyCOneallotConfigList = (List<XzyCOneallotConfig>) oneAllotConfig.get("data");
            List<XzyCOneallotConfig> xzyCOneallotConfigs = xzyCOneallotConfigList.stream().filter(t -> t.getsMarshalnum().equals(marshalcount)).collect(Collectors.toList());
            if (xzyCOneallotConfigs.size() > 0) {
                List<XzyCOneallotTemplate> xzyCOneallotTemplateList = xzyCOneallotConfigs.get(0).getOneallotTemplateList();
                if (xzyCOneallotTemplateList.size() > 0) {
                    carNos = xzyCOneallotTemplateList.get(0).getsCarnolist();
                }
            }
            List<String> carNoList = Arrays.asList(carNos.split(","));
            int carNoListSize = carNoList.size();
            int number = Integer.parseInt(marshalcount) / carNoListSize;
            int car = Integer.parseInt(carNoSplit.get(0));
            for (int i = 0; i < number; i++) {
                PlanCarNoOrPart planCarNoOrPart = new PlanCarNoOrPart();
                String carSplit = "0" + car;
                planCarNoOrPart.setCarNoOrPartName(carSplit);
                if (carNoOrPartFinish.contains(carSplit)) {
                    planCarNoOrPart.setIsChoseState("1");
                } else {
                    planCarNoOrPart.setIsChoseState("0");
                }
                planCarNoOrPartList.add(planCarNoOrPart);
                car++;
                if (car == Integer.parseInt(marshalcount)) {
                    PlanCarNoOrPart planCarNoOrPart1 = new PlanCarNoOrPart();
                    carSplit = "00";
                    planCarNoOrPart1.setCarNoOrPartName(carSplit);
                    if (carNoOrPartFinish.contains(carSplit)) {
                        planCarNoOrPart1.setIsChoseState("1");
                    } else {
                        planCarNoOrPart1.setIsChoseState("0");
                    }
                    planCarNoOrPartList.add(planCarNoOrPart1);
                    break;
                }
            }
        } else {
            //?????????????????????
            //????????????????????????????????????????????????
            //??????????????????????????????????????????????????????
            JSONObject faultPacketInfo = this.getFaultPacketInfo();
            JSONArray jsonArrayCarNo = new JSONArray();
            if (!ObjectUtils.isEmpty(faultPacketInfo)) {
                String faultPacketCode = faultPacketInfo.getString("S_PACKET_CODE");
                if (faultPacketCode.equals(packetCode)) {
                    //??????????????????
                    //????????????????????????
                    JSONObject faultParams = new JSONObject();
                    faultParams.put("findFaultTime", dayPlanId.substring(0, dayPlanId.length() - 3));
                    faultParams.put("trainsetIdList", trainsetIdList);
                    JSONArray faultData = remoteService.getFaultData(faultParams);
                    faultData = faultData.stream().filter(t -> ((JSONObject) t).get("faultId").equals(itemCode)).collect(Collectors.toCollection(JSONArray::new));
                    if (faultData.size() > 0) {
                        //????????????carNo
                        jsonArrayCarNo = faultData.stream().map(t -> ((JSONObject) t).get("carNo")).distinct().collect(Collectors.toCollection(JSONArray::new));
                        for (Object carNo : jsonArrayCarNo) {
                            PlanCarNoOrPart planCarNoOrPart = new PlanCarNoOrPart();
                            planCarNoOrPart.setCarNoOrPartName(carNo.toString());
                            boolean hasCarNo = carNoOrPartFinish.stream().anyMatch(t -> t.equals(carNo));
                            if (hasCarNo) {
                                //???????????????
                                planCarNoOrPart.setIsChoseState("1");
                            } else {
                                //???????????????
                                planCarNoOrPart.setIsChoseState("0");
                            }
                            planCarNoOrPartList.add(planCarNoOrPart);
                        }
                    }
                }
            }
            if (ztTaskPacketEntityList.size() > 0) {
                List<ZtTaskItemEntity> ztTaskItemEntityList = ztTaskPacketEntityList.get(0).getLstTaskItemInfo();
                List<ZtTaskItemEntity> ztTaskItemEntities = ztTaskItemEntityList.stream().filter(t -> t.getItemCode().equals(itemCode)).collect(Collectors.toList());
                //????????????????????????????????????????????????
//                if(jsonArrayCarNo.)
                //????????????????????????????????????
                String arrangeType = ztTaskItemEntities.get(0).getArrangeType();
                if ("0".equals(arrangeType)) {
                    //??????
                    List<String> carNoList = ztTaskItemEntities.stream().map(m -> m.getCarNo()).distinct().collect(Collectors.toList());
                    //?????????00????????????00???????????????
                    if (carNoList.contains("00")) {
                        carNoList.remove("00");
                        carNoList.add("00");
                    }
                    //????????????????????????
                    for (String carNo : carNoList) {
                        PlanCarNoOrPart planCarNoOrPart = new PlanCarNoOrPart();
                        planCarNoOrPart.setCarNoOrPartName(carNo);
                        boolean hasCarNo = carNoOrPartFinish.stream().anyMatch(t -> t.equals(carNo));
                        if (hasCarNo) {
                            //???????????????
                            planCarNoOrPart.setIsChoseState("1");
                        } else {
                            //???????????????
                            planCarNoOrPart.setIsChoseState("0");
                        }
                        planCarNoOrPartList.add(planCarNoOrPart);
                    }
                } else {
                    //??????
                    for (ZtTaskItemEntity ztTaskItemEntity : ztTaskItemEntities) {
                        PlanCarNoOrPart planCarNoOrPart = new PlanCarNoOrPart();
                        String positionCode = ztTaskItemEntity.getPositionCode();
                        planCarNoOrPart.setCarNoOrPartName(ztTaskItemEntity.getPositionName());
                        boolean hasCarPart = carNoOrPartFinish.stream().anyMatch(t -> t.equals(positionCode));
                        if (hasCarPart) {
                            //???????????????
                            planCarNoOrPart.setIsChoseState("1");
                        } else {
                            //???????????????
                            planCarNoOrPart.setIsChoseState("0");
                        }
                        planCarNoOrPartList.add(planCarNoOrPart);
                    }
                }
            }


        }
        return planCarNoOrPartList;
    }

    @Override
    public List<PlanAllotInfo> getPlanAllotInfo(String dayPlanId, String unitCode, String deptCode, String trainsetId, String
            packetCode, String itemCode, String displayItemName) {
        //????????????????????????
        List<String> deptCodeList = Arrays.asList(deptCode);
        List<String> trainsetIdList = Arrays.asList(trainsetId);
        List<TaskAllotPacketEntity> getTaskAllotData = midGroundService.getTaskAllotData(unitCode, dayPlanId, null, trainsetIdList, deptCodeList, null, null);
        List<TaskAllotItemEntity> taskAllotItemEntities = new ArrayList<>();
        List<TaskAllotPacketEntity> taskAllotPacketEntityList = new ArrayList<>();
        if (getTaskAllotData.size() > 0) {
            //??????packetCode????????????
            taskAllotPacketEntityList = getTaskAllotData.stream().filter(t -> t.getPacketCode().equals(packetCode)).collect(Collectors.toList());
            if (taskAllotPacketEntityList.size() > 0) {
                List<TaskAllotItemEntity> taskAllotItemEntityList = taskAllotPacketEntityList.get(0).getTaskAllotItemEntityList();
                //??????itemCode????????????
                taskAllotItemEntities = taskAllotItemEntityList.stream().filter(t -> t.getItemCode().equals(itemCode)).collect(Collectors.toList());
            }
        }
        List<PlanAllotInfo> planAllotInfoList = new ArrayList<>();
//        List<TaskAllotPacketEntity> finalTaskAllotPacketEntityList = taskAllotPacketEntityList;
//        TaskAllotPacketEntity taskAllotPacketEntity = finalTaskAllotPacketEntityList.get(0);
        if (ToolUtil.isNotEmpty(displayItemName)) {
            //?????????
            //???displayItemName???????????????????????????????????????01-04????????????01???02???03???04
            int index1 = displayItemName.indexOf('(');
            int index2 = displayItemName.indexOf(')');
            String carNo = displayItemName.substring(index1 + 1, index2);
            List<String> carNoSplit = Arrays.asList(carNo.split("-"));
            //?????????????????????
            TrainsetInfo trainsetInfo = remoteService.getTrainsetDetialInfo(trainsetId);
            String marshalcount = trainsetInfo.getIMarshalcount();
            String carNos = "";
            Map oneAllotConfig = coneAllotConfigController.getOneAllotConfig(unitCode, deptCode);
            List<XzyCOneallotConfig> xzyCOneallotConfigList = (List<XzyCOneallotConfig>) oneAllotConfig.get("data");
            List<XzyCOneallotConfig> xzyCOneallotConfigs = xzyCOneallotConfigList.stream().filter(t -> t.getsMarshalnum().equals(marshalcount)).collect(Collectors.toList());
            if (xzyCOneallotConfigs.size() > 0) {
                List<XzyCOneallotTemplate> xzyCOneallotTemplateList = xzyCOneallotConfigs.get(0).getOneallotTemplateList();
                if (xzyCOneallotTemplateList.size() > 0) {
                    carNos = xzyCOneallotTemplateList.get(0).getsCarnolist();
                }
            }
            List<String> carNoList = Arrays.asList(carNos.split(","));
            int carNoListSize = carNoList.size();
            int number = Integer.parseInt(marshalcount) / carNoListSize;
            int car = Integer.parseInt(carNoSplit.get(0));
            for (int i = 0; i < number; i++) {
                String carSplit = "0" + car;
                //??????????????????????????????????????????
                if (taskAllotItemEntities.size() > 0) {
                    String finalCarSplit = carSplit;
                    List<TaskAllotItemEntity> taskAllotItemEntityList = taskAllotItemEntities.stream().filter(t -> t.getCarNo().equals(finalCarSplit)).collect(Collectors.toList());
                    if (taskAllotItemEntityList.size() > 0) {
                        PlanAllotInfo planAllotInfo = new PlanAllotInfo();
                        planAllotInfo.setCarNoOrPartName(finalCarSplit);
                        List<TaskAllotPersonEntity> taskAllotPersonEntityList = taskAllotItemEntityList.get(0).getTaskAllotPersonEntityList();
                        List<String> planWorkerList = taskAllotPersonEntityList.stream().map(m -> m.getWorkerName()).distinct().collect(Collectors.toList());
                        String planWorkers = com.ydzbinfo.emis.utils.StringUtils.join(planWorkerList, ",");
                        planAllotInfo.setPlanWorkers(planWorkers);
                        planAllotInfoList.add(planAllotInfo);
                    }
                }
                car++;
                if (car == Integer.parseInt(marshalcount)) {
                    carSplit = "00";
                    //??????????????????????????????????????????
                    if (taskAllotItemEntities.size() > 0) {
                        String finalCarSplit = carSplit;
                        List<TaskAllotItemEntity> taskAllotItemEntityList = taskAllotItemEntities.stream().filter(t -> t.getCarNo().equals(finalCarSplit)).collect(Collectors.toList());
                        if (taskAllotItemEntityList.size() > 0) {
                            PlanAllotInfo planAllotInfo = new PlanAllotInfo();
                            planAllotInfo.setCarNoOrPartName(finalCarSplit);
                            List<TaskAllotPersonEntity> taskAllotPersonEntityList = taskAllotItemEntityList.get(0).getTaskAllotPersonEntityList();
                            List<String> planWorkerList = taskAllotPersonEntityList.stream().map(m -> m.getWorkerName()).distinct().collect(Collectors.toList());
                            String planWorkers = com.ydzbinfo.emis.utils.StringUtils.join(planWorkerList, ",");
                            planAllotInfo.setPlanWorkers(planWorkers);
                            planAllotInfo.setPlanWorkers(planWorkers);
                            planAllotInfoList.add(planAllotInfo);
                        }
                    }
                    break;
                }
            }
        } else {
            //???????????????
            if (taskAllotItemEntities.size() > 0) {
                planAllotInfoList = taskAllotItemEntities.stream().map(entity -> {
                    PlanAllotInfo planAllotInfo = new PlanAllotInfo();
                    //????????????????????????
                    String arrageType = entity.getArrageType();
                    //??????????????????????????????????????????
                    JSONObject faultPacketInfo = this.getFaultPacketInfo();
                    String faultPacketCode = faultPacketInfo.getString("S_PACKET_CODE");
                    RecheckPacket overRunPacketInfo = this.getOverRunPacketInfo();
//                    String faultPacketCode1 = overRunPacketInfo.getPacketCode();
                    if (faultPacketCode.equals(packetCode)) {
                        //?????????
                        String carNo = entity.getCarNo();
                        String itemName = entity.getItemName();
                        String carNoOrPartName = "[?????????" + carNo + "]" + itemName;
                        planAllotInfo.setCarNoOrPartName(carNoOrPartName);
                    } else {
                        if ("0".equals(arrageType)) {
                            //??????
                            planAllotInfo.setCarNoOrPartName(entity.getCarNo());
                        } else {
                            //??????
                            planAllotInfo.setCarNoOrPartName(entity.getPartName());
                        }
                    }
                    List<TaskAllotPersonEntity> taskAllotPersonEntityList = entity.getTaskAllotPersonEntityList();
                    List<String> planWorkerList = taskAllotPersonEntityList.stream().map(m -> m.getWorkerName()).distinct().collect(Collectors.toList());
                    String planWorkers = com.ydzbinfo.emis.utils.StringUtils.join(planWorkerList, ",");
                    planAllotInfo.setPlanWorkers(planWorkers);
                    return planAllotInfo;
                }).collect(Collectors.toList());
            }
        }
        return planAllotInfoList;
    }

    @Override
    public List<TaskAllotPacketEntity> submitAllotInfo(String unitCode, String unitName, String deptCode, String
            deptName, String dayPlanId, List<PlanWorker> planWorkerList, List<AllotTrainset> allotTrainsetList) {
        //????????????????????????
        List<ZtTaskPacketEntity> taskPacketEntityList = remoteService.getPacketTaskByCondition(dayPlanId, null, null, deptCode, unitCode);
        List<String> deptCodeList = Arrays.asList(deptCode);
        List<String> trainsetIdList = Arrays.asList("CR400AF-2124");
        List<TaskAllotPacketEntity> getTaskAllotData = midGroundService.getTaskAllotData(unitCode, dayPlanId, null, trainsetIdList, deptCodeList, null, null);
        //????????????
        List<TaskAllotPersonEntity> taskAllotPersonEntities = new ArrayList<>();
        for (PlanWorker planWorker : planWorkerList) {
            TaskAllotPersonEntity taskAllotPersonEntity = new TaskAllotPersonEntity();
            taskAllotPersonEntity.setSId("");
            taskAllotPersonEntity.setWorkerId(planWorker.getWorkerId());
            //?????????????????????
            taskAllotPersonEntity.setWorkerType("");
            taskAllotPersonEntity.setWorkerName(planWorker.getWorkerName());
            taskAllotPersonEntity.setProcessId("");
            taskAllotPersonEntity.setBranchCode(planWorker.getBranchCode());
            taskAllotPersonEntities.add(taskAllotPersonEntity);
        }
        List<TaskAllotPacketEntity> taskAllotPacketEntities = new ArrayList<>();
        ShiroUser user = ShiroKit.getUser();
        for (AllotTrainset allotTrainset : allotTrainsetList) {
            String trainsetId = allotTrainset.getTrainsetId();
            String trainsetName = allotTrainset.getTrainsetName();
            //????????????id????????????
            TrainsetBaseInfo getTrainsetBaseinfoByID = remoteService.getTrainsetBaseinfoByID(trainsetId);
            String trainsetType = getTrainsetBaseinfoByID.getTraintype();
            List<AllotPacket> allotPacketList = allotTrainset.getAllotPacketList();
            List<ZtTaskPacketEntity> ztTaskPacketEntityList = taskPacketEntityList.stream().filter(t -> t.getTrainsetId().equals(trainsetId)).collect(Collectors.toList());
            if (allotPacketList.size() == 0) {
                //????????????,????????????????????????
                for (ZtTaskPacketEntity ztTaskPacketEntity : ztTaskPacketEntityList) {
                    TaskAllotPacketEntity taskAllotPacketEntity = new TaskAllotPacketEntity();
                    taskAllotPacketEntity.setTaskAllotDeptId("");
                    taskAllotPacketEntity.setDeptCode(deptCode);
                    taskAllotPacketEntity.setDeptName(deptName);
                    taskAllotPacketEntity.setTrainsetId(trainsetId);
                    taskAllotPacketEntity.setTrainsetName(trainsetName);
                    taskAllotPacketEntity.setRecordCode(user.getStaffId());
                    taskAllotPacketEntity.setRecordName(user.getName());
                    taskAllotPacketEntity.setRecordTime(new Date());
                    taskAllotPacketEntity.setDayPlanId(dayPlanId);
                    String packetCode = ztTaskPacketEntity.getPacketCode();
                    //????????????
                    String taskRepairCode = ztTaskPacketEntity.getTaskRepairCode();
                    taskAllotPacketEntity.setMainCyc(taskRepairCode);
                    taskAllotPacketEntity.setUnitCode(unitCode);
                    taskAllotPacketEntity.setUnitName(unitName);
                    taskAllotPacketEntity.setTaskAllotPacketId("");
                    String taskId = ztTaskPacketEntity.getTaskId();
                    taskAllotPacketEntity.setTaskId(taskId);
                    taskAllotPacketEntity.setPacketCode(packetCode);
                    taskAllotPacketEntity.setPacketName(ztTaskPacketEntity.getPacketName());
                    String packetTypeCode = ztTaskPacketEntity.getPacketTypeCode();
                    taskAllotPacketEntity.setPacketTypeCode(packetTypeCode);
                    //????????????
                    List<TaskAllotItemEntity> taskAllotItemEntities = new ArrayList<>();
                    if ("6".equals(packetTypeCode)) {
                        //??????????????????????????????????????????????????????
                        TaskAllotItemEntity taskAllotItemEntity = new TaskAllotItemEntity();
                        taskAllotItemEntity.setTaskItemId(taskId);
                        taskAllotItemEntity.setRepairMode("0");
                        taskAllotItemEntity.setCarNo("??????");
                        taskAllotItemEntity.setTrainsetType(trainsetType);
                        taskAllotItemEntity.setItemType(packetTypeCode);
                        taskAllotItemEntity.setArrageType("0");
                        taskAllotItemEntity.setItemCode(packetCode);
                        taskAllotItemEntity.setItemName(ztTaskPacketEntity.getPacketName());
                        taskAllotItemEntity.setTaskAllotPersonEntityList(taskAllotPersonEntities);
                        taskAllotItemEntity.setDataSource("1");
                        taskAllotItemEntities.add(taskAllotItemEntity);
                    } else {
                        List<ZtTaskItemEntity> ztTaskItemEntityList = ztTaskPacketEntity.getLstTaskItemInfo();
                        //???????????????????????????
                        for (ZtTaskItemEntity ztTaskItemEntity : ztTaskItemEntityList) {
                            TaskAllotItemEntity taskAllotItemEntity = new TaskAllotItemEntity();
                            taskAllotItemEntity.setRemark(taskAllotItemEntity.getRemark());
                            taskAllotItemEntity.setPartType(ztTaskItemEntity.getKeyPartTypeId());
                            taskAllotItemEntity.setPartName(ztTaskItemEntity.getPositionName());
                            taskAllotItemEntity.setPartId(ztTaskItemEntity.getPartId());
                            taskAllotItemEntity.setPartPosition(ztTaskItemEntity.getPositionCode());
                            taskAllotItemEntity.setLocationCode(ztTaskItemEntity.getLocationCode());
                            taskAllotItemEntity.setLocationName(ztTaskItemEntity.getLocationName());
                            taskAllotItemEntity.setTaskItemId("");
                            taskAllotItemEntity.setRepairMode("");
                            taskAllotItemEntity.setCarNo(ztTaskItemEntity.getCarNo());
                            taskAllotItemEntity.setTrainsetType(trainsetType);
                            taskAllotItemEntity.setItemType(ztTaskItemEntity.getItemType());
                            taskAllotItemEntity.setPublishCode("");
                            taskAllotItemEntity.setArrageType(ztTaskItemEntity.getArrangeType());
                            taskAllotItemEntity.setId("");
                            taskAllotItemEntity.setItemCode(ztTaskItemEntity.getItemCode());
                            taskAllotItemEntity.setItemName(ztTaskItemEntity.getItemName());
                            taskAllotItemEntity.setTaskAllotPersonEntityList(taskAllotPersonEntities);
                            taskAllotItemEntity.setTaskAllotPacketId("");
                            taskAllotItemEntity.setDataSource("1");
                            taskAllotItemEntities.add(taskAllotItemEntity);
                        }
                    }
                    taskAllotPacketEntity.setTaskAllotItemEntityList(taskAllotItemEntities);
                    taskAllotPacketEntities.add(taskAllotPacketEntity);
                }
            } else {
                //????????????????????????????????????
                for (AllotPacket allotPacket : allotPacketList) {
                    TaskAllotPacketEntity taskAllotPacketEntity = new TaskAllotPacketEntity();
                    taskAllotPacketEntity.setTaskAllotDeptId("");
                    taskAllotPacketEntity.setDeptCode(deptCode);
                    taskAllotPacketEntity.setDeptName(deptName);
                    taskAllotPacketEntity.setTrainsetId(trainsetId);
                    taskAllotPacketEntity.setTrainsetName(trainsetName);
                    taskAllotPacketEntity.setRecordCode(user.getStaffId());
                    taskAllotPacketEntity.setRecordName(user.getName());
                    taskAllotPacketEntity.setRecordTime(new Date());
                    taskAllotPacketEntity.setDayPlanId(dayPlanId);
                    String packetCode = allotPacket.getPacketCode();
                    List<ZtTaskPacketEntity> ztTaskPacketEntityList1 = ztTaskPacketEntityList.stream().filter(t -> t.getPacketCode().equals(packetCode)).collect(Collectors.toList());
                    ZtTaskPacketEntity ztTaskPacketEntity = ztTaskPacketEntityList1.get(0);
                    //????????????
                    String taskRepairCode = ztTaskPacketEntity.getTaskRepairCode();
                    taskAllotPacketEntity.setMainCyc(taskRepairCode);
                    taskAllotPacketEntity.setUnitCode(unitCode);
                    taskAllotPacketEntity.setUnitName(unitName);
                    taskAllotPacketEntity.setTaskAllotPacketId("");
                    String taskId = ztTaskPacketEntity.getTaskId();
                    taskAllotPacketEntity.setTaskId(taskId);
                    taskAllotPacketEntity.setPacketCode(packetCode);
                    taskAllotPacketEntity.setPacketName(allotPacket.getPacketName());
                    String packetTypeCode = ztTaskPacketEntity.getPacketTypeCode();
                    taskAllotPacketEntity.setPacketTypeCode(packetTypeCode);
                    List<AllotItem> allotItemList = allotPacket.getAllotItemList();
                    //????????????
                    List<TaskAllotItemEntity> taskAllotItemEntities = new ArrayList<>();
                    List<ZtTaskItemEntity> ztTaskItemEntityList = ztTaskPacketEntity.getLstTaskItemInfo();
                    if (allotItemList.size() == 0) {
                        //??????????????????????????????????????????
                        if ("6".equals(packetTypeCode)) {
                            //??????????????????????????????????????????????????????
                            TaskAllotItemEntity taskAllotItemEntity = new TaskAllotItemEntity();
                            taskAllotItemEntity.setTaskItemId(taskId);
                            taskAllotItemEntity.setRepairMode("0");
                            taskAllotItemEntity.setCarNo("??????");
                            taskAllotItemEntity.setTrainsetType(trainsetType);
                            taskAllotItemEntity.setItemType(packetTypeCode);
                            taskAllotItemEntity.setArrageType("0");
                            taskAllotItemEntity.setItemCode(packetCode);
                            taskAllotItemEntity.setItemName(ztTaskPacketEntity.getPacketName());
                            taskAllotItemEntity.setTaskAllotPersonEntityList(taskAllotPersonEntities);
                            taskAllotItemEntity.setDataSource("1");
                            taskAllotItemEntities.add(taskAllotItemEntity);
                        } else {
                            for (ZtTaskItemEntity ztTaskItemEntity : ztTaskItemEntityList) {
                                TaskAllotItemEntity taskAllotItemEntity = new TaskAllotItemEntity();
                                taskAllotItemEntity.setRemark(taskAllotItemEntity.getRemark());
                                taskAllotItemEntity.setPartType(ztTaskItemEntity.getKeyPartTypeId());
                                taskAllotItemEntity.setPartName(ztTaskItemEntity.getPositionName());
                                taskAllotItemEntity.setPartId(ztTaskItemEntity.getPartId());
                                taskAllotItemEntity.setPartPosition(ztTaskItemEntity.getPositionCode());
                                taskAllotItemEntity.setLocationCode(ztTaskItemEntity.getLocationCode());
                                taskAllotItemEntity.setLocationName(ztTaskItemEntity.getLocationName());
                                taskAllotItemEntity.setTaskItemId("");
                                taskAllotItemEntity.setRepairMode("");
                                taskAllotItemEntity.setCarNo(ztTaskItemEntity.getCarNo());
                                taskAllotItemEntity.setTrainsetType(trainsetType);
                                taskAllotItemEntity.setItemType(ztTaskItemEntity.getItemType());
                                taskAllotItemEntity.setPublishCode("");
                                taskAllotItemEntity.setArrageType(ztTaskItemEntity.getArrangeType());
                                taskAllotItemEntity.setId("");
                                taskAllotItemEntity.setItemCode(ztTaskItemEntity.getItemCode());
                                taskAllotItemEntity.setItemName(ztTaskItemEntity.getItemName());
                                taskAllotItemEntity.setTaskAllotPersonEntityList(taskAllotPersonEntities);
                                taskAllotItemEntity.setTaskAllotPacketId("");
                                taskAllotItemEntity.setDataSource("1");
                                taskAllotItemEntities.add(taskAllotItemEntity);
                            }

                        }


                    } else {
                        for (AllotItem allotItem : allotItemList) {
                            String itemCode = allotItem.getItemCode();
                            List<AllotCarOrPart> allotCarOrPartList = allotItem.getAllotCarOrPartList();
                            List<ZtTaskItemEntity> ztTaskItemEntityList1 = ztTaskItemEntityList.stream().filter(t -> t.getItemCode().equals(itemCode)).collect(Collectors.toList());
                            //????????????????????????????????????????????????????????????
                            //??????????????????
                            List<ZtTaskItemEntity> ztTaskItemEntityList2 = ztTaskItemEntityList1.stream().filter(t -> t.getArrangeType().equals("1")).collect(Collectors.toList());
                            if (ztTaskItemEntityList2.size() > 0) {
                                //??????
                                for (ZtTaskItemEntity ztTaskItemEntity : ztTaskItemEntityList2) {
                                    TaskAllotItemEntity taskAllotItemEntity = new TaskAllotItemEntity();
                                    taskAllotItemEntity.setRemark(taskAllotItemEntity.getRemark());
                                    taskAllotItemEntity.setPartType(ztTaskItemEntity.getKeyPartTypeId());
                                    taskAllotItemEntity.setPartName(ztTaskItemEntity.getPositionName());
                                    taskAllotItemEntity.setPartId(ztTaskItemEntity.getPartId());
                                    taskAllotItemEntity.setPartPosition(ztTaskItemEntity.getPositionCode());
                                    taskAllotItemEntity.setLocationCode(ztTaskItemEntity.getLocationCode());
                                    taskAllotItemEntity.setLocationName(ztTaskItemEntity.getLocationName());
                                    taskAllotItemEntity.setTaskItemId("");
                                    taskAllotItemEntity.setRepairMode("");
                                    taskAllotItemEntity.setCarNo(ztTaskItemEntity.getCarNo());
                                    taskAllotItemEntity.setTrainsetType(trainsetType);
                                    taskAllotItemEntity.setItemType(ztTaskItemEntity.getItemType());
                                    taskAllotItemEntity.setPublishCode("");
                                    taskAllotItemEntity.setArrageType(ztTaskItemEntity.getArrangeType());
                                    taskAllotItemEntity.setId("");
                                    taskAllotItemEntity.setItemCode(ztTaskItemEntity.getItemCode());
                                    taskAllotItemEntity.setItemName(ztTaskItemEntity.getItemName());
                                    taskAllotItemEntity.setTaskAllotPersonEntityList(taskAllotPersonEntities);
                                    taskAllotItemEntity.setTaskAllotPacketId("");
                                    taskAllotItemEntity.setDataSource("1");
                                    taskAllotItemEntities.add(taskAllotItemEntity);
                                }
                            } else {
                                //??????
                                for (AllotCarOrPart allotCarOrPart : allotCarOrPartList) {
                                    TaskAllotItemEntity taskAllotItemEntity = new TaskAllotItemEntity();
                                    String carNoOrPartName = allotCarOrPart.getCarNoOrPartName();
                                    List<ZtTaskItemEntity> ztTaskItemEntityList3;
                                    if ("1".equals(taskRepairCode)) {
                                        //?????????
                                        ztTaskItemEntityList3 = ztTaskItemEntityList1;
                                    } else {
                                        ztTaskItemEntityList3 = ztTaskItemEntityList1.stream().filter(t -> t.getCarNo().equals(carNoOrPartName)).collect(Collectors.toList());
                                    }
                                    ZtTaskItemEntity ztTaskItemEntity = ztTaskItemEntityList3.get(0);
                                    taskAllotItemEntity.setRemark(ztTaskItemEntity.getRemark());
                                    taskAllotItemEntity.setPartType(ztTaskItemEntity.getKeyPartTypeId());
                                    taskAllotItemEntity.setPartName(ztTaskItemEntity.getPositionName());
                                    taskAllotItemEntity.setPartId(ztTaskItemEntity.getPartId());
                                    taskAllotItemEntity.setPartPosition(ztTaskItemEntity.getPositionCode());
                                    taskAllotItemEntity.setLocationCode(ztTaskItemEntity.getLocationCode());
                                    taskAllotItemEntity.setLocationName(ztTaskItemEntity.getLocationName());
                                    taskAllotItemEntity.setTaskItemId("");
                                    taskAllotItemEntity.setRepairMode("");
                                    taskAllotItemEntity.setCarNo(carNoOrPartName);
                                    taskAllotItemEntity.setTrainsetType(trainsetType);
                                    taskAllotItemEntity.setItemType(ztTaskItemEntity.getItemType());
                                    taskAllotItemEntity.setPublishCode("");
                                    taskAllotItemEntity.setArrageType(ztTaskItemEntity.getArrangeType());
                                    taskAllotItemEntity.setId("");
                                    taskAllotItemEntity.setItemCode(itemCode);
                                    taskAllotItemEntity.setItemName(allotItem.getItemName());
                                    taskAllotItemEntity.setTaskAllotPersonEntityList(taskAllotPersonEntities);
                                    taskAllotItemEntity.setTaskAllotPacketId("");
                                    taskAllotItemEntity.setDataSource("1");
                                    taskAllotItemEntities.add(taskAllotItemEntity);
                                }
                            }
                        }
                    }
                    taskAllotPacketEntity.setTaskAllotItemEntityList(taskAllotItemEntities);
                    taskAllotPacketEntities.add(taskAllotPacketEntity);
                }
            }
        }
        return taskAllotPacketEntities;
    }
}
