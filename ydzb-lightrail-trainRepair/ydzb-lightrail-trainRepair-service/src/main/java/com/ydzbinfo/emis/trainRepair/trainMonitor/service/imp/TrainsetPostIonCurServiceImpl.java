package com.ydzbinfo.emis.trainRepair.trainMonitor.service.imp;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.ydzbinfo.emis.trainRepair.common.service.IRemoteService;
import com.ydzbinfo.emis.trainRepair.common.util.ConfigUtil;
import com.ydzbinfo.emis.trainRepair.remotemodel.trainuse.ZtTaskPacketEntity;
import com.ydzbinfo.emis.trainRepair.remotemodel.trainuse.track.ZtTrackAreaEntity;
import com.ydzbinfo.emis.trainRepair.remotemodel.trainuse.track.ZtTrackPositionEntity;
import com.ydzbinfo.emis.trainRepair.repairworkflow.config.RepairWorkflowProperties;
import com.ydzbinfo.emis.trainRepair.repairworkflow.dao.FlowMapper;
import com.ydzbinfo.emis.trainRepair.repairworkflow.dao.FlowRunMapper;
import com.ydzbinfo.emis.trainRepair.repairworkflow.model.*;
import com.ydzbinfo.emis.trainRepair.repairworkflow.querymodel.ExtraFlowType;
import com.ydzbinfo.emis.trainRepair.repairworkflow.querymodel.FlowType;
import com.ydzbinfo.emis.trainRepair.repairworkflow.querymodel.base.BaseFlowType;
import com.ydzbinfo.emis.trainRepair.repairworkflow.service.IExtraFlowTypeService;
import com.ydzbinfo.emis.trainRepair.repairworkflow.service.IFlowRunService;
import com.ydzbinfo.emis.trainRepair.repairworkflow.service.IFlowTypeService;
import com.ydzbinfo.emis.trainRepair.repairworkflow.utils.*;
import com.ydzbinfo.emis.trainRepair.trainMonitor.dao.TrainsetPositionCurMapper;
import com.ydzbinfo.emis.trainRepair.trainMonitor.model.FlowWithFlowRun;
import com.ydzbinfo.emis.trainRepair.trainMonitor.model.RepairTask;
import com.ydzbinfo.emis.trainRepair.trainMonitor.model.TrainsetIsConnect;
import com.ydzbinfo.emis.trainRepair.trainMonitor.model.TrainsetWithTaskPacket;
import com.ydzbinfo.emis.trainRepair.trainMonitor.querymodel.TrackPowerStateCur;
import com.ydzbinfo.emis.trainRepair.trainMonitor.querymodel.TrainsetPostionCur;
import com.ydzbinfo.emis.trainRepair.trainMonitor.querymodel.TrainsetPostionHis;
import com.ydzbinfo.emis.trainRepair.trainMonitor.service.ConfigService;
import com.ydzbinfo.emis.trainRepair.trainMonitor.service.TrackPowerStateCurService;
import com.ydzbinfo.emis.trainRepair.trainMonitor.service.TrainsetPostIonCurService;
import com.ydzbinfo.emis.trainRepair.trainMonitor.service.TrainsetPostionHisService;
import com.ydzbinfo.emis.trainRepair.workProcessMonitor.model.FaultItem;
import com.ydzbinfo.emis.trainRepair.workProcessMonitor.service.IProcessMonitorService;
import com.ydzbinfo.emis.utils.*;
import com.ydzbinfo.emis.utils.result.RestRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author gaohan
 * @description
 * @createDate 2021/2/26 12:06
 **/
@Transactional
@Service
public class TrainsetPostIonCurServiceImpl implements TrainsetPostIonCurService {
    @Resource
    TrainsetPositionCurMapper trainsetPositionCurMapper;

    @Resource
    TrainsetPostionHisService trainsetpostionHisService;
    @Autowired
    IRemoteService remoteService;
    @Autowired
    FlowRunMapper flowRunMapper;
    @Autowired
    FlowMapper flowMapper;
    @Autowired
    IFlowRunService flowRunService;

    @Autowired
    private RepairWorkflowProperties repairWorkflowProperties;

    @Autowired
    IFlowTypeService flowTypeService;

    @Autowired
    TrainsetPostIonCurService trainsetPostIonCurService;

    @Autowired
    TrackPowerStateCurService trackPowerStateCurService;


    @Autowired
    ConfigService configService;


    @Autowired
    IExtraFlowTypeService extraFlowTypeService;

    @Autowired
    IProcessMonitorService processMonitorService;

    public Logger logger = LoggerFactory.getLogger(getClass());


    @Override
    public List<TrainsetPostionCur> getByTrainsetIds(String... trainsetIds) {
        return trainsetPositionCurMapper.getByTrainsetIds(trainsetIds);
    }

    @Override
    public List<TrainsetPostionCur> getTrainsetPostion(Set<String> trackCodes, String unitCode) {
        return trainsetPositionCurMapper.getTrainsetPostion(trackCodes, unitCode);
    }

    @Override
    public List<TrainsetPostionCur> getTrainsetPostionByTrackCode(String trackCode) {
        return trainsetPositionCurMapper.getTrainsetPostionByTrackCode(trackCode);
    }

    @Override
    public int addTrainsetPostion(TrainsetPostionCur trainsetPositionEntity) {
        return trainsetPositionCurMapper.addTrainsetPostion(trainsetPositionEntity);
    }

    @Override
    public int updateTrainsetPostion(TrainsetPostionCur trainsetPositionEntity) {
        return trainsetPositionCurMapper.updateTrainsetPostion(trainsetPositionEntity);
    }

    @Override
    public TrainsetPostionCur getTrainsetPositionById(String trainsetId) {
        return trainsetPositionCurMapper.getTrainsetPositionById(trainsetId);
    }

    @Override
    public int deleteTrainsetPostion(String trainsetId) {
        return trainsetPositionCurMapper.deleteTrainsetPostion(trainsetId);
    }

    @Override
    public String setTrainsetPosition(TrainsetPostionCur trainsetPositionEntity) {
//        trainsetPositionEntity.setUnitCode(UserShiroUtil.getUserInfo().getDeptCode());
//        trainsetPositionEntity.setRecordUserCode(UserShiroUtil.getUserInfo().getDeptCode());
//        trainsetPositionEntity.setRecordUserName(UserShiroUtil.getUserInfo().getUserName());
        // 判断车组是否离开
        if (trainsetPositionEntity.getOutTime() == null) {
            if (trainsetPositionEntity.getTrackCode() != null) {// 添加
                // 根据股道编号查询车组位置信息
                List<TrainsetPostionCur> trainsetPositionEntityList = this.getTrainsetPostionByTrackCode(trainsetPositionEntity.getTrackCode());
                if (CommonUtils.some(trainsetPositionEntityList,
                    curTrainsetPositionEntity -> {
                        List<String> a = Arrays.asList(curTrainsetPositionEntity.getHeadDirectionPlaCode(), curTrainsetPositionEntity.getTailDirectionPlaCode());
                        List<String> b = Arrays.asList(trainsetPositionEntity.getHeadDirectionPlaCode(), trainsetPositionEntity.getTailDirectionPlaCode());
                        return CommonUtils.some(a, aa -> CommonUtils.some(b, bb -> Objects.equals(aa, bb)));
                    })) {
                    throw RestRequestException.normalFail("请先出车");
                } else {
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String nowDate = simpleDateFormat.format(new Date());
                    String id = UUID.randomUUID().toString();
                    trainsetPositionEntity.setTrainsetPostionId(id);
                    //比当前时间大并且不是本股道
                    Date date = this.getOutTime(nowDate, trainsetPositionEntity.getTrackCode());
                    if (date == null) {
                        date = new Date();
                    }
                    trainsetPositionEntity.setOutTime(date);
                    this.addTrainsetPostion(trainsetPositionEntity);
                    return id;
                }
            } else {// 更新
                this.updateTrainsetPostion(trainsetPositionEntity);
            }
        } else {// 车组离开
            // 查询出完整车组位置信息
            TrainsetPostionCur fullTrainsetPositionEntity = this.getTrainsetPositionById(trainsetPositionEntity.getTrainsetId());
            // 删除车组位置信息
            int a = this.deleteTrainsetPostion(trainsetPositionEntity.getTrainsetId());
            if (fullTrainsetPositionEntity == null || a == 0) {
                throw RestRequestException.normalFail("出车失败，请刷新");
            }
            // 在车组位置信息历史表添加数据
            TrainsetPostionHis trainsetpostionHis = new TrainsetPostionHis();
            BeanUtils.copyProperties(fullTrainsetPositionEntity, trainsetpostionHis);
            trainsetpostionHis.setId(fullTrainsetPositionEntity.getTrainsetPostionId());
            trainsetpostionHis.setTrainsetId(fullTrainsetPositionEntity.getTrainsetId());
            trainsetpostionHisService.addTrainsetPostionHis(trainsetpostionHis);
        }

        return null;
    }

    @Override
    public void setTrainsetState(TrainsetIsConnect trainsetIsConnect) {
        trainsetPositionCurMapper.setTrainsetState(trainsetIsConnect);
    }

    @Override
    public Date getOutTime(String nowDate, String trackCode) {
        return trainsetPositionCurMapper.getOutTime(nowDate, trackCode);
    }

    @Override
    public void updTrackCode(TrainsetPostionCur trainsetPostIonCur) {
        trainsetPositionCurMapper.updTrackCode(trainsetPostIonCur);
    }

    @Override
    public List<RepairTask> getRepairTask(String dayPlanId, String trainsetIdStr, String unitCode, Boolean showDayRepairTask,Boolean showForceEndFlowRun) {
        List<String> trainsetIds = new ArrayList<>();
        if (trainsetIdStr != null && !trainsetIdStr.equals("")) {
            trainsetIds = Arrays.asList(trainsetIdStr.split(","));
        }

        List<FlowTypeInfoWithPackets> flowTypeInfoWithPacketList = CacheUtil.getDataUseThreadCache(
            "flowTypeService.getFlowTypeAndPacket_" + unitCode,
            () -> flowTypeService.getFlowTypeAndPacket(unitCode)
        );

        //获取流程运行
        FlowRunInfo flowRunInfos = flowRunService.getFlowRunInfosByTrainMonitor(FlowPageCodeEnum.TRAIN_MONITOR.getValue(), unitCode, dayPlanId, trainsetIdStr, showDayRepairTask);
        List<FlowRunInfoGroup> flowRunInfoGroups = flowRunInfos.getFlowRunInfoGroups();
        Map<String, FlowRunInfoGroup> groupedFlowRunInfoGroupsByTrainsetId = CommonUtils.collectionToMap(flowRunInfoGroups, FlowRunInfoGroup::getTrainsetId);


        //获取到日计划按车组分组
        List<ZtTaskPacketEntity> taskPacketEntities = remoteService.getPacketTaskByCondition(dayPlanId, "", "", "", unitCode);
        Map<String, List<ZtTaskPacketEntity>> groupedMatchedTaskPacketEntity = taskPacketEntities.stream().collect(
            Collectors.groupingBy(ZtTaskPacketEntity::getTrainsetId)
        );

        String lastDayPlanId = FlowUtil.getLastDayPlanId(dayPlanId);
        //获取上一个日计划按车组分组
        List<ZtTaskPacketEntity> lastTaskPacketEntities = remoteService.getPacketTaskByCondition(lastDayPlanId, "", "", "", unitCode);
        Map<String, List<ZtTaskPacketEntity>> groupedMatchedLastTaskPacketEntity = lastTaskPacketEntities.stream().collect(
            Collectors.groupingBy(ZtTaskPacketEntity::getTrainsetId)
        );

        //关键作业
        List<String> dayPlanIds = FlowUtil.getQueryDayPlanIds(
            DayPlanUtil.getDayPlanId(unitCode),
            FlowDatabaseConfigUtil.getKeyWorkMonitorShowDayPlanCount()
        );
        List<KeyWorkFlowRunWithTrainset> keyWorkFlowRunWithTrainsetList = flowRunService.getKeyWorkFlowRunWithTrainsetList(dayPlanIds, unitCode, null, showForceEndFlowRun);
        Map<String, KeyWorkFlowRunWithTrainset> groupedKeyWorkFlowRunByTrainsetId = CommonUtils.collectionToMap(keyWorkFlowRunWithTrainsetList,KeyWorkFlowRunWithTrainset::getTrainsetId);

        List<RepairTask> repairTasks = new ArrayList<>();
        for (String trainsetId : trainsetIds) {
            //本班次本车组计划
            List<ZtTaskPacketEntity> taskPacketEntitiesByTrainsetId = groupedMatchedTaskPacketEntity.get(trainsetId);

            //获取到上一个班次本车组的计划
            List<ZtTaskPacketEntity> lastTaskPacketEntitiesByTrainsetId = groupedMatchedLastTaskPacketEntity.get(trainsetId);
            List<ZtTaskPacketEntity> packetEntities = new ArrayList<>();

            //看最后返回是本班次计划还是上班次计划方便以后前段展示
            String resultDayPlanId = dayPlanId;

            if (showDayRepairTask) {
                packetEntities = taskPacketEntitiesByTrainsetId;
            } else {
                if (taskPacketEntitiesByTrainsetId != null) {
                    packetEntities = taskPacketEntitiesByTrainsetId;
                } else if (lastTaskPacketEntitiesByTrainsetId != null) {
                    packetEntities = lastTaskPacketEntitiesByTrainsetId;
                    resultDayPlanId = lastDayPlanId;
                }
            }

            RepairTask repairTask = new RepairTask();
            repairTask.setTrainsetId(trainsetId);
            repairTask.setDayPlan(resultDayPlanId.equals(dayPlanId) ? true : false);
            if (packetEntities != null && packetEntities.size() > 0) {
                //故障
                try {
                    List<FaultItem> faultItems = processMonitorService.getFaultList(unitCode, dayPlanId, trainsetId, null);
                    repairTask.setFaultItemList(faultItems);
                    repairTask.setFaultNumber(faultItems.stream().filter(v -> v.getDealWithDesc().equals("未处理")).collect(Collectors.toList()).size());
                } catch (Exception e) {
                    logger.error("车组监控获取故障失败", e);
                }


                //滤网
                repairTask.setFilterNumber(packetEntities.stream().anyMatch(v -> v.getPacketTypeCode().equals("2")));
                //一级修
                repairTask.setRepairOne(packetEntities.stream().anyMatch(v -> v.getTaskRepairCode().equals("1") && v.getPacketTypeCode().equals("1")));
                //二级修
                repairTask.setRepairTwo(packetEntities.stream().anyMatch(v -> v.getTaskRepairCode().equals("2") && v.getPacketTypeCode().equals("1")));

                //临修
                repairTask.setTemporaryNumber(packetEntities.stream().filter(v -> v.getPacketTypeCode().equals("3")).collect(Collectors.toList()).size());


                //机检
                boolean triggerMachine = packetEntities.stream().anyMatch(v -> v.getTaskRepairCode().equals("1") && v.getPacketTypeCode().equals("16"));
                if (triggerMachine) {
                    FlowWithFlowRun flowWithFlowRun = getFlowWithFlowRunByFlowTypeCode(groupedFlowRunInfoGroupsByTrainsetId.get(trainsetId), BasicFlowTypeEnum.REPAIR_ONE.getValue());
                    flowWithFlowRun.setTrigger(true);
                    repairTask.setMachineRepair(flowWithFlowRun);
                    //找流程id和流程运行id
                }
                //漩轮
                FlowTypeInfoWithPackets millWheelFlowType = CommonUtils.find(flowTypeInfoWithPacketList, v -> v.getCode().equals(repairWorkflowProperties.getMillWheelFlowTypeCode()));
                if (millWheelFlowType != null) {
                    FlowTypeConfigTypeEnum configTypeEnum = EnumUtils.findEnum(FlowTypeConfigTypeEnum.class, FlowTypeConfigTypeEnum::getValue, millWheelFlowType.getConfigType());
                    boolean isMillWheelFlowType = configTypeEnum.isFlowTypeMatchedByTaskPackets(millWheelFlowType, packetEntities);
                    if (isMillWheelFlowType) {
                        FlowWithFlowRun flowWithFlowRun = getFlowWithFlowRunByFlowTypeCode(groupedFlowRunInfoGroupsByTrainsetId.get(trainsetId), repairWorkflowProperties.getMillWheelFlowTypeCode());
                        flowWithFlowRun.setTrigger(true);
                        repairTask.setMillWheel(flowWithFlowRun);
                        //找流程id和流程运行id
                    }
                }

                //整备作业
                boolean triggerHosting = FlowPageCodeEnum.HOSTLING.match(packetEntities);
                if (triggerHosting) {
                    FlowWithFlowRun flowWithFlowRun = getFlowWithFlowRunByFlowTypeCode(groupedFlowRunInfoGroupsByTrainsetId.get(trainsetId), repairWorkflowProperties.getHostlingFlowTypeCode());
                    if (StringUtils.isNotBlank(flowWithFlowRun.getFlowId())) {
                        flowWithFlowRun.setTrigger(true);
                        repairTask.setHostLing(flowWithFlowRun);
                    } else {
                        repairTask.setHostLing(null);
                    }

                }
            }
            KeyWorkFlowRunWithTrainset keyWorkFlowRunWithTrainset = groupedKeyWorkFlowRunByTrainsetId.get(trainsetId);
            if (keyWorkFlowRunWithTrainset != null) {
                List<KeyWorkFlowRunInfo> keyWorkFlowRunInfoList = keyWorkFlowRunWithTrainset.getKeyWorkFlowRunInfos();
                if(keyWorkFlowRunInfoList.stream().anyMatch(v->dayPlanIds.contains(v.getDayPlanId()) && v.getState().equals("0"))){
                    repairTask.setPlanLessKeyDay(false);
                }else{
                    repairTask.setPlanLessKeyDay(true);
                }
                repairTask.setPlanLessKeyFlowId(keyWorkFlowRunInfoList.stream().map(v->v.getFlowId()).collect(Collectors.toList()));
                repairTask.setPlanLessKeyNumber(keyWorkFlowRunInfoList.size());
            }


            repairTasks.add(repairTask);
        }
        return repairTasks;
    }

    private FlowWithFlowRun getFlowWithFlowRunByFlowTypeCode(FlowRunInfoGroup flowRunInfoGroup, String flowTypeCode) {
        if (flowRunInfoGroup == null) {
            return new FlowWithFlowRun();
        }
        List<FlowRunInfoForSimpleShow> flowRunInfoForSimpleShowList = flowRunInfoGroup.getFlowRunInfoForSimpleShows().stream().filter(v -> v.getFlowTypeCode().equals(flowTypeCode)).collect(Collectors.toList());
        if (flowRunInfoForSimpleShowList.size() > 0) {
            FlowWithFlowRun flowWithFlowRun = new FlowWithFlowRun();
            flowWithFlowRun.setFlowId(flowRunInfoForSimpleShowList.get(0).getFlowId());
            flowWithFlowRun.setFlowRunId(flowRunInfoForSimpleShowList.get(0).getId());
            return flowWithFlowRun;
        }
        return new FlowWithFlowRun();
    }


    @Override
    public Map getTrainsetAndTrackPowerInfo(String dayPlanId, String flowPageCode, String unitCode, Date startDateTime, Date endDateTime) {
        Map<String, Object> map = new HashMap();
        //获取所有股道
        List<TrackPowerStateCur> trackPowerStateCurs = trackPowerStateCurService.getAllTrackPowers(unitCode);
        Set<String> trackCodes = trackPowerStateCurs.stream().map(v -> v.getTrackCode()).collect(Collectors.toSet());
        //获取车组位置信息
        List<TrainsetPostionCur> trainsetPostIonCurs = trainsetPostIonCurService.getTrainsetPostion(trackCodes, unitCode);
        List<TrainsetWithTaskPacket> filterTrainsetByFlowPageAndTask = filterTrainsetByFlowPageAndTask(flowPageCode, dayPlanId, unitCode, trainsetPostIonCurs, startDateTime, endDateTime);
        //获取股道供断电信息
        List<TrackPowerStateCur> trackPowerStateCurList = trackPowerStateCurService.getTrackPowerInfo(trackCodes, unitCode);
        trackPowerStateCurList.sort(Comparator.comparing(TrackPowerStateCur::getTrackName));
        filterTrainsetByFlowPageAndTask.sort(Comparator.comparing(TrainsetWithTaskPacket::getTrackName));
        map.put("trainsetPostIon", filterTrainsetByFlowPageAndTask);
        map.put("trackPowerState", trackPowerStateCurList);
        return map;
    }

    public List<TrainsetWithTaskPacket> filterTrainsetByFlowPageAndTask(String flowPageCode, String dayPlanId, String unitCode, List<TrainsetPostionCur> trainsetPostIonCurs, Date startDataTime, Date endDataTime) {
        if (StringUtils.isBlank(flowPageCode)) {
            return new ArrayList<>();
        }
        FlowPageCodeEnum flowPageCodeEnum = EnumUtils.findEnum(FlowPageCodeEnum.class, FlowPageCodeEnum::getValue, flowPageCode);
        //根据日计划查计划
        List<ZtTaskPacketEntity> taskPacketEntityList = remoteService.getPacketTaskByCondition(dayPlanId, "", "", "", unitCode);
        //要保留的车组
        Set<String> trainsetIdList;
        if (flowPageCodeEnum != null) {
            trainsetIdList = new HashSet<>();
            if (flowPageCodeEnum == FlowPageCodeEnum.KEY_WORK_HANDLE) {
                //未完成并且在股道上的
                List<String> planLessKeyTrainsetIds = flowRunService.getFlowRunList(startDataTime, endDataTime, unitCode, BasicFlowTypeEnum.PLANLESS_KEY.getValue());
                trainsetIdList.addAll(planLessKeyTrainsetIds);
            } else if (flowPageCodeEnum == FlowPageCodeEnum.KEY_WORK_INPUT || flowPageCodeEnum == FlowPageCodeEnum.FAULT_TO_KEY_WORK) {
                //显示所有股道上的车组
                trainsetIdList.addAll(trainsetPostIonCurs.stream().map(v -> v.getTrainsetId()).collect(Collectors.toList()));
            } else {
                Map<String, List<ZtTaskPacketEntity>> groupedTaskPacketByTrainsetId = taskPacketEntityList.stream().collect(Collectors.groupingBy(ZtTaskPacketEntity::getTrainsetId));
                List<String> filteredTrainsetIds = groupedTaskPacketByTrainsetId.keySet().stream().filter(
                    trainsetId -> {
                        List<ZtTaskPacketEntity> taskPacketsOnCurrentTrainset = groupedTaskPacketByTrainsetId.get(trainsetId);
                        return flowPageCodeEnum.match(taskPacketsOnCurrentTrainset);
                    }
                ).collect(Collectors.toList());
                trainsetIdList.addAll(filteredTrainsetIds);
            }

        } else {
            Set<String> extraFlowPageFlowTypes = FlowDatabaseConfigUtil.getExtraFlowPageFlowTypes(flowPageCode);
            List<BaseFlowType> flowTypeList = flowTypeService.getFlowTypeList(unitCode);
            List<BaseFlowType> filteredFlowTypeList = flowTypeList.stream().filter(baseFlowType -> extraFlowPageFlowTypes.contains(baseFlowType.getCode())).collect(Collectors.toList());
            Set<BasicFlowTypeEnum> basicFlowTypeEnums = new HashSet<>();
            for (BaseFlowType baseFlowType : filteredFlowTypeList) {
                if (baseFlowType instanceof FlowType) {
                    basicFlowTypeEnums.add(BasicFlowTypeEnum.from(baseFlowType.getCode()));
                } else if (baseFlowType instanceof ExtraFlowType) {
                    ExtraFlowType extraFlowType = (ExtraFlowType) baseFlowType;
                    basicFlowTypeEnums.add(BasicFlowTypeEnum.from(extraFlowType.getParentFlowTypeCode()));
                }
            }

            Map<String, List<ZtTaskPacketEntity>> groupedTaskPacketByTrainsetId = taskPacketEntityList.stream().collect(Collectors.groupingBy(ZtTaskPacketEntity::getTrainsetId));
            trainsetIdList = groupedTaskPacketByTrainsetId.keySet().stream().filter(
                trainsetId -> {
                    List<ZtTaskPacketEntity> taskPacketsOnCurrentTrainset = groupedTaskPacketByTrainsetId.get(trainsetId);
                    return basicFlowTypeEnums.stream().anyMatch(
                        basicFlowTypeEnum -> basicFlowTypeEnum.match(taskPacketsOnCurrentTrainset)
                    );
                }
            ).collect(Collectors.toSet());
        }
        //过滤没有在股道上的车
        trainsetPostIonCurs = trainsetPostIonCurs.stream().filter(v -> trainsetIdList.contains(v.getTrainsetId())).collect(Collectors.toList());
        List<TrainsetWithTaskPacket> trainsetWithTaskPackets = new ArrayList<>();
        for (TrainsetPostionCur trainsetPostIonCur : trainsetPostIonCurs) {
            TrainsetWithTaskPacket trainsetWithTaskPacket = new TrainsetWithTaskPacket();
            BeanUtils.copyProperties(trainsetPostIonCur, trainsetWithTaskPacket);
            trainsetWithTaskPacket.setZtTaskPacketEntities(taskPacketEntityList.stream().filter(v -> v.getTrainsetId().equals(trainsetPostIonCur.getTrainsetId())).collect(Collectors.toList()));
            trainsetWithTaskPackets.add(trainsetWithTaskPacket);
        }


        Map<String, List<ZtTrackPositionEntity>> trackPositionInfoMap = new HashMap();

        List<ZtTrackAreaEntity> trackAreaEntityList = CacheUtil.getDataUseThreadCache(
            "remoteService.getTrackAreaByDept_" + unitCode,
            () -> remoteService.getTrackAreaByDept(unitCode)
        );
        trackAreaEntityList.forEach(trackAreaEntity -> {
            trackAreaEntity.getLstTrackInfo().forEach(trackInfo -> {
                trackPositionInfoMap.put(String.valueOf(trackInfo.getTrackCode()), trackInfo.getLstTrackPositionInfo());
            });
        });
        String monitorTrackPlaceShowType = ConfigUtil.getMonitorTrackPlaceShowType();
        if (monitorTrackPlaceShowType != null && monitorTrackPlaceShowType.equals("2")) {
            //显示方向名称还是列位编码
            trainsetWithTaskPackets.forEach(trainsetWithTaskPacket -> {
                List<ZtTrackPositionEntity> trackPositionEntitiesByTrackCode = trackPositionInfoMap.get(trainsetWithTaskPacket.getTrackCode());
                if (trackPositionEntitiesByTrackCode != null) {
                    ZtTrackPositionEntity head = trackPositionEntitiesByTrackCode.stream().filter(v -> v.getTrackPositionCode() == Integer.parseInt(trainsetWithTaskPacket.getHeadDirectionPlaCode())).collect(Collectors.toList()).get(0);
                    trainsetWithTaskPacket.setHeadDirectionPla(head.getDirectionCode());
                    ZtTrackPositionEntity tail = trackPositionEntitiesByTrackCode.stream().filter(v -> v.getTrackPositionCode() == Integer.parseInt(trainsetWithTaskPacket.getTailDirectionPlaCode())).collect(Collectors.toList()).get(0);
                    trainsetWithTaskPacket.setTailDirectionPla(tail.getDirectionCode());
                }
            });
        }
        List<TrainsetWithTaskPacket> result = JSON.parseArray(JSON.toJSONString(trainsetWithTaskPackets, SerializerFeature.DisableCircularReferenceDetect), TrainsetWithTaskPacket.class);
        return result;
    }


}
