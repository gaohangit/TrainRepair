package com.ydzbinfo.emis.trainRepair.workProcess.service.impl;

import com.baomidou.mybatisplus.plugins.Page;
import com.jxdinfo.hussar.core.shiro.ShiroKit;
import com.jxdinfo.hussar.core.shiro.ShiroUser;
import com.ydzbinfo.emis.trainRepair.common.service.IRemoteService;
import com.ydzbinfo.emis.trainRepair.common.service.IRepairMidGroundService;
import com.ydzbinfo.emis.trainRepair.mobile.model.MainCycPacket;
import com.ydzbinfo.emis.trainRepair.remotemodel.resume.TrainsetBaseInfo;
import com.ydzbinfo.emis.trainRepair.remotemodel.trainuse.ZtTaskPacketEntity;
import com.ydzbinfo.emis.trainRepair.repairmanagement.querymodel.XzyCWorkcritertion;
import com.ydzbinfo.emis.trainRepair.repairmanagent.service.IXzyCWorkcritertionService;
import com.ydzbinfo.emis.trainRepair.taskAllot.model.AllotData;
import com.ydzbinfo.emis.trainRepair.workProcess.dao.WorkProcessMapper;
import com.ydzbinfo.emis.trainRepair.workProcess.service.*;
import com.ydzbinfo.emis.trainRepair.workProcessMonitor.controller.ProcessMonitorController;
import com.ydzbinfo.emis.trainRepair.workprocess.model.*;
import com.ydzbinfo.emis.trainRepair.workprocess.querymodel.*;
import com.ydzbinfo.emis.utils.CommonUtils;
import com.ydzbinfo.emis.utils.MybatisOgnlUtils;
import com.ydzbinfo.emis.utils.StringUtils;
import com.ydzbinfo.emis.utils.mybatisplus.MybatisPlusUtils;
import com.ydzbinfo.emis.utils.mybatisplus.param.ColumnParam;
import com.ydzbinfo.emis.utils.mybatisplus.param.ColumnParamUtil;
import com.ydzbinfo.emis.utils.upload.UploadedFileInfo;
import com.ydzbinfo.hussar.core.util.ToolUtil;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

import static com.ydzbinfo.emis.utils.mybatisplus.MybatisPlusUtils.eqParam;
import static com.ydzbinfo.emis.utils.mybatisplus.MybatisPlusUtils.inParam;


/**
 * <p>
 * ???????????????
 * </p>
 *
 * @author ??????
 * @since 2020-06-09
 */
@Service
public class WorkProcessServiceImpl implements IWorkProcessService {

    //????????????
    protected static final Logger logger = LoggerFactory.getLogger(WorkProcessServiceImpl.class);

    @Resource
    WorkProcessMapper workProcessMapper;

    @Resource
    IRemoteService iRemoteService;

    /**
     * ????????????????????????????????????
     */
    @Autowired
    IProcessTimeRecordService IProcessTimeRecordService;

    /**
     * ????????????????????????
     */
    @Autowired
    IXzyCWorkcritertionService xzyCWorkcritertionService;

    /**
     * ???????????????????????????????????????????????????
     */
    @Autowired
    IRfidCardSummaryService rfidCardSummaryService;

    /**
     * ?????????????????????????????????
     */
    @Autowired
    IProcessCarPartService processCarPartService;

    /**
     * ???????????????????????????
     */
    @Autowired
    IProcessDeptService processDeptService;

    /**
     * ??????????????????????????????
     */
    @Autowired
    IProcessPacketService processPacketService;

    /**
     * ???????????????????????????
     */
    @Autowired
    IProcessPersonService processPersonService;

    /**
     * ?????????????????????????????????
     */
    @Autowired
    IProcessTimeRecordService processTimeRecordService;

    /**
     * ???????????????????????????
     */
    @Autowired
    IProcessLocationService processLocationService;

    /**
     * ???????????????????????????
     */
    @Autowired
    IProcessPicService processPicService;

    /**
     * ?????????????????????????????????
     */
    @Autowired
    IProcessPersonConfirmService processPersonConfirmService;

    @Autowired
    IRemoteService remoteService;

    @Autowired
    IRepairMidGroundService repairMidGroundService;

    @Value("${server.port}")
    private String port;

    /**
     * @author: ??????
     * @Date: 2021/5/12
     * @Description: ??????????????????key??????????????????????????????
     */
    @Data
    public static class ProcessGroupKey {
        private String dayPlanId;
        private String trainsetId;
        private String trainsetName;
        private String workerId;
        private String workerName;
        private String itemCode;
        private String itemName;
        private String packetCode;
        private String packetName;
        private String deptCode;
        private String deptName;
        private String workerType;
        private String unitCode;
    }

    /**-------------------------------------------?????????-------------------------------------------------------------*/

    /**
     * @author: ??????
     * @Date: 2021/5/8
     * @Description: ?????????????????????
     */
    public List<QueryOneWorkProcessData> getOneWorkProcessList(QueryOneWorkProcessData queryOneWorkProcessData) {
        //??????trainsetName???QueryOneWorkProcessData??????trainsetId
        String trainsetName = queryOneWorkProcessData.getTrainsetName();
        if (ToolUtil.isNotEmpty(trainsetName)) {
            String trainsetid = remoteService.getTrainsetidByName(queryOneWorkProcessData.getTrainsetName());
            queryOneWorkProcessData.setTrainsetId(trainsetid);
        }
        //????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
        List<RfidCardSummary> rfidCardSummaryList1 = workProcessMapper.getRfidCardSummaryList(MybatisOgnlUtils.getNewEntityReplacedWildcardChars(queryOneWorkProcessData, QueryOneWorkProcessData::getWorkerName));
        Map<ProcessGroupKey, List<RfidCardSummary>> groupList1 = rfidCardSummaryList1.stream().collect(Collectors.groupingBy(v -> {
            ProcessGroupKey key = new ProcessGroupKey();
            key.setDayPlanId(v.getDayPlanId());
            key.setTrainsetId(v.getTrainsetId());
            key.setWorkerId(v.getStuffId());
            key.setWorkerName(v.getStuffName());
            key.setItemCode(v.getItemCode());
            key.setItemName(v.getItemName());
            key.setUnitCode(v.getUnitCode());
            return key;
        }, LinkedHashMap::new, Collectors.toList()));
        //2.????????????????????????
        List<QueryOneWorkProcessData> collList1 = new ArrayList<>();
        groupList1.forEach(((processGroupKey, item) -> {
            //2.1???????????????????????????????????????
            QueryOneWorkProcessData resItem = new QueryOneWorkProcessData();
            resItem.setDayPlanId(processGroupKey.getDayPlanId());
            resItem.setTrainsetId(processGroupKey.getTrainsetId());
            resItem.setUnitCode(processGroupKey.getUnitCode());
            //??????trainsetId??????trainsetName
            TrainsetBaseInfo trainsetBaseInfo = remoteService.getTrainsetBaseinfoByID(processGroupKey.getTrainsetId());
            if (ToolUtil.isNotEmpty(trainsetBaseInfo)) {
                resItem.setTrainsetName(trainsetBaseInfo.getTrainsetname());
            }
            resItem.setItemCode(processGroupKey.getItemCode());
            resItem.setItemName(processGroupKey.getItemName());
            resItem.setWorkerId(processGroupKey.getWorkerId());
            resItem.setWorkerName(processGroupKey.getWorkerName());
            //??????????????????????????????????????????????????????
            if (item != null && item.size() > 0) {
                resItem.setDataSource(item.get(0).getDataSource());
                //2.3??????????????????
                List<RfidCardSummary> startSummaryList = item.stream().filter(t -> t.getTimeTag().equals("1")).collect(Collectors.toList());
                RfidCardSummary startSummary = new RfidCardSummary();
                if (startSummaryList != null && startSummaryList.size() > 0) {
                    startSummary = startSummaryList.get(0);
                    if (startSummary != null) {
                        resItem.setStartTime(startSummary.getRepairTime());
                    }
                    resItem.setDeptCode(startSummary.getDeptCode());
                    resItem.setDeptName(startSummary.getDeptName());
                }
                //2.4??????????????????
                List<RfidCardSummary> endSummaryList = item.stream().filter(t -> t.getTimeTag().equals("4")).collect(Collectors.toList());
                RfidCardSummary endSummary = new RfidCardSummary();
                if (endSummaryList != null && endSummaryList.size() > 0) {
                    endSummary = endSummaryList.get(0);
                    if (endSummary != null) {
                        resItem.setEndTime(endSummary.getRepairTime());
                    }
                }
                //2.5??????????????????????????????????????????????????????
                List<RfidCardSummary> suspendedAndContineSummaryList = item.stream().
                    filter((RfidCardSummary t) -> t.getTimeTag().equals("2") || t.getTimeTag().equals("3"))
                    .sorted(new Comparator<RfidCardSummary>() {
                        @Override
                        public int compare(RfidCardSummary o1, RfidCardSummary o2) {
                            int s = new Long(o1.getRepairTime().getTime()).intValue();
                            int e = new Long(o2.getRepairTime().getTime()).intValue();
                            return s - e;
                        }
                    }).collect(Collectors.toList());
                //2.6??????????????????
                long suspendedTime = 0;
                if (suspendedAndContineSummaryList.size() > 0) {
                    for (int i = 0; i < suspendedAndContineSummaryList.size(); i++) {
                        if (i != suspendedAndContineSummaryList.size() - 1) {
                            RfidCardSummary currentSummary = suspendedAndContineSummaryList.get(i);
                            RfidCardSummary nextSummary = suspendedAndContineSummaryList.get(i + 1);
                            if (currentSummary.getTimeTag().equals("2") && nextSummary != null) {
                                suspendedTime += (nextSummary.getRepairTime().getTime() - currentSummary.getRepairTime().getTime()) / 1000 / 60;
                            }
                        }
                    }
                }
                resItem.setSuspendedTime(suspendedTime);
                //2.7???????????????
                long totalTime = 0;//?????????
                long totalTime1 = 0;
                if (startSummary != null && endSummary != null && endSummary.getRepairTime() != null && startSummary.getRepairTime() != null) {
                    //??????????????????????????????0????????????1???????????????????????????1??????
                    if ((endSummary.getRepairTime().getTime() - startSummary.getRepairTime().getTime()) / 1000 / 60 > 0) {
                        totalTime = (endSummary.getRepairTime().getTime() - startSummary.getRepairTime().getTime()) / 1000 / 60 + 1;
                    } else {
                        if ((endSummary.getRepairTime().getTime() - startSummary.getRepairTime().getTime()) / 1000 > 0) {
                            totalTime = 1;
                        }
                    }
                    //????????????=???????????????????????????
                    totalTime1 = totalTime - suspendedTime;
                    resItem.setTotalTime(totalTime - suspendedTime);
                }
                //2.8?????? ???????????? ?????? ??????id??????????????? ??????????????????????????????
                if (!ObjectUtils.isEmpty(startSummary.getTrainsetId())) {
                    List<XzyCWorkcritertion> xzyCWorkcritertionList = xzyCWorkcritertionService.getWorkcritertionLByTrainsetId(startSummary.getTrainsetId());
                    String itemName = startSummary.getItemName();
                    XzyCWorkcritertion xzyCWorkcritertion = Optional.ofNullable(xzyCWorkcritertionList).orElseGet(ArrayList::new).stream().filter(t -> t.getsItemname().equals(itemName)).findFirst().orElse(null);
                    if (xzyCWorkcritertion != null) {
                        resItem.setTrainsetType(xzyCWorkcritertion.getsTrainsettype());
                        //????????????????????????
                        resItem.setStandardPicCount(xzyCWorkcritertion.getiPiccount());
                        //2.9?????????????????????????????????????????????????????? ???????????????????????????????????????
                        if (startSummary != null && endSummary != null && endSummary.getRepairTime() != null && startSummary.getRepairTime() != null) {
                            //?????????????????????
                            if (xzyCWorkcritertion.getiMinworktime() != null) {
                                //??????????????????????????????
                                if (totalTime1 < xzyCWorkcritertion.getiMinworktime()) {
                                    resItem.setTimeStatus("??????");
                                } else if (totalTime1 == xzyCWorkcritertion.getiMinworktime()) {
                                    resItem.setTimeStatus("??????");
                                } else {
                                    if (xzyCWorkcritertion.getiMaxworktime() != null) {
                                        if (totalTime1 <= xzyCWorkcritertion.getiMaxworktime()) {
                                            resItem.setTimeStatus("??????");
                                        } else {
                                            resItem.setTimeStatus("??????");
                                        }
                                    } else {
                                        resItem.setTimeStatus("??????");
                                    }
                                }
                            } else {
                                //???????????????????????????
                                if (xzyCWorkcritertion.getiMaxworktime() != null) {
                                    if(totalTime1 <= xzyCWorkcritertion.getiMaxworktime()){
                                        resItem.setTimeStatus("??????");
                                    }else{
                                        resItem.setTimeStatus("??????");
                                    }
                                }else{
                                    //?????????????????????????????????????????????????????????
                                    resItem.setTimeStatus("??????");
                                }
                            }
                        }
                    } else {
                        resItem.setTimeStatus("??????");
                    }
                } else {
                    resItem.setTimeStatus("??????");
                }
                resItem.setCritertionId(startSummary.getCritertionId());
                resItem.setRecordTime(startSummary.getRecordTime());
            }
            //??????????????????????????????????????????
            List<Map<String, Object>> carNoList = workProcessMapper.getCarNoList(processGroupKey.getDayPlanId(), processGroupKey.getTrainsetId(), processGroupKey.getItemCode(), processGroupKey.getWorkerId());
            if (carNoList.size() > 0 && carNoList != null) {
                resItem.setCarNos(carNoList.stream().map(t -> (String) t.get("carNo")).sorted().collect(Collectors.toList()));
            }
            //????????????????????????
            List<ProcessPic> processPicList = MybatisPlusUtils.selectList(
                processPicService,
                ColumnParamUtil.filterBlankParams(
                    eqParam(ProcessPic::getWorkerId, processGroupKey.getWorkerId()),
                    eqParam(ProcessPic::getItemCode, processGroupKey.getItemCode()),
                    eqParam(ProcessPic::getTrainsetId, processGroupKey.getTrainsetId()),
                    eqParam(ProcessPic::getDayplanId, processGroupKey.getDayPlanId()),
                    eqParam(ProcessPic::getWorkType, "1")
                )
            );
            resItem.setProcessPicList(processPicList);
            resItem.setActualPicCount(processPicList != null ? processPicList.size() : 0);
            collList1.add(resItem);
        }));
        List<QueryOneWorkProcessData> resList = collList1;
        //????????????????????????
        if (queryOneWorkProcessData.getTimeStatus() != null && !queryOneWorkProcessData.getTimeStatus().equals("")) {
            resList = resList.stream().filter(t -> queryOneWorkProcessData.getTimeStatus().equals(t.getTimeStatus())).collect(Collectors.toList());
        }
        resList = resList.stream().sorted(Comparator.comparing(QueryOneWorkProcessData::getTrainsetName, Comparator.nullsLast(String::compareTo)).thenComparing(QueryOneWorkProcessData::getItemName, Comparator.nullsLast(String::compareTo)).thenComparing(QueryOneWorkProcessData::getWorkerName, Comparator.nullsLast(String::compareTo))).collect(Collectors.toList());
        return resList;
    }

    /**
     * @author: ??????
     * @Date: 2021/5/8
     * @Description: ???????????????????????????
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public boolean addOneWorkProcess(OneWorkProcessData oneWorkProcessData, List<UploadedFileInfo> uploadedFileInfos) {
        //?????????????????????????????????????????????????????????????????????????????????
        for (ProcessWorker workerItem : oneWorkProcessData.getProcessWorkerList()) {
            List<RfidCardSummary> rfidCardSummaryList = MybatisPlusUtils.selectList(
                rfidCardSummaryService,
                ColumnParamUtil.filterBlankParams(
                    eqParam(RfidCardSummary::getStuffId, workerItem.getWorkerId()),
                    eqParam(RfidCardSummary::getItemCode, oneWorkProcessData.getItemCode()),
                    eqParam(RfidCardSummary::getTrainsetId, oneWorkProcessData.getTrainsetId()),
                    eqParam(RfidCardSummary::getDayPlanId, oneWorkProcessData.getDayPlanId()),
                    eqParam(RfidCardSummary::getDeptCode, oneWorkProcessData.getDeptCode()),
                    eqParam(RfidCardSummary::getRepairType, "0"),
                    eqParam(RfidCardSummary::getFlag, "1")
                )
            );
            if (!CollectionUtils.isEmpty(rfidCardSummaryList)) {
                String dataSource = "";
                RfidCardSummary rfidCardSummary = rfidCardSummaryList.get(0);
                if(!ObjectUtils.isEmpty(rfidCardSummary)){
                    dataSource = rfidCardSummary.getDataSource();
                }
                if("1".equals(dataSource)){
                    throw new RuntimeException("["+workerItem.getWorkerName()+"]???????????????????????????????????????????????????");
                }else if("2".equals(dataSource)){
                    throw new RuntimeException("["+workerItem.getWorkerName()+"]????????????????????????????????????????????????");
                }else{
                    throw new RuntimeException("["+workerItem.getWorkerName()+"]???????????????????????????????????????");
                }
            }
        }

        //??????????????????
        ShiroUser currentUser = ShiroKit.getUser();
        //????????????
        Date currentDate = new Date();
        String unitCode = currentUser.getThirdOrgan() == null ? "" : currentUser.getThirdOrgan().getOrganCode();
        String unitName = currentUser.getThirdOrgan() == null ? "" : currentUser.getThirdOrgan().getOrganName();
        List<ProcessPic> insertPicList = new ArrayList<>();//??????????????????

        //????????????
        TrainsetBaseInfo trainsetBaseInfo = remoteService.getTrainsetBaseinfoByID(oneWorkProcessData.getTrainsetId());

        List<ProcessSummaryEntity> insertSummaryEntityList = new ArrayList<>();//??????????????????
        ProcessPacketEntity insertPacketEntity = new ProcessPacketEntity();//???????????????
        String insertPacketId = UUID.randomUUID().toString();
        insertPacketEntity.setProcessPacketId(insertPacketId);
        insertPacketEntity.setDeptCode(oneWorkProcessData.getDeptCode());
        insertPacketEntity.setDeptName(oneWorkProcessData.getDeptName());
        insertPacketEntity.setDayPlanId(oneWorkProcessData.getDayPlanId());
        insertPacketEntity.setTrainsetId(oneWorkProcessData.getTrainsetId());
        insertPacketEntity.setTrainsetName(oneWorkProcessData.getTrainsetName());
        insertPacketEntity.setRecordTime(currentDate);
        insertPacketEntity.setRecordCode(currentUser.getStaffId());
        insertPacketEntity.setRecordName(currentUser.getName());
        insertPacketEntity.setUnitCode(unitCode);
        insertPacketEntity.setUnitName(unitName);
        insertPacketEntity.setPacketCode(oneWorkProcessData.getItemCode());
        insertPacketEntity.setPacketName(oneWorkProcessData.getItemName());
        insertPacketEntity.setPacketType("1");
        //?????????????????? ??????????????????
        List<ProcessCarPartEntity> isnertCarPartEntityList = new ArrayList<>();
        for (String carNoItem : oneWorkProcessData.getCarNos()) {
            ProcessCarPartEntity insertCarPartEntity = new ProcessCarPartEntity();
            List<ProcessPersonEntity> insertPersonEntityList = new ArrayList<>();
            insertCarPartEntity.setCarNo(carNoItem);
            insertCarPartEntity.setItemType("0");
            insertCarPartEntity.setItemCode(oneWorkProcessData.getItemCode());
            insertCarPartEntity.setItemName(oneWorkProcessData.getItemName());
            insertCarPartEntity.setTrainsetType(trainsetBaseInfo.getTraintype());
            insertCarPartEntity.setDataSource("2");
            //?????????????????????????????????
            for (ProcessWorker workerItem : oneWorkProcessData.getProcessWorkerList()) {
                ProcessPersonEntity inertPersonEntity = new ProcessPersonEntity();
                inertPersonEntity.setWorkerId(workerItem.getWorkerId());
                inertPersonEntity.setWorkerName(workerItem.getWorkerName());
                inertPersonEntity.setWorkerType("1");
                //?????????????????????????????????????????????
                List<ProcessTimeRecordEntity> insertTimeRecordEntityList = new ArrayList<>();
                ProcessTimeRecordEntity insertStartTimeRecordEntity = new ProcessTimeRecordEntity();
                insertStartTimeRecordEntity.setTime(oneWorkProcessData.getStartTime());
                insertStartTimeRecordEntity.setItemTimeState("1");
                ProcessTimeRecordEntity insertEndTimeRecordEntity = new ProcessTimeRecordEntity();
                insertEndTimeRecordEntity.setTime(oneWorkProcessData.getEndTime());
                insertEndTimeRecordEntity.setItemTimeState("4");
                insertTimeRecordEntityList.add(insertStartTimeRecordEntity);
                insertTimeRecordEntityList.add(insertEndTimeRecordEntity);

                inertPersonEntity.setProcessTimeRecordEntityList(insertTimeRecordEntityList);
                insertPersonEntityList.add(inertPersonEntity);
            }
            insertCarPartEntity.setProcessPersonEntityList(insertPersonEntityList);
            isnertCarPartEntityList.add(insertCarPartEntity);
        }
        insertPacketEntity.setProcessCarPartEntityList(isnertCarPartEntityList);
        //?????????????????????????????????
        for (ProcessWorker workerItem : oneWorkProcessData.getProcessWorkerList()) {
            ProcessSummaryEntity insertStartSummaryEntity = new ProcessSummaryEntity();
            insertStartSummaryEntity.setDayPlanId(oneWorkProcessData.getDayPlanId());
            insertStartSummaryEntity.setTrainsetId(oneWorkProcessData.getTrainsetId());
            insertStartSummaryEntity.setItemCode(oneWorkProcessData.getItemCode());
            insertStartSummaryEntity.setItemName(oneWorkProcessData.getItemName());
            insertStartSummaryEntity.setStuffId(workerItem.getWorkerId());
            insertStartSummaryEntity.setStuffName(workerItem.getWorkerName());
            insertStartSummaryEntity.setRepairTime(oneWorkProcessData.getStartTime());
            insertStartSummaryEntity.setRecorderCode(currentUser.getStaffId());
            insertStartSummaryEntity.setRecorderName(currentUser.getName());
            insertStartSummaryEntity.setRecordTime(currentDate);
            insertStartSummaryEntity.setDeptCode(oneWorkProcessData.getDeptCode());
            insertStartSummaryEntity.setDeptName(oneWorkProcessData.getDeptName());
            insertStartSummaryEntity.setTimeTag("1");
            insertStartSummaryEntity.setRepairType("0");
            insertStartSummaryEntity.setCritertionId(oneWorkProcessData.getCritertionId());
            insertStartSummaryEntity.setUnitCode(unitCode);
            insertStartSummaryEntity.setTrainsetType(trainsetBaseInfo.getTraintype());
            insertStartSummaryEntity.setDataSource("2");
            ProcessSummaryEntity insertEndSummaryEntity = new ProcessSummaryEntity();
            BeanUtils.copyProperties(insertStartSummaryEntity, insertEndSummaryEntity);
            insertEndSummaryEntity.setRepairTime(oneWorkProcessData.getEndTime());
            insertEndSummaryEntity.setTimeTag("4");
            insertSummaryEntityList.add(insertStartSummaryEntity);
            insertSummaryEntityList.add(insertEndSummaryEntity);
            for (String carNoItem : oneWorkProcessData.getCarNos()) {
                //???????????????????????????
                if (uploadedFileInfos != null && uploadedFileInfos.size() > 0) {
                    //?????????????????????????????????
                    List<UploadedFileInfo> carNoFiles = uploadedFileInfos.stream().filter(t -> t.getParamName().equals(carNoItem)).collect(Collectors.toList());
                    if (carNoFiles != null && carNoFiles.size() > 0) {
                        for (UploadedFileInfo carNoFile : carNoFiles) {
                            ProcessPic processPic = new ProcessPic();
                            String picId = UUID.randomUUID().toString();
                            processPic.setPicId(picId);
                            processPic.setPicAddress(carNoFile.getRelativeUrl());
                            processPic.setPicName(carNoFile.getOldName());
                            processPic.setPicType("1");
                            processPic.setWorkType("1");
                            processPic.setDayplanId(oneWorkProcessData.getDayPlanId());
                            processPic.setCreateTime(currentDate);
                            processPic.setTrainsetId(oneWorkProcessData.getTrainsetId());
                            processPic.setTrainsetName(oneWorkProcessData.getTrainsetName());
                            processPic.setUnitCode(unitCode);
                            processPic.setItemCode(oneWorkProcessData.getItemCode());
                            processPic.setCarNo(carNoItem);
                            processPic.setWorkerId(workerItem.getWorkerId());
                            insertPicList.add(processPic);
                        }
                    }
                }
            }
        }
        //??????????????????
        if (oneWorkProcessData.getProcessPicList() != null && oneWorkProcessData.getProcessPicList().size() > 0) {
            insertPicList.addAll(oneWorkProcessData.getProcessPicList());
        }
        if (insertPicList.size() > 0) {
            processPicService.insertBatch(insertPicList);
        }
        //??????????????????????????????
        if (insertSummaryEntityList.size() > 0) {
            repairMidGroundService.addSummary(insertSummaryEntityList);
        }
        //???????????????????????????????????????
        List<ProcessPacketEntity> insertPacketEntityList = new ArrayList<>();
        insertPacketEntityList.add(insertPacketEntity);
        repairMidGroundService.addWorkProcess(insertPacketEntityList);
        return true;
    }

    /**
     * @author: ??????
     * @Date: 2021/5/8
     * @Description: ???????????????????????????
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public boolean delOneWorkProcess(OneWorkProcessData oneWorkProcessData) {
        //??????????????????
        ShiroUser currentUser = ShiroKit.getUser();
        //????????????
        Date currentDate = new Date();
        //1.???????????????????????????????????????
        String workerId = oneWorkProcessData.getWorkerId();
        if (workerId == null || workerId.equals("")) {
            if (oneWorkProcessData.getProcessWorkerList() != null && oneWorkProcessData.getProcessWorkerList().size() > 0) {
                workerId = oneWorkProcessData.getProcessWorkerList().get(0).getWorkerId();
            }
        }
        if (workerId == null || workerId.equals("")) {
            throw new RuntimeException("?????????????????????????????????!");
        }
        List<ProcessPerson> totalPersonList = workProcessMapper.getDelPersonList(oneWorkProcessData.getDayPlanId(), oneWorkProcessData.getTrainsetId(), oneWorkProcessData.getItemCode(), null, "0",null);
        List<ProcessPerson> delPersonList = new ArrayList<>();
        if (totalPersonList != null && totalPersonList.size() > 0) {
            String finalWorkerId = workerId;
            delPersonList = totalPersonList.stream().filter(t -> t.getWorkerId().equals(finalWorkerId)).collect(Collectors.toList());
        }
        //2.????????????
        for (ProcessPerson item : delPersonList) {
            String personId = item.getProcessPersonId();
            String processId = item.getProcessId();
            String delWorkerId = item.getWorkerId();
            //2.1??????????????????????????????
            List<ProcessTimeRecord> processTimeRecordList = MybatisPlusUtils.selectList(
                processTimeRecordService,
                eqParam(ProcessTimeRecord::getProcessPersonId, personId)
            );
            processTimeRecordList.forEach(t -> {
                t.setFlag("0");
                t.setDelTime(currentDate);
                t.setDelUserCode(currentUser.getStaffId());
                t.setDelUserName(currentUser.getName());
            });
            processTimeRecordService.updateBatchById(processTimeRecordList);
            //2.2??????????????????
            ProcessPerson processPerson = new ProcessPerson();
            processPerson.setProcessPersonId(personId);
            processPerson.setFlag("0");
            processPerson.setDelTime(currentDate);
            processPerson.setDelUserCode(currentUser.getStaffId());
            processPerson.setDelUserName(currentUser.getName());

            MybatisPlusUtils.update(
                processPersonService,
                processPerson,
                eqParam(ProcessPerson::getProcessPersonId, personId)
            );
            //2.3???????????????????????? ?????????????????????????????????????????????????????? ????????????????????????????????? ???????????????
            ProcessCarPart processCarPart = new ProcessCarPart();
            processCarPart.setProcessId(processId);
            processCarPart.setFlag("1");
            long count = totalPersonList.stream().filter(t -> (!t.getWorkerId().equals(delWorkerId)) && t.getProcessId().equals(processId)).count();
            if (count == 0) {
                processCarPart.setFlag("0");
                processCarPart.setDelTime(currentDate);
                processCarPart.setDelUserCode(currentUser.getStaffId());
                processCarPart.setDelUserName(currentUser.getName());

                MybatisPlusUtils.update(
                    processCarPartService,
                    processCarPart,
                    eqParam(ProcessCarPart::getProcessId, processId)
                );
            }
        }
        //4.????????????
        RfidCardSummary rfidCardSummary = new RfidCardSummary();
        rfidCardSummary.setFlag("0");
        rfidCardSummary.setDelTime(currentDate);
        rfidCardSummary.setDelUserCode(currentUser.getStaffId());
        rfidCardSummary.setDelUserName(currentUser.getName());

        MybatisPlusUtils.update(
            rfidCardSummaryService,
            rfidCardSummary,
            eqParam(RfidCardSummary::getDayPlanId, oneWorkProcessData.getDayPlanId()),
            eqParam(RfidCardSummary::getTrainsetId, oneWorkProcessData.getTrainsetId()),
            eqParam(RfidCardSummary::getItemCode, oneWorkProcessData.getItemCode()),
            eqParam(RfidCardSummary::getStuffId, workerId)
            // eqParam(RfidCardSummary::getDeptCode, oneWorkProcessData.getDeptCode())
        );
        //?????????????????????(??????????????????)
        MybatisPlusUtils.delete(
            processPicService,
            eqParam(ProcessPic::getDayplanId, oneWorkProcessData.getDayPlanId()),
            eqParam(ProcessPic::getItemCode, oneWorkProcessData.getItemCode()),
            eqParam(ProcessPic::getTrainsetId, oneWorkProcessData.getTrainsetId()),
            eqParam(ProcessPic::getWorkerId, workerId)
        );
        return true;
    }

    /**----------------------------------------------------?????????-------------------------------------------------------------*/

    /**
     * @author: ??????
     * @Date: 2021/5/8
     * @Description: ?????????????????????
     */
    @Override
    public Page<QueryTwoWorkProcessData> getTwoWorkProcessList(TwoWorkProcessData twoWorkProcessData) {
        //????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
        List<TwoWorkProcessData> twoWorkProcessDataList = workProcessMapper.getTwoWorkProcessList(MybatisOgnlUtils.getNewEntityReplacedWildcardChars(twoWorkProcessData, TwoWorkProcessData::getWorkerName));
        Map<ProcessGroupKey, List<TwoWorkProcessData>> groupList = twoWorkProcessDataList.stream().collect(Collectors.groupingBy(v -> {
            ProcessGroupKey key = new ProcessGroupKey();
            key.setDayPlanId(v.getDayPlanId());
            key.setTrainsetId(v.getTrainsetId());
            //key.setTrainsetName(v.getTrainsetName());
            key.setWorkerId(v.getWorkerId());
            key.setWorkerName(v.getWorkerName());
            key.setPacketCode(v.getPacketCode());
            key.setPacketName(v.getPacketName());
            return key;
        }, LinkedHashMap::new, Collectors.toList()));
        //3.????????????
        List<QueryTwoWorkProcessData> collList = new ArrayList<>();
        groupList.forEach(((processGroupKey, item) -> {
            QueryTwoWorkProcessData resItem = new QueryTwoWorkProcessData();
            resItem.setDayPlanId(processGroupKey.getDayPlanId());
            resItem.setTrainsetId(processGroupKey.getTrainsetId());
            resItem.setWorkerId(processGroupKey.getWorkerId());
            resItem.setWorkerName(processGroupKey.getWorkerName());
            //??????trainsetId??????trainsetName
            TrainsetBaseInfo trainsetBaseInfo = remoteService.getTrainsetBaseinfoByID(processGroupKey.getTrainsetId());
            if (ToolUtil.isNotEmpty(trainsetBaseInfo)) {
                resItem.setTrainsetName(trainsetBaseInfo.getTrainsetname());
            }
            //?????????????????????
            List<ProcessPacket> resPacketList = new ArrayList<>();
            //??????????????????
            List<String> itemCodes = item.stream().map(ProcessData::getItemCode).distinct().collect(Collectors.toList());
            Integer itemCount = itemCodes.size();
            if (itemCodes.get(0) == null) {
                resItem.setItemCount(0);
            } else {
                resItem.setItemCount(itemCount);
            }
            List<ProcessItem> processItemList = new ArrayList<>();
            ProcessPacket resPacket = new ProcessPacket();
            if (ToolUtil.isNotEmpty(processGroupKey.getPacketCode())) {
                resPacket.setPacketCode(processGroupKey.getPacketCode());
                resPacket.setPacketName(processGroupKey.getPacketName());
                //?????????????????????
                processItemList = item.stream().filter(t -> t.getPacketCode().equals(resPacket.getPacketCode())).map(twoData2 -> {
                    ProcessItem processItem1 = new ProcessItem();
                    processItem1.setItemCode(twoData2.getItemCode());
                    processItem1.setItemName(twoData2.getItemName());
                    return processItem1;
                }).distinct().collect(Collectors.toList());


                //key ??????code value ????????????
                Map<String, String> itemNameMap = item.stream().collect(Collectors.toMap(TwoWorkProcessData::getItemCode, TwoWorkProcessData::getItemName, (k1, k2) -> k1));
                List<ProcessPic> processPicList = MybatisPlusUtils.selectList(
                    processPicService,
                    inParam(ProcessPic::getItemCode, itemCodes),
                    eqParam(ProcessPic::getDayplanId, processGroupKey.getDayPlanId()),
                    eqParam(ProcessPic::getWorkerId, processGroupKey.getWorkerId()),
                    eqParam(ProcessPic::getTrainsetId, processGroupKey.getTrainsetId()),
                    eqParam(ProcessPic::getWorkType, "2")
                );
                processPicList.forEach(p -> {
                    //??????????????????
                    p.setItemName(itemNameMap.get(p.getItemCode()));
                });
                resItem.setProcessPicList(processPicList);
            }
            resPacket.setProcessItemList(processItemList);
            resPacketList.add(resPacket);
            resItem.setProcessPacketList(resPacketList);
            //??????????????????
            resItem.setDeptCode(item.stream().map(t -> t.getDeptCode()).collect(Collectors.toList()).get(0));
            resItem.setDeptName(item.stream().map(t -> t.getDeptName()).collect(Collectors.toList()).get(0));
            //????????????????????????
            ProcessPic processPic = new ProcessPic();
            processPic.setDayplanId(processGroupKey.getDayPlanId());
            processPic.setTrainsetId(processGroupKey.getTrainsetId());
            processPic.setWorkerId(processGroupKey.getWorkerId());
            processPic.setWorkType("2");
            Integer actualPicCount = MybatisPlusUtils.selectCount(
                processPicService,
                inParam(ProcessPic::getItemCode, itemCodes),
                eqParam(ProcessPic::getDayplanId, processGroupKey.getDayPlanId()),
                eqParam(ProcessPic::getWorkerId, processGroupKey.getWorkerId()),
                eqParam(ProcessPic::getTrainsetId, processGroupKey.getTrainsetId()),
                eqParam(ProcessPic::getWorkType, "2")
            );
            resItem.setActualPicCount(actualPicCount);
            //??????????????????????????????????????????????????????????????????????????????????????????????????????????????????
            List<String> personIdList = item.stream().map(t -> t.getProcessPersonId()).collect(Collectors.toList());
            if(!CollectionUtils.isEmpty(personIdList)){
                List<ProcessTimeRecord> processTimeRecordList = MybatisPlusUtils.selectList(
                    IProcessTimeRecordService,
                    eqParam(ProcessTimeRecord::getFlag, "1"),
                    inParam(ProcessTimeRecord::getProcessPersonId, personIdList)
                );
                if(!CollectionUtils.isEmpty(processTimeRecordList)){
                    ProcessTimeRecord startRecordTime = processTimeRecordList.stream().filter(t->"1".equals(t.getItemTimeState())).sorted(Comparator.comparing(ProcessTimeRecord::getTime)).findFirst().orElse(null);
                    if(!ObjectUtils.isEmpty(startRecordTime)){
                        resItem.setStartTime(startRecordTime.getTime());
                    }
                    ProcessTimeRecord endRecordTime = processTimeRecordList.stream().filter(t->"4".equals(t.getItemTimeState())).sorted(Comparator.comparing(ProcessTimeRecord::getTime).reversed()).findFirst().orElse(null);
                    if(!ObjectUtils.isEmpty(endRecordTime)){
                        resItem.setEndTime(endRecordTime.getTime());
                    }
                }
            }
            /**PC??????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
            //????????????????????????
            List<RfidCardSummary> rfidCardSummaryList = MybatisPlusUtils.selectList(
                rfidCardSummaryService,
                ColumnParamUtil.filterBlankParams(
                    eqParam(RfidCardSummary::getDayPlanId, processGroupKey.getDayPlanId()),
                    eqParam(RfidCardSummary::getTrainsetId, processGroupKey.getTrainsetId()),
                    eqParam(RfidCardSummary::getStuffId, processGroupKey.getWorkerId()),
                    eqParam(RfidCardSummary::getRepairType, "1"),
                    eqParam(RfidCardSummary::getFlag, "1")
                )
            );
            if (rfidCardSummaryList != null && rfidCardSummaryList.size() > 0) {
                //????????????????????????????????????
                List<RfidCardSummary> startSummaryList = rfidCardSummaryList.stream().filter(t -> t.getTimeTag().equals("1")).sorted(Comparator.comparing(RfidCardSummary::getRepairTime)).collect(Collectors.toList());
                if (startSummaryList.size() > 0) {
                    RfidCardSummary startSummary = startSummaryList.get(0);
                    if (startSummary != null) {
                        resItem.setStartTime(startSummary.getRepairTime());
                        resItem.setDeptCode(startSummary.getDeptCode());
                        resItem.setDeptName(startSummary.getDeptName());
                        resItem.setRecordTime(startSummary.getRecordTime());
                    }
                }
                //????????????,???????????????????????????
                List<RfidCardSummary> endSummaryList = rfidCardSummaryList.stream().filter(t -> t.getTimeTag().equals("4")).sorted(Comparator.comparing(RfidCardSummary::getRepairTime).reversed()).collect(Collectors.toList());
                if (endSummaryList.size() > 0) {
                    RfidCardSummary endSummary = endSummaryList.get(0);
                    Date endSummaryRepairTime= endSummary.getRepairTime();
                    //??????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
                    List<RfidCardSummary> startSummaryList1 = rfidCardSummaryList.stream().filter(t -> t.getTimeTag().equals("1")).sorted(Comparator.comparing(RfidCardSummary::getRepairTime).reversed()).collect(Collectors.toList());
                    if(startSummaryList1.size() > 0){
                        RfidCardSummary startSummary = startSummaryList1.get(0);
                        Date startSummaryRepairTime = startSummary.getRepairTime();
                        int i = endSummaryRepairTime.compareTo(startSummaryRepairTime);
                        if (i > 0) {
                            resItem.setEndTime(endSummary.getRepairTime());
                        }
                    }

                }
            }**/
            //??????????????????????????????
            resItem.setUnitCode(item.get(0).getUnitCode());
            resItem.setUnitName(item.get(0).getUnitName());
            resItem.setDataSource(item.get(0).getDataSource());
            //????????????????????????
            ProcessPic picew = new ProcessPic();
            picew.setWorkerId(processGroupKey.getWorkerId());
            picew.setItemCode(processGroupKey.getItemCode());
            picew.setTrainsetId(processGroupKey.getTrainsetId());
            picew.setDayplanId(processGroupKey.getDayPlanId());
            picew.setWorkType("2");
            collList.add(resItem);
        }));
        Collections.sort(collList);
        List<QueryTwoWorkProcessData> resList = collList;
        return CommonUtils.getPage(resList, twoWorkProcessData.getPageNum(), twoWorkProcessData.getPageSize());
    }

    @Override
    public Page<QueryTwoWorkProcess> getTwoWorkProcessData(TwoWorkProcess twoWorkProcess) {
        //????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
        List<TwoWorkProcessData> twoWorkProcessDataList = workProcessMapper.getTwoWorkProcessData(twoWorkProcess);
        Map<ProcessGroupKey, List<TwoWorkProcessData>> groupList = twoWorkProcessDataList.stream().collect(Collectors.groupingBy(v -> {
            ProcessGroupKey key = new ProcessGroupKey();
            key.setDayPlanId(v.getDayPlanId());
            key.setTrainsetId(v.getTrainsetId());
            key.setWorkerId(v.getWorkerId());
            key.setWorkerName(v.getWorkerName());
            key.setPacketCode(v.getPacketCode());
            key.setPacketName(v.getPacketName());
            return key;
        }, LinkedHashMap::new, Collectors.toList()));
        return null;
    }

    /**
     * @author: ??????
     * @Date: 2021/5/8
     * @Description: ???????????????????????????
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public boolean addTwoWorkProcess(TwoWorkProcessData twoWorkProcessData, List<UploadedFileInfo> uploadedFileInfos) {
        //1.??????????????????
        //1.1??????????????????
        ShiroUser currentUser = ShiroKit.getUser();
        String unitCode = currentUser.getThirdOrgan() == null ? "" : currentUser.getThirdOrgan().getOrganCode();
        String unitName = currentUser.getThirdOrgan() == null ? "" : currentUser.getThirdOrgan().getOrganName();
        //1.2???????????????????????????????????????
        String deptCode=twoWorkProcessData.getDeptCode();
        String deptName=twoWorkProcessData.getDeptName();
        String dayPlanId=twoWorkProcessData.getDayPlanId();
        String trainsetId = twoWorkProcessData.getTrainsetId();
        String trainsetName = twoWorkProcessData.getTrainsetName();
        //1.3.??????????????????
        Date currentDate = new Date();
        //1.4.????????????ID??????????????????
        List<String> carNoList = remoteService.getCarno(trainsetId);
        //1.5????????????
        TrainsetBaseInfo trainsetBaseInfo = remoteService.getTrainsetBaseinfoByID(trainsetId);
        //1.6?????????????????????
        List<ProcessPacket> processPacketList = twoWorkProcessData.getProcessPacketList();//???????????????
        if(CollectionUtils.isEmpty(processPacketList)){
            throw new RuntimeException("?????????????????????????????????????????????");
        }
        //1.7????????????????????????
        List<ProcessWorker> processWorkerList = twoWorkProcessData.getProcessWorkerList();//??????????????????
        if(CollectionUtils.isEmpty(processWorkerList)){
            throw new RuntimeException("??????????????????????????????????????????");
        }
        //2.????????????????????????,???????????????????????????????????????
        for(ProcessPacket processPacket : processPacketList){
            for (ProcessWorker workerItem : processWorkerList) {
                logger.info("addTwoWorkProcess??????getWorkCount???????????????dayPlanId:"+dayPlanId+",trainsetId:"+trainsetId+",packetCode:"+processPacket.getPacketCode()+",deptCode:"+deptCode+",workerId:"+workerItem.getWorkerId());
                Integer workCount = workProcessMapper.getWorkCount(dayPlanId, trainsetId, processPacket.getPacketCode(), deptCode, workerItem.getWorkerId());
                if(!ObjectUtils.isEmpty(workCount)&&workCount>0){
                    throw new RuntimeException("?????????"+workerItem.getWorkerName()+"???"+processPacket.getPacketName()+"????????????,??????????????????!");
                }
            }
        }
        //3.?????????????????????
        ProcessPacketEntity insertPacketEntity = new ProcessPacketEntity();
        insertPacketEntity.setDeptCode(deptCode);
        insertPacketEntity.setDeptName(deptName);
        insertPacketEntity.setDayPlanId(dayPlanId);
        insertPacketEntity.setTrainsetId(trainsetId);
        insertPacketEntity.setTrainsetName(trainsetName);
        insertPacketEntity.setRecordTime(currentDate);
        insertPacketEntity.setRecordCode(currentUser.getStaffId());
        insertPacketEntity.setRecordName(currentUser.getName());
        insertPacketEntity.setUnitCode(unitCode);
        insertPacketEntity.setUnitName(unitName);
        if (processPacketList != null && processPacketList.size() > 0) {
            insertPacketEntity.setPacketCode(processPacketList.get(0).getPacketCode());
            insertPacketEntity.setPacketName(processPacketList.get(0).getPacketName());
        }
        insertPacketEntity.setPacketType("1");
        //4.????????????????????????????????????
        List<ProcessSummaryEntity> summaryEntityList = new ArrayList<>();
        for (ProcessWorker workerItem : processWorkerList) {
            ProcessSummaryEntity insertStartSummaryEntity = new ProcessSummaryEntity();
            insertStartSummaryEntity.setRecordId(UUID.randomUUID().toString());
            insertStartSummaryEntity.setDayPlanId(twoWorkProcessData.getDayPlanId());
            insertStartSummaryEntity.setTrainsetId(twoWorkProcessData.getTrainsetId());
            insertStartSummaryEntity.setItemCode(twoWorkProcessData.getPacketCode());
            insertStartSummaryEntity.setItemName(twoWorkProcessData.getPacketName());
            insertStartSummaryEntity.setStuffId(workerItem.getWorkerId());
            insertStartSummaryEntity.setStuffName(workerItem.getWorkerName());
            insertStartSummaryEntity.setRepairTime(twoWorkProcessData.getStartTime());
            insertStartSummaryEntity.setRecorderCode(currentUser.getStaffId());
            insertStartSummaryEntity.setRecorderName(currentUser.getName());
            insertStartSummaryEntity.setRecordTime(currentDate);
            insertStartSummaryEntity.setDeptCode(twoWorkProcessData.getDeptCode());
            insertStartSummaryEntity.setDeptName(twoWorkProcessData.getDeptName());
            insertStartSummaryEntity.setTimeTag("1");
            insertStartSummaryEntity.setRepairType("1");
            insertStartSummaryEntity.setCritertionId(UUID.randomUUID().toString());
            insertStartSummaryEntity.setUnitCode(unitCode);
            insertStartSummaryEntity.setTrainsetType(trainsetBaseInfo.getTraintype());
            insertStartSummaryEntity.setDataSource("2");
            ProcessSummaryEntity insertEndSummaryEntity = new ProcessSummaryEntity();
            BeanUtils.copyProperties(insertStartSummaryEntity, insertEndSummaryEntity);
            insertEndSummaryEntity.setRepairTime(twoWorkProcessData.getEndTime());
            insertEndSummaryEntity.setTimeTag("4");
            insertEndSummaryEntity.setCritertionId(UUID.randomUUID().toString());//???????????????UUID
            summaryEntityList.add(insertStartSummaryEntity);
            summaryEntityList.add(insertEndSummaryEntity);
        }

        //5.?????????????????????????????????????????????????????????
        List<ProcessCarPartEntity> isnertCarPartEntityList = new ArrayList<>();//??????????????????
        List<ProcessPic> insertPicList = new ArrayList<>();//????????????????????????
        for (ProcessPacket processPacket : processPacketList) {
            for (ProcessItem processItem : processPacket.getProcessItemList()) {
                for (String carNoItem : carNoList) {
                    //??????????????????????????????????????????
                    String carNo = carNoItem.substring(carNoItem.length() - 2, carNoItem.length());
                    //5.1????????????
                    ProcessCarPartEntity insertCarPartEntity = new ProcessCarPartEntity();
                    List<ProcessPersonEntity> insertPersonEntityList = new ArrayList<>();
                    insertCarPartEntity.setCarNo(carNo);
                    insertCarPartEntity.setItemType("1");
                    insertCarPartEntity.setItemCode(processItem.getItemCode());
                    insertCarPartEntity.setItemName(processItem.getItemName());
                    insertCarPartEntity.setDataSource("2");
                    insertCarPartEntity.setTrainsetType(trainsetBaseInfo.getTraintype());
                    //5.2?????????????????????????????????
                    for (ProcessWorker workerItem : twoWorkProcessData.getProcessWorkerList()) {
                        ProcessPersonEntity inertPersonEntity = new ProcessPersonEntity();//????????????
                        inertPersonEntity.setWorkerId(workerItem.getWorkerId());
                        inertPersonEntity.setWorkerName(workerItem.getWorkerName());
                        inertPersonEntity.setWorkerType("1");
                        //5.3?????????????????????????????????????????????
                        List<ProcessTimeRecordEntity> insertTimeRecordEntityList = new ArrayList<>();//????????????????????????????????????
                        ProcessTimeRecordEntity insertStartTimeRecordEntity = new ProcessTimeRecordEntity();
                        insertStartTimeRecordEntity.setTime(twoWorkProcessData.getStartTime());
                        insertStartTimeRecordEntity.setItemTimeState("1");
                        ProcessTimeRecordEntity insertEndTimeRecordEntity = new ProcessTimeRecordEntity();
                        insertEndTimeRecordEntity.setTime(twoWorkProcessData.getEndTime());
                        insertEndTimeRecordEntity.setItemTimeState("4");
                        insertTimeRecordEntityList.add(insertStartTimeRecordEntity);
                        insertTimeRecordEntityList.add(insertEndTimeRecordEntity);
                        inertPersonEntity.setProcessTimeRecordEntityList(insertTimeRecordEntityList);//???????????????????????????????????????????????????????????????
                        insertPersonEntityList.add(inertPersonEntity);//?????????????????????????????????????????????
                        //5.4???????????????????????????
                        if (uploadedFileInfos != null && uploadedFileInfos.size() > 0) {
                            //5.5?????????????????????????????????
                            String s = carNo + processItem.getItemCode();
                            List<UploadedFileInfo> carNoFiles = uploadedFileInfos.stream().filter(t -> t.getParamName().equals(s)).collect(Collectors.toList());
                            if (carNoFiles != null && carNoFiles.size() > 0) {
                                for (UploadedFileInfo carNoFile : carNoFiles) {
                                    ProcessPic processPic = new ProcessPic();
                                    String picId = UUID.randomUUID().toString();
                                    processPic.setPicId(picId);
                                    processPic.setPicAddress(carNoFile.getRelativeUrl());
                                    processPic.setPicName(carNoFile.getOldName());
                                    processPic.setPicType("1");
                                    processPic.setWorkType("2");
                                    processPic.setDayplanId(twoWorkProcessData.getDayPlanId());
                                    processPic.setCreateTime(currentDate);
                                    processPic.setTrainsetId(twoWorkProcessData.getTrainsetId());
                                    processPic.setTrainsetName(twoWorkProcessData.getTrainsetName());
                                    processPic.setUnitCode(unitCode);
                                    processPic.setItemCode(processItem.getItemCode());
                                    processPic.setCarNo(carNo);
                                    processPic.setWorkerId(workerItem.getWorkerId());
                                    insertPicList.add(processPic);
                                }
                            }
                        }
                    }
                    insertCarPartEntity.setProcessPersonEntityList(insertPersonEntityList);//?????????????????????????????????????????????
                    isnertCarPartEntityList.add(insertCarPartEntity);//?????????????????????????????????????????????
                }
            }
        }
        insertPacketEntity.setProcessCarPartEntityList(isnertCarPartEntityList);//??????????????????????????????????????????
        //6.????????????
        if (twoWorkProcessData.getProcessPicList() != null && twoWorkProcessData.getProcessPicList().size() > 0) {
            insertPicList.addAll(twoWorkProcessData.getProcessPicList());
        }
        //7.??????????????????
        if (insertPicList.size() > 0) {
            processPicService.insertBatch(insertPicList);
        }
        //8.??????????????????????????????
        if (summaryEntityList.size() > 0) {
            for(ProcessWorker workerItem : processWorkerList) {
                //????????????????????????????????????????????????
                List<RfidCardSummary> exitSummaryList = MybatisPlusUtils.selectList(
                    rfidCardSummaryService,
                    eqParam(RfidCardSummary::getDayPlanId, dayPlanId),
                    eqParam(RfidCardSummary::getTrainsetId, trainsetId),
                    eqParam(RfidCardSummary::getStuffId, workerItem.getWorkerId()),
                    eqParam(RfidCardSummary::getFlag, "1")
                );
                if(!CollectionUtils.isEmpty(exitSummaryList)){
                    ProcessSummaryEntity startSummary = summaryEntityList.stream().filter(t -> t.getStuffId().equals(workerItem.getWorkerId()) && "1".equals(t.getTimeTag())).findFirst().orElse(null);
                    ProcessSummaryEntity endSummary = summaryEntityList.stream().filter(t -> t.getStuffId().equals(workerItem.getWorkerId()) && "4".equals(t.getTimeTag())).findFirst().orElse(null);
                    RfidCardSummary exitStartSummary = exitSummaryList.stream().filter(t -> "1".equals(t.getTimeTag())).sorted(Comparator.comparing(RfidCardSummary::getRepairTime)).findFirst().orElse(null);
                    RfidCardSummary exitEndSummary = exitSummaryList.stream().filter(t -> "4".equals(t.getTimeTag())).sorted(Comparator.comparing(RfidCardSummary::getRepairTime).reversed()).findFirst().orElse(null);
                    //?????????????????????????????????
                    if(!ObjectUtils.isEmpty(startSummary)){
                        if(!ObjectUtils.isEmpty(exitStartSummary)){
                            //????????????????????????????????????????????????????????????????????????????????????
                            Date startTime = startSummary.getRepairTime();
                            Date exitStartTime = exitStartSummary.getRepairTime();
                            if(!ObjectUtils.isEmpty(startTime)&&!ObjectUtils.isEmpty(exitStartTime)){
                                summaryEntityList.remove(startSummary);
                                int i = startTime.compareTo(exitStartTime);
                                if(!ObjectUtils.isEmpty(i)&&i>0){//??????????????????????????????????????????

                                }else{//???????????????????????????????????????
                                    startSummary.setRecordId(exitStartSummary.getRecordId());
                                    summaryEntityList.add(startSummary);
                                }
                            }else{
                                throw new RuntimeException("??????????????????????????????????????????startTime??????");
                            }
                        }
                    }else{
                        throw new RuntimeException("??????????????????????????????????????????startSummary??????");
                    }
                    //?????????????????????????????????
                    if(!ObjectUtils.isEmpty(endSummary)){
                        if(!ObjectUtils.isEmpty(exitEndSummary)){
                            //????????????????????????????????????????????????????????????????????????????????????
                            Date endTime = endSummary.getRepairTime();
                            Date exitEndTime = exitEndSummary.getRepairTime();
                            if(!ObjectUtils.isEmpty(endTime)&&!ObjectUtils.isEmpty(exitEndTime)){
                                summaryEntityList.remove(endSummary);
                                int i = endTime.compareTo(exitEndTime);
                                if(!ObjectUtils.isEmpty(i)&&i>0){//?????????????????????????????????????????????
                                    endSummary.setRecordId(exitEndSummary.getRecordId());
                                    summaryEntityList.add(endSummary);
                                }
                            }
                        }
                    }else{
                        throw new RuntimeException("??????????????????????????????????????????endSummary??????");
                    }
                }else{

                }
            }
            repairMidGroundService.addOrUpdateSummary(summaryEntityList);
        }
        //???????????????????????????????????????
        List<ProcessPacketEntity> insertPacketEntityList = new ArrayList<>();
        insertPacketEntityList.add(insertPacketEntity);
        repairMidGroundService.addWorkProcess(insertPacketEntityList);
        return true;
    }

    /**
     * @author: ??????
     * @Date: 2021/5/8
     * @Description: ???????????????????????????
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public boolean delTwoWorkProcess(TwoWorkProcessData twoWorkProcessData) {
        //??????????????????
        ShiroUser currentUser = ShiroKit.getUser();
        //????????????
        Date currentDate = new Date();
        //1.???????????????????????????????????????
        String workerId = twoWorkProcessData.getWorkerId();
        String packetCode = twoWorkProcessData.getPacketCode();
        if (StringUtils.isBlank(workerId)) {
            throw new RuntimeException("?????????????????????????????????!");
        }
        if(StringUtils.isBlank(packetCode)){
            throw new RuntimeException("????????????????????????????????????!");
        }
        List<ProcessPerson> totalPersonList = workProcessMapper.getDelPersonList(twoWorkProcessData.getDayPlanId(), twoWorkProcessData.getTrainsetId(), "", null, "1",twoWorkProcessData.getPacketCode());
        List<ProcessPerson> delPersonList = new ArrayList<>();
        if (totalPersonList != null && totalPersonList.size() > 0) {
            String finalWorkerId = workerId;
            delPersonList = totalPersonList.stream().filter(t -> t.getWorkerId().equals(finalWorkerId)).collect(Collectors.toList());
        }
        List<String> picIdList = new ArrayList<>();
        //2.????????????
        for (ProcessPerson item : delPersonList) {
            String personId = item.getProcessPersonId();
            String processId = item.getProcessId();
            String delWorkerId = item.getWorkerId();
            //2.1??????????????????????????????
            List<ProcessTimeRecord> processTimeRecordList = MybatisPlusUtils.selectList(
                processTimeRecordService,
                eqParam(ProcessTimeRecord::getProcessPersonId, personId)
            );
            processTimeRecordList.forEach(t -> {
                t.setFlag("0");
                t.setDelTime(currentDate);
                t.setDelUserCode(currentUser.getStaffId());
                t.setDelUserName(currentUser.getName());
            });
            if (processTimeRecordList.size() > 0) {
                processTimeRecordService.updateBatchById(processTimeRecordList);
            }
            //2.2??????????????????
            ProcessPerson processPerson = new ProcessPerson();
            processPerson.setProcessPersonId(personId);
            processPerson.setFlag("0");
            processPerson.setDelTime(currentDate);
            processPerson.setDelUserCode(currentUser.getStaffId());
            processPerson.setDelUserName(currentUser.getName());
            MybatisPlusUtils.update(
                processPersonService,
                processPerson,
                eqParam(ProcessPerson::getProcessPersonId, personId)
            );
            //2.3???????????????????????? ?????????????????????????????????????????????????????? ????????????????????????????????? ???????????????
            long count = totalPersonList.stream().filter(t -> (!t.getWorkerId().equals(delWorkerId)) && t.getProcessId().equals(processId)).count();
            if (count == 0) {
                ProcessCarPart processCarPart = new ProcessCarPart();
                processCarPart.setProcessId(processId);
                processCarPart.setFlag("0");
                processCarPart.setDelTime(currentDate);
                processCarPart.setDelUserCode(currentUser.getStaffId());
                processCarPart.setDelUserName(currentUser.getName());
                MybatisPlusUtils.update(
                    processCarPartService,
                    processCarPart,
                    eqParam(ProcessCarPart::getProcessId, processId)
                );
            }
        }
        //4.????????????
        //4.1???????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
        List<ProcessPerson> exitProcessPersonList = workProcessMapper.getDelPersonList(twoWorkProcessData.getDayPlanId(), twoWorkProcessData.getTrainsetId(), "", workerId, "1", null);
        if(CollectionUtils.isEmpty(exitProcessPersonList)){
            RfidCardSummary rfidCardSummary = new RfidCardSummary();
            rfidCardSummary.setFlag("0");
            rfidCardSummary.setDelTime(currentDate);
            rfidCardSummary.setDelUserCode(currentUser.getStaffId());
            rfidCardSummary.setDelUserName(currentUser.getName());
            MybatisPlusUtils.update(
                rfidCardSummaryService,
                rfidCardSummary,
                eqParam(RfidCardSummary::getDayPlanId, twoWorkProcessData.getDayPlanId()),
                eqParam(RfidCardSummary::getTrainsetId, twoWorkProcessData.getTrainsetId()),
                eqParam(RfidCardSummary::getStuffId, workerId)
            );
        }
        //?????????????????????(??????????????????)
        List<ProcessPacket> processPacketList = twoWorkProcessData.getProcessPacketList();
        List<String> itemCodeList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(processPacketList)) {
            itemCodeList = processPacketList.stream().flatMap(t -> t.getProcessItemList().stream()).map(ProcessItem::getItemCode).collect(Collectors.toList());
        }
        if (!CollectionUtils.isEmpty(itemCodeList)) {
            MybatisPlusUtils.delete(
                processPicService,
                eqParam(ProcessPic::getDayplanId, twoWorkProcessData.getDayPlanId()),
                inParam(ProcessPic::getItemCode, itemCodeList),
                eqParam(ProcessPic::getTrainsetId, twoWorkProcessData.getTrainsetId()),
                eqParam(ProcessPic::getWorkerId, workerId)
            );
        } else {
            MybatisPlusUtils.delete(
                processPicService,
                eqParam(ProcessPic::getDayplanId, twoWorkProcessData.getDayPlanId()),
                eqParam(ProcessPic::getTrainsetId, twoWorkProcessData.getTrainsetId()),
                eqParam(ProcessPic::getWorkerId, workerId)
            );
        }
        return true;
    }


    /**-----------------------------------------------?????????-------------------------------------------------------------*/

    /**
     * @author: ??????
     * @Date: 2021/5/8
     * @Description: ???????????????????????????
     */
    @Override
    public List<IntegrationProcessData> getIntegrationList(ProcessData processData) {
        //1.????????????
        List<ProcessData> processDataList = workProcessMapper.getIntegrationList(MybatisOgnlUtils.getNewEntityReplacedWildcardChars(processData, ProcessData::getWorkerName));
        //2.???????????????????????????????????????
        Map<ProcessGroupKey, List<ProcessData>> groupList = processDataList.stream().collect(Collectors.groupingBy(v -> {
            ProcessGroupKey key = new ProcessGroupKey();
            key.setDayPlanId(v.getDayPlanId());
            key.setTrainsetId(v.getTrainsetId());
            key.setTrainsetName(v.getTrainsetName());
            key.setItemCode(v.getItemCode());
            key.setItemName(v.getItemName());
            return key;
        }));
        List<IntegrationProcessData> collList = new ArrayList<>();//??????????????????
        groupList.forEach((processGroupKey, entity) -> {
            IntegrationProcessData resItem = new IntegrationProcessData();
            resItem.setDayPlanId(processGroupKey.getDayPlanId());
            resItem.setTrainsetId(processGroupKey.getTrainsetId());
            resItem.setTrainsetName(processGroupKey.getTrainsetName());
            resItem.setCarNos(entity.stream().map(t -> t.getCarNo()).distinct().collect(Collectors.toList()));
            resItem.setItemCode(processGroupKey.getItemCode());
            resItem.setItemName(processGroupKey.getItemName());
            List<ProcessWorker> personList = new ArrayList<>();
            //??????????????????
            List<ProcessData> workList = entity.stream().filter(t -> t.getWorkerType().equals("3")).collect(Collectors.toList());
            if (workList != null && workList.size() > 0) {
                ProcessData lastCompleteData = workList.stream().sorted(Comparator.comparing(ProcessData::getEndTime).reversed()).findFirst().orElse(null);
                //????????????
                personList = new ArrayList<>();
                for (ProcessData work : workList) {
                    ProcessWorker personItem = new ProcessWorker();
                    personItem.setWorkerId(work.getWorkerId());
                    personItem.setWorkerName(work.getWorkerName());
                    if (!personList.contains(personItem)) {
                        personList.add(personItem);
                    }
                }
                resItem.setProcessWorkerList(personList);
                //????????????
                resItem.setDeptCode(workList.get(0).getDeptCode());
                resItem.setDeptName(workList.get(0).getDeptName());
                if(!ObjectUtils.isEmpty(lastCompleteData)){
                    resItem.setWorkCarCount(lastCompleteData.getCarCount());
                    resItem.setWorkEndTime(lastCompleteData.getEndTime());
                }else{
                    //????????????
                    resItem.setWorkCarCount(workList.get(0).getCarCount());
                    //??????????????????
                    resItem.setWorkEndTime(workList.get(0).getEndTime());
                }
            }
            //??????????????????
            List<ProcessData> confirmList = entity.stream().filter(t -> t.getWorkerType().equals("4")).collect(Collectors.toList());
            if (confirmList != null && confirmList.size() > 0) {
                ProcessData lastConfirmData = confirmList.stream().sorted(Comparator.comparing(ProcessData::getEndTime).reversed()).findFirst().orElse(null);
                //????????????
                personList = new ArrayList<>();
                for (ProcessData confirm : confirmList) {
                    ProcessWorker confirmItem = new ProcessWorker();
                    confirmItem.setWorkerId(confirm.getWorkerId());
                    confirmItem.setWorkerName(confirm.getWorkerName());
                    if (!personList.contains(confirmItem)) {
                        personList.add(confirmItem);
                    }
                }
                resItem.setProcessConfirmList(personList);
                //????????????
                resItem.setConfirmDeptCode(confirmList.get(0).getDeptCode());
                resItem.setConfirmDeptName(confirmList.get(0).getDeptName());
                if(!ObjectUtils.isEmpty(lastConfirmData)){
                    //????????????
                    resItem.setConfirmCarCount(lastConfirmData.getCarCount());
                    //??????????????????
                    resItem.setConfirmEndTime(lastConfirmData.getEndTime());
                }else{
                    //????????????
                    resItem.setConfirmCarCount(confirmList.get(0).getCarCount());
                    //??????????????????
                    resItem.setConfirmEndTime(confirmList.get(0).getEndTime());
                }
            }
            //??????????????????????????????
            resItem.setUnitCode(entity.get(0).getUnitCode());
            resItem.setUnitName(entity.get(0).getUnitName());
            resItem.setDataSource(entity.get(0).getDataSource());
            collList.add(resItem);
        });
        List<IntegrationProcessData> resList = collList;
        return resList;
    }

    /**
     * @author: ??????
     * @Date: 2021/5/8
     * @Description: ???????????????????????????
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public boolean addIntegration(IntegrationProcessData integrationProcessData, String phoneFlag) {
        //??????????????????
        List<ProcessWorker> processWorkerList = integrationProcessData.getProcessWorkerList();
        //??????????????????
        List<ProcessWorker> confirmList = integrationProcessData.getProcessConfirmList();

        List<ProcessDept> insertDeptList = new ArrayList<>();//????????????????????????
        List<ProcessPacket> insertPacketList = new ArrayList<>();//???????????????????????????
        List<ProcessCarPart> insertPartList = new ArrayList<>();//????????????????????????
        List<ProcessPerson> insertPersonList = new ArrayList<>();//????????????????????????
        List<ProcessTimeRecord> insertRecordList = new ArrayList<>();//????????????????????????????????????
        List<RfidCardSummary> insertRfidList = new ArrayList<>();//????????????????????????
        List<ProcessPersonConfirm> insertPersonConfirmList = new ArrayList<>();//???????????????????????????
        if (processWorkerList != null && processWorkerList.size() > 0) {
            getIntegrationData(integrationProcessData, processWorkerList, insertDeptList, insertPacketList, insertPartList, insertPersonList, insertRecordList, insertRfidList, insertPersonConfirmList, "3", phoneFlag);
        }
        if (confirmList != null && confirmList.size() > 0) {
            getIntegrationData(integrationProcessData, confirmList, insertDeptList, insertPacketList, insertPartList, insertPersonList, insertRecordList, insertRfidList, insertPersonConfirmList, "4", phoneFlag);
        }
        //??????????????????
        if (insertDeptList.size() > 0) {
            processDeptService.insertBatch(insertDeptList);
        }
        //?????????????????????
        if (insertPacketList.size() > 0) {
            processPacketService.insertBatch(insertPacketList);
        }
        //??????????????????
        if (insertPartList.size() > 0) {
            processCarPartService.insertBatch(insertPartList);
        }
        //??????????????????
        if (insertPersonList.size() > 0) {
            processPersonService.insertBatch(insertPersonList);
        }
        //????????????????????????????????????
        if (insertRecordList.size() > 0) {
            processTimeRecordService.insertBatch(insertRecordList);
        }
        //??????????????????
        if (insertRfidList.size() > 0) {
            rfidCardSummaryService.insertBatch(insertRfidList);
        }
        if (insertPersonConfirmList.size() > 0) {
            processPersonConfirmService.insertBatch(insertPersonConfirmList);
        }
        return true;
    }

    //???????????????????????????
    public void getIntegrationData(IntegrationProcessData integrationProcessData, List<ProcessWorker> processWorkerList, List<ProcessDept> insertDeptList,
                                   List<ProcessPacket> insertPacketList, List<ProcessCarPart> insertPartList, List<ProcessPerson> insertPersonList,
                                   List<ProcessTimeRecord> insertRecordList, List<RfidCardSummary> insertRfidList,
                                   List<ProcessPersonConfirm> insertPersonConfirmList, String workerType, String phoneFlag) {

        //????????????ID????????????
        TrainsetBaseInfo trainsetBaseInfo = remoteService.getTrainsetBaseinfoByID(integrationProcessData.getTrainsetId());

        //??????????????????
        ShiroUser currentUser = ShiroKit.getUser();
        String unitCode = currentUser.getThirdOrgan() == null ? "" : currentUser.getThirdOrgan().getOrganCode();
        String unitName = currentUser.getThirdOrgan() == null ? "" : currentUser.getThirdOrgan().getOrganName();
        //????????????
        Date currentDate = new Date();
        for (String carNoItem : integrationProcessData.getCarNos()) {
            //1.???????????????
            ProcessDept processDept = new ProcessDept();
            String deptId = UUID.randomUUID().toString();
            processDept.setProcessDeptId(deptId);
            processDept.setDeptCode(workerType.equals("3") ? integrationProcessData.getDeptCode() : integrationProcessData.getConfirmDeptCode());
            processDept.setDeptName(workerType.equals("3") ? integrationProcessData.getDeptName() : integrationProcessData.getConfirmDeptName());
            insertDeptList.add(processDept);
            //2.???????????????
            ProcessPacket processPacket = new ProcessPacket();
            String packetId = UUID.randomUUID().toString();
            processPacket.setProcessPacketId(packetId);
            processPacket.setPacketType("6");
            processPacket.setPacketCode(integrationProcessData.getItemCode());
            processPacket.setPacketName(integrationProcessData.getItemName());
            insertPacketList.add(processPacket);
            //3.???????????????
            ProcessCarPart processCarPart = new ProcessCarPart();
            String processId = UUID.randomUUID().toString();
            processCarPart.setProcessId(processId);
            processCarPart.setProcessPacketId(packetId);
            processCarPart.setProcessDeptId(deptId);
            processCarPart.setProcessId(processId);
            processCarPart.setDayPlanId(integrationProcessData.getDayPlanId());
            processCarPart.setTrainsetId(integrationProcessData.getTrainsetId());
            processCarPart.setTrainsetName(integrationProcessData.getTrainsetName());
            if (trainsetBaseInfo != null) {
                processCarPart.setTrainsetType(trainsetBaseInfo.getTraintype());
            }
            processCarPart.setItemCode(integrationProcessData.getItemCode());
            processCarPart.setItemName(integrationProcessData.getItemName());
            processCarPart.setDataSource(phoneFlag);
            processCarPart.setRecorderCode(currentUser.getStaffId());
            processCarPart.setRecorderName(currentUser.getName());
            processCarPart.setRecordTime(currentDate);
            processCarPart.setFlag("1");
            processCarPart.setRepairType("6");
            processCarPart.setCarNo(carNoItem);
            processCarPart.setUnitCode(unitCode);
            processCarPart.setUnitName(unitName);
            insertPartList.add(processCarPart);
            //4.???????????????????????????
            if (processWorkerList != null && processWorkerList.size() > 0) {
                for (ProcessWorker workerItem : processWorkerList) {
                    ProcessPerson processPerson = new ProcessPerson();
                    String personId = UUID.randomUUID().toString();
                    processPerson.setProcessId(processId);
                    processPerson.setProcessPersonId(personId);
                    processPerson.setWorkerId(workerItem.getWorkerId());
                    processPerson.setWorkerName(workerItem.getWorkerName());
                    processPerson.setWorkerType(workerType);
                    processPerson.setFlag("1");
                    processPerson.setRecordTime(currentDate);
                    processPerson.setRecorderCode(currentUser.getStaffId());
                    processPerson.setRecorderName(currentUser.getName());
                    insertPersonList.add(processPerson);
                    //?????????????????????????????????
                    ProcessTimeRecord processTimeRecord = new ProcessTimeRecord();
                    String recordId = UUID.randomUUID().toString();
                    processTimeRecord.setProcessId(recordId);
                    processTimeRecord.setProcessPersonId(personId);
                    processTimeRecord.setFlag("1");
                    processTimeRecord.setItemTimeState("4");
                    processTimeRecord.setRecordTime(currentDate);
                    processTimeRecord.setRecorderCode(currentUser.getStaffId());
                    processTimeRecord.setRecorderName(currentUser.getName());
                    processTimeRecord.setTime(workerType.equals("3") ? integrationProcessData.getWorkEndTime() : integrationProcessData.getConfirmEndTime());
                    insertRecordList.add(processTimeRecord);
                    //???????????????????????????
                    ProcessPersonConfirm personConfirm = new ProcessPersonConfirm();
                    String confirmId = UUID.randomUUID().toString();
                    personConfirm.setConfirmId(confirmId);
                    personConfirm.setProcessPersonId(personId);
                    personConfirm.setCarNoCount(workerType.equals("3") ? integrationProcessData.getWorkCarCount() : integrationProcessData.getConfirmCarCount());
                    insertPersonConfirmList.add(personConfirm);
                }
            }
        }
    }

    /**
     * @author: ??????
     * @Date: 2021/5/8
     * @Description: ???????????????????????????
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public boolean delIntegration(IntegrationProcessData integrationProcessData) {
        //??????????????????
        ShiroUser currentUser = ShiroKit.getUser();
        //????????????
        Date currentDate = new Date();
        //1.????????????
        List<ProcessCarPart> delPartList = MybatisPlusUtils.selectList(
            processCarPartService,
            ColumnParamUtil.filterBlankParams(
                eqParam(ProcessCarPart::getUnitCode, integrationProcessData.getUnitCode()),
                eqParam(ProcessCarPart::getDayPlanId, integrationProcessData.getDayPlanId()),
                eqParam(ProcessCarPart::getTrainsetId, integrationProcessData.getTrainsetId()),
                eqParam(ProcessCarPart::getItemCode, integrationProcessData.getItemCode()),
                eqParam(ProcessCarPart::getFlag, "1")
            )
        );
        if (delPartList != null && delPartList.size() > 0) {
            delPartList.stream().map(delPart -> {
                delPart.setDelUserCode(currentUser.getStaffId());
                delPart.setDelUserName(currentUser.getName());
                delPart.setDelTime(currentDate);
                delPart.setFlag("0");
                return delPart;
            }).collect(Collectors.toList());
        }
        processCarPartService.updateBatchById(delPartList);
        //??????????????????
        List<String> delPartIdList = delPartList.stream().map(t -> t.getProcessId()).collect(Collectors.toList());
        List<ProcessPerson> delPersonList = processPersonService.getDelPersonList(delPartIdList);
        if (delPersonList != null && delPersonList.size() > 0) {
            delPersonList.stream().map(delPerson -> {
                delPerson.setDelUserCode(currentUser.getStaffId());
                delPerson.setDelUserName(currentUser.getName());
                delPerson.setDelTime(currentDate);
                delPerson.setFlag("0");
                return delPerson;
            }).collect(Collectors.toList());
        }
        processPersonService.updateBatchById(delPersonList);
        //??????????????????
        List<String> delPersonIdList = delPersonList.stream().map(t -> t.getProcessPersonId()).collect(Collectors.toList());
        List<ProcessTimeRecord> delTimeRecordList = processTimeRecordService.getDelTimeRecordList(delPersonIdList);
        if (delTimeRecordList != null && delTimeRecordList.size() > 0) {
            delTimeRecordList.stream().map(delTimeRecord -> {
                delTimeRecord.setDelUserCode(currentUser.getStaffId());
                delTimeRecord.setDelUserName(currentUser.getName());
                delTimeRecord.setDelTime(currentDate);
                delTimeRecord.setFlag("0");
                return delTimeRecord;
            }).collect(Collectors.toList());
        }
        processTimeRecordService.updateBatchById(delTimeRecordList);
        return true;
    }

    @Data
    public static class IntegrationGroupKey {
        private String trainsetId;
        private String trainsetName;
    }

    @Override
    public List<Map<String, String>> getIntegrationTrainsetList(String unitCode, String dayPlanId, String repairType) {
        List<Map<String, String>> resList = new ArrayList<>();
        //1.?????????????????????????????????
        List<String> packetTypeCodeList = new ArrayList<>();
        packetTypeCodeList.add("1");
        packetTypeCodeList.add("6");
        String packetTypeStrs = String.join(",", packetTypeCodeList);
        List<ZtTaskPacketEntity> ztTaskPacketEntityList = iRemoteService.getPacketTaskByCondition(dayPlanId, "", packetTypeStrs, "", unitCode).stream().collect(Collectors.toList());
        if (repairType.equals("1")) {
            ztTaskPacketEntityList = ztTaskPacketEntityList.stream().filter(t -> t.getTaskRepairCode().equals("1")).collect(Collectors.toList());
        } else if (repairType.equals("2")) {
            ztTaskPacketEntityList = ztTaskPacketEntityList.stream().filter(t -> t.getTaskRepairCode().equals("2")).collect(Collectors.toList());
        } else if (repairType.equals("6")) {
            ztTaskPacketEntityList = ztTaskPacketEntityList.stream().filter(t -> t.getPacketTypeCode().equals("6")).collect(Collectors.toList());
        }
        //2.???????????????????????????????????????
        //2.1?????????????????????????????????
        ProcessData queryModel = new ProcessData();
        queryModel.setUnitCode(unitCode);
        queryModel.setDayPlanId(dayPlanId);
        queryModel.setRepairType("6");
        queryModel.setPageNum(1);
        queryModel.setPageSize(Integer.MAX_VALUE);
        List<IntegrationProcessData> integrationList = this.getIntegrationList(queryModel);
        //3.????????????????????????????????????
        if (!CollectionUtils.isEmpty(ztTaskPacketEntityList)) {
            Map<IntegrationGroupKey, List<ZtTaskPacketEntity>> groupList = ztTaskPacketEntityList.stream().collect(Collectors.groupingBy(v -> {
                IntegrationGroupKey key = new IntegrationGroupKey();
                key.setTrainsetId(v.getTrainsetId());
                key.setTrainsetName(v.getTrainsetName());
                return key;
            }));
            groupList.forEach((groupKey, groupItem) -> {
                String trainsetId = groupKey.getTrainsetId();
                //??????????????????????????????????????????
                List<String> taskItemCodeList = groupItem.stream().map(t -> t.getPacketCode()).collect(Collectors.toList());
                //???????????????????????????????????????????????????
                if (!CollectionUtils.isEmpty(integrationList)) {
                    List<String> integrationItemCodeList = integrationList.stream().filter(v->v.getTrainsetId().equals(groupKey.getTrainsetId())).map(t ->t.getItemCode()).collect(Collectors.toList());
                    //?????????
                    taskItemCodeList.removeAll(integrationItemCodeList);
                    if (!CollectionUtils.isEmpty(taskItemCodeList)) {
                        Map<String, String> item = new HashMap<>();
                        item.put("trainsetId", groupKey.getTrainsetId());
                        item.put("trainsetName", groupKey.getTrainsetName());
                        resList.add(item);
                    }
                } else {
                    Map<String, String> item = new HashMap<>();
                    item.put("trainsetId", groupKey.getTrainsetId());
                    item.put("trainsetName", groupKey.getTrainsetName());
                    resList.add(item);
                }
            });
        }
        List<Map<String, String>> returnList = resList;
        returnList = returnList.stream().sorted(Comparator.comparing(e -> e.get("trainsetName"))).collect(Collectors.toList());
        return returnList;
    }

    /**
     * @author: ??????
     * @Date: 2021/12/10
     * @Description: ???????????????????????????????????????
     */
    @Override
    public List<ZtTaskPacketEntity> getIntegrationTaskList(String dayPlanId, String trainsetId, String repairDeptCode, String unitCode, String lstPacketTypeCode) {
        List<Map<String, String>> resList = new ArrayList<>();
        //1.?????????????????????????????????
        List<String> packetTypeCodeList = new ArrayList<>();
        packetTypeCodeList.add("6");
        String packetTypeStrs = String.join(",", packetTypeCodeList);
        List<ZtTaskPacketEntity> ztTaskPacketEntityList = iRemoteService.getPacketTaskByCondition(dayPlanId, trainsetId, packetTypeStrs, "", unitCode).stream().collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(ztTaskPacketEntityList)) {
            //2.???????????????????????????????????????
            //2.1?????????????????????????????????
            ProcessData queryModel = new ProcessData();
            queryModel.setUnitCode(unitCode);
            queryModel.setDayPlanId(dayPlanId);
            queryModel.setRepairType("6");
            queryModel.setPageNum(1);
            queryModel.setPageSize(Integer.MAX_VALUE);
            queryModel.setTrainsetId(trainsetId);
            List<IntegrationProcessData> integrationList = this.getIntegrationList(queryModel);
            if (!CollectionUtils.isEmpty(integrationList)) {
                List<String> integrationItemCodeList = integrationList.stream().map(t -> t.getItemCode()).collect(Collectors.toList());
                ztTaskPacketEntityList = ztTaskPacketEntityList.stream().filter(t -> !integrationItemCodeList.contains(t.getPacketCode())).collect(Collectors.toList());
            }
        }
        ztTaskPacketEntityList = ztTaskPacketEntityList.stream().sorted(Comparator.comparing(ZtTaskPacketEntity::getPacketName)).collect(Collectors.toList());
        return ztTaskPacketEntityList;
    }


    /**-----------------------------------------------------??????-------------------------------------------------------------*/

    /**
     * @author: ??????
     * @Date: 2021/5/8
     * @Description: ???????????????????????????
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public boolean addProcessTimeRecordList(List<ProcessTimeRecord> processTimeRecordList) {
        return processTimeRecordService.insertBatch(processTimeRecordList);
    }

    /**
     * @author: ??????
     * @Date: 2021/5/8
     * @Description: ???????????????????????????
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public boolean addProcessLocationList(List<ProcessLocation> processLocationList) {
        return processLocationService.insertBatch(processLocationList);
    }

    /**
     * @author: ??????
     * @Date: 2021/5/8
     * @Description: ???????????????????????????
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public boolean addProcessPicList(List<ProcessPic> processPicList) {
        return processPicService.insertBatch(processPicList);
    }
}