package com.ydzbinfo.emis.trainRepair.mobile.taskpandect.service.impl;

import com.ydzbinfo.emis.trainRepair.common.model.ConfigParamsModel;
import com.ydzbinfo.emis.trainRepair.common.querymodel.XzyCConfig;
import com.ydzbinfo.emis.trainRepair.common.service.IRemoteService;
import com.ydzbinfo.emis.trainRepair.common.service.IRepairMidGroundService;
import com.ydzbinfo.emis.trainRepair.mobile.model.MainCycPacket;
import com.ydzbinfo.emis.trainRepair.mobile.model.MobileTaskPandect;
import com.ydzbinfo.emis.trainRepair.mobile.model.PacketPersonnel;
import com.ydzbinfo.emis.trainRepair.mobile.taskpandect.service.IPhoneTaskPandectService;
import com.ydzbinfo.emis.trainRepair.remotemodel.trainuse.ShuntPlanModel;
import com.ydzbinfo.emis.trainRepair.remotemodel.trainuse.ZtTaskPacketEntity;
import com.ydzbinfo.emis.trainRepair.taskAllot.querymodel.XzyMTaskallotpacket;
import com.ydzbinfo.emis.trainRepair.taskAllot.querymodel.XzyMTaskallotperson;
import com.ydzbinfo.emis.trainRepair.taskAllot.querymodel.XzyMTaskcarpart;
import com.ydzbinfo.emis.trainRepair.taskAllot.service.XzyMTaskAllotDeptService;
import com.ydzbinfo.emis.trainRepair.taskAllot.service.XzyMTaskAllotPersonService;
import com.ydzbinfo.emis.trainRepair.taskAllot.service.XzyMTaskallotpacketService;
import com.ydzbinfo.emis.trainRepair.taskAllot.service.XzyMTaskcarpartService;
import com.ydzbinfo.emis.trainRepair.trackPowerInfo.service.TrackPowerInfoService;
import com.ydzbinfo.emis.trainRepair.trainsetPostion.querymodel.TrackPowerEntity;
import com.ydzbinfo.emis.trainRepair.trainsetPostion.querymodel.TrainsetPositionEntity;
import com.ydzbinfo.emis.trainRepair.trainsetPostion.service.TrainsetPostionService;
import com.ydzbinfo.emis.trainRepair.workprocess.model.WorkTime;
import com.ydzbinfo.emis.utils.DateUtils;
import com.ydzbinfo.emis.utils.DayPlanUtil;
import com.ydzbinfo.emis.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Description:任务总单业务类实现
 * Author: wuyuechang
 * Create Date Time: 2021/4/26 14:12
 * Update Date Time: 2021/4/26 14:12
 *
 * @see
 */
@Service
@Slf4j
public class PhoneTaskPandectServiceImpl implements IPhoneTaskPandectService {

    @Autowired
    IRemoteService remoteService;

    @Autowired
    IRepairMidGroundService midGroundService;

    @Autowired
    TrainsetPostionService trainsetPostionService;

    @Autowired
    TrackPowerInfoService trackPowerInfoService;

    @Autowired
    XzyMTaskallotpacketService taskallotpacketService;

    @Autowired
    XzyMTaskcarpartService taskcarpartService;

    @Autowired
    XzyMTaskAllotPersonService taskAllotPersonService;

    @Autowired
    XzyMTaskAllotDeptService taskAllotDeptService;

    @Override
    public List<MobileTaskPandect> getPhoneTaskPandect(String unitCode, String workerId, String dayPlanID, List<String> deptCodeList, String isSelf) throws ParseException {
        List<ZtTaskPacketEntity> ztTaskPacketEntities = new ArrayList<>();
        try {
            ztTaskPacketEntities = remoteService.getPacketTaskByDayplanId(dayPlanID, unitCode);
        } catch (Exception e) {
            log.error("获取检修计划接口异常:getPacketTaskByDayplanId", e);
        }

        //获取调车计划
        Map<String, List<ShuntPlanModel>> trackArea = getStringListMap(unitCode, dayPlanID);

        //只要本班的任务
        List<ZtTaskPacketEntity> ztTaskPacketEntityList = ztTaskPacketEntities.stream().filter(ztTaskPacketEntity -> !CollectionUtils.isEmpty(deptCodeList)&&deptCodeList.contains(ztTaskPacketEntity.getRepairDeptCode())).collect(Collectors.toList());
        Map<String, MobileTaskPandect> mobileTaskPandectHashMap = new HashMap<>();
        Map<String, List<ZtTaskPacketEntity>> taskPacketEntityMap = ztTaskPacketEntityList.stream().collect(Collectors.groupingBy(ZtTaskPacketEntity::getTrainsetId));
        List<String> taskIds = ztTaskPacketEntityList.stream().map(ZtTaskPacketEntity::getTaskId).distinct().collect(Collectors.toList());
        List<XzyMTaskallotpacket> taskallotpacketList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(taskIds)) {
            taskallotpacketList = taskallotpacketService.getTaskAllotPacketByTaskId(taskIds);
        }

        List<String> taskAllotPacketIds = taskallotpacketList.stream().map(XzyMTaskallotpacket::getTaskAllotPacketId).distinct().collect(Collectors.toList());
        List<XzyMTaskcarpart> taskAllotListByPacketIds = new ArrayList<>();
        if (!CollectionUtils.isEmpty(taskAllotPacketIds)) {
            taskAllotListByPacketIds = taskcarpartService.getTaskAllotListByPacketIds(taskAllotPacketIds);

        }

        List<String> processIds = taskAllotListByPacketIds.stream().map(XzyMTaskcarpart::getProcessId).distinct().collect(Collectors.toList());
        List<XzyMTaskallotperson> xzyMTaskallotpeople = new ArrayList<>();
        if (!CollectionUtils.isEmpty(processIds)) {
            xzyMTaskallotpeople = taskAllotPersonService.getTaskAllotPersonListByProcessIds(processIds);
        }

        for (String trainsetId : taskPacketEntityMap.keySet()) {
            MobileTaskPandect mobileTaskPandect = getMobileTaskPandect(trackArea, taskPacketEntityMap, trainsetId);
            List<PacketPersonnel> packetPersonnels = new ArrayList<>();
            for (ZtTaskPacketEntity ztTaskPacketEntity : taskPacketEntityMap.get(trainsetId)) {
                PacketPersonnel packetPersonnel = new PacketPersonnel();
                packetPersonnel.setPacketName(ztTaskPacketEntity.getPacketName());
                //根据任务包id获取派工包
                Map<String, String> map = new HashMap<>();
                //所有的人名
                List<String> workerNames = new ArrayList<>();
                //所有人的Id
                Set<String> workerIdSet = new HashSet<>();
                List<XzyMTaskallotpacket> xzyMTaskallotpacketList = taskallotpacketList.stream().filter(t -> t.getStaskId().equals(ztTaskPacketEntity.getTaskId())).collect(Collectors.toList());
                for (XzyMTaskallotpacket packet : xzyMTaskallotpacketList) {
                    //根据派工包获取派工辆序部件数据
                    List<XzyMTaskcarpart> taskcarpartList = taskAllotListByPacketIds.stream().filter(t -> t.getTaskAllotPacketId().equals(packet.getTaskAllotPacketId())).collect(Collectors.toList());
                    for (XzyMTaskcarpart carpart : taskcarpartList) {
                        String processId = carpart.getProcessId();
                        //根据派工辆序部件获取派工人员和派工部门数据
                        List<XzyMTaskallotperson> personList = xzyMTaskallotpeople.stream().filter(t -> t.getProcessId().equals(processId)).collect(Collectors.toList());
                        Map<String, String> workerIds = personList.stream().collect(Collectors.toMap(XzyMTaskallotperson::getWorkerID, XzyMTaskallotperson::getWorkerName, (k1, k2) -> k2));
                        map.putAll(workerIds);
                        for (String id : map.keySet()) {
                            workerIdSet.add(id);
                            if (workerId.equals(id)) {
                                packetPersonnel.setIsAuthor("1");
                            }
                        }
                    }
                }
                for (String s : workerIdSet) {
                    if (workerId.equals(s)) {
                        workerNames.add(0, map.get(s));
                    } else {
                        workerNames.add(map.get(s));
                    }
                }
                packetPersonnel.setWorkerList(workerNames);
                packetPersonnels.add(packetPersonnel);
            }
            mobileTaskPandect.setPacketList(packetPersonnels);
            mobileTaskPandectHashMap.put(trainsetId, mobileTaskPandect);
        }

        Collection<MobileTaskPandect> trackPower = getTrackPower(unitCode, mobileTaskPandectHashMap);
        List<MobileTaskPandect> mobileTaskPandects = new ArrayList<>();
        for (MobileTaskPandect mobileTaskPandect : trackPower) {
            boolean flag = false;
            for (PacketPersonnel packetPersonnel : mobileTaskPandect.getPacketList()) {
                if ("1".equals(packetPersonnel.getIsAuthor())) {
                    flag = true;
                }
            }
            if (flag) {
                mobileTaskPandects.add(mobileTaskPandect);
            }
        }
        //个人返回数据
        List<MobileTaskPandect> result = mobileTaskPandects.stream().sorted(Comparator.comparing(MobileTaskPandect::getTrainsetName)).collect(Collectors.toList());

        //剩下班组数据
        if ("2".equals(isSelf)) {
            trackPower.removeAll(result);
            List<MobileTaskPandect> deptMobileTaskPandect = trackPower.stream().sorted(Comparator.comparing(MobileTaskPandect::getTrainsetName)).collect(Collectors.toList());
            result.addAll(deptMobileTaskPandect);
        }
        return result;
    }

    private MobileTaskPandect getMobileTaskPandect(Map<String, List<ShuntPlanModel>> trackArea, Map<String, List<ZtTaskPacketEntity>> taskPacketEntityMap, String trainsetId) {
        MobileTaskPandect mobileTaskPandect = new MobileTaskPandect();
        mobileTaskPandect.setTrainsetId(trainsetId);
        mobileTaskPandect.setTrainsetName(taskPacketEntityMap.get(trainsetId).get(0).getTrainsetName());
        List<ShuntPlanModel> shuntPlanModels = trackArea.get(trainsetId);
        if (!CollectionUtils.isEmpty(shuntPlanModels)) {
            String inTime = shuntPlanModels.stream().min(Comparator.comparing(ShuntPlanModel::getInTime)).toString();
            String outTime = shuntPlanModels.stream().max(Comparator.comparing(ShuntPlanModel::getOutTime)).toString();
            String trackCode = shuntPlanModels.stream().map(ShuntPlanModel::getTrackCode).collect(Collectors.joining());
            String trackName = shuntPlanModels.stream().map(ShuntPlanModel::getTrackName).collect(Collectors.joining());
            mobileTaskPandect.setPlanRepairBeginTime(DateUtils.strToDateTime(inTime));
            mobileTaskPandect.setPlanRepairEndTime(DateUtils.strToDateTime(outTime));
            mobileTaskPandect.setRepairTrackCode(trackCode);
            mobileTaskPandect.setRepairTrackName(trackName);
        }
        mobileTaskPandect.setRepairDeptName(taskPacketEntityMap.get(trainsetId).get(0).getRepairDeptName());
        mobileTaskPandect.setRepairDeptCode(taskPacketEntityMap.get(trainsetId).get(0).getRepairDeptCode());
        return mobileTaskPandect;
    }

    @Override
    public List<MobileTaskPandect> getMobileRepairPlan(String unitCode, String dayPlanID, String deptCode) {

        List<ZtTaskPacketEntity> ztTaskPacketEntities = new ArrayList<>();
        try {
            ztTaskPacketEntities = remoteService.getPacketTaskByDayplanId(dayPlanID, unitCode);
        } catch (Exception e) {
            log.error("获取检修计划接口异常:getPacketTaskByDayplanId", e);
        }
        //获取调车计划
        Map<String, List<ShuntPlanModel>> trackArea = getStringListMap(unitCode, dayPlanID);

        Map<String, MobileTaskPandect> mobileTaskPandectHashMap = new HashMap<>();
        Map<String, List<ZtTaskPacketEntity>> taskPacketEntityMap = ztTaskPacketEntities.stream().collect(Collectors.groupingBy(ZtTaskPacketEntity::getTrainsetId));
        for (String trainsetId : taskPacketEntityMap.keySet()) {
            MobileTaskPandect mobileTaskPandect = getMobileTaskPandect(trackArea, taskPacketEntityMap, trainsetId);

            List<MainCycPacket> mainCycPacketList = new ArrayList<>();
            for (ZtTaskPacketEntity ztTaskPacketEntity : taskPacketEntityMap.get(trainsetId)) {
                getMainCycPacket(ztTaskPacketEntity, mainCycPacketList);
            }
            Map<String, List<MainCycPacket>> mainCycPacketMap = mainCycPacketList.stream().collect(Collectors.groupingBy(MainCycPacket::getTaskRepairName));
            List<MainCycPacket> packetList = new ArrayList<>();
            for (String taskRepairName : mainCycPacketMap.keySet()) {
                MainCycPacket packet = new MainCycPacket();
                packet.setTaskRepairName(taskRepairName);
                packet.setTaskRepairCode(mainCycPacketMap.get(taskRepairName).get(0).getTaskRepairCode());
                packet.setTaskRepairName(mainCycPacketMap.get(taskRepairName).get(0).getTaskRepairName());
                List<String> packetNames = mainCycPacketMap.get(taskRepairName).stream().map(MainCycPacket::getPacketNames).flatMap(Collection::stream).collect(Collectors.toList());
                packet.setPacketNames(packetNames);
                packetList.add(packet);
            }
            mobileTaskPandect.setMainCycPacket(packetList);
            mobileTaskPandectHashMap.put(trainsetId, mobileTaskPandect);
        }
        Collection<MobileTaskPandect> trackPower = getTrackPower(unitCode, mobileTaskPandectHashMap);
        return trackPower.stream().sorted(Comparator.comparing(MobileTaskPandect::getTrainsetName)).collect(Collectors.toList());
    }

    private Map<String, List<ShuntPlanModel>> getStringListMap(String unitCode, String dayPlanID) {
        //根据当前日计划获取工作开始结束时间
        WorkTime worker = DayPlanUtil.getWorkTimeByDayPlanId(dayPlanID);
        ConfigParamsModel configParamsModel = new ConfigParamsModel();
        configParamsModel.setType("G");
        configParamsModel.setName("AppTrackareaConfig");
        List<XzyCConfig> configs = midGroundService.getXzyCConfigs(configParamsModel);
        Set<String> trackAreaNames = StringUtils.commaDelimitedListToSet(configs.get(0).getParamValue());
        //获取调车计划
        List<ShuntPlanModel> shuntingPlanByCondition = remoteService.getShuntingPlanByCondition(unitCode, null, DateUtils.dateTimeToStr(worker.getStartTime()), DateUtils.dateTimeToStr(worker.getEndTime()));
        //根据key为车组id value为调车计划对象转map
        return shuntingPlanByCondition.stream().filter(shuntPlanModel -> trackAreaNames.contains(shuntPlanModel.getTrackAreaName())).collect(Collectors.groupingBy(ShuntPlanModel::getEmuId));
    }

    private void getMainCycPacket(ZtTaskPacketEntity ztTaskPacketEntity, List<MainCycPacket> mainCycPacketList) {
        MainCycPacket mainCycPacket = new MainCycPacket();
        mainCycPacket.setPacketTypeCode(ztTaskPacketEntity.getPacketTypeCode());
        mainCycPacket.setTaskRepairCode(ztTaskPacketEntity.getTaskRepairCode());
        if ("1".equals(ztTaskPacketEntity.getTaskRepairCode())) {
            mainCycPacket.setTaskRepairName("一级修");
        } else if ("2".equals(ztTaskPacketEntity.getTaskRepairCode())) {
            mainCycPacket.setTaskRepairName("二级修");
        } else if ("3".equals(ztTaskPacketEntity.getPacketTypeCode())) {
            mainCycPacket.setTaskRepairName("临时任务");
        } else {
            mainCycPacket.setTaskRepairName("其他");
        }
        List<String> packetNames = Collections.singletonList(ztTaskPacketEntity.getPacketName());
        mainCycPacket.setPacketNames(packetNames);
        mainCycPacketList.add(mainCycPacket);
    }

    private Collection<MobileTaskPandect> getTrackPower(String unitCode, Map<String, MobileTaskPandect> mobileTaskPandectHashMap) {
        Set<String> keys = mobileTaskPandectHashMap.keySet();
        for (String key : keys) {
            MobileTaskPandect mobileTaskPandect = mobileTaskPandectHashMap.get(key);
            List<String> trackCodeList = new ArrayList<>();
            String repairTrackCode = mobileTaskPandect.getRepairTrackCode();
            trackCodeList.add(repairTrackCode);
            List<TrainsetPositionEntity> trainsetPositionList;
            //根据条件查询车组位置信息
            List<String> unitCodes = new ArrayList<>();
            unitCodes.add(unitCode);
            //车组名称
            List<String> trainsetName = new ArrayList<>();
            trainsetName.add(mobileTaskPandect.getTrainsetName());
            //根据条件查询车组位置信息
            trainsetPositionList = trainsetPostionService.getTrainsetPostion(trackCodeList, trainsetName, unitCodes);
            List<TrackPowerEntity> trackPowerEntityList;

            //判断车组位置信息是否为空
            if (trainsetPositionList.size() > 0) {
                //遍历车组位置信息集合
                for (TrainsetPositionEntity trainsetPositionEntity : trainsetPositionList) {
                    if (trainsetPositionEntity.getTrainsetId().equals(mobileTaskPandect.getTrainsetId())) {
                        String trackCode = trainsetPositionEntity.getTrackCode();
                        String trackPlaCode = trainsetPositionEntity.getHeadDirectionPlaCode();
                        StringBuilder stringBuilder = new StringBuilder();
                        stringBuilder.append(trainsetPositionEntity.getTrackName()).append("-").append(trainsetPositionEntity.getHeadDirectionPla());
                        if (trainsetPositionEntity.getHeadDirectionPla().equals(trainsetPositionEntity.getTailDirectionPla())) {
                            //根据条件查询股道供断电信息
                            trackPowerEntityList = trackPowerInfoService.getTrackPowerInfoByOne(trackCode, unitCode, trackPlaCode);
                        } else {
                            trackPowerEntityList = trackPowerInfoService.getTrackPowerInfoByOne(trackCode, unitCode, "");
                            stringBuilder.append("(").append(trainsetPositionEntity.getTailDirectionPla()).append(")");
                        }
                        mobileTaskPandect.setPlanRepairTrack(stringBuilder.toString());
                        mobileTaskPandect.setTrackPowerEntityList(trackPowerEntityList);
                    }
                }
            }
            mobileTaskPandectHashMap.put(key, mobileTaskPandect);
        }
        return mobileTaskPandectHashMap.values();
    }
}
