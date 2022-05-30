package com.ydzbinfo.emis.trainRepair.mobile.workprocess.service.impl;

import com.ydzbinfo.emis.trainRepair.common.service.IRemoteService;
import com.ydzbinfo.emis.trainRepair.common.service.IRepairMidGroundService;
import com.ydzbinfo.emis.trainRepair.mobile.model.NoMainCyc;
import com.ydzbinfo.emis.trainRepair.mobile.model.NoMainCycPacketInfo;
import com.ydzbinfo.emis.trainRepair.mobile.model.NoMainCycPersonInfo;
import com.ydzbinfo.emis.trainRepair.mobile.workprocess.service.IPhoneNoMainCycWorkService;
import com.ydzbinfo.emis.trainRepair.remotemodel.resume.TrainsetInfo;
import com.ydzbinfo.emis.trainRepair.remotemodel.trainuse.ZtTaskPacketEntity;
import com.ydzbinfo.emis.trainRepair.trackPowerInfo.service.TrackPowerInfoService;
import com.ydzbinfo.emis.trainRepair.trainsetPostion.querymodel.TrackPowerEntity;
import com.ydzbinfo.emis.trainRepair.trainsetPostion.querymodel.TrainsetPositionEntity;
import com.ydzbinfo.emis.trainRepair.trainsetPostion.service.TrainsetPostionService;
import com.ydzbinfo.emis.trainRepair.workProcess.service.IProcessCarPartService;
import com.ydzbinfo.emis.trainRepair.workProcess.service.IWorkProcessService;
import com.ydzbinfo.emis.trainRepair.workprocess.model.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

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
public class PhoneNoMainCycWorkServiceImpl implements IPhoneNoMainCycWorkService {

    @Autowired
    private IProcessCarPartService processCarPartService;

    @Autowired
    private IRemoteService remoteService;

    @Autowired
    private IRepairMidGroundService midGroundService;

    @Autowired
    private TrainsetPostionService trainsetPostionService;

    @Autowired
    private TrackPowerInfoService trackPowerInfoService;

    @Autowired
    private IWorkProcessService workProcessService;

    @Override
    public List<NoMainCyc> getWorkList(String unitCode, String dayPlanID, String workerType) {
        List<NoMainCyc> noMainCycs = new ArrayList<>();
        List<ZtTaskPacketEntity> ztTaskPacketEntities = remoteService.getPacketTaskByDayplanId(dayPlanID, unitCode);
        Map<String, List<ZtTaskPacketEntity>> ztTaskPacketEntityMap = ztTaskPacketEntities.stream().filter(ztTaskPacketEntity
            -> ztTaskPacketEntity.getPacketTypeCode().equals("6"))
            .collect(Collectors.groupingBy(ZtTaskPacketEntity::getTrainsetId));
        List<ZtTaskPacketEntity> taskPacketEntities = ztTaskPacketEntityMap.values().stream().flatMap(Collection::stream).collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(taskPacketEntities)) {
            List<String> itemCodes = taskPacketEntities.stream().map(ZtTaskPacketEntity::getPacketCode).collect(Collectors.toList());
            List<NoMainCycPersonInfo> noMainCycInfos = processCarPartService.selectCarPartItemList(ztTaskPacketEntityMap.keySet(), taskPacketEntities.get(0).getPacketTypeCode(), dayPlanID, workerType, itemCodes);
            //查询车组位置信息
            List<String> trainsetName = ztTaskPacketEntityMap.values().stream().flatMap(Collection::stream).map(ZtTaskPacketEntity::getTrainsetName).distinct().collect(Collectors.toList());
            List<String> unitCodes = new ArrayList<>();
            unitCodes.add(unitCode);
            List<TrainsetPositionEntity> trainsetPositionList = trainsetPostionService.getTrainsetPostion(null, trainsetName, unitCodes);

            //循环根据车组处理检修计划
            for (Map.Entry<String, List<ZtTaskPacketEntity>> stringListEntry : ztTaskPacketEntityMap.entrySet()) {
                NoMainCyc noMainCyc = new NoMainCyc();
                //获取编组数
                TrainsetInfo trainsetDetialInfo = remoteService.getTrainsetDetialInfo(stringListEntry.getKey());
                List<TrackPowerEntity> trackPowerEntityList;
                noMainCyc.setIMarshalcount(trainsetDetialInfo.getIMarshalcount());
                //遍历车组位置信息集合
                for (TrainsetPositionEntity trainsetPositionEntity : trainsetPositionList) {
                    if (trainsetPositionEntity.getTrainsetId().equals(stringListEntry.getKey())) {
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
                        noMainCyc.setTrackPowerEntityList(trackPowerEntityList);
                        noMainCyc.setPlanRepairTrack(stringBuilder.toString());
                    }
                }
                List<ZtTaskPacketEntity> ztTaskPacketEntityList = stringListEntry.getValue();
                List<NoMainCycPacketInfo> noMainCycPacketInfos = new ArrayList<>();
                //保存包信息
                for (ZtTaskPacketEntity ztTaskPacketEntity : ztTaskPacketEntityList) {
                    List<NoMainCycPersonInfo> collect = noMainCycInfos.stream()
                        .filter(t -> t.getItemCode().equals(ztTaskPacketEntity.getPacketCode()) && stringListEntry.getKey().equals(t.getTrainsetId()))
                        .sorted(Comparator.comparing(NoMainCycPersonInfo::getTime).reversed())
                        .collect(Collectors.toList());
                    NoMainCycPacketInfo noMainCycPacketInfo = new NoMainCycPacketInfo();
                    noMainCycPacketInfo.setPacketCode(ztTaskPacketEntity.getPacketCode());
                    noMainCycPacketInfo.setPacketName(ztTaskPacketEntity.getPacketName());
                    noMainCycPacketInfo.setPacketType(ztTaskPacketEntity.getPacketTypeCode());
                    noMainCycPacketInfo.setNoMainCycPersonInfos(collect);
                    noMainCycPacketInfos.add(noMainCycPacketInfo);
                }

                noMainCyc.setTrainsetId(stringListEntry.getKey());
                noMainCyc.setTrainsetName(stringListEntry.getValue().get(0).getTrainsetName());
                noMainCyc.setNoMainCycPacketInfos(noMainCycPacketInfos);
                noMainCycs.add(noMainCyc);
            }
        }
        return noMainCycs;
    }

    @Override
    public void setNoManCycInfo(NoMainCyc noMainCyc) {
        //把数据拼成pc接口需要的格式调用pc接口
        IntegrationProcessData integrationProcessData = new IntegrationProcessData();
        integrationProcessData.setDayPlanId(noMainCyc.getDayPlanId());
        integrationProcessData.setTrainsetId(noMainCyc.getTrainsetId());
        integrationProcessData.setTrainsetName(noMainCyc.getTrainsetName());
        //根据车组ID获取所有辆序
        List<String> carNoList = remoteService.getCarno(noMainCyc.getTrainsetId());
        List<String> carNos = new ArrayList<>();
        for (String carNoItem : carNoList) {
            //截取辆序字符串，只取最后两位
            String carNo = carNoItem.substring(carNoItem.length() - 2);
            carNos.add(carNo);
        }
        integrationProcessData.setCarNos(carNos);
        List<NoMainCycPacketInfo> noMainCycPacketInfos = noMainCyc.getNoMainCycPacketInfos();
        if (noMainCycPacketInfos.size() > 0) {
            String workerType = noMainCyc.getWorkerType();
            NoMainCycPacketInfo noMainCycPacketInfo = noMainCycPacketInfos.get(0);
            integrationProcessData.setItemCode(noMainCycPacketInfo.getPacketCode());
            integrationProcessData.setItemName(noMainCycPacketInfo.getPacketName());
            List<ProcessWorker> processWorkerList = new ArrayList<>();
            ProcessWorker processWorker = new ProcessWorker();
            processWorker.setWorkerId(noMainCyc.getWorkerId());
            processWorker.setWorkerName(noMainCyc.getWorkerName());
            processWorkerList.add(processWorker);
            if ("3".equals(workerType)) {
                integrationProcessData.setWorkCarCount(noMainCycPacketInfo.getCarNo());
                integrationProcessData.setWorkEndTime(noMainCycPacketInfo.getTime());
                integrationProcessData.setProcessWorkerList(processWorkerList);
                integrationProcessData.setDeptCode(noMainCyc.getDeptCode());
                integrationProcessData.setDeptName(noMainCyc.getDeptName());
            } else if ("4".equals(workerType)) {
                integrationProcessData.setConfirmCarCount(noMainCycPacketInfo.getCarNo());
                integrationProcessData.setConfirmDeptCode(noMainCyc.getDeptCode());
                integrationProcessData.setConfirmDeptName(noMainCyc.getDeptName());
                integrationProcessData.setConfirmEndTime(noMainCycPacketInfo.getTime());
                integrationProcessData.setProcessConfirmList(processWorkerList);
            }
        }
        workProcessService.addIntegration(integrationProcessData, "1");
        // List<ProcessPacketEntity> processPacketEntities = new ArrayList<>();
        // List<NoMainCycPacketInfo> noMainCycPacketInfos = noMainCyc.getNoMainCycPacketInfos();
        // for (NoMainCycPacketInfo noMainCycPacketInfo : noMainCycPacketInfos) {
        //     ProcessPacketEntity processPacketEntity = new ProcessPacketEntity();
        //     processPacketEntity.setDeptCode(noMainCyc.getDeptCode());
        //     processPacketEntity.setDeptName(noMainCyc.getDeptName());
        //     processPacketEntity.setTrainsetId(noMainCyc.getTrainsetId());
        //     processPacketEntity.setTrainsetName(noMainCyc.getTrainsetName());
        //     processPacketEntity.setRecordCode(noMainCyc.getDeptCode());
        //     processPacketEntity.setRecordName(noMainCyc.getWorkerName());
        //     processPacketEntity.setRecordTime(new Date());
        //     processPacketEntity.setDayPlanId(noMainCyc.getDayPlanId());
        //     processPacketEntity.setUnitCode(noMainCyc.getUnitCode());
        //     processPacketEntity.setUnitName(noMainCyc.getUnitName());
        //     processPacketEntity.setPacketCode(noMainCycPacketInfo.getPacketCode());
        //     processPacketEntity.setPacketType(noMainCycPacketInfo.getPacketType());
        //     processPacketEntity.setPacketName(noMainCycPacketInfo.getPacketName());
        //     List<ProcessCarPartEntity> processCarPartEntities = new ArrayList<>();
        //     ProcessCarPartEntity processCarPartEntity = new ProcessCarPartEntity();
        //     processCarPartEntity.setItemCode(noMainCycPacketInfo.getPacketCode());
        //     processCarPartEntity.setItemName(noMainCycPacketInfo.getPacketName());
        //     processCarPartEntity.setPublishCode(noMainCycPacketInfo.getItemPublicShed());
        //     processCarPartEntity.setItemType(noMainCycPacketInfo.getPacketType());
        //     processCarPartEntity.setDataSource("1");
        //     List<ProcessPersonEntity> processPersonEntities = new ArrayList<>();
        //     ProcessPersonEntity processPersonEntity = new ProcessPersonEntity();
        //     processPersonEntity.setWorkerId(noMainCyc.getWorkerId());
        //     processPersonEntity.setWorkerType(noMainCyc.getWorkerType());
        //     processPersonEntity.setWorkerName(noMainCyc.getWorkerName());
        //     processPersonEntity.setCarNoCount(noMainCycPacketInfo.getCarNo());
        //     List<ProcessTimeRecordEntity> processTimeRecordEntityList = new ArrayList<>();
        //     ProcessTimeRecordEntity processTimeRecordEntity = new ProcessTimeRecordEntity();
        //     processTimeRecordEntity.setItemTimeState("2");
        //     processTimeRecordEntity.setTime(new Date());
        //     processTimeRecordEntityList.add(processTimeRecordEntity);
        //     processPersonEntity.setProcessTimeRecordEntityList(processTimeRecordEntityList);
        //     processPersonEntities.add(processPersonEntity);
        //     processCarPartEntity.setProcessPersonEntityList(processPersonEntities);
        //     processCarPartEntities.add(processCarPartEntity);
        //     processPacketEntity.setProcessCarPartEntityList(processCarPartEntities);
        //     processPacketEntities.add(processPacketEntity);
        //     midGroundService.addWorkProcess(processPacketEntities);
        // }
    }
}


