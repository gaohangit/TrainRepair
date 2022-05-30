package com.ydzbinfo.emis.trainRepair.mobile.workprocess.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.ydzbinfo.emis.trainRepair.common.service.IRemoteService;
import com.ydzbinfo.emis.trainRepair.common.service.IRepairMidGroundService;
import com.ydzbinfo.emis.trainRepair.mobile.model.*;
import com.ydzbinfo.emis.trainRepair.mobile.workprocess.service.IPhoneOneWorkService;
import com.ydzbinfo.emis.trainRepair.mobile.workprocess.service.IPhoneTwoWorkService;
import com.ydzbinfo.emis.trainRepair.remotemodel.resume.TrainsetBaseInfo;
import com.ydzbinfo.emis.trainRepair.remotemodel.trainuse.ZtTaskPositionEntity;
import com.ydzbinfo.emis.trainRepair.repairmanagement.querymodel.XzyCWorkcritertion;
import com.ydzbinfo.emis.trainRepair.repairmanagent.service.IXzyCWorkcritertionService;
import com.ydzbinfo.emis.trainRepair.taskAllot.querymodel.TaskAllotItemEntity;
import com.ydzbinfo.emis.trainRepair.taskAllot.querymodel.TaskAllotPacketEntity;
import com.ydzbinfo.emis.trainRepair.taskAllot.querymodel.XzyMTaskallotpacket;
import com.ydzbinfo.emis.trainRepair.taskAllot.querymodel.XzyMTaskcarpart;
import com.ydzbinfo.emis.trainRepair.trackPowerInfo.service.TrackPowerInfoService;
import com.ydzbinfo.emis.trainRepair.trainsetPostion.querymodel.TrackPowerEntity;
import com.ydzbinfo.emis.trainRepair.trainsetPostion.querymodel.TrainsetPositionEntity;
import com.ydzbinfo.emis.trainRepair.trainsetPostion.service.TrainsetPostionService;
import com.ydzbinfo.emis.trainRepair.workProcess.service.*;
import com.ydzbinfo.emis.trainRepair.workprocess.model.ProcessCarPartEntity;
import com.ydzbinfo.emis.trainRepair.workprocess.model.ProcessPacketEntity;
import com.ydzbinfo.emis.trainRepair.workprocess.model.ProcessPersonEntity;
import com.ydzbinfo.emis.trainRepair.workprocess.model.ProcessTimeRecordEntity;
import com.ydzbinfo.emis.trainRepair.workprocess.querymodel.*;
import com.ydzbinfo.emis.utils.StringUtils;
import com.ydzbinfo.emis.utils.mybatisplus.MybatisPlusUtils;
import com.ydzbinfo.emis.utils.mybatisplus.param.ColumnParam;
import com.ydzbinfo.emis.utils.mybatisplus.param.ColumnParamUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.util.*;
import java.util.stream.Collectors;

import static com.ydzbinfo.emis.utils.mybatisplus.MybatisPlusUtils.eqParam;
import static com.ydzbinfo.emis.utils.mybatisplus.MybatisPlusUtils.inParam;

/**
 * Description:
 * Author: wuyuechang
 * Create Date Time: 2021/5/6 9:52
 * Update Date Time: 2021/5/6 9:52
 *
 * @see
 */
@Service
@Slf4j
public class PhoneTwoWorkServiceImpl implements IPhoneTwoWorkService {

    @Autowired
    private TrainsetPostionService trainsetPostionService;

    @Autowired
    private IWorkProcessService workprocessService;

    @Autowired
    private IProcessPicService processPicService;

    @Autowired
    private IPhoneOneWorkService phoneOneWorkService;

    @Autowired
    private IProcessDeptService processDeptService;

    @Autowired
    private IProcessCarPartService processCarPartService;

    @Autowired
    private IProcessPacketService processPacketService;

    @Autowired
    private IRemoteService remoteService;

    @Autowired
    private IRepairMidGroundService midGroundService;

    @Autowired
    private IXzyCWorkcritertionService xzyCWorkcritertionService;

    @Autowired
    private TrackPowerInfoService trackPowerInfoService;

    @Autowired
    IProcessPersonService processPersonService;

    @Override
    public TwoWork getWorkList(String unitCode, String dayPlanID, String deptCode, List<String> staffIDs, List<String> trainsetIds) {
        List<String> deptCodeList = new ArrayList<>();
        deptCodeList.add(deptCode);
        List<TaskAllotPacketEntity> taskAllotData = midGroundService.getTaskAllotData(unitCode, dayPlanID, "1", trainsetIds, deptCodeList, staffIDs, null);
        //当前车组的车组id
        String trainsetID = taskAllotData.get(0).getTrainsetId();
        //当前车组的车组名称
        String trainsetName = taskAllotData.get(0).getTrainsetName();

        //获取作业标准配置
        List<XzyCWorkcritertion> xzyCWorkcritertionList = xzyCWorkcritertionService.getWorkcritertionLByTrainsetId(trainsetID);

        //查询图片
        List<ProcessPic> processPics = MybatisPlusUtils.selectList(
            processPicService,
            ColumnParamUtil.filterBlankParams(
                eqParam(ProcessPic::getDayplanId, dayPlanID),
                eqParam(ProcessPic::getTrainsetId, trainsetID),
                eqParam(ProcessPic::getUnitCode, unitCode),
                eqParam(ProcessPic::getWorkerId, staffIDs.get(0)),
                eqParam(ProcessPic::getWorkType, "2")
            )
        );

        //查询作业过程时间
        List<RfidCardSummary> rfidCardSummaries = phoneOneWorkService.getRfidCardSummaryTimeInfo(staffIDs.get(0),
            trainsetID, "1", null, dayPlanID);

        //查询动车位置
        List<String> trainsetNames = taskAllotData.stream().map(TaskAllotPacketEntity::getTrainsetName).distinct().collect(Collectors.toList());
        List<String> unitCodes = Collections.singletonList(unitCode);
        List<TrainsetPositionEntity> trainsetPositionList = trainsetPostionService.getTrainsetPostion(null, trainsetNames, unitCodes);




        TwoWork twoWork = new TwoWork();
        twoWork.setTrainsetId(trainsetID);
        twoWork.setTrainsetName(trainsetName);
        twoWork.setDayPlanId(dayPlanID);
        TrainsetBaseInfo trainsetBaseInfo = remoteService.getTrainsetBaseinfoByID(trainsetID);
        twoWork.setTrainsetType(trainsetBaseInfo.getTraintype());
        if (!CollectionUtils.isEmpty(trainsetPositionList)) {
            twoWork.setTrainsetPositionEntity(trainsetPositionList.get(0));
        }
        twoWork.setRfidCardSummaries(rfidCardSummaries);

        List<TwoWorkPacket> twoWorkPackets = new ArrayList<>();
        for (TaskAllotPacketEntity taskAllotDatum : taskAllotData) {
            //添加包信息
            TwoWorkPacket twoWorkPacket = new TwoWorkPacket();
            twoWorkPacket.setPacketCode(taskAllotDatum.getPacketCode());
            twoWorkPacket.setPacketName(taskAllotDatum.getPacketName());
            twoWorkPacket.setPacketType(taskAllotDatum.getPacketType());
            //项目列表
            List<TwoWorkCarPart> twoWorkCarParts = new ArrayList<>();
            List<TaskAllotItemEntity> taskAllotItemEntityList = taskAllotDatum.getTaskAllotItemEntityList();
            taskAllotItemEntityList.forEach(taskAllotItemEntity -> {
                TwoWorkCarPart twoWorkCarPart = new TwoWorkCarPart();
                twoWorkCarPart.setTrainsetId(trainsetID);
                twoWorkCarPart.setTrainsetName(trainsetName);
                twoWorkCarPart.setDayPlanID(dayPlanID);
                twoWorkCarPart.setUnitCode(unitCode);
                twoWorkCarPart.setUnitName(taskAllotDatum.getUnitName());
                twoWorkCarPart.setItemCode(taskAllotItemEntity.getItemCode());
                twoWorkCarPart.setItemName(taskAllotItemEntity.getItemName());
                twoWorkCarPart.setRepairType("1");
                twoWorkCarPart.setArrageType(taskAllotItemEntity.getArrageType());
                twoWorkCarPart.setItemPublished(taskAllotItemEntity.getPublishCode());
                twoWorkCarPart.setDataSource("1");
                twoWorkCarPart.setPartType(taskAllotItemEntity.getPartType());
                twoWorkCarPart.setPartName(taskAllotItemEntity.getPartName());
                twoWorkCarPart.setPartPosition(taskAllotItemEntity.getPartPosition());
                twoWorkCarPart.setRemark(taskAllotItemEntity.getRemark());
                twoWorkCarPart.setRepairMode(taskAllotItemEntity.getRepairMode());
                twoWorkCarPart.setCarNo(taskAllotItemEntity.getCarNo());
                twoWorkCarPart.setTrainsetType(taskAllotItemEntity.getTrainsetType());

                List<XzyCWorkcritertion> workcritertions = xzyCWorkcritertionList.stream().filter(xzyCWorkcritertion -> xzyCWorkcritertion.getsItemcode().equals(taskAllotItemEntity.getItemCode())).collect(Collectors.toList());
                if (!CollectionUtils.isEmpty(workcritertions)) {
                    twoWorkCarPart.setXzyCWorkcritertion(workcritertions.get(0));
                }
                List<ProcessPic> processPicList = processPics.stream().filter(pic -> taskAllotItemEntity.getItemCode().equals(pic.getItemCode()) && taskAllotItemEntity.getCarNo().equals(pic.getCarNo())).collect(Collectors.toList());
                if (!CollectionUtils.isEmpty(processPicList)) {
                    twoWorkCarPart.setProcessPics(processPicList);
                }
                twoWorkCarParts.add(twoWorkCarPart);
            });
            Map<String, List<TwoWorkCarPart>> twoWorkCarPartMap = twoWorkCarParts.stream().collect(Collectors.groupingBy(TwoWorkCarPart::getItemCode));
            for (String key : twoWorkCarPartMap.keySet()) {
                List<TwoWorkCarPart> twoWorkCarPartList = twoWorkCarPartMap.get(key);
                //查询已结束的项目
                List<NoMainCycPersonInfo> noMainCycPersonInfos = processCarPartService.selectCarPartEndItemList(trainsetID, dayPlanID, "1", key, staffIDs.get(0));
                //设置完成时间
                twoWorkCarPartList.forEach(twoWorkCarPart -> {
                    //设置作业状态
                    List<NoMainCycPersonInfo> filterNoMainCycPersonInfos = new ArrayList<>();
                    if (StringUtils.isNotBlank(twoWorkCarPart.getPartName())) {
                        filterNoMainCycPersonInfos = noMainCycPersonInfos.stream().filter(t -> twoWorkCarPart.getCarNo().equals(t.getCarNo()) || twoWorkCarPart.getPartName().equals(t.getPartName())).collect(Collectors.toList());
                    } else {
                        filterNoMainCycPersonInfos = noMainCycPersonInfos.stream().filter(t -> twoWorkCarPart.getCarNo().equals(t.getCarNo())).collect(Collectors.toList());
                    }
                    if (filterNoMainCycPersonInfos != null && filterNoMainCycPersonInfos.size() > 0) {
                        //设置项目开始作业时间
                        NoMainCycPersonInfo startInfo = filterNoMainCycPersonInfos.stream().filter(t -> "1".equals(t.getItemTimeState())).findFirst().orElse(null);
                        if(!ObjectUtils.isEmpty(startInfo)){
                            twoWorkCarPart.setStartTime(startInfo.getTime());
                        }
                        if (filterNoMainCycPersonInfos.stream().anyMatch(t -> "4".equals(t.getItemTimeState()))) {
                            twoWorkCarPart.setIsWork("4");
                            List<NoMainCycPersonInfo> finshList = filterNoMainCycPersonInfos.stream().filter(t -> "4".equals(t.getItemTimeState())).collect(Collectors.toList());
                            twoWorkCarPart.setFinishTime(finshList.stream().findFirst().get().getTime());
                        } else {
                            twoWorkCarPart.setIsWork(filterNoMainCycPersonInfos.stream().findFirst().get().getItemTimeState());
                        }
                    } else {
                        twoWorkCarPart.setIsWork("");
                    }
                });
            }
            twoWorkPacket.setTwoWorkCarParts(twoWorkCarParts);
            twoWorkPackets.add(twoWorkPacket);
        }

        List<TwoWorkPacket> packets = JSONArray.parseArray(JSON.toJSONString(twoWorkPackets, SerializerFeature.DisableCircularReferenceDetect), TwoWorkPacket.class);
        twoWork.setPacketList(packets);
        return twoWork;
    }

    @Override
    public List<TwoWork> getWorkTrainsetList(String unitCode, String dayPlanID, String deptCode, List<String> staffIDs) {
        List<String> deptCodeList = new ArrayList<>();
        deptCodeList.add(deptCode);
        List<TaskAllotPacketEntity> taskAllotData = midGroundService.getTaskAllotData(unitCode, dayPlanID, "1", new ArrayList<>(), deptCodeList, staffIDs, null);
        if (CollectionUtils.isEmpty(taskAllotData)) {
            return new ArrayList<>();
        }

        Map<String, List<TaskAllotPacketEntity>> map = taskAllotData.stream().collect(Collectors.groupingBy(TaskAllotPacketEntity::getTrainsetId));

        List<String> unitCodes = new ArrayList<>();
        unitCodes.add(unitCode);
        List<String> trainsetNames = taskAllotData.stream().map(TaskAllotPacketEntity::getTrainsetName).distinct().collect(Collectors.toList());
        List<TrainsetPositionEntity> trainsetPositionList = trainsetPostionService.getTrainsetPostion(null, trainsetNames, unitCodes);
        //如果没有股道则返回空
        if (CollectionUtils.isEmpty(trainsetPositionList)) {
            return new ArrayList<>();
        }
        List<TwoWork> twoWorks = new ArrayList<>();

        //遍历车组位置信息集合
        for (TrainsetPositionEntity trainsetPositionEntity : trainsetPositionList) {
            String trainsetId = trainsetPositionEntity.getTrainsetId();
            TwoWork twoWork = new TwoWork();
            List<ZtTaskPositionEntity> ztTaskPositionEntities = remoteService.getTrackPositionByTrackCode(trainsetPositionEntity.getTrackCode(), unitCode);
            for (ZtTaskPositionEntity ztTaskPositionEntity : ztTaskPositionEntities) {
                if (ztTaskPositionEntity.getTrackPositionCode().equals(trainsetPositionEntity.getHeadDirectionPlaCode())) {
                    trainsetPositionEntity.setHeadDirectionMode(ztTaskPositionEntity.getDirectionCode());
                } else if (ztTaskPositionEntity.getTrackPositionCode().equals(trainsetPositionEntity.getTailDirectionPlaCode())) {
                    trainsetPositionEntity.setTailDirectionMode(ztTaskPositionEntity.getDirectionCode());
                }
            }
            twoWork.setTrainsetId(trainsetId);
            twoWork.setTrainsetName(map.get(trainsetId).get(0).getTrainsetName());
            List<TrackPowerEntity> trackPowerEntityList;
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
            trainsetPositionEntity.setTrackPowerEntityList(trackPowerEntityList);
            twoWork.setPlanRepairTrack(stringBuilder.toString());
            twoWork.setTrainsetPositionEntity(trainsetPositionEntity);
            twoWorks.add(twoWork);
        }

        //设置排序
        List<TwoWork> twoWorkList = new ArrayList<>();
        List<TwoWork> twoWorkArrayList = new ArrayList<>();
        for (TwoWork twoWork : twoWorks) {
            if (StringUtils.isNotBlank(twoWork.getPlanRepairTrack())) {
                twoWorkList.add(twoWork);
            } else {
                twoWorkArrayList.add(twoWork);
            }
        }
        twoWorkList.sort(Comparator.comparing(TwoWork::getTrainsetName));
        twoWorkArrayList.sort(Comparator.comparing(TwoWork::getTrainsetName));
        twoWorkList.addAll(twoWorkArrayList);
        return twoWorkList;
    }

    @Override
    public void setPacketItemInfo(RfidProcessCarPartInfo rfidProcessCarPartInfo) {
        //根据车组id获取车型
        TrainsetBaseInfo trainsetBaseInfo = remoteService.getTrainsetBaseinfoByID(rfidProcessCarPartInfo.getTrainsetId());

        List<ProcessPacketEntity> processPacketEntities = new ArrayList<>();
        List<XzyMTaskallotpacket> packetList = rfidProcessCarPartInfo.getPacketList();
        for (XzyMTaskallotpacket xzyMTaskallotpacket : packetList) {
            ProcessPacketEntity processPacketEntity = new ProcessPacketEntity();
            processPacketEntity.setDeptCode(rfidProcessCarPartInfo.getDeptCode());
            processPacketEntity.setDeptName(rfidProcessCarPartInfo.getDeptName());
            processPacketEntity.setTrainsetId(rfidProcessCarPartInfo.getTrainsetId());
            processPacketEntity.setTrainsetName(rfidProcessCarPartInfo.getTrainsetName());
            processPacketEntity.setRecordCode(rfidProcessCarPartInfo.getDeptCode());
            processPacketEntity.setRecordName(rfidProcessCarPartInfo.getWorkerName());
            processPacketEntity.setRecordTime(new Date());
            processPacketEntity.setDayPlanId(rfidProcessCarPartInfo.getDayplanId());
            processPacketEntity.setUnitCode(rfidProcessCarPartInfo.getUnitCode());
            processPacketEntity.setUnitName(rfidProcessCarPartInfo.getUnitName());
            processPacketEntity.setPacketCode(xzyMTaskallotpacket.getPacketCode());
            processPacketEntity.setPacketType(xzyMTaskallotpacket.getPacketType());
            processPacketEntity.setPacketName(xzyMTaskallotpacket.getPacketName());
            List<XzyMTaskcarpart> taskcarpartList = xzyMTaskallotpacket.getTaskcarpartList();
            List<ProcessCarPartEntity> processCarPartEntities = new ArrayList<>();
            for (XzyMTaskcarpart xzyMTaskcarpart : taskcarpartList) {
                ProcessCarPartEntity processCarPartEntity = new ProcessCarPartEntity();
                processCarPartEntity.setRemark(xzyMTaskcarpart.getRemark());
                processCarPartEntity.setPartType(xzyMTaskcarpart.getPartType());
                processCarPartEntity.setPartName(xzyMTaskcarpart.getPartName());
                processCarPartEntity.setPartPosition(xzyMTaskcarpart.getPartPosition());
                processCarPartEntity.setRepairMode(xzyMTaskcarpart.getRepairMode());
                processCarPartEntity.setCarNo(xzyMTaskcarpart.getCarNo());
                processCarPartEntity.setTrainsetType(trainsetBaseInfo.getTraintype());
                processCarPartEntity.setItemType(xzyMTaskcarpart.getRepairType());
                processCarPartEntity.setPublishCode(rfidProcessCarPartInfo.getItemPublcished());
                processCarPartEntity.setArrageType(xzyMTaskcarpart.getArrageType());
                processCarPartEntity.setItemCode(xzyMTaskcarpart.getItemCode());
                processCarPartEntity.setItemName(xzyMTaskcarpart.getItemName());
                processCarPartEntity.setDataSource("1");

                List<ProcessPersonEntity> processPersonEntities = new ArrayList<>();
                ProcessPersonEntity processPersonEntity = new ProcessPersonEntity();
                processPersonEntity.setWorkerId(rfidProcessCarPartInfo.getStuffId());
                processPersonEntity.setWorkerType(rfidProcessCarPartInfo.getWorkerType());
                processPersonEntity.setWorkerName(rfidProcessCarPartInfo.getWorkerName());
                List<ProcessTimeRecordEntity> processTimeRecordEntityList = new ArrayList<>();
                ProcessTimeRecordEntity processTimeRecordStartEntity = new ProcessTimeRecordEntity();
//                processTimeRecordStartEntity.setItemTimeState("1");
                processTimeRecordStartEntity.setItemTimeState(rfidProcessCarPartInfo.getItemTimeState());
                processTimeRecordStartEntity.setTime(rfidProcessCarPartInfo.getRepairTime());
                processTimeRecordEntityList.add(processTimeRecordStartEntity);
                processPersonEntity.setProcessTimeRecordEntityList(processTimeRecordEntityList);
                processPersonEntities.add(processPersonEntity);
                processCarPartEntity.setProcessPersonEntityList(processPersonEntities);
                processCarPartEntities.add(processCarPartEntity);
            }
            processPacketEntity.setProcessCarPartEntityList(processCarPartEntities);
            processPacketEntities.add(processPacketEntity);
        }
        midGroundService.addWorkProcess(processPacketEntities);
    }

    @Override
    public void setItemEnd(RfidProcessCarPartInfo rfidProcessCarPartInfo) {
        //库中含有的辆序部件
        List<ProcessCarPart> processCarPartList = MybatisPlusUtils.selectList(
            processCarPartService,
            ColumnParamUtil.filterBlankParams(
                eqParam(ProcessCarPart::getTrainsetId, rfidProcessCarPartInfo.getTrainsetId()),
                eqParam(ProcessCarPart::getDayPlanId, rfidProcessCarPartInfo.getDayplanId()),
                eqParam(ProcessCarPart::getRepairType, rfidProcessCarPartInfo.getRepairType())
            )
        );

        //结束的辆序部件
        List<String> processIds = new ArrayList<>();
        List<XzyMTaskcarpart> xzyMTaskcarparts = rfidProcessCarPartInfo.getPacketList().stream().flatMap(t -> t.getTaskcarpartList().stream()).collect(Collectors.toList());
        for (XzyMTaskcarpart taskcarpart : xzyMTaskcarparts) {
            List<ProcessCarPart> processCarParts = processCarPartList.stream().filter(processCarPart -> processCarPart.getItemCode().equals(taskcarpart.getItemCode())
                && processCarPart.getCarNo().equals(taskcarpart.getCarNo())).collect(Collectors.toList());
            if (!CollectionUtils.isEmpty(processCarParts)) {
                ProcessCarPart processCarPart = processCarParts.get(0);
                processIds.add(processCarPart.getProcessId());
            }
        }

        //查询作业人员
        List<ProcessPerson> processPeople = MybatisPlusUtils.selectList(
            processPersonService,
            inParam(ProcessPerson::getProcessId, processIds),
            eqParam(ProcessPerson::getWorkerId, rfidProcessCarPartInfo.getStuffId()),
            eqParam(ProcessPerson::getWorkerType, "1")
        );

        //记录时间
        List<ProcessTimeRecord> processTimeRecords = new ArrayList<>();
        for (ProcessPerson processPerson : processPeople) {
            ProcessTimeRecord processTimeRecord = new ProcessTimeRecord();
            processTimeRecord.setTime(rfidProcessCarPartInfo.getRepairTime());
            processTimeRecord.setItemTimeState("4");
            processTimeRecord.setProcessPersonId(processPerson.getProcessPersonId());
            processTimeRecord.setFlag("1");
            processTimeRecord.setRecordTime(new Date());
            processTimeRecord.setRecorderName(rfidProcessCarPartInfo.getWorkerName());
            processTimeRecord.setRecorderCode(rfidProcessCarPartInfo.getDeptCode());
            processTimeRecords.add(processTimeRecord);
        }
        workprocessService.addProcessTimeRecordList(processTimeRecords);
    }

    @Override
    public void setRfIdInfo(List<ProcessLocation> locations) {
        locations.forEach(location -> {
                location.setLocationId(UUID.randomUUID().toString());
                location.setCreateTime(new Date());
            }
        );
        workprocessService.addProcessLocationList(locations);
    }
}
