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
 * Description: 手持机派工业务处理类
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

    //PC端派工包接口服务
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
            //1.调用PC接口获取派工数据
            repairTask = xzyMTaskallotpacketService.getRepairTask(unitCode, dayPlanId, deptCode, null, "1");
        } catch (Exception e) {
            logger.error("手持机调用PC接口获取派工数据异常", e);
        }
        JSONArray data = repairTask.getJSONArray("data");
        JSONArray queryList = repairTask.getJSONArray("queryList");
        JSONArray allotTypeDict = repairTask.getJSONArray("allotTypeDict");
        JSONArray taskAllotTypeDict = repairTask.getJSONArray("taskAllotTypeDict");
        //2.将数据转换为AllotData实体对象集合
        List<AllotData> allotDataList = JSONArray.parseArray(data.toJSONString(), AllotData.class);
        if (!CollectionUtils.isEmpty(allotDataList)) {
            //作业包类型集合
            List<PacketType> packetTypeArrayList = new ArrayList<>();
            //车组名称集合
            List<String> trainsetNames = allotDataList.stream().map(AllotData::getTrainsetName).distinct().collect(Collectors.toList());
            Set<String> packetTypes = new HashSet<>();
            List<String> iMarshalcounts = new ArrayList<>();
            //车组集合
            List<String> trainsetIdList = allotDataList.stream().map(AllotData::getTrainsetId).distinct().collect(Collectors.toList());
            //获取派工显示字典表集合数据
            List<XzyBTaskallotshowDict> xzyBTaskallotshowDictList = xzyBTaskallotshowDictService.getShowDictByTaskAllotType("");
            //3.获取需要工长认领的故障信息
            JSONArray faultData = new JSONArray();
            try {
                if (!CollectionUtils.isEmpty(trainsetIdList)) {
                    JSONObject faultParams = new JSONObject();
                    faultParams.put("findFaultTime", dayPlanId.substring(0, dayPlanId.length() - 3));
                    faultParams.put("trainsetIdList", trainsetIdList);
                    faultData = remoteService.getFaultData(faultParams);
                }
            } catch (Exception e) {
                logger.error("手持机获取工长认领故障异常", e);
            }

            //4.获取需要工长认领的复核任务信息
            List<EntitySJOverRunRecord> entitySJOverRunRecordList = new ArrayList<>();
            try {
                Map<String, Object> overRunParams = new HashMap<>();
                overRunParams.put("TrainsetNameList", trainsetNames);
                entitySJOverRunRecordList = reCheckTaskService.getReCheckTaskList(overRunParams);
            } catch (Exception ex) {
                logger.error("手持机获取工长认领复核异常", ex);
            }

            List<PhoneAllotData> phoneAllotDataArrayList = new ArrayList<>();
            Map<String, List<AllotData>> allotDataMap = allotDataList.stream().collect(Collectors.groupingBy(AllotData::getTrainsetId));
            for (String trainsetId : allotDataMap.keySet()) {
                //获取编组数
                String iMarshalcount = allotDataMap.get(trainsetId).get(0).getMarshal();
                String trainsetName = allotDataMap.get(trainsetId).get(0).getTrainsetName();
                String trainsetType = allotDataMap.get(trainsetId).get(0).getTrainsetTypeCode();
                logger.info("获取车组的编组数信息：{}", iMarshalcount);
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
                        //获取作业包类型
                        String packetType = packet.getPacketType();
                        packetTypes.add(packetType);
                    }
                    packetData.setShowMode(allotData.getShowMode());
                    packetDataArrayList.add(packetData);
                }
                /** 暂时注释掉，改为组织完数据之后统一处理派工状态
                 //当前车组下的所有包
                 List<AllotData> currentAllotDataList = allotDataMap.get(trainsetId);
                 //未派工的包数量
                 long wCount = currentAllotDataList.stream().filter(t -> "0".equals(t.getAllotStateCode())).count();
                 //已派工的包数量
                 long yCount = currentAllotDataList.stream().filter(t -> "1".equals(t.getAllotStateCode())).count();
                 //部分派工的包数量
                 long bCount = currentAllotDataList.stream().filter(t -> "2".equals(t.getAllotStateCode())).count();
                 if (bCount > 0) {
                 phoneAllotData.setAllotStateCode("2");
                 phoneAllotData.setAllotStateName("部分派工");
                 } else if (wCount > 0) {
                 if (yCount > 0) {
                 phoneAllotData.setAllotStateCode("2");
                 phoneAllotData.setAllotStateName("部分派工");
                 } else {
                 phoneAllotData.setAllotStateCode("0");
                 phoneAllotData.setAllotStateName("未派工");
                 }
                 } else if (yCount > 0) {
                 phoneAllotData.setAllotStateCode("1");
                 phoneAllotData.setAllotStateName("已派工");
                 } else {
                 phoneAllotData.setAllotStateCode("0");
                 phoneAllotData.setAllotStateName("未派工");
                 }**/

                //5.将需要工长认领的故障添加到该车的任务集合下边
                if (faultData != null && faultData.size() > 0) {
                    //5.1获取该车中是否有故障任务的包
                    PacketData faultPacketData = packetDataArrayList.stream().filter(t -> !ObjectUtils.isEmpty(t.getPacket()) && "5".equals(t.getPacket().getPacketType())).findFirst().orElse(null);
                    if (ObjectUtils.isEmpty(faultPacketData)) {//5.2 new一个故障的作业包并添加到该车的任务集合下
                        faultPacketData = new PacketData();
                        JSONObject faultPacketInfo = this.getFaultPacketInfo();
                        if (!ObjectUtils.isEmpty(faultPacketInfo)) {
                            faultPacketData.setWorkTaskDisplayName(faultPacketInfo.getString("S_PACKET_NAME"));
                            faultPacketData.setWorkTypeCode(faultPacketInfo.getString("S_PACKET_TYPE"));
                            faultPacketData.setWorkTaskCode(faultPacketInfo.getString("S_PACKET_CODE"));
                            faultPacketData.setWorkTaskName(faultPacketInfo.getString("S_PACKET_NAME"));
                            faultPacketData.setNewPacket("1");
                            faultPacketData.setAllotStateCode("0");
                            faultPacketData.setAllotStateName("未派工");

                            XzyMTaskallotpacket taskAllotPacket = new XzyMTaskallotpacket();
                            taskAllotPacket.setPacketName(faultPacketInfo.getString("S_PACKET_NAME"));
                            taskAllotPacket.setPacketType(faultPacketInfo.getString("S_PACKET_TYPE"));
                            taskAllotPacket.setPacketCode(faultPacketInfo.getString("S_PACKET_CODE"));
                            taskAllotPacket.setStaskId(UUID.randomUUID().toString());
                            taskAllotPacket.setMainCyc("-1");
                            taskAllotPacket.setTrainsetId(trainsetId);


                            faultPacketData.setPacket(taskAllotPacket);
                        } else {
                            logger.info("调用计划接口获取故障包信息为空");
                        }
                    }
                    packetDataArrayList.remove(faultPacketData);
                    //5.2如果故障任务包下有项目，则循环工长认领故障集合，判断工长认领故障在该任务下的项目集合中是否存在
                    //   如果不存在则将此工长认领故障添加进去，如果存在则不做任何操作
                    if (!ObjectUtils.isEmpty(faultPacketData.getPacket())) {//如果故障包的信息是空的，说明调用计划获取故障包信息有问题，则不往下进行
                        List<XzyMTaskallotItem> faultItemList = faultPacketData.getPacket().getTaskallotItemList();
                        if (CollectionUtils.isEmpty(faultItemList)) {
                            faultItemList = new ArrayList<>();
                        }
                        faultPacketData.getPacket().setTaskallotItemList(new ArrayList<>());
                        for (Object o : faultData) {
                            JSONObject fault = (JSONObject) o;
                            String faultId = fault.getString("faultId");
                            String faultTrainsetId = fault.getString("trainSetId");
                            if (trainsetId.equals(faultTrainsetId)) {//只匹配本车组下的故障
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
                                            faultPacketData.setAllotStateName("部分派工");
                                        }
                                    } else {
                                        xzyMTaskcarpartList.add(carPart);
                                        faultPacketData.setAllotStateCode("2");
                                        faultPacketData.setAllotStateName("部分派工");
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
                                    faultPacketData.setAllotStateName("部分派工");
                                } else {
                                    faultPacketData.setAllotStateCode("0");
                                    faultPacketData.setAllotStateName("未派工");
                                }
                            }
                            //设置显示内容
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

                //6.将需要工长认领的复核任务添加到该车的任务集合下边
                if (!CollectionUtils.isEmpty(entitySJOverRunRecordList)) {
                    PacketData overRunPacketData = packetDataArrayList.stream().filter(t -> !ObjectUtils.isEmpty(t.getPacket()) && "12".equals(t.getPacket().getPacketType())).findFirst().orElse(null);
                    if (ObjectUtils.isEmpty(overRunPacketData)) {//6.2 new一个复核的作业包并添加到该车的任务集合下
                        overRunPacketData = new PacketData();
                        RecheckPacket overRunPacketInfo = this.getOverRunPacketInfo();
                        if (!ObjectUtils.isEmpty(overRunPacketInfo)) {
                            overRunPacketData.setWorkTaskDisplayName(overRunPacketInfo.getPacketName());
                            overRunPacketData.setWorkTypeCode(overRunPacketInfo.getPacketType());
                            overRunPacketData.setWorkTaskCode(overRunPacketInfo.getPacketCode());
                            overRunPacketData.setWorkTaskName(overRunPacketInfo.getPacketName());
                            overRunPacketData.setNewPacket("1");
                            overRunPacketData.setAllotStateCode("0");
                            overRunPacketData.setAllotStateName("未派工");

                            XzyMTaskallotpacket taskAllotPacket = new XzyMTaskallotpacket();
                            taskAllotPacket.setPacketName(overRunPacketInfo.getPacketName());
                            taskAllotPacket.setPacketType(overRunPacketInfo.getPacketType());
                            taskAllotPacket.setPacketCode(overRunPacketInfo.getPacketCode());
                            taskAllotPacket.setStaskId(UUID.randomUUID().toString());
                            taskAllotPacket.setMainCyc("-1");
                            taskAllotPacket.setTrainsetId(trainsetId);

                            overRunPacketData.setPacket(taskAllotPacket);
                        } else {
                            logger.info("调用计划接口获取复核包信息为空");
                        }
                    }
                    packetDataArrayList.remove(overRunPacketData);
                    //6.3如果故障任务包下有项目，则循环工长认领故障集合，判断工长认领故障在该任务下的项目集合中是否存在
                    //   如果不存在则将此工长认领故障添加进去，如果存在则不做任何操作
                    if (!ObjectUtils.isEmpty(overRunPacketData.getPacket())) {//如果复核包的信息是空的，说明调用计划获取复核包信息有问题，则不往下进行
                        List<XzyMTaskallotItem> overRunItemList = overRunPacketData.getPacket().getTaskallotItemList();
                        if (CollectionUtils.isEmpty(overRunItemList)) {
                            overRunItemList = new ArrayList<>();
                        }
                        overRunPacketData.getPacket().setTaskallotItemList(new ArrayList<>());
                        for (EntitySJOverRunRecord overRunRecord : entitySJOverRunRecordList) {
                            String overRunId = overRunRecord.getId();
                            String overRunTrainsetId = overRunRecord.getTrainsetId();
                            if (trainsetId.equals(overRunTrainsetId)) {//只匹配本车组下的复核任务
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
                                            overRunPacketData.setAllotStateName("部分派工");
                                        }
                                    } else {
                                        xzyMTaskcarpartList.add(carPart);
                                        overRunPacketData.setAllotStateCode("2");
                                        overRunPacketData.setAllotStateName("部分派工");
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
                                    overRunPacketData.setAllotStateName("部分派工");
                                } else {
                                    overRunPacketData.setAllotStateCode("0");
                                    overRunPacketData.setAllotStateName("未派工");
                                }
                            }
                            //设置显示内容
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

                /** 获取工长认领的故障和复核任务旧代码 暂时注释掉
                 //项目集合
                 List<XzyMTaskallotItem> taskallotItems = new ArrayList<>();
                 //设置复核
                 List<XzyMTaskcarpart> xzyMTaskcarparts = new ArrayList<>();
                 if (!CollectionUtils.isEmpty(entitySJOverRunRecordList)) {
                 for (EntitySJOverRunRecord entitySJOverRunRecord : entitySJOverRunRecordList) {
                 if (trainsetId.equals(entitySJOverRunRecord.getTrainsetId())) {
                 //加入新项目
                 XzyMTaskcarpart xzyMTaskcarpart = getXzyMTaskallotItem(unitCode, dayPlanId, allotDataMap, trainsetId, entitySJOverRunRecord.getTrainsetName(), entitySJOverRunRecord.getCheckItemName(), entitySJOverRunRecord.getCheckItem(), entitySJOverRunRecord.getCarNo());
                 xzyMTaskcarparts.add(xzyMTaskcarpart);
                 }
                 }
                 }
                 //设置故障
                 if (!CollectionUtils.isEmpty(faultDataJSONArray)) {
                 for (Object o : faultDataJSONArray) {
                 JSONObject fault = (JSONObject) o;
                 if (trainsetId.equals(fault.getString("trainsetId"))) {
                 //加入新项目
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
                 logger.error("派工查询获取故障包信息异常", e);
                 }

                 JSONArray faultResult = faultPacket.getJSONArray("result");
                 for (Object o : faultResult) {
                 JSONObject json = (JSONObject) o;
                 //判断复核任务是否为空，为空则没有任务添加
                 PacketData packetData = new PacketData();
                 packetData.setWorkTaskDisplayName(json.getString("S_PACKET_NAME"));
                 packetData.setWorkTypeCode(json.getString("S_PACKET_TYPE"));
                 packetData.setNewPacket("1");
                 packetData.setAllotStateCode("1");
                 packetData.setAllotStateName("已派工");
                 //设置作业包
                 XzyMTaskallotpacket taskAllotPacket = new XzyMTaskallotpacket();
                 taskAllotPacket.setPacketName(json.getString("S_PACKET_NAME"));
                 taskAllotPacket.setPacketType(json.getString("S_PACKET_TYPE"));
                 taskAllotPacket.setPacketCode(json.getString("S_PACKET_CODE"));
                 taskAllotPacket.setStaskId(UUID.randomUUID().toString());
                 //设置派工项目
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

                //按照修程、作业包名称排序
                packetDataArrayList = Optional.ofNullable(packetDataArrayList).orElseGet(ArrayList::new).stream().sorted(Comparator.comparing((PacketData t) -> t.getWorkTypeCode()).thenComparing((PacketData v) -> v.getPacket().getPacketName())).collect(Collectors.toList());
                phoneAllotData.setPacketData(packetDataArrayList);
                phoneAllotDataArrayList.add(phoneAllotData);
            }

            //组织车组的派工状态
            Optional.ofNullable(phoneAllotDataArrayList).orElseGet(ArrayList::new).forEach(phoneAllotData -> {
                List<PacketData> packetDataList = phoneAllotData.getPacketData();
                if (!CollectionUtils.isEmpty(packetDataList)) {
                    //未派工的包数量
                    long wCount = packetDataList.stream().filter(t -> "0".equals(t.getAllotStateCode())).count();
                    //已派工的包数量
                    long yCount = packetDataList.stream().filter(t -> "1".equals(t.getAllotStateCode())).count();
                    //部分派工的包数量
                    long bCount = packetDataList.stream().filter(t -> "2".equals(t.getAllotStateCode())).count();
                    if (bCount > 0) {
                        phoneAllotData.setAllotStateCode("2");
                        phoneAllotData.setAllotStateName("部分派工");
                    } else if (wCount > 0) {
                        if (yCount > 0) {
                            phoneAllotData.setAllotStateCode("2");
                            phoneAllotData.setAllotStateName("部分派工");
                        } else {
                            phoneAllotData.setAllotStateCode("0");
                            phoneAllotData.setAllotStateName("未派工");
                        }
                    } else if (yCount > 0) {
                        phoneAllotData.setAllotStateCode("1");
                        phoneAllotData.setAllotStateName("已派工");
                    } else {
                        phoneAllotData.setAllotStateCode("0");
                        phoneAllotData.setAllotStateName("未派工");
                    }
                }
            });

            //获取最大编组数
            logger.info("车组的编组数集合:{}", iMarshalcounts);
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

            //组装作业包类型集合
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
     * @author:冯帅
     * @desc: 手持机获取车组派工状态
     * @date: 2021/9/3
     * @param: []
     * @return: com.alibaba.fastjson.JSONObject
     */
    @Override
    public List<TaskAllotPhoneTrainSetState> getPhoneTaskTrainSetState(String unitCode, String deptCode, String dayPlanId) {
        List<TaskAllotPhoneTrainSetState> result = new ArrayList<>();
        //1.调用PC接口获取派工相关数据
        JSONObject repairTask = xzyMTaskallotpacketService.getRepairTask(unitCode, dayPlanId, deptCode, "", "1");
        //2.计算派工状态
        JSONArray jsonArray = repairTask.getJSONArray("data");
        if (jsonArray != null && jsonArray.size() > 0) {
            List<AllotData> allotDataList = jsonArray.toJavaList(AllotData.class);
            if (allotDataList != null && allotDataList.size() > 0) {
                List<String> trainsetIdList = allotDataList.stream().map(t -> t.getTrainsetId()).distinct().collect(Collectors.toList());
                if (trainsetIdList != null && trainsetIdList.size() > 0) {
                    for (String trainsetId : trainsetIdList) {
                        //未派工的包数量
                        long wCount = allotDataList.stream().filter(t -> trainsetId.equals(t.getTrainsetId()) && "0".equals(t.getAllotStateCode())).count();
                        //已派工的包数量
                        long yCount = allotDataList.stream().filter(t -> trainsetId.equals(t.getTrainsetId()) && "1".equals(t.getAllotStateCode())).count();
                        //部分派工的包数量
                        long bCount = allotDataList.stream().filter(t -> trainsetId.equals(t.getTrainsetId()) && "2".equals(t.getAllotStateCode())).count();
                        TaskAllotPhoneTrainSetState taskAllotPhoneTrainSetState = new TaskAllotPhoneTrainSetState();
                        taskAllotPhoneTrainSetState.setTrainSetId(trainsetId);
                        if (bCount > 0) {
                            taskAllotPhoneTrainSetState.setState("部分派工");
                        } else if (wCount > 0) {
                            if (yCount > 0) {
                                taskAllotPhoneTrainSetState.setState("部分派工");
                            } else {
                                taskAllotPhoneTrainSetState.setState("未派工");
                            }
                        } else if (yCount > 0) {
                            taskAllotPhoneTrainSetState.setState("已派工");
                        } else {
                            taskAllotPhoneTrainSetState.setState("未知");
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
     * @author: 冯帅
     * @desc: 获取作业项目对象
     */
    private XzyMTaskallotItem getTaskAllotItem() {
        return null;
    }

    /***
     * @author: 冯帅
     * @desc: 获取作业辆序对象
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
            carPart.setDisplayCarPartName("[辆序：" + carPart.getCarNo() + "]" + jsonObject.getString("faultDescription"));
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
     * @author: 冯帅
     * @desc: 获取故障相关包信息
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
            logger.error("获取故障包信息失败getFaultPacketInfo", ex);
        }
        return res;
    }

    /***
     * @author: 冯帅
     * @desc: 获取复核相关包信息
     */
    private RecheckPacket getOverRunPacketInfo() {
        RecheckPacket res = null;
        try {
            res = CacheUtil.getDataUseThreadCache(
                    "remoteService.getRecheckPacket",
                    () -> remoteService.getRecheckPacket()
            );
        } catch (Exception ex) {
            logger.error("获取复核包信息失败getOverRunPacketInfo", ex);
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
            log.info("复核查询数据ly{}", lyJson);
            sjJson = HttpUtil.doPost(recheckTaskProperties.getSjGetOverRunRecordUrl(), req, recheckTaskProperties, jsonRootBean);
            log.info("复核查询数据sj{}", lyJson);
        } catch (Exception e) {
            log.error("获取复核任务接口异常", e);
        }
        List<EntitySJOverRunRecord> entitySJOverRunRecords = new ArrayList<>();
        log.info("复核查询数据{}", lyJson);
        //添加ly复核任务
        if (!CollectionUtils.isEmpty(lyJson)) {
            List<EntitySJOverRunRecord> data = JSON.parseArray(lyJson.getString("Data"), EntitySJOverRunRecord.class);
            if (!CollectionUtils.isEmpty(data)) {
                entitySJOverRunRecords.addAll(data);
            }
        } else if (!CollectionUtils.isEmpty(sjJson)) {
            //添加sj复核任务
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
        //保存派工数据
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

        //调用PC接口  调用计划服务，计划中项目状态回写+工长认领故障和复核任务回写计划
        if (phoneAllotDataList != null && phoneAllotDataList.size() > 0) {
            this.phoneSetTaskDataAndStatus(phoneAllotDataList);
        }
    }


    public boolean phoneSetTaskDataAndStatus(List<PhoneAllotData> phoneAllotDataList) {
        String unitCode = "";//运用所编码
        String deptCode = "";//班组编码
        String deptName = "";//班组名称
        String dayPlanId = "";//日计划id
        List<ZtTaskPacketEntity> ztTaskPacketEntityList = new ArrayList<>();
        List<RepairPacketStatu> repairPacketStatuList = new ArrayList<>();//更新计划中项目状态实体集合
        for (PhoneAllotData allotData : phoneAllotDataList) {
            List<PacketData> packetList = allotData.getPacketData();
            for (PacketData packetData : packetList) {
                XzyMTaskallotpacket packet = packetData.getPacket();
                List<XzyMTaskallotItem> taskallotItemList = new ArrayList<>();//作业包中的项目集合
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
                    ztTaskPacketEntity.setRemark("工长认领");
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
                                ztTaskPacketEntity.setDayplanId(xzyMTaskcarpart.getDayPlanID());//作业包信息实体日计划赋值
                                ztTaskItemEntity.setItemCode(xzyMTaskcarpart.getItemCode());
                                ztTaskItemEntity.setItemName(xzyMTaskcarpart.getItemName());
                                ztTaskItemEntity.setItemType(xzyMTaskcarpart.getRepairType());
                                ztTaskItemEntity.setArrangeType(xzyMTaskcarpart.getArrageType());
                                ztTaskItemEntity.setCarNo(xzyMTaskcarpart.getCarNo());
                                ztTaskItemEntity.setRemark("工长认领");
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
                            repairPacketStatu.setCStatue("4");//已派工状态
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
        //调用计划服务-批量添加故障复核任务接口
        try {
            //将没有新项目的实体过滤掉
            if (!CollectionUtils.isEmpty(ztTaskPacketEntityList)) {
                ztTaskPacketEntityList = ztTaskPacketEntityList.stream().filter(t -> !CollectionUtils.isEmpty(t.getLstTaskItemInfo())).collect(Collectors.toList());
                if (!CollectionUtils.isEmpty(ztTaskPacketEntityList)) {
                    JSONObject addRes = remoteService.addFaultReCheckPacketList(ztTaskPacketEntityList);
                }
            }
        } catch (Exception ex) {
            logger.error("手持机调用计划更新任务状态接口失败:" + ex.getMessage());
        }
        //调用计划服务-更新任务状态接口
        try {
            if (!CollectionUtils.isEmpty(repairPacketStatuList)) {
                JSONObject updateRes = remoteService.updateRepairPacketStatus(repairPacketStatuList);
            }
        } catch (Exception ex) {
            logger.error("手持机调用计划批量添加故障复核任务接口失败:" + ex.getMessage());
        }
        return true;
    }

    /**
     * 手持机获取计划车组信息
     *
     * @param
     * @return
     */
    @Override
    public List<PlanTrainset> getPlanTrainset(String dayPlanId, String unitCode, String deptCode) {
        List<String> deptCodeList = Arrays.asList(deptCode);
        Date beginTime = new Date();
        //1.获取计划全部数据
        List<ZtTaskPacketEntity> taskPacketEntityList = remoteService.getPacketTaskByCondition(dayPlanId, null, null, deptCode, unitCode);
        Date time1 = new Date();
        logger.info("getPlanTrainset调用计划接口获取计划属性消耗时间为：" + (time1.getTime() - beginTime.getTime()));
        //2.获取中台派工数据
        List<TaskAllotPacketEntity> getTaskAllotData = midGroundService.getTaskAllotDataToItem(unitCode, dayPlanId, null, null, deptCodeList, null);
        Date time2 = new Date();
        logger.info("getPlanTrainset调用中台派工接口获取派工数据消耗时间为：" + (time2.getTime() - time1.getTime()));
        //3.组织返回数据
        List<PlanTrainset> planTrainsetList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(taskPacketEntityList)) {
            //3.1把计划全部数据根据车组id分组
            Map<String, List<ZtTaskPacketEntity>> ztTaskPacketEntityMap = taskPacketEntityList.stream().collect(Collectors.groupingBy(ZtTaskPacketEntity::getTrainsetId));
            ztTaskPacketEntityMap.forEach((currentTrainsetId,ztTaskPacketEntityList)->{
                PlanTrainset planTrainset = new PlanTrainset();
                planTrainset.setTrainsetId(currentTrainsetId);
                planTrainset.setTrainsetName(ztTaskPacketEntityList.get(0).getTrainsetName());
                //3.2
                for (ZtTaskPacketEntity ztTaskPacketEntity : ztTaskPacketEntityList) {
                    String currentPacketCode = ztTaskPacketEntity.getPacketCode();//当前包编码
                }
                planTrainsetList.add(planTrainset);
            });
        }
        Date time3 = new Date();
        logger.info("getPlanTrainset组织派工状态消耗时间为：" + (time3.getTime() - time2.getTime()));
        Date endTime = new Date();
        logger.info("getPlanTrainset总消耗时间为：" + (endTime.getTime() - beginTime.getTime()));
        return planTrainsetList;
    }

    @Override
    public List<PlanPacket> getPlanPacket(String dayPlanId, String unitCode, String deptCode, String trainsetId) {
        List<String> trainsetIdList = Arrays.asList(trainsetId);
        List<String> deptCodeList = Arrays.asList(deptCode);
        //获取计划全部数据
        List<ZtTaskPacketEntity> taskPacketEntityList = remoteService.getPacketTaskByCondition(dayPlanId, trainsetId, null, deptCode, unitCode);
        //获取中台派工数据
        List<TaskAllotPacketEntity> getTaskAllotData = midGroundService.getTaskAllotData(unitCode, dayPlanId, null, trainsetIdList, deptCodeList, null, null);
        List<PlanPacketWorkType> planPacketWorkTypeList = new ArrayList<>();
        //先处理非故障认领与故障复核数据
        List<ZtTaskPacketEntity> taskPacketEntityList1 = taskPacketEntityList.stream().filter(t -> !t.getPacketTypeCode().equals("5") && !t.getPacketTypeCode().equals("12")).collect(Collectors.toList());
        for (ZtTaskPacketEntity ztTaskPacketEntity : taskPacketEntityList1) {
            String packetCode = ztTaskPacketEntity.getPacketCode();
            PlanPacketWorkType planPacketWorkType = new PlanPacketWorkType();
            planPacketWorkType.setPacketCode(packetCode);
            planPacketWorkType.setPacketName(ztTaskPacketEntity.getPacketName());
            //判断是否无修程 如果无修程workModeTypeCode为0，展示到包，1展示到项目
            String packetTypeCode = ztTaskPacketEntity.getPacketTypeCode();
            planPacketWorkType.setWorkTypeCode(packetTypeCode);
            if ("6".equals(packetTypeCode)) {
                //无修程，展示到包
                planPacketWorkType.setWorkModeTypeCode("0");
                //继续判断包下是否有人
                List<TaskAllotPacketEntity> taskAllotPacketEntityList = getTaskAllotData.stream().filter(t -> t.getPacketCode().equals(packetCode)).collect(Collectors.toList());
                if (taskAllotPacketEntityList.size() > 0) {
                    //包下有人，已经派工
                    planPacketWorkType.setIsExistPerson("1");
                } else {
                    //包下无人，未派工
                    planPacketWorkType.setIsExistPerson("0");
                }
            } else {
                //有修程，展示到项目
                planPacketWorkType.setWorkModeTypeCode("1");
            }
            planPacketWorkTypeList.add(planPacketWorkType);

        }
        //计划数据分别与故障认领与故障复核数据对比，如果计划下有不是新包，如果计划下没有是新包。
        //故障包与复核包是两个包。
        //需要故障认领数据
        //获取需要工长认领的故障信息,如果没有数据后台会报错
        JSONArray faultData = new JSONArray();
        try {
            JSONObject faultParams = new JSONObject();
            faultParams.put("findFaultTime", dayPlanId.substring(0, dayPlanId.length() - 3));
            faultParams.put("trainsetIdList", trainsetId);
            faultData = remoteService.getFaultData(faultParams);
        } catch (Exception e) {
            logger.error("手持机获取工长认领故障异常", e);
        }
        //List<Map<String, Object>> mapList = (List) faultDataJSONArray;
        //mapList = mapList.stream().filter(t -> t.get("trainSetId").equals(trainsetId)).collect(Collectors.toList());
        TrainsetBaseInfo trainsetBaseInfo = remoteService.getTrainsetBaseinfoByID(trainsetId);
        String trainsetName = trainsetBaseInfo.getTrainsetname();
        //需要故障复核数据
//        List<String> trainsetNames = Arrays.asList(trainsetName);
//        Map<String, Object> overRunParams = new HashMap<>();
//        overRunParams.put("TrainsetNameList", trainsetNames);
//        List<EntitySJOverRunRecord> entitySJOverRunRecordList = reCheckTaskService.getReCheckTaskList(overRunParams);
//        entitySJOverRunRecordList = entitySJOverRunRecordList.stream().filter(t -> t.getTrainsetId().equals(trainsetId)).collect(Collectors.toList());
        List<ZtTaskPacketEntity> ztTaskPacketEntities = taskPacketEntityList.stream().filter(t -> t.getPacketTypeCode().equals("5")).collect(Collectors.toList());
        if (ztTaskPacketEntities.size() > 0) {
            ZtTaskPacketEntity ztTaskPacketEntity = ztTaskPacketEntities.get(0);
            //计划下有故障包，说明是旧包
            PlanPacketWorkType planPacketWorkType = new PlanPacketWorkType();
            planPacketWorkType.setPacketCode(ztTaskPacketEntity.getPacketCode());
            planPacketWorkType.setPacketName(ztTaskPacketEntity.getPacketName());
            planPacketWorkType.setNewPacket("0");
            planPacketWorkType.setWorkModeTypeCode("1");
            planPacketWorkType.setWorkTypeCode(ztTaskPacketEntity.getPacketTypeCode());
            planPacketWorkTypeList.add(planPacketWorkType);
        } else {
            //计划下无故障包
            if (faultData.size() > 0) {
                //需要故障认领中有数据,说明是新包
                //new一个新故障的作业包
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
//            //计划下有复核包，说明是旧包。
//            PlanPacket planPacket = new PlanPacket();
//            planPacket.setPacketCode(ztTaskPacketEntity.getPacketCode());
//            planPacket.setPacketName(ztTaskPacketEntity.getPacketName());
//            planPacket.setNewPacket("0");
//            planPacket.setWorkModeTypeCode("1");
//            planPacketList.add(planPacket);
//        } else {
//            //计划下无复核包
//            if (entitySJOverRunRecordList.size() > 0) {
//                //需要故障复核中有数据,说明是新包
//                //new一个新复核的作业包
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
        //按照修程，作业包名称排序
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
        //获取计划全部数据
        List<ZtTaskPacketEntity> taskPacketEntityList = remoteService.getPacketTaskByCondition(dayPlanId, trainsetId, null, deptCode, unitCode);
        //获取中台派工数据
        List<TaskAllotPacketEntity> getTaskAllotData = midGroundService.getTaskAllotData(unitCode, dayPlanId, null, trainsetIdList, deptCodeList, null, null);
        //是故障包或者复核包，并且计划下没有这个包，是新包，需要获取待工长认领或复核数据
        //首先，判断当前包是故障包或者是复核包
        JSONObject faultPacketInfo = this.getFaultPacketInfo();
        String faultPacketCode = "";
        List<PlanItem> planItemList = new ArrayList<>();
        //计划下发的故障包
        List<ZtTaskPacketEntity> taskPacketEntityList1 = taskPacketEntityList.stream().filter(t -> t.getPacketTypeCode().equals("5")).collect(Collectors.toList());
        if (!ObjectUtils.isEmpty(faultPacketInfo)) {
            faultPacketCode = faultPacketInfo.getString("S_PACKET_CODE");
            if (faultPacketCode.equals(packetCode)) {
                //需要故障认领数据
                JSONObject faultParams = new JSONObject();
                faultParams.put("findFaultTime", dayPlanId.substring(0, dayPlanId.length() - 3));
                faultParams.put("trainsetIdList", trainsetIdList);
                JSONArray faultData = remoteService.getFaultData(faultParams);
                if (faultData.size() > 0) {
                    List<TaskAllotPacketEntity> taskAllotPacketEntityList = getTaskAllotData.stream().filter(t -> t.getPacketCode().equals(packetCode)).collect(Collectors.toList());
                    List<String> itemCodeList = new ArrayList<>();
                    if (taskAllotPacketEntityList.size() > 0) {
                        //故障包下有项目已经派工
                        TaskAllotPacketEntity taskAllotPacketEntity = taskAllotPacketEntityList.get(0);
                        List<TaskAllotItemEntity> taskAllotItemEntities = taskAllotPacketEntity.getTaskAllotItemEntityList();
                        //取出已经派工的itemCode
                        itemCodeList = taskAllotItemEntities.stream().map(TaskAllotItemEntity::getItemCode).collect(Collectors.toList());
                    }
                    for (int i = 0; i < faultData.size(); i++) {
                        JSONObject jsonObject = faultData.getJSONObject(i);
                        PlanItem planItem = new PlanItem();
                        String faultId = jsonObject.getString("faultId");
                        planItem.setItemCode(faultId);
                        //项目名称需要组装
                        String carNo = jsonObject.getString("carNo");
                        String faultDescription = jsonObject.getString("faultDescription");
                        String itemName = "[辆序：" + carNo + "]" + faultDescription;
                        planItem.setItemName(itemName);
                        if (itemCodeList.size() > 0) {
                            if (itemCodeList.contains(faultId)) {
                                //当前项目已派工
                                planItem.setIsExistPerson("1");
                                //旧项目
                                planItem.setNewItem("0");
                            } else {
                                //当前项目未派工
                                planItem.setIsExistPerson("0");
                                planItem.setNewItem("1");
                            }
                        } else {
                            //故障包下所有项目未派工
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
        //这里需要把计划下发的故障与故障认领数据去重
        if (!ObjectUtils.isEmpty(faultPacketInfo)) {
            faultPacketCode = faultPacketInfo.getString("S_PACKET_CODE");
            if (faultPacketCode.equals(packetCode)) {
                if (taskPacketEntityList1.size() > 0) {
                    ZtTaskPacketEntity ztTaskPacketEntity = taskPacketEntityList1.get(0);
                    List<ZtTaskItemEntity> ztTaskItemEntityList = ztTaskPacketEntity.getLstTaskItemInfo();
                    //取出其中的itemCode
                    List<String> itemCodeList = ztTaskItemEntityList.stream().map(m -> m.getItemCode()).distinct().collect(Collectors.toList());
                    List<String> itemCodeList1 = planItemList.stream().map(m -> m.getItemCode()).distinct().collect(Collectors.toList());
                    for (String itemCode : itemCodeList) {
                        if (!itemCodeList1.contains(itemCode)) {
                            //计划中存在 不在需要故障认领中的故障包
                            List<ZtTaskItemEntity> ztTaskItemEntity = ztTaskItemEntityList.stream().filter(t -> t.getItemCode().equals(itemCode)).collect(Collectors.toList());
                            ZtTaskItemEntity ztTaskItemEntity1 = ztTaskItemEntity.get(0);
                            List<TaskAllotPacketEntity> taskAllotPacketEntityList = getTaskAllotData.stream().filter(t -> t.getPacketCode().equals(packetCode)).collect(Collectors.toList());
                            List<String> itemCodeList2 = new ArrayList<>();
                            if (taskAllotPacketEntityList.size() > 0) {
                                //故障包下有项目已经派工
                                TaskAllotPacketEntity taskAllotPacketEntity = taskAllotPacketEntityList.get(0);
                                List<TaskAllotItemEntity> taskAllotItemEntities = taskAllotPacketEntity.getTaskAllotItemEntityList();
                                //取出已经派工的itemCode
                                itemCodeList2 = taskAllotItemEntities.stream().map(TaskAllotItemEntity::getItemCode).collect(Collectors.toList());
                                PlanItem planItem = new PlanItem();
                                planItem.setItemCode(itemCode);
                                planItem.setItemName(ztTaskItemEntity1.getItemName());
                                if (itemCodeList2.size() > 0) {
                                    if (itemCodeList2.contains(itemCode)) {
                                        //当前项目已派工
                                        planItem.setIsExistPerson("1");
                                        //旧项目
                                        planItem.setNewItem("0");
                                    } else {
                                        //当前项目未派工
                                        planItem.setIsExistPerson("0");
                                        planItem.setNewItem("1");
                                    }
                                } else {
                                    //故障包下所有项目未派工
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
        //计划下发的复核包
        List<ZtTaskPacketEntity> taskPacketEntityList2 = taskPacketEntityList.stream().filter(t -> t.getPacketTypeCode().equals("12")).collect(Collectors.toList());
//        if (!ObjectUtils.isEmpty(overRunPacketInfo)) {
//            faultPacketCode1 = overRunPacketInfo.getPacketCode();
//            if (faultPacketCode1.equals(packetCode)) {
//                TrainsetBaseInfo trainsetBaseInfo = remoteService.getTrainsetBaseinfoByID(trainsetId);
//                String trainsetName = trainsetBaseInfo.getTrainsetname();
        //需要故障复核数据
//                List<String> trainsetNames = Arrays.asList(trainsetName);
//                Map<String, Object> overRunParams = new HashMap<>();
//                overRunParams.put("TrainsetNameList", trainsetNames);
//                List<EntitySJOverRunRecord> entitySJOverRunRecordList = reCheckTaskService.getReCheckTaskList(overRunParams);
//                if (entitySJOverRunRecordList.size() > 0) {
//                    List<TaskAllotPacketEntity> taskAllotPacketEntityList = getTaskAllotData.stream().filter(t -> t.getPacketCode().equals(packetCode)).collect(Collectors.toList());
//                    List<String> itemCodeList = new ArrayList<>();
//                    if (taskAllotPacketEntityList.size() > 0) {
//                        //复核包下有项目已经派工
//                        TaskAllotPacketEntity taskAllotPacketEntity = taskAllotPacketEntityList.get(0);
//                        List<TaskAllotItemEntity> taskAllotItemEntities = taskAllotPacketEntity.getTaskAllotItemEntityList();
//                        //取出已经派工的itemCode
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
//                                //当前项目已派工
//                                planItem.setIsExistPerson("1");
//                                //旧项目
//                                planItem.setNewItem("0");
//                            } else {
//                                //当前项目未派工
//                                planItem.setIsExistPerson("0");
//                                planItem.setNewItem("1");
//                            }
//                        } else {
//                            //故障包下所有项目未派工
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
        //这里需要把计划下发的复核与复核认领数据去重
//        if (taskPacketEntityList2.size() > 0) {
//            ZtTaskPacketEntity ztTaskPacketEntity = taskPacketEntityList2.get(0);
//            List<ZtTaskItemEntity> ztTaskItemEntityList = ztTaskPacketEntity.getLstTaskItemInfo();
//            //取出其中的itemCode
//            List<String> itemCodeList = ztTaskItemEntityList.stream().map(m -> m.getItemCode()).distinct().collect(Collectors.toList());
//            List<String> itemCodeList1 = planItemList.stream().map(m -> m.getItemCode()).distinct().collect(Collectors.toList());
//            for (String itemCode : itemCodeList) {
//                if (!itemCodeList1.contains(itemCode)) {
//                    //计划中存在 不在需要故障认领中的故障包
//                    List<ZtTaskItemEntity> ztTaskItemEntity = ztTaskItemEntityList.stream().filter(t -> t.getItemCode().equals(itemCode)).collect(Collectors.toList());
//                    ZtTaskItemEntity ztTaskItemEntity1 = ztTaskItemEntity.get(0);
//                    List<TaskAllotPacketEntity> taskAllotPacketEntityList = getTaskAllotData.stream().filter(t -> t.getPacketCode().equals(packetCode)).collect(Collectors.toList());
//                    List<String> itemCodeList2 = new ArrayList<>();
//                    if (taskAllotPacketEntityList.size() > 0) {
//                        //故障包下有项目已经派工
//                        TaskAllotPacketEntity taskAllotPacketEntity = taskAllotPacketEntityList.get(0);
//                        List<TaskAllotItemEntity> taskAllotItemEntities = taskAllotPacketEntity.getTaskAllotItemEntityList();
//                        //取出已经派工的itemCode
//                        itemCodeList2 = taskAllotItemEntities.stream().map(TaskAllotItemEntity::getItemCode).collect(Collectors.toList());
//                        PlanItem planItem = new PlanItem();
//                        planItem.setItemCode(itemCode);
//                        planItem.setItemName(ztTaskItemEntity1.getItemName());
//                        if (itemCodeList2.size() > 0) {
//                            if (itemCodeList2.contains(itemCode)) {
//                                //当前项目已派工
//                                planItem.setIsExistPerson("1");
//                                //旧项目
//                                planItem.setNewItem("0");
//                            } else {
//                                //当前项目未派工
//                                planItem.setIsExistPerson("0");
//                                planItem.setNewItem("1");
//                            }
//                        } else {
//                            //故障包下所有项目未派工
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
            //如果既不是故障包也不是复核包
            //根据包code筛选计划数据
            List<ZtTaskPacketEntity> ztTaskPacketEntityList = taskPacketEntityList.stream().filter(t -> t.getPacketCode().equals(packetCode)).collect(Collectors.toList());
            String isExistPerson = "";
            List<TaskAllotPacketEntity> taskAllotPacketEntityList = new ArrayList<>();
            if (getTaskAllotData.size() == 0) {
                //当前车组未派工,即车组下无人
                isExistPerson = "0";
            } else {
                //根据包code筛选中台派工数据
                taskAllotPacketEntityList = getTaskAllotData.stream().filter(t -> t.getPacketCode().equals(packetCode)).collect(Collectors.toList());
                if (taskAllotPacketEntityList.size() == 0) {
                    //当前包未派工，即包下无人
                    isExistPerson = "0";
                }
            }

            if (ztTaskPacketEntityList.size() > 0) {
                ZtTaskPacketEntity ztTaskPacketEntity = ztTaskPacketEntityList.get(0);
                List<ZtTaskItemEntity> ztTaskItemEntityList = ztTaskPacketEntity.getLstTaskItemInfo();
                String repairCode = ztTaskPacketEntity.getTaskRepairCode();
                if ("1".equals(repairCode)) {
                    String arrangeType = ztTaskItemEntityList.get(0).getArrangeType();
                    //一级修，查询配置表拼接数据
                    //首先查询当前车组编组数
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
                    //查询fillType是否可以修改字段
                    //暂时这字段写为可以修改，暂时没写逻辑
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

                    //查询辆序或部件字段
                    List<String> carNoList = Arrays.asList(carNos.split(","));
                    if (carNoList.size() > 0) {
                        for (String carNo : carNoList) {
                            PlanItem planItem = new PlanItem();
                            String itemName = ztTaskPacketEntity.getPacketName();
                            planItem.setItemCode(packetCode);
                            planItem.setItemName(itemName);
                            planItem.setArrangeType(arrangeType);
                            //把carNo还原数据，例如01-04还原为01，02，03，04
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
                            //判断项目下是否有人
                            if (ToolUtil.isEmpty(taskAllotPacketEntityList)) {
                                //整个包下无人
                                planItem.setIsExistPerson(isExistPerson);
                            } else {
                                //包下有人
                                //判断carnumber辆序是否已经派工
                                List<TaskAllotItemEntity> taskAllotItemEntityList = taskAllotPacketEntityList.get(0).getTaskAllotItemEntityList();
                                List<String> carNosList = taskAllotItemEntityList.stream().map(m -> m.getCarNo()).distinct().collect(Collectors.toList());
                                if (Collections.disjoint(carNosList, carNumbers)) {
                                    //未派工，包下无人
                                    planItem.setIsExistPerson("0");
                                } else {
                                    //已派工，包下有人
                                    planItem.setIsExistPerson("1");
                                }
                            }
                            planItemList.add(planItem);
                        }
                    }
                } else {
                    //不是一级修
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
                                //根据项目code筛选中台派工数据
                                List<TaskAllotItemEntity> itemEntityList = taskAllotItemEntityList.stream().filter(t -> t.getItemCode().equals(itemCode)).collect(Collectors.toList());
                                boolean hasWorkerList = itemEntityList.stream().anyMatch(t -> t.getTaskAllotPersonEntityList() != null && t.getTaskAllotPersonEntityList().size() > 0);
                                if (hasWorkerList) {
                                    //项目下有人
                                    planItem.setIsExistPerson("1");
                                } else {
                                    //项目下无人
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
        //获取计划全部数据
        List<ZtTaskPacketEntity> taskPacketEntityList = remoteService.getPacketTaskByCondition(dayPlanId, trainsetId, null, deptCode, unitCode);
        //根据packetCode筛选数据
        List<ZtTaskPacketEntity> ztTaskPacketEntityList = taskPacketEntityList.stream().filter(t -> t.getPacketCode().equals(packetCode)).collect(Collectors.toList());
        //获取中台派工数据
        List<String> deptCodeList = Arrays.asList(deptCode);
        List<String> trainsetIdList = Arrays.asList(trainsetId);
        List<TaskAllotPacketEntity> getTaskAllotData = midGroundService.getTaskAllotData(unitCode, dayPlanId, null, trainsetIdList, deptCodeList, null, null);
        List<String> carNoOrPartFinish = new ArrayList<>();
        if (getTaskAllotData.size() > 0) {
            //根据packetCode筛选数据
            List<TaskAllotPacketEntity> taskAllotPacketEntityList = getTaskAllotData.stream().filter(t -> t.getPacketCode().equals(packetCode)).collect(Collectors.toList());
            if (taskAllotPacketEntityList.size() > 0) {
                List<TaskAllotItemEntity> taskAllotItemEntityList = taskAllotPacketEntityList.get(0).getTaskAllotItemEntityList();
                //根据itemCode筛选数据
                List<TaskAllotItemEntity> taskAllotItemEntities = taskAllotItemEntityList.stream().filter(t -> t.getItemCode().equals(itemCode)).collect(Collectors.toList());
                if (taskAllotItemEntities.size() > 0) {
                    //判断已经派工中当前项目下是辆序还是部件，并找出已经派工的辆序或部件的id
                    String arrageType = taskAllotItemEntities.get(0).getArrageType();
                    if ("0".equals(arrageType)) {
                        //辆序
                        carNoOrPartFinish = taskAllotItemEntities.stream().map(m -> m.getCarNo()).distinct().collect(Collectors.toList());
                    } else {
                        //部件
                        carNoOrPartFinish = taskAllotItemEntities.stream().map(m -> m.getPartPosition()).distinct().collect(Collectors.toList());
                    }
                }

            }
        }
        List<PlanCarNoOrPart> planCarNoOrPartList = new ArrayList<>();
        if (ToolUtil.isNotEmpty(displayItemName)) {
            //说明是一级修
            //把displayItemName还原数据，例如人检一级修（01-04）还原为01，02，03，04
            int index1 = displayItemName.indexOf('(');
            int index2 = displayItemName.indexOf(')');
            String carNo = displayItemName.substring(index1 + 1, index2);
            List<String> carNoSplit = Arrays.asList(carNo.split("-"));
            //当前车组编组数
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
            //说明不是一级修
            //找到项目下辆序以及哪个辆序被派工
            //首先判断这个包是不是故障包或者复核包
            JSONObject faultPacketInfo = this.getFaultPacketInfo();
            JSONArray jsonArrayCarNo = new JSONArray();
            if (!ObjectUtils.isEmpty(faultPacketInfo)) {
                String faultPacketCode = faultPacketInfo.getString("S_PACKET_CODE");
                if (faultPacketCode.equals(packetCode)) {
                    //说明是故障包
                    //需要故障认领数据
                    JSONObject faultParams = new JSONObject();
                    faultParams.put("findFaultTime", dayPlanId.substring(0, dayPlanId.length() - 3));
                    faultParams.put("trainsetIdList", trainsetIdList);
                    JSONArray faultData = remoteService.getFaultData(faultParams);
                    faultData = faultData.stream().filter(t -> ((JSONObject) t).get("faultId").equals(itemCode)).collect(Collectors.toCollection(JSONArray::new));
                    if (faultData.size() > 0) {
                        //取出其中carNo
                        jsonArrayCarNo = faultData.stream().map(t -> ((JSONObject) t).get("carNo")).distinct().collect(Collectors.toCollection(JSONArray::new));
                        for (Object carNo : jsonArrayCarNo) {
                            PlanCarNoOrPart planCarNoOrPart = new PlanCarNoOrPart();
                            planCarNoOrPart.setCarNoOrPartName(carNo.toString());
                            boolean hasCarNo = carNoOrPartFinish.stream().anyMatch(t -> t.equals(carNo));
                            if (hasCarNo) {
                                //辆序已派工
                                planCarNoOrPart.setIsChoseState("1");
                            } else {
                                //辆序未派工
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
                //首先判断故障包，不能重复添加辆序
//                if(jsonArrayCarNo.)
                //判断项目下是辆序还是部件
                String arrangeType = ztTaskItemEntities.get(0).getArrangeType();
                if ("0".equals(arrangeType)) {
                    //辆序
                    List<String> carNoList = ztTaskItemEntities.stream().map(m -> m.getCarNo()).distinct().collect(Collectors.toList());
                    //如果有00辆序，把00辆序放末尾
                    if (carNoList.contains("00")) {
                        carNoList.remove("00");
                        carNoList.add("00");
                    }
                    //判断辆序是否选中
                    for (String carNo : carNoList) {
                        PlanCarNoOrPart planCarNoOrPart = new PlanCarNoOrPart();
                        planCarNoOrPart.setCarNoOrPartName(carNo);
                        boolean hasCarNo = carNoOrPartFinish.stream().anyMatch(t -> t.equals(carNo));
                        if (hasCarNo) {
                            //辆序已派工
                            planCarNoOrPart.setIsChoseState("1");
                        } else {
                            //辆序未派工
                            planCarNoOrPart.setIsChoseState("0");
                        }
                        planCarNoOrPartList.add(planCarNoOrPart);
                    }
                } else {
                    //部件
                    for (ZtTaskItemEntity ztTaskItemEntity : ztTaskItemEntities) {
                        PlanCarNoOrPart planCarNoOrPart = new PlanCarNoOrPart();
                        String positionCode = ztTaskItemEntity.getPositionCode();
                        planCarNoOrPart.setCarNoOrPartName(ztTaskItemEntity.getPositionName());
                        boolean hasCarPart = carNoOrPartFinish.stream().anyMatch(t -> t.equals(positionCode));
                        if (hasCarPart) {
                            //部件已派工
                            planCarNoOrPart.setIsChoseState("1");
                        } else {
                            //部件未派工
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
        //获取中台派工数据
        List<String> deptCodeList = Arrays.asList(deptCode);
        List<String> trainsetIdList = Arrays.asList(trainsetId);
        List<TaskAllotPacketEntity> getTaskAllotData = midGroundService.getTaskAllotData(unitCode, dayPlanId, null, trainsetIdList, deptCodeList, null, null);
        List<TaskAllotItemEntity> taskAllotItemEntities = new ArrayList<>();
        List<TaskAllotPacketEntity> taskAllotPacketEntityList = new ArrayList<>();
        if (getTaskAllotData.size() > 0) {
            //根据packetCode筛选数据
            taskAllotPacketEntityList = getTaskAllotData.stream().filter(t -> t.getPacketCode().equals(packetCode)).collect(Collectors.toList());
            if (taskAllotPacketEntityList.size() > 0) {
                List<TaskAllotItemEntity> taskAllotItemEntityList = taskAllotPacketEntityList.get(0).getTaskAllotItemEntityList();
                //根据itemCode筛选数据
                taskAllotItemEntities = taskAllotItemEntityList.stream().filter(t -> t.getItemCode().equals(itemCode)).collect(Collectors.toList());
            }
        }
        List<PlanAllotInfo> planAllotInfoList = new ArrayList<>();
//        List<TaskAllotPacketEntity> finalTaskAllotPacketEntityList = taskAllotPacketEntityList;
//        TaskAllotPacketEntity taskAllotPacketEntity = finalTaskAllotPacketEntityList.get(0);
        if (ToolUtil.isNotEmpty(displayItemName)) {
            //一级修
            //把displayItemName还原数据，例如人检一级修（01-04）还原为01，02，03，04
            int index1 = displayItemName.indexOf('(');
            int index2 = displayItemName.indexOf(')');
            String carNo = displayItemName.substring(index1 + 1, index2);
            List<String> carNoSplit = Arrays.asList(carNo.split("-"));
            //当前车组编组数
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
                //从已派工中寻找对应辆序的数据
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
                    //从已派工中寻找对应辆序的数据
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
            //不是一级修
            if (taskAllotItemEntities.size() > 0) {
                planAllotInfoList = taskAllotItemEntities.stream().map(entity -> {
                    PlanAllotInfo planAllotInfo = new PlanAllotInfo();
                    //区分部件或者辆序
                    String arrageType = entity.getArrageType();
                    //判断是故障包或者复核包或不是
                    JSONObject faultPacketInfo = this.getFaultPacketInfo();
                    String faultPacketCode = faultPacketInfo.getString("S_PACKET_CODE");
                    RecheckPacket overRunPacketInfo = this.getOverRunPacketInfo();
//                    String faultPacketCode1 = overRunPacketInfo.getPacketCode();
                    if (faultPacketCode.equals(packetCode)) {
                        //故障包
                        String carNo = entity.getCarNo();
                        String itemName = entity.getItemName();
                        String carNoOrPartName = "[辆序：" + carNo + "]" + itemName;
                        planAllotInfo.setCarNoOrPartName(carNoOrPartName);
                    } else {
                        if ("0".equals(arrageType)) {
                            //辆序
                            planAllotInfo.setCarNoOrPartName(entity.getCarNo());
                        } else {
                            //部件
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
        //获取计划全部数据
        List<ZtTaskPacketEntity> taskPacketEntityList = remoteService.getPacketTaskByCondition(dayPlanId, null, null, deptCode, unitCode);
        List<String> deptCodeList = Arrays.asList(deptCode);
        List<String> trainsetIdList = Arrays.asList("CR400AF-2124");
        List<TaskAllotPacketEntity> getTaskAllotData = midGroundService.getTaskAllotData(unitCode, dayPlanId, null, trainsetIdList, deptCodeList, null, null);
        //组装人员
        List<TaskAllotPersonEntity> taskAllotPersonEntities = new ArrayList<>();
        for (PlanWorker planWorker : planWorkerList) {
            TaskAllotPersonEntity taskAllotPersonEntity = new TaskAllotPersonEntity();
            taskAllotPersonEntity.setSId("");
            taskAllotPersonEntity.setWorkerId(planWorker.getWorkerId());
            //分组编码，必传
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
            //根据车组id查询车型
            TrainsetBaseInfo getTrainsetBaseinfoByID = remoteService.getTrainsetBaseinfoByID(trainsetId);
            String trainsetType = getTrainsetBaseinfoByID.getTraintype();
            List<AllotPacket> allotPacketList = allotTrainset.getAllotPacketList();
            List<ZtTaskPacketEntity> ztTaskPacketEntityList = taskPacketEntityList.stream().filter(t -> t.getTrainsetId().equals(trainsetId)).collect(Collectors.toList());
            if (allotPacketList.size() == 0) {
                //有车无包,循环计划下所有包
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
                    //查询修程
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
                    //项目集合
                    List<TaskAllotItemEntity> taskAllotItemEntities = new ArrayList<>();
                    if ("6".equals(packetTypeCode)) {
                        //无修程，包下无项目，自己组装包下项目
                        TaskAllotItemEntity taskAllotItemEntity = new TaskAllotItemEntity();
                        taskAllotItemEntity.setTaskItemId(taskId);
                        taskAllotItemEntity.setRepairMode("0");
                        taskAllotItemEntity.setCarNo("全列");
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
                        //循环计划下所有项目
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
                //有车有包，循环入参所有包
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
                    //查询修程
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
                    //项目集合
                    List<TaskAllotItemEntity> taskAllotItemEntities = new ArrayList<>();
                    List<ZtTaskItemEntity> ztTaskItemEntityList = ztTaskPacketEntity.getLstTaskItemInfo();
                    if (allotItemList.size() == 0) {
                        //有包无项目，循环计划所有项目
                        if ("6".equals(packetTypeCode)) {
                            //无修程，包下无项目，自己组装包下项目
                            TaskAllotItemEntity taskAllotItemEntity = new TaskAllotItemEntity();
                            taskAllotItemEntity.setTaskItemId(taskId);
                            taskAllotItemEntity.setRepairMode("0");
                            taskAllotItemEntity.setCarNo("全列");
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
                            //取出计划下发项目中当前包当前项目所有集合
                            //取出部件数据
                            List<ZtTaskItemEntity> ztTaskItemEntityList2 = ztTaskItemEntityList1.stream().filter(t -> t.getArrangeType().equals("1")).collect(Collectors.toList());
                            if (ztTaskItemEntityList2.size() > 0) {
                                //部件
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
                                //辆序
                                for (AllotCarOrPart allotCarOrPart : allotCarOrPartList) {
                                    TaskAllotItemEntity taskAllotItemEntity = new TaskAllotItemEntity();
                                    String carNoOrPartName = allotCarOrPart.getCarNoOrPartName();
                                    List<ZtTaskItemEntity> ztTaskItemEntityList3;
                                    if ("1".equals(taskRepairCode)) {
                                        //一级修
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
