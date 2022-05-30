package com.ydzbinfo.emis.trainRepair.mobile.workprocess.service.impl;

import com.ydzbinfo.emis.trainRepair.common.model.ConfigParamsModel;
import com.ydzbinfo.emis.trainRepair.common.querymodel.XzyCConfig;
import com.ydzbinfo.emis.trainRepair.common.service.IRemoteService;
import com.ydzbinfo.emis.trainRepair.common.service.IRepairMidGroundService;
import com.ydzbinfo.emis.trainRepair.mobile.model.OneWork;
import com.ydzbinfo.emis.trainRepair.mobile.model.OneWorkCarPart;
import com.ydzbinfo.emis.trainRepair.mobile.model.RfidProcessCarPartInfo;
import com.ydzbinfo.emis.trainRepair.mobile.workprocess.service.IPhoneOneWorkService;
import com.ydzbinfo.emis.trainRepair.remotemodel.resume.TrainsetBaseInfo;
import com.ydzbinfo.emis.trainRepair.remotemodel.trainuse.ZtTaskPositionEntity;
import com.ydzbinfo.emis.trainRepair.repairmanagement.querymodel.XzyCWorkcritertion;
import com.ydzbinfo.emis.trainRepair.repairmanagement.querymodel.XzyCWorkcritertionPost;
import com.ydzbinfo.emis.trainRepair.repairmanagement.querymodel.XzyCWorkritertionPriRole;
import com.ydzbinfo.emis.trainRepair.repairmanagent.service.IXzyCWorkcritertionService;
import com.ydzbinfo.emis.trainRepair.taskAllot.querymodel.TaskAllotPacketEntity;
import com.ydzbinfo.emis.trainRepair.taskAllot.querymodel.TaskAllotPersonEntity;
import com.ydzbinfo.emis.trainRepair.taskAllot.querymodel.TaskAllotPersonPostEntity;
import com.ydzbinfo.emis.trainRepair.trackPowerInfo.service.TrackPowerInfoService;
import com.ydzbinfo.emis.trainRepair.trainsetPostion.querymodel.TrackPowerEntity;
import com.ydzbinfo.emis.trainRepair.trainsetPostion.querymodel.TrainsetPositionEntity;
import com.ydzbinfo.emis.trainRepair.trainsetPostion.service.TrainsetPostionService;
import com.ydzbinfo.emis.trainRepair.warnmanagent.querymodel.WorkWorning;
import com.ydzbinfo.emis.trainRepair.warnmanagent.service.IWorkWorningService;
import com.ydzbinfo.emis.trainRepair.workProcess.service.*;
import com.ydzbinfo.emis.trainRepair.workprocess.model.*;
import com.ydzbinfo.emis.trainRepair.workprocess.querymodel.ProcessCarPart;
import com.ydzbinfo.emis.trainRepair.workprocess.querymodel.ProcessLocation;
import com.ydzbinfo.emis.trainRepair.workprocess.querymodel.ProcessPic;
import com.ydzbinfo.emis.trainRepair.workprocess.querymodel.RfidCardSummary;
import com.ydzbinfo.emis.utils.StringUtils;
import com.ydzbinfo.emis.utils.mybatisplus.MybatisPlusUtils;
import com.ydzbinfo.emis.utils.mybatisplus.param.ColumnParam;
import com.ydzbinfo.emis.utils.mybatisplus.param.ColumnParamUtil;
import com.ydzbinfo.emis.utils.mybatisplus.param.OrderBy;
import com.ydzbinfo.emis.utils.upload.UpLoadFileUtils;
import com.ydzbinfo.emis.utils.upload.UploadedFileInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import static com.ydzbinfo.emis.utils.mybatisplus.MybatisPlusUtils.eqParam;
import static com.ydzbinfo.emis.utils.mybatisplus.MybatisPlusUtils.orderBy;

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
public class PhoneOneWorkServiceImpl implements IPhoneOneWorkService {

    @Autowired
    private TrainsetPostionService trainsetPostionService;

    @Autowired
    private TrackPowerInfoService trackPowerInfoService;

    @Autowired
    private IWorkProcessService workprocessService;

    @Autowired
    private IProcessPicService processPicService;

    @Autowired
    private IRfidCardSummaryService rfidCardSummaryService;

    @Autowired
    private IProcessCarPartService processCarPartService;

    @Autowired
    private IRepairMidGroundService midGroundService;

    @Autowired
    private IRemoteService remoteService;

    @Autowired
    private IXzyCWorkcritertionService xzyCWorkcritertionService;

    @Autowired
    private IProcessLocationService processLocationService;

    @Autowired
    private IWorkWorningService workWorningService;

    @Value("${server.port}")
    private String port;

    @Override
    public OneWork getWorkList(String unitCode, String dayPlanID, String deptCode, List<String> staffIDs, List<String> trainsetIds, List<String> roleNames, String itemCode) {
        List<XzyCConfig> isUserPhotos = midGroundService.getXzyCConfigs(ConfigParamsModel.builder().type("7").name("IsUsePhoto").build());
        List<String> deptCodeList = new ArrayList<>();
        deptCodeList.add(deptCode);
        List<TaskAllotPacketEntity> taskAllotData = midGroundService.getTaskAllotData(unitCode, dayPlanID, "0", trainsetIds, deptCodeList, staffIDs, itemCode);
        OneWork oneWork = new OneWork();
        if (taskAllotData.size() > 0) {
            //当前车组的车组id
            String trainsetID = taskAllotData.get(0).getTrainsetId();
            //当前车组的车组名称
            String trainsetName = taskAllotData.get(0).getTrainsetName();

            //查询作业标准配置表
            List<XzyCWorkcritertion> xzyCWorkcritertionList = xzyCWorkcritertionService.getWorkcritertionLByTrainsetId(trainsetID);

            List<String> carnos = remoteService.getCarno(trainsetID);
            List<String> carNoList = carnos.stream().map(c -> c.substring(c.length() - 2, c.length())).collect(Collectors.toList());

            //查询图片
            List<ProcessPic> processPics = MybatisPlusUtils.selectList(
                processPicService,
                ColumnParamUtil.filterBlankParams(
                    eqParam(ProcessPic::getDayplanId, dayPlanID),
                    eqParam(ProcessPic::getTrainsetId, trainsetID),
                    eqParam(ProcessPic::getUnitCode, taskAllotData.get(0).getUnitCode()),
                    eqParam(ProcessPic::getWorkerId, staffIDs.get(0)),
                    eqParam(ProcessPic::getWorkType, "1"),
                    eqParam(ProcessPic::getItemCode,itemCode)
                )
            );

            //查询作业过程时间
            List<RfidCardSummary> rfidCardSummaries = getRfidCardSummaryTimeInfo(staffIDs.get(0),
                trainsetID, "0", itemCode, dayPlanID);

            //查询动车位置
            List<String> trainsetNames = taskAllotData.stream().map(TaskAllotPacketEntity::getTrainsetName).distinct().collect(Collectors.toList());
            List<String> unitCodes = Collections.singletonList(unitCode);
            List<TrainsetPositionEntity> trainsetPositionList = trainsetPostionService.getTrainsetPostion(null, trainsetNames, unitCodes);

            //一级修作业过程开始模式
            List<XzyCConfig> xzyCConfigWorkModes = midGroundService.getXzyCConfigs(ConfigParamsModel.builder().type("7").name("OneProcessWorkMode").build());


            oneWork.setIsUsePhoto(isUserPhotos.get(0).getParamValue());
            oneWork.setTrainsetId(trainsetID);
            oneWork.setTrainsetName(trainsetName);
            oneWork.setDayPlanId(dayPlanID);
            oneWork.setProcessPics(processPics);
            oneWork.setTrainsetCarNoList(carNoList);
            if (!CollectionUtils.isEmpty(trainsetPositionList)) {
                oneWork.setTrainsetPositionEntity(trainsetPositionList.get(0));
            }
            oneWork.setRfidCardSummaries(rfidCardSummaries);
            List<XzyCWorkcritertion> workcritertion = xzyCWorkcritertionList.stream().filter(w -> w.getsItemcode().equals(itemCode)).collect(Collectors.toList());
            if(workcritertion.size() > 0){
                oneWork.setXzyCWorkcritertion(workcritertion.get(0));
            }
            //添加定位信息
            List<ProcessLocation> locations = MybatisPlusUtils.selectList(
                processLocationService,
                ColumnParamUtil.filterBlankParams(
                    eqParam(ProcessLocation::getDayplanId, dayPlanID),
                    eqParam(ProcessLocation::getTrainsetId, trainsetID),
                    eqParam(ProcessLocation::getItemCode, workcritertion.get(0).getsItemcode()),
                    eqParam(ProcessLocation::getWorkerId, staffIDs.get(0)),
                    eqParam(ProcessLocation::getWorkType, "1")
                )
            );
            oneWork.setProcessLocations(locations);

            List<ProcessCarPart> processCarParts = MybatisPlusUtils.selectList(
                processCarPartService,
                ColumnParamUtil.filterBlankParams(
                    eqParam(ProcessCarPart::getTrainsetId, trainsetID),
                    eqParam(ProcessCarPart::getDayPlanId, dayPlanID),
                    eqParam(ProcessCarPart::getRepairType, "0"),
                    eqParam(ProcessCarPart::getItemCode, itemCode),
                    eqParam(ProcessCarPart::getUnitCode, unitCode)
                )
            );

            List<OneWorkCarPart> oneWorkCarParts = new ArrayList<>();
            for (ProcessCarPart processCarPart : processCarParts) {
                OneWorkCarPart oneWorkCarPart = new OneWorkCarPart();
                BeanUtils.copyProperties(processCarPart, oneWorkCarPart);
                oneWorkCarPart.setDayPlanID(processCarPart.getDayPlanId());
                oneWorkCarPart.setTrainsetId(processCarPart.getTrainsetId());
                oneWorkCarPart.setTrainsetName(processCarPart.getTrainsetName());
                oneWorkCarPart.setTrainsetType(processCarPart.getTrainsetType());
                oneWorkCarParts.add(oneWorkCarPart);
            }
            oneWork.setItemList(oneWorkCarParts);
            for (TaskAllotPacketEntity taskAllotDatum : taskAllotData) {
                if (!CollectionUtils.isEmpty(xzyCConfigWorkModes) && "0".equals(xzyCConfigWorkModes.get(0).getParamValue())) {
                    //获取人员集合
                    List<TaskAllotPersonEntity> taskAllotPersonEntities = taskAllotDatum.getTaskAllotItemEntityList().stream().flatMap(t -> t.getTaskAllotPersonEntityList().stream()).collect(Collectors.toList());
                    //根据workerId去重
                    List<TaskAllotPersonEntity> taskAllotPersonEntityList = taskAllotPersonEntities.stream().collect(Collectors.collectingAndThen(
                        Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(TaskAllotPersonEntity::getWorkerId))), ArrayList::new));
                    taskAllotPersonEntityList.forEach(taskAllotPersonEntity -> {
                        //当前人的岗位信息
                        List<TaskAllotPersonPostEntity> taskAllotPersonPostEntityList = taskAllotPersonEntity.getTaskAllotPersonPostEntityList();
                        List<XzyCWorkritertionPriRole> xzyCWorkritertionPriRoleList = workcritertion.get(0).getXzyCWorkritertionPriRoleList();
                        for (XzyCWorkritertionPriRole xzyCWorkritertionPriRole : xzyCWorkritertionPriRoleList) {
                            if ("1".equals(xzyCWorkritertionPriRole.getType()) && taskAllotPersonPostEntityList.stream().anyMatch(t -> t.getPostName().equals(xzyCWorkritertionPriRole.getPriRoleName()))) {
                                oneWork.setIsBegin("1");
                                break;
                            } else if ("2".equals(xzyCWorkritertionPriRole.getType()) && roleNames.stream().anyMatch(t -> t.equals(xzyCWorkritertionPriRole.getPriRoleName()))) {
                                oneWork.setIsBegin("1");
                                break;
                            }
                        }
                    });
                } else {
                    oneWork.setIsBegin("1");
                }
            }
        }
        return oneWork;
    }

    @Override
    public List<RfidCardSummary> getRfidCardSummaryTimeInfo(String staffId, String trainsetId, String repairType, String itemCode, String dayplanId) {
        List<ColumnParam<RfidCardSummary>> columnParamList = ColumnParamUtil.filterBlankParamList(
                eqParam(RfidCardSummary::getDayPlanId, dayplanId),
                eqParam(RfidCardSummary::getTrainsetId, trainsetId),
                eqParam(RfidCardSummary::getRepairType, repairType),
                eqParam(RfidCardSummary::getStuffId, staffId),
                eqParam(RfidCardSummary::getItemCode, itemCode),
                eqParam(RfidCardSummary::getFlag,"1")
        );
        List<OrderBy<RfidCardSummary>> orderByList = Collections.singletonList(
                orderBy(RfidCardSummary::getRepairTime, true)
        );

        return MybatisPlusUtils.selectList(
                rfidCardSummaryService,
                columnParamList,
                orderByList
        );
    }

    @Override
    public void setRfIdInfo(RfidProcessCarPartInfo rfidProcessCarPartInfo) {
        //根据车组id获取车型
        TrainsetBaseInfo trainsetBaseInfo = remoteService.getTrainsetBaseinfoByID(rfidProcessCarPartInfo.getTrainsetId());

        List<ProcessPacketEntity> processPacketEntities = new ArrayList<>();
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
        processPacketEntity.setPacketCode(rfidProcessCarPartInfo.getPacketCode());
        processPacketEntity.setPacketType(rfidProcessCarPartInfo.getPacketType());
        processPacketEntity.setPacketName(rfidProcessCarPartInfo.getPacketName());
        List<ProcessCarPartEntity> processCarPartEntities = new ArrayList<>();
        ProcessCarPartEntity processCarPartEntity = new ProcessCarPartEntity();
        processCarPartEntity.setRemark(rfidProcessCarPartInfo.getRemark());
        processCarPartEntity.setPartType(rfidProcessCarPartInfo.getPartType());
        processCarPartEntity.setPartName(rfidProcessCarPartInfo.getPartName());
        processCarPartEntity.setPartPosition(rfidProcessCarPartInfo.getPartPosition());
        processCarPartEntity.setRepairMode(rfidProcessCarPartInfo.getRepairMode());
        processCarPartEntity.setCarNo(rfidProcessCarPartInfo.getCarNo());
        processCarPartEntity.setTrainsetType(trainsetBaseInfo.getTraintype());
        processCarPartEntity.setItemType(rfidProcessCarPartInfo.getRepairType());
        processCarPartEntity.setPublishCode(rfidProcessCarPartInfo.getItemPublcished());
        processCarPartEntity.setArrageType(rfidProcessCarPartInfo.getArrageType());
        processCarPartEntity.setItemCode(rfidProcessCarPartInfo.getItemCode());
        processCarPartEntity.setItemName(rfidProcessCarPartInfo.getItemName());
        processCarPartEntity.setDataSource(rfidProcessCarPartInfo.getDataSource());
        List<ProcessPersonEntity> processPersonEntities = new ArrayList<>();
        ProcessPersonEntity processPersonEntity = new ProcessPersonEntity();
        processPersonEntity.setWorkerId(rfidProcessCarPartInfo.getStuffId());
        processPersonEntity.setWorkerType(rfidProcessCarPartInfo.getWorkerType());
        processPersonEntity.setWorkerName(rfidProcessCarPartInfo.getWorkerName());
        List<ProcessTimeRecordEntity> processTimeRecordEntityList = new ArrayList<>();
        ProcessTimeRecordEntity processTimeRecordStartEntity = new ProcessTimeRecordEntity();
        processTimeRecordStartEntity.setItemTimeState("1");
        processTimeRecordStartEntity.setTime(rfidProcessCarPartInfo.getRepairTime());
        ProcessTimeRecordEntity processTimeRecordEndEntity = new ProcessTimeRecordEntity();
        processTimeRecordEndEntity.setItemTimeState("4");
        processTimeRecordEndEntity.setTime(rfidProcessCarPartInfo.getRepairTime());
        processTimeRecordEntityList.add(processTimeRecordStartEntity);
        processTimeRecordEntityList.add(processTimeRecordEndEntity);
        processPersonEntity.setProcessTimeRecordEntityList(processTimeRecordEntityList);
        processPersonEntities.add(processPersonEntity);
        processCarPartEntity.setProcessPersonEntityList(processPersonEntities);
        processCarPartEntities.add(processCarPartEntity);
        processPacketEntity.setProcessCarPartEntityList(processCarPartEntities);
        processPacketEntities.add(processPacketEntity);
        midGroundService.addWorkProcess(processPacketEntities);
        //添加定位
        List<ProcessLocation> processLocations = getProcessLocations(rfidProcessCarPartInfo);
        if (!CollectionUtils.isEmpty(processLocations)) {
            processLocationService.insertBatch(processLocations);
        }
    }

    @Override
    public void updateTime(RfidProcessCarPartInfo rfidProcessCarPartInfo) {
        //拼装作业过程开始结束时间记录
        List<ProcessSummaryEntity> processSummaryEntityList = new ArrayList<>();
        ProcessSummaryEntity rfidCardSummary = new ProcessSummaryEntity();
        BeanUtils.copyProperties(rfidProcessCarPartInfo, rfidCardSummary);
        rfidCardSummary.setRecordId(UUID.randomUUID().toString());
        rfidCardSummary.setStuffName(rfidProcessCarPartInfo.getWorkerName());
        rfidCardSummary.setDayPlanId(rfidProcessCarPartInfo.getDayplanId());
        rfidCardSummary.setRecorderCode(rfidProcessCarPartInfo.getDeptCode());
        rfidCardSummary.setRecorderName(rfidProcessCarPartInfo.getWorkerName());
        rfidCardSummary.setRecordTime(new Date());
        rfidCardSummary.setUnitCode(rfidProcessCarPartInfo.getUnitCode());
        rfidCardSummary.setTrainsetType(rfidProcessCarPartInfo.getTrainsetType());
        rfidCardSummary.setDataSource("1"); //数据来源，1手持机
        processSummaryEntityList.add(rfidCardSummary);
        midGroundService.addSummary(processSummaryEntityList);
    }

    @Override
    public void uploadImage(ProcessPic processPic, HttpServletRequest multipartHttpServletRequest) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String dir = sdf.format(new Date()) + "/"
                + processPic.getDayplanId() + "/"
                + processPic.getTrainsetId() + "/"
                + processPic.getCarNo();
        List<UploadedFileInfo> uploadedFileInfos = UpLoadFileUtils.uploadImages(multipartHttpServletRequest, dir);
        List<ProcessPic> processPics = new ArrayList<>();
        uploadedFileInfos.forEach(uploadedFileInfo -> {
            ProcessPic pic = new ProcessPic();
            BeanUtils.copyProperties(processPic, pic);
            pic.setPicId(UUID.randomUUID().toString());
            pic.setCreateTime(new Date());
            pic.setPicName(uploadedFileInfo.getOldName());
            pic.setPicAddress(uploadedFileInfo.getRelativeUrl());
            pic.setPicType("1");
            processPics.add(pic);
        });
        workprocessService.addProcessPicList(processPics);
    }

    @Override
    public void removeImage(String picId) {
        processPicService.deleteById(picId);
    }

    @Override
    public List<OneWork> getWorkTrainsetList(String unitCode, String dayPlanID, String deptCode, List<String> workerIds) {
        List<TaskAllotPacketEntity> taskAllotData = new ArrayList<>();
        try {
            List<String> deptCodeList = new ArrayList<>();
            deptCodeList.add(deptCode);
            taskAllotData = midGroundService.getTaskAllotData(unitCode, dayPlanID, "0", new ArrayList<>(), deptCodeList, workerIds, null);
        } catch (Exception e) {
            log.error("获取检修计划接口异常：getTaskAllotData", e);
        }
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

        //一级修作业过程号位模式
        List<XzyCConfig> xzyCConfigPostModes = midGroundService.getXzyCConfigs(ConfigParamsModel.builder().type("7").name("OneProcessPostMode").build());

        List<OneWork> oneWorks = new ArrayList<>();

        //遍历车组位置信息集合
        for (TrainsetPositionEntity trainsetPositionEntity : trainsetPositionList) {
            String trainsetId = trainsetPositionEntity.getTrainsetId();
            List<TaskAllotPacketEntity> taskAllotPacketEntities = map.get(trainsetId);
            OneWork oneWork = new OneWork();
            List<ZtTaskPositionEntity> ztTaskPositionEntities = remoteService.getTrackPositionByTrackCode(trainsetPositionEntity.getTrackCode(), unitCode);
            for (ZtTaskPositionEntity ztTaskPositionEntity : ztTaskPositionEntities) {
                if (ztTaskPositionEntity.getTrackPositionCode().equals(trainsetPositionEntity.getHeadDirectionPlaCode())) {
                    trainsetPositionEntity.setHeadDirectionMode(ztTaskPositionEntity.getDirectionCode());
                } else if (ztTaskPositionEntity.getTrackPositionCode().equals(trainsetPositionEntity.getTailDirectionPlaCode())) {
                    trainsetPositionEntity.setTailDirectionMode(ztTaskPositionEntity.getDirectionCode());
                }
            }
            oneWork.setTrainsetId(trainsetId);
            oneWork.setTrainsetName(taskAllotPacketEntities.get(0).getTrainsetName());
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
            oneWork.setPlanRepairTrack(stringBuilder.toString());
            oneWork.setTrainsetPositionEntity(trainsetPositionEntity);

            //查询作业标准配置表
            List<XzyCWorkcritertion> xzyCWorkcritertionList = xzyCWorkcritertionService.getWorkcritertionLByTrainsetId(trainsetId);
            //如果作业标准为空则给出提示
            if (CollectionUtils.isEmpty(xzyCWorkcritertionList)) {
                oneWork.setRemark("当前车组没有作业内容，请进行配置");
            } else {
                List<OneWorkCarPart> oneWorkCarParts = new ArrayList<>();
                for (TaskAllotPacketEntity taskAllotPacketEntity : taskAllotPacketEntities) {
                    for (XzyCWorkcritertion workcritertion : xzyCWorkcritertionList) {
                        OneWorkCarPart oneWorkCarPart = new OneWorkCarPart();
                        oneWorkCarPart.setItemCode(workcritertion.getsItemcode());
                        oneWorkCarPart.setItemName(workcritertion.getsItemname());

                        //判断是否和岗位有关
                        if (!CollectionUtils.isEmpty(xzyCConfigPostModes) && "1".equals(xzyCConfigPostModes.get(0).getParamValue())) {
                            //获取当前人的岗位
                            List<TaskAllotPersonEntity> taskAllotItemEntity = taskAllotPacketEntity.getTaskAllotItemEntityList().stream().flatMap(t -> t.getTaskAllotPersonEntityList().stream()).collect(Collectors.toList());
                            List<TaskAllotPersonEntity> taskAllotPersonEntities = taskAllotItemEntity.stream().filter(t -> t.getWorkerId().equals(workerIds.get(0))).collect(Collectors.toList());

                            List<TaskAllotPersonPostEntity> taskAllotPersonPostEntityList = taskAllotPersonEntities.get(0).getTaskAllotPersonPostEntityList();
                            List<XzyCWorkcritertionPost> xzyCWorkcritertionPostList = workcritertion.getXzyCWorkcritertionPostList();
                            List<String> postNameList = taskAllotPersonPostEntityList.stream().map(TaskAllotPersonPostEntity::getPostName).distinct().collect(Collectors.toList());
                            List<String> critertionPostNameList = xzyCWorkcritertionPostList.stream().map(XzyCWorkcritertionPost::getPostName).distinct().collect(Collectors.toList());
                            if (!CollectionUtils.isEmpty(postNameList) && !CollectionUtils.isEmpty(critertionPostNameList)) {
                                //取交集
                                critertionPostNameList.retainAll(postNameList);
                                if (!CollectionUtils.isEmpty(critertionPostNameList)) {
                                    oneWorkCarParts.add(oneWorkCarPart);
                                }
                            }
//                            for (TaskAllotPersonPostEntity taskAllotPersonPostEntity : taskAllotPersonPostEntityList) {
//                                if (xzyCWorkcritertionPostList.stream().anyMatch(t -> t.getPostName().equals(taskAllotPersonPostEntity.getPostName()))) {
//                                    oneWorkCarParts.add(oneWorkCarPart);
//                                }
//                            }
                        } else {
                            oneWorkCarParts.add(oneWorkCarPart);
                        }
                    }
                }
                oneWork.setItemList(oneWorkCarParts);
            }
            oneWorks.add(oneWork);
        }

        //设置排序
        List<OneWork> oneWorkList = new ArrayList<>();
        List<OneWork> oneWorkArrayList = new ArrayList<>();
        for (OneWork twoWork : oneWorks) {
            if (StringUtils.isNotBlank(twoWork.getPlanRepairTrack())) {
                oneWorkList.add(twoWork);
            } else {
                oneWorkArrayList.add(twoWork);
            }
        }
        oneWorkList.sort(Comparator.comparing(OneWork::getTrainsetName));
        oneWorkArrayList.sort(Comparator.comparing(OneWork::getTrainsetName));
        oneWorkList.addAll(oneWorkArrayList);
        return oneWorkList;
    }

    @Override
    public void earlyWarning(WorkWorning workWorning) {
        workWorning.setWorningId(UUID.randomUUID().toString());
        workWorning.setEffectState("1");
        workWorningService.insert(workWorning);
    }

    private List<ProcessLocation> getProcessLocations(RfidProcessCarPartInfo rfidProcessCarPartInfo) {
        List<ProcessLocation> processLocationList = new ArrayList<>();
        ProcessLocation processLocation = new ProcessLocation();
        BeanUtils.copyProperties(rfidProcessCarPartInfo, processLocation);
        processLocation.setLocationId(UUID.randomUUID().toString());
        processLocation.setWorkType(rfidProcessCarPartInfo.getWorkerType());
        processLocation.setCreateTime(new Date());
        processLocation.setTrainsetname(rfidProcessCarPartInfo.getTrainsetName());
        processLocation.setPartposition(rfidProcessCarPartInfo.getPartPosition());
        processLocation.setWorkerId(rfidProcessCarPartInfo.getStuffId());
        processLocation.setParttype(rfidProcessCarPartInfo.getPartType());
        processLocation.setPartname(rfidProcessCarPartInfo.getPartName());
        processLocationList.add(processLocation);
        return processLocationList;
    }
}
