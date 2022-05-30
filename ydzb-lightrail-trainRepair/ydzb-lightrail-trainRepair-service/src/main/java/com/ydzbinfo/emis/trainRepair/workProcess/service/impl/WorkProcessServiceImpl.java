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
 * 服务实现类
 * </p>
 *
 * @author 冯帅
 * @since 2020-06-09
 */
@Service
public class WorkProcessServiceImpl implements IWorkProcessService {

    //日志记录
    protected static final Logger logger = LoggerFactory.getLogger(WorkProcessServiceImpl.class);

    @Resource
    WorkProcessMapper workProcessMapper;

    @Resource
    IRemoteService iRemoteService;

    /**
     * 作业过程操作时间记录服务
     */
    @Autowired
    IProcessTimeRecordService IProcessTimeRecordService;

    /**
     * 作业标准配置服务
     */
    @Autowired
    IXzyCWorkcritertionService xzyCWorkcritertionService;

    /**
     * 作业过程开始结束操作时间记录表服务
     */
    @Autowired
    IRfidCardSummaryService rfidCardSummaryService;

    /**
     * 作业过程辆序部件表服务
     */
    @Autowired
    IProcessCarPartService processCarPartService;

    /**
     * 作业过程部门表服务
     */
    @Autowired
    IProcessDeptService processDeptService;

    /**
     * 作业过程包记录表服务
     */
    @Autowired
    IProcessPacketService processPacketService;

    /**
     * 作业过程人员表服务
     */
    @Autowired
    IProcessPersonService processPersonService;

    /**
     * 作业过程时间记录表服务
     */
    @Autowired
    IProcessTimeRecordService processTimeRecordService;

    /**
     * 作业过程定位表服务
     */
    @Autowired
    IProcessLocationService processLocationService;

    /**
     * 作业过程图片表服务
     */
    @Autowired
    IProcessPicService processPicService;

    /**
     * 作业过程确认辆序表服务
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
     * @author: 冯帅
     * @Date: 2021/5/12
     * @Description: 作业过程分组key，用于给作业过程分组
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

    /**-------------------------------------------一级修-------------------------------------------------------------*/

    /**
     * @author: 冯帅
     * @Date: 2021/5/8
     * @Description: 查询一级修列表
     */
    public List<QueryOneWorkProcessData> getOneWorkProcessList(QueryOneWorkProcessData queryOneWorkProcessData) {
        //通过trainsetName给QueryOneWorkProcessData赋值trainsetId
        String trainsetName = queryOneWorkProcessData.getTrainsetName();
        if (ToolUtil.isNotEmpty(trainsetName)) {
            String trainsetid = remoteService.getTrainsetidByName(queryOneWorkProcessData.getTrainsetName());
            queryOneWorkProcessData.setTrainsetId(trainsetid);
        }
        //根据查询条件查询作业过程开始结束操作时间记录表，不能根据作业过程辆序部件表，因为手持机录入的数据可能没有辆序数据
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
        //2.处理组装返回数据
        List<QueryOneWorkProcessData> collList1 = new ArrayList<>();
        groupList1.forEach(((processGroupKey, item) -> {
            //2.1创建最终返回结果对象并赋值
            QueryOneWorkProcessData resItem = new QueryOneWorkProcessData();
            resItem.setDayPlanId(processGroupKey.getDayPlanId());
            resItem.setTrainsetId(processGroupKey.getTrainsetId());
            resItem.setUnitCode(processGroupKey.getUnitCode());
            //根据trainsetId查询trainsetName
            TrainsetBaseInfo trainsetBaseInfo = remoteService.getTrainsetBaseinfoByID(processGroupKey.getTrainsetId());
            if (ToolUtil.isNotEmpty(trainsetBaseInfo)) {
                resItem.setTrainsetName(trainsetBaseInfo.getTrainsetname());
            }
            resItem.setItemCode(processGroupKey.getItemCode());
            resItem.setItemName(processGroupKey.getItemName());
            resItem.setWorkerId(processGroupKey.getWorkerId());
            resItem.setWorkerName(processGroupKey.getWorkerName());
            //根据总表数据获取开始时间等字段并计算
            if (item != null && item.size() > 0) {
                resItem.setDataSource(item.get(0).getDataSource());
                //2.3获取开始时间
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
                //2.4获取结束时间
                List<RfidCardSummary> endSummaryList = item.stream().filter(t -> t.getTimeTag().equals("4")).collect(Collectors.toList());
                RfidCardSummary endSummary = new RfidCardSummary();
                if (endSummaryList != null && endSummaryList.size() > 0) {
                    endSummary = endSummaryList.get(0);
                    if (endSummary != null) {
                        resItem.setEndTime(endSummary.getRepairTime());
                    }
                }
                //2.5获取暂停、继续记录（按时间升序排序）
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
                //2.6计算暂停时长
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
                //2.7计算总时长
                long totalTime = 0;//总时长
                long totalTime1 = 0;
                if (startSummary != null && endSummary != null && endSummary.getRepairTime() != null && startSummary.getRepairTime() != null) {
                    //如果作业时长秒数大于0但是不到1分钟，则直接设置为1分钟
                    if ((endSummary.getRepairTime().getTime() - startSummary.getRepairTime().getTime()) / 1000 / 60 > 0) {
                        totalTime = (endSummary.getRepairTime().getTime() - startSummary.getRepairTime().getTime()) / 1000 / 60 + 1;
                    } else {
                        if ((endSummary.getRepairTime().getTime() - startSummary.getRepairTime().getTime()) / 1000 > 0) {
                            totalTime = 1;
                        }
                    }
                    //作业时长=总时长减去暂停时长
                    totalTime1 = totalTime - suspendedTime;
                    resItem.setTotalTime(totalTime - suspendedTime);
                }
                //2.8根据 开始记录 中的 车组id和项目名称 获取该项目的作业标准
                if (!ObjectUtils.isEmpty(startSummary.getTrainsetId())) {
                    List<XzyCWorkcritertion> xzyCWorkcritertionList = xzyCWorkcritertionService.getWorkcritertionLByTrainsetId(startSummary.getTrainsetId());
                    String itemName = startSummary.getItemName();
                    XzyCWorkcritertion xzyCWorkcritertion = Optional.ofNullable(xzyCWorkcritertionList).orElseGet(ArrayList::new).stream().filter(t -> t.getsItemname().equals(itemName)).findFirst().orElse(null);
                    if (xzyCWorkcritertion != null) {
                        resItem.setTrainsetType(xzyCWorkcritertion.getsTrainsettype());
                        //设置标准图片数量
                        resItem.setStandardPicCount(xzyCWorkcritertion.getiPiccount());
                        //2.9根据开始、结束时间、作业标准中的配置 来计算作业时长、超欠时状态
                        if (startSummary != null && endSummary != null && endSummary.getRepairTime() != null && startSummary.getRepairTime() != null) {
                            //计算超欠时状态
                            if (xzyCWorkcritertion.getiMinworktime() != null) {
                                //最小检修时长不为空时
                                if (totalTime1 < xzyCWorkcritertion.getiMinworktime()) {
                                    resItem.setTimeStatus("欠时");
                                } else if (totalTime1 == xzyCWorkcritertion.getiMinworktime()) {
                                    resItem.setTimeStatus("正常");
                                } else {
                                    if (xzyCWorkcritertion.getiMaxworktime() != null) {
                                        if (totalTime1 <= xzyCWorkcritertion.getiMaxworktime()) {
                                            resItem.setTimeStatus("正常");
                                        } else {
                                            resItem.setTimeStatus("超时");
                                        }
                                    } else {
                                        resItem.setTimeStatus("正常");
                                    }
                                }
                            } else {
                                //最小检修时长为空时
                                if (xzyCWorkcritertion.getiMaxworktime() != null) {
                                    if(totalTime1 <= xzyCWorkcritertion.getiMaxworktime()){
                                        resItem.setTimeStatus("正常");
                                    }else{
                                        resItem.setTimeStatus("超时");
                                    }
                                }else{
                                    //最小检修时长为空，且最大检修时长为空。
                                    resItem.setTimeStatus("未知");
                                }
                            }
                        }
                    } else {
                        resItem.setTimeStatus("未知");
                    }
                } else {
                    resItem.setTimeStatus("未知");
                }
                resItem.setCritertionId(startSummary.getCritertionId());
                resItem.setRecordTime(startSummary.getRecordTime());
            }
            //根据总表中字段筛选出辆序字段
            List<Map<String, Object>> carNoList = workProcessMapper.getCarNoList(processGroupKey.getDayPlanId(), processGroupKey.getTrainsetId(), processGroupKey.getItemCode(), processGroupKey.getWorkerId());
            if (carNoList.size() > 0 && carNoList != null) {
                resItem.setCarNos(carNoList.stream().map(t -> (String) t.get("carNo")).sorted().collect(Collectors.toList()));
            }
            //获取图片数据集合
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
        //根据作业状态过滤
        if (queryOneWorkProcessData.getTimeStatus() != null && !queryOneWorkProcessData.getTimeStatus().equals("")) {
            resList = resList.stream().filter(t -> queryOneWorkProcessData.getTimeStatus().equals(t.getTimeStatus())).collect(Collectors.toList());
        }
        resList = resList.stream().sorted(Comparator.comparing(QueryOneWorkProcessData::getTrainsetName, Comparator.nullsLast(String::compareTo)).thenComparing(QueryOneWorkProcessData::getItemName, Comparator.nullsLast(String::compareTo)).thenComparing(QueryOneWorkProcessData::getWorkerName, Comparator.nullsLast(String::compareTo))).collect(Collectors.toList());
        return resList;
    }

    /**
     * @author: 冯帅
     * @Date: 2021/5/8
     * @Description: 添加一级修作业过程
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public boolean addOneWorkProcess(OneWorkProcessData oneWorkProcessData, List<UploadedFileInfo> uploadedFileInfos) {
        //先判断手持机中是否录入这条数据，如果存在，提示不能增加
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
                    throw new RuntimeException("["+workerItem.getWorkerName()+"]已在手持机维护此记录，无法重复添加");
                }else if("2".equals(dataSource)){
                    throw new RuntimeException("["+workerItem.getWorkerName()+"]已在电脑维护此记录，无法重复添加");
                }else{
                    throw new RuntimeException("["+workerItem.getWorkerName()+"]已维护此记录，无法重复添加");
                }
            }
        }

        //获取当前用户
        ShiroUser currentUser = ShiroKit.getUser();
        //当前时间
        Date currentDate = new Date();
        String unitCode = currentUser.getThirdOrgan() == null ? "" : currentUser.getThirdOrgan().getOrganCode();
        String unitName = currentUser.getThirdOrgan() == null ? "" : currentUser.getThirdOrgan().getOrganName();
        List<ProcessPic> insertPicList = new ArrayList<>();//作业图片集合

        //获取车型
        TrainsetBaseInfo trainsetBaseInfo = remoteService.getTrainsetBaseinfoByID(oneWorkProcessData.getTrainsetId());

        List<ProcessSummaryEntity> insertSummaryEntityList = new ArrayList<>();//总表实体集合
        ProcessPacketEntity insertPacketEntity = new ProcessPacketEntity();//作业包实体
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
        //循环辆序集合 组织辆序数据
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
            //辆序实体下边挂人员实体
            for (ProcessWorker workerItem : oneWorkProcessData.getProcessWorkerList()) {
                ProcessPersonEntity inertPersonEntity = new ProcessPersonEntity();
                inertPersonEntity.setWorkerId(workerItem.getWorkerId());
                inertPersonEntity.setWorkerName(workerItem.getWorkerName());
                inertPersonEntity.setWorkerType("1");
                //人员下边挂作业过程时间记录实体
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
        //循环人员，组织总表数据
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
                //添加作业图片表数据
                if (uploadedFileInfos != null && uploadedFileInfos.size() > 0) {
                    //获取该辆序下的所有图片
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
        //插入图片数据
        if (oneWorkProcessData.getProcessPicList() != null && oneWorkProcessData.getProcessPicList().size() > 0) {
            insertPicList.addAll(oneWorkProcessData.getProcessPicList());
        }
        if (insertPicList.size() > 0) {
            processPicService.insertBatch(insertPicList);
        }
        //调用中台插入总表数据
        if (insertSummaryEntityList.size() > 0) {
            repairMidGroundService.addSummary(insertSummaryEntityList);
        }
        //调用中台插入作业过程相关表
        List<ProcessPacketEntity> insertPacketEntityList = new ArrayList<>();
        insertPacketEntityList.add(insertPacketEntity);
        repairMidGroundService.addWorkProcess(insertPacketEntityList);
        return true;
    }

    /**
     * @author: 冯帅
     * @Date: 2021/5/8
     * @Description: 删除一级修作业过程
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public boolean delOneWorkProcess(OneWorkProcessData oneWorkProcessData) {
        //获取当前用户
        ShiroUser currentUser = ShiroKit.getUser();
        //当前时间
        Date currentDate = new Date();
        //1.查询出来需要删除的人、辆序
        String workerId = oneWorkProcessData.getWorkerId();
        if (workerId == null || workerId.equals("")) {
            if (oneWorkProcessData.getProcessWorkerList() != null && oneWorkProcessData.getProcessWorkerList().size() > 0) {
                workerId = oneWorkProcessData.getProcessWorkerList().get(0).getWorkerId();
            }
        }
        if (workerId == null || workerId.equals("")) {
            throw new RuntimeException("没有选择人员，修改失败!");
        }
        List<ProcessPerson> totalPersonList = workProcessMapper.getDelPersonList(oneWorkProcessData.getDayPlanId(), oneWorkProcessData.getTrainsetId(), oneWorkProcessData.getItemCode(), null, "0",null);
        List<ProcessPerson> delPersonList = new ArrayList<>();
        if (totalPersonList != null && totalPersonList.size() > 0) {
            String finalWorkerId = workerId;
            delPersonList = totalPersonList.stream().filter(t -> t.getWorkerId().equals(finalWorkerId)).collect(Collectors.toList());
        }
        //2.循环删除
        for (ProcessPerson item : delPersonList) {
            String personId = item.getProcessPersonId();
            String processId = item.getProcessId();
            String delWorkerId = item.getWorkerId();
            //2.1删除作业过程时间记录
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
            //2.2删除作业人员
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
            //2.3删除作业过程辆序 先查询该人员所在辆序有无其它作业人员 如果无则删除辆序表数据 否则不删除
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
        //4.删除总表
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
        //删除图片表信息(直接物理删除)
        MybatisPlusUtils.delete(
            processPicService,
            eqParam(ProcessPic::getDayplanId, oneWorkProcessData.getDayPlanId()),
            eqParam(ProcessPic::getItemCode, oneWorkProcessData.getItemCode()),
            eqParam(ProcessPic::getTrainsetId, oneWorkProcessData.getTrainsetId()),
            eqParam(ProcessPic::getWorkerId, workerId)
        );
        return true;
    }

    /**----------------------------------------------------二级修-------------------------------------------------------------*/

    /**
     * @author: 冯帅
     * @Date: 2021/5/8
     * @Description: 查询二级修列表
     */
    @Override
    public Page<QueryTwoWorkProcessData> getTwoWorkProcessList(TwoWorkProcessData twoWorkProcessData) {
        //根据查询条件查询作业过程开始结束操作时间记录表，不能根据作业过程辆序部件表，因为手持机录入的数据可能没有辆序数据
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
        //3.组织数据
        List<QueryTwoWorkProcessData> collList = new ArrayList<>();
        groupList.forEach(((processGroupKey, item) -> {
            QueryTwoWorkProcessData resItem = new QueryTwoWorkProcessData();
            resItem.setDayPlanId(processGroupKey.getDayPlanId());
            resItem.setTrainsetId(processGroupKey.getTrainsetId());
            resItem.setWorkerId(processGroupKey.getWorkerId());
            resItem.setWorkerName(processGroupKey.getWorkerName());
            //根据trainsetId查询trainsetName
            TrainsetBaseInfo trainsetBaseInfo = remoteService.getTrainsetBaseinfoByID(processGroupKey.getTrainsetId());
            if (ToolUtil.isNotEmpty(trainsetBaseInfo)) {
                resItem.setTrainsetName(trainsetBaseInfo.getTrainsetname());
            }
            //获取作业包集合
            List<ProcessPacket> resPacketList = new ArrayList<>();
            //获取项目数量
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
                //获取包下的项目
                processItemList = item.stream().filter(t -> t.getPacketCode().equals(resPacket.getPacketCode())).map(twoData2 -> {
                    ProcessItem processItem1 = new ProcessItem();
                    processItem1.setItemCode(twoData2.getItemCode());
                    processItem1.setItemName(twoData2.getItemName());
                    return processItem1;
                }).distinct().collect(Collectors.toList());


                //key 项目code value 项目名称
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
                    //添加项目名称
                    p.setItemName(itemNameMap.get(p.getItemCode()));
                });
                resItem.setProcessPicList(processPicList);
            }
            resPacket.setProcessItemList(processItemList);
            resPacketList.add(resPacket);
            resItem.setProcessPacketList(resPacketList);
            //获取作业班组
            resItem.setDeptCode(item.stream().map(t -> t.getDeptCode()).collect(Collectors.toList()).get(0));
            resItem.setDeptName(item.stream().map(t -> t.getDeptName()).collect(Collectors.toList()).get(0));
            //获取实际图片数量
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
            //设置开始结束时间，取作业包的开始结束时间（作业包中所有项目的最早和最晚时间）
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
            /**PC端列表开始结束时间展示作业包的开始结束时间（作业包中所有项目取最早和最晚），不再展示车的开始结束时间
            //获取开始结束时间
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
                //开始时间，取最开始的时间
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
                //结束时间,取最末尾的结束时间
                List<RfidCardSummary> endSummaryList = rfidCardSummaryList.stream().filter(t -> t.getTimeTag().equals("4")).sorted(Comparator.comparing(RfidCardSummary::getRepairTime).reversed()).collect(Collectors.toList());
                if (endSummaryList.size() > 0) {
                    RfidCardSummary endSummary = endSummaryList.get(0);
                    Date endSummaryRepairTime= endSummary.getRepairTime();
                    //最末尾的结束时间和最末尾的开始时间比较，如果最末尾的结束时间大于最末尾的开始时间则是结束时间，否则不
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
            //设置运用所编码和名称
            resItem.setUnitCode(item.get(0).getUnitCode());
            resItem.setUnitName(item.get(0).getUnitName());
            resItem.setDataSource(item.get(0).getDataSource());
            //获取图片数据集合
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
        //根据查询条件查询作业过程开始结束操作时间记录表，不能根据作业过程辆序部件表，因为手持机录入的数据可能没有辆序数据
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
     * @author: 冯帅
     * @Date: 2021/5/8
     * @Description: 添加二级修作业过程
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public boolean addTwoWorkProcess(TwoWorkProcessData twoWorkProcessData, List<UploadedFileInfo> uploadedFileInfos) {
        //1.获取相关数据
        //1.1获取当前用户
        ShiroUser currentUser = ShiroKit.getUser();
        String unitCode = currentUser.getThirdOrgan() == null ? "" : currentUser.getThirdOrgan().getOrganCode();
        String unitName = currentUser.getThirdOrgan() == null ? "" : currentUser.getThirdOrgan().getOrganName();
        //1.2获取班组、日计划、车组信息
        String deptCode=twoWorkProcessData.getDeptCode();
        String deptName=twoWorkProcessData.getDeptName();
        String dayPlanId=twoWorkProcessData.getDayPlanId();
        String trainsetId = twoWorkProcessData.getTrainsetId();
        String trainsetName = twoWorkProcessData.getTrainsetName();
        //1.3.获取当前时间
        Date currentDate = new Date();
        //1.4.根据车组ID获取所有辆序
        List<String> carNoList = remoteService.getCarno(trainsetId);
        //1.5获取车型
        TrainsetBaseInfo trainsetBaseInfo = remoteService.getTrainsetBaseinfoByID(trainsetId);
        //1.6获取作业包集合
        List<ProcessPacket> processPacketList = twoWorkProcessData.getProcessPacketList();//作业包集合
        if(CollectionUtils.isEmpty(processPacketList)){
            throw new RuntimeException("参数错误：作业包为空，添加失败");
        }
        //1.7获取作业人员集合
        List<ProcessWorker> processWorkerList = twoWorkProcessData.getProcessWorkerList();//作业人员集合
        if(CollectionUtils.isEmpty(processWorkerList)){
            throw new RuntimeException("参数错误：人员为空，添加失败");
        }
        //2.循环人员和包集合,判断要添加的数据是否已存在
        for(ProcessPacket processPacket : processPacketList){
            for (ProcessWorker workerItem : processWorkerList) {
                logger.info("addTwoWorkProcess调用getWorkCount方法参数：dayPlanId:"+dayPlanId+",trainsetId:"+trainsetId+",packetCode:"+processPacket.getPacketCode()+",deptCode:"+deptCode+",workerId:"+workerItem.getWorkerId());
                Integer workCount = workProcessMapper.getWorkCount(dayPlanId, trainsetId, processPacket.getPacketCode(), deptCode, workerItem.getWorkerId());
                if(!ObjectUtils.isEmpty(workCount)&&workCount>0){
                    throw new RuntimeException("已存在"+workerItem.getWorkerName()+"的"+processPacket.getPacketName()+"作业数据,无法重复添加!");
                }
            }
        }
        //3.组织作业包实体
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
        //4.循环人员集合组织总表实体
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
            insertEndSummaryEntity.setCritertionId(UUID.randomUUID().toString());//暂时给一个UUID
            summaryEntityList.add(insertStartSummaryEntity);
            summaryEntityList.add(insertEndSummaryEntity);
        }

        //5.组织辆序、人员、作业过程记录、图片实体
        List<ProcessCarPartEntity> isnertCarPartEntityList = new ArrayList<>();//辆序实体集合
        List<ProcessPic> insertPicList = new ArrayList<>();//作业图片实体集合
        for (ProcessPacket processPacket : processPacketList) {
            for (ProcessItem processItem : processPacket.getProcessItemList()) {
                for (String carNoItem : carNoList) {
                    //截取辆序字符串，只取最后两位
                    String carNo = carNoItem.substring(carNoItem.length() - 2, carNoItem.length());
                    //5.1辆序实体
                    ProcessCarPartEntity insertCarPartEntity = new ProcessCarPartEntity();
                    List<ProcessPersonEntity> insertPersonEntityList = new ArrayList<>();
                    insertCarPartEntity.setCarNo(carNo);
                    insertCarPartEntity.setItemType("1");
                    insertCarPartEntity.setItemCode(processItem.getItemCode());
                    insertCarPartEntity.setItemName(processItem.getItemName());
                    insertCarPartEntity.setDataSource("2");
                    insertCarPartEntity.setTrainsetType(trainsetBaseInfo.getTraintype());
                    //5.2辆序实体下边挂人员实体
                    for (ProcessWorker workerItem : twoWorkProcessData.getProcessWorkerList()) {
                        ProcessPersonEntity inertPersonEntity = new ProcessPersonEntity();//人员实体
                        inertPersonEntity.setWorkerId(workerItem.getWorkerId());
                        inertPersonEntity.setWorkerName(workerItem.getWorkerName());
                        inertPersonEntity.setWorkerType("1");
                        //5.3人员下边挂作业过程时间记录实体
                        List<ProcessTimeRecordEntity> insertTimeRecordEntityList = new ArrayList<>();//作业过程时间记录实体集合
                        ProcessTimeRecordEntity insertStartTimeRecordEntity = new ProcessTimeRecordEntity();
                        insertStartTimeRecordEntity.setTime(twoWorkProcessData.getStartTime());
                        insertStartTimeRecordEntity.setItemTimeState("1");
                        ProcessTimeRecordEntity insertEndTimeRecordEntity = new ProcessTimeRecordEntity();
                        insertEndTimeRecordEntity.setTime(twoWorkProcessData.getEndTime());
                        insertEndTimeRecordEntity.setItemTimeState("4");
                        insertTimeRecordEntityList.add(insertStartTimeRecordEntity);
                        insertTimeRecordEntityList.add(insertEndTimeRecordEntity);
                        inertPersonEntity.setProcessTimeRecordEntityList(insertTimeRecordEntityList);//将作业过程时间记录实体集合挂到人员实体下边
                        insertPersonEntityList.add(inertPersonEntity);//将人员实体添加到人员实体集合中
                        //5.4添加作业图片表数据
                        if (uploadedFileInfos != null && uploadedFileInfos.size() > 0) {
                            //5.5获取该辆序下的所有图片
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
                    insertCarPartEntity.setProcessPersonEntityList(insertPersonEntityList);//将人员实体集合挂到辆序实体下边
                    isnertCarPartEntityList.add(insertCarPartEntity);//将辆序实体添加到辆序实体集合中
                }
            }
        }
        insertPacketEntity.setProcessCarPartEntityList(isnertCarPartEntityList);//将辆序实体集合挂到包实体下边
        //6.图片数据
        if (twoWorkProcessData.getProcessPicList() != null && twoWorkProcessData.getProcessPicList().size() > 0) {
            insertPicList.addAll(twoWorkProcessData.getProcessPicList());
        }
        //7.插入图片数据
        if (insertPicList.size() > 0) {
            processPicService.insertBatch(insertPicList);
        }
        //8.调用中台插入总表数据
        if (summaryEntityList.size() > 0) {
            for(ProcessWorker workerItem : processWorkerList) {
                //先去数据库中查询是否存在总表数据
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
                    //处理本次作业的开始时间
                    if(!ObjectUtils.isEmpty(startSummary)){
                        if(!ObjectUtils.isEmpty(exitStartSummary)){
                            //比较本次传过来的开始时间和数据库中存在的开始时间哪个更早
                            Date startTime = startSummary.getRepairTime();
                            Date exitStartTime = exitStartSummary.getRepairTime();
                            if(!ObjectUtils.isEmpty(startTime)&&!ObjectUtils.isEmpty(exitStartTime)){
                                summaryEntityList.remove(startSummary);
                                int i = startTime.compareTo(exitStartTime);
                                if(!ObjectUtils.isEmpty(i)&&i>0){//数据库中的时间早，则无需更新

                                }else{//将数据库中的这个时间更新了
                                    startSummary.setRecordId(exitStartSummary.getRecordId());
                                    summaryEntityList.add(startSummary);
                                }
                            }else{
                                throw new RuntimeException("处理本次作业的开始时间错误：startTime为空");
                            }
                        }
                    }else{
                        throw new RuntimeException("处理本次作业的开始时间错误：startSummary为空");
                    }
                    //处理本次作业的结束时间
                    if(!ObjectUtils.isEmpty(endSummary)){
                        if(!ObjectUtils.isEmpty(exitEndSummary)){
                            //比较本次传过来的开始时间和数据库中存在的开始时间哪个更晚
                            Date endTime = endSummary.getRepairTime();
                            Date exitEndTime = exitEndSummary.getRepairTime();
                            if(!ObjectUtils.isEmpty(endTime)&&!ObjectUtils.isEmpty(exitEndTime)){
                                summaryEntityList.remove(endSummary);
                                int i = endTime.compareTo(exitEndTime);
                                if(!ObjectUtils.isEmpty(i)&&i>0){//本次添加的时间最晚，则需要更新
                                    endSummary.setRecordId(exitEndSummary.getRecordId());
                                    summaryEntityList.add(endSummary);
                                }
                            }
                        }
                    }else{
                        throw new RuntimeException("处理本次作业的结束时间错误：endSummary为空");
                    }
                }else{

                }
            }
            repairMidGroundService.addOrUpdateSummary(summaryEntityList);
        }
        //调用中台插入作业过程相关表
        List<ProcessPacketEntity> insertPacketEntityList = new ArrayList<>();
        insertPacketEntityList.add(insertPacketEntity);
        repairMidGroundService.addWorkProcess(insertPacketEntityList);
        return true;
    }

    /**
     * @author: 冯帅
     * @Date: 2021/5/8
     * @Description: 删除二级修作业过程
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public boolean delTwoWorkProcess(TwoWorkProcessData twoWorkProcessData) {
        //获取当前用户
        ShiroUser currentUser = ShiroKit.getUser();
        //当前时间
        Date currentDate = new Date();
        //1.查询出来需要删除的人、辆序
        String workerId = twoWorkProcessData.getWorkerId();
        String packetCode = twoWorkProcessData.getPacketCode();
        if (StringUtils.isBlank(workerId)) {
            throw new RuntimeException("人员参数错误，修改失败!");
        }
        if(StringUtils.isBlank(packetCode)){
            throw new RuntimeException("作业包参数错误，修改失败!");
        }
        List<ProcessPerson> totalPersonList = workProcessMapper.getDelPersonList(twoWorkProcessData.getDayPlanId(), twoWorkProcessData.getTrainsetId(), "", null, "1",twoWorkProcessData.getPacketCode());
        List<ProcessPerson> delPersonList = new ArrayList<>();
        if (totalPersonList != null && totalPersonList.size() > 0) {
            String finalWorkerId = workerId;
            delPersonList = totalPersonList.stream().filter(t -> t.getWorkerId().equals(finalWorkerId)).collect(Collectors.toList());
        }
        List<String> picIdList = new ArrayList<>();
        //2.循环删除
        for (ProcessPerson item : delPersonList) {
            String personId = item.getProcessPersonId();
            String processId = item.getProcessId();
            String delWorkerId = item.getWorkerId();
            //2.1删除作业过程时间记录
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
            //2.2删除作业人员
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
            //2.3删除作业过程辆序 先查询该人员所在辆序有无其它作业人员 如果无则删除辆序表数据 否则不删除
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
        //4.删除总表
        //4.1查询数据库，判断该车下是否有其它包的作业记录，如果一条记录都没有，那么将该车的开始结束时间也删除掉
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
        //删除图片表信息(直接物理删除)
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


    /**-----------------------------------------------一体化-------------------------------------------------------------*/

    /**
     * @author: 冯帅
     * @Date: 2021/5/8
     * @Description: 获取一体化查询列表
     */
    @Override
    public List<IntegrationProcessData> getIntegrationList(ProcessData processData) {
        //1.获取数据
        List<ProcessData> processDataList = workProcessMapper.getIntegrationList(MybatisOgnlUtils.getNewEntityReplacedWildcardChars(processData, ProcessData::getWorkerName));
        //2.根据日计划、车组、项目分组
        Map<ProcessGroupKey, List<ProcessData>> groupList = processDataList.stream().collect(Collectors.groupingBy(v -> {
            ProcessGroupKey key = new ProcessGroupKey();
            key.setDayPlanId(v.getDayPlanId());
            key.setTrainsetId(v.getTrainsetId());
            key.setTrainsetName(v.getTrainsetName());
            key.setItemCode(v.getItemCode());
            key.setItemName(v.getItemName());
            return key;
        }));
        List<IntegrationProcessData> collList = new ArrayList<>();//返回数据集合
        groupList.forEach((processGroupKey, entity) -> {
            IntegrationProcessData resItem = new IntegrationProcessData();
            resItem.setDayPlanId(processGroupKey.getDayPlanId());
            resItem.setTrainsetId(processGroupKey.getTrainsetId());
            resItem.setTrainsetName(processGroupKey.getTrainsetName());
            resItem.setCarNos(entity.stream().map(t -> t.getCarNo()).distinct().collect(Collectors.toList()));
            resItem.setItemCode(processGroupKey.getItemCode());
            resItem.setItemName(processGroupKey.getItemName());
            List<ProcessWorker> personList = new ArrayList<>();
            //作业实体集合
            List<ProcessData> workList = entity.stream().filter(t -> t.getWorkerType().equals("3")).collect(Collectors.toList());
            if (workList != null && workList.size() > 0) {
                ProcessData lastCompleteData = workList.stream().sorted(Comparator.comparing(ProcessData::getEndTime).reversed()).findFirst().orElse(null);
                //作业人员
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
                //作业班组
                resItem.setDeptCode(workList.get(0).getDeptCode());
                resItem.setDeptName(workList.get(0).getDeptName());
                if(!ObjectUtils.isEmpty(lastCompleteData)){
                    resItem.setWorkCarCount(lastCompleteData.getCarCount());
                    resItem.setWorkEndTime(lastCompleteData.getEndTime());
                }else{
                    //作业辆序
                    resItem.setWorkCarCount(workList.get(0).getCarCount());
                    //作业完成时间
                    resItem.setWorkEndTime(workList.get(0).getEndTime());
                }
            }
            //确认实体集合
            List<ProcessData> confirmList = entity.stream().filter(t -> t.getWorkerType().equals("4")).collect(Collectors.toList());
            if (confirmList != null && confirmList.size() > 0) {
                ProcessData lastConfirmData = confirmList.stream().sorted(Comparator.comparing(ProcessData::getEndTime).reversed()).findFirst().orElse(null);
                //作业人员
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
                //作业班组
                resItem.setConfirmDeptCode(confirmList.get(0).getDeptCode());
                resItem.setConfirmDeptName(confirmList.get(0).getDeptName());
                if(!ObjectUtils.isEmpty(lastConfirmData)){
                    //作业辆序
                    resItem.setConfirmCarCount(lastConfirmData.getCarCount());
                    //作业完成时间
                    resItem.setConfirmEndTime(lastConfirmData.getEndTime());
                }else{
                    //作业辆序
                    resItem.setConfirmCarCount(confirmList.get(0).getCarCount());
                    //作业完成时间
                    resItem.setConfirmEndTime(confirmList.get(0).getEndTime());
                }
            }
            //设置运用所编码及名称
            resItem.setUnitCode(entity.get(0).getUnitCode());
            resItem.setUnitName(entity.get(0).getUnitName());
            resItem.setDataSource(entity.get(0).getDataSource());
            collList.add(resItem);
        });
        List<IntegrationProcessData> resList = collList;
        return resList;
    }

    /**
     * @author: 冯帅
     * @Date: 2021/5/8
     * @Description: 无修程作业过程确认
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public boolean addIntegration(IntegrationProcessData integrationProcessData, String phoneFlag) {
        //作业人员集合
        List<ProcessWorker> processWorkerList = integrationProcessData.getProcessWorkerList();
        //确认人员集合
        List<ProcessWorker> confirmList = integrationProcessData.getProcessConfirmList();

        List<ProcessDept> insertDeptList = new ArrayList<>();//插入部门数据集合
        List<ProcessPacket> insertPacketList = new ArrayList<>();//插入作业包数据集合
        List<ProcessCarPart> insertPartList = new ArrayList<>();//插入辆序数据集合
        List<ProcessPerson> insertPersonList = new ArrayList<>();//插入人员数据集合
        List<ProcessTimeRecord> insertRecordList = new ArrayList<>();//插入过程时间记录数据集合
        List<RfidCardSummary> insertRfidList = new ArrayList<>();//插入总表数据集合
        List<ProcessPersonConfirm> insertPersonConfirmList = new ArrayList<>();//插入确认辆序表数据
        if (processWorkerList != null && processWorkerList.size() > 0) {
            getIntegrationData(integrationProcessData, processWorkerList, insertDeptList, insertPacketList, insertPartList, insertPersonList, insertRecordList, insertRfidList, insertPersonConfirmList, "3", phoneFlag);
        }
        if (confirmList != null && confirmList.size() > 0) {
            getIntegrationData(integrationProcessData, confirmList, insertDeptList, insertPacketList, insertPartList, insertPersonList, insertRecordList, insertRfidList, insertPersonConfirmList, "4", phoneFlag);
        }
        //插入部门数据
        if (insertDeptList.size() > 0) {
            processDeptService.insertBatch(insertDeptList);
        }
        //插入作业包数据
        if (insertPacketList.size() > 0) {
            processPacketService.insertBatch(insertPacketList);
        }
        //插入辆序数据
        if (insertPartList.size() > 0) {
            processCarPartService.insertBatch(insertPartList);
        }
        //插入人员数据
        if (insertPersonList.size() > 0) {
            processPersonService.insertBatch(insertPersonList);
        }
        //插入作业过程时间记录数据
        if (insertRecordList.size() > 0) {
            processTimeRecordService.insertBatch(insertRecordList);
        }
        //插入总表数据
        if (insertRfidList.size() > 0) {
            rfidCardSummaryService.insertBatch(insertRfidList);
        }
        if (insertPersonConfirmList.size() > 0) {
            processPersonConfirmService.insertBatch(insertPersonConfirmList);
        }
        return true;
    }

    //组织一体化新增数据
    public void getIntegrationData(IntegrationProcessData integrationProcessData, List<ProcessWorker> processWorkerList, List<ProcessDept> insertDeptList,
                                   List<ProcessPacket> insertPacketList, List<ProcessCarPart> insertPartList, List<ProcessPerson> insertPersonList,
                                   List<ProcessTimeRecord> insertRecordList, List<RfidCardSummary> insertRfidList,
                                   List<ProcessPersonConfirm> insertPersonConfirmList, String workerType, String phoneFlag) {

        //根据车组ID获取车型
        TrainsetBaseInfo trainsetBaseInfo = remoteService.getTrainsetBaseinfoByID(integrationProcessData.getTrainsetId());

        //获取当前用户
        ShiroUser currentUser = ShiroKit.getUser();
        String unitCode = currentUser.getThirdOrgan() == null ? "" : currentUser.getThirdOrgan().getOrganCode();
        String unitName = currentUser.getThirdOrgan() == null ? "" : currentUser.getThirdOrgan().getOrganName();
        //当前时间
        Date currentDate = new Date();
        for (String carNoItem : integrationProcessData.getCarNos()) {
            //1.部门表数据
            ProcessDept processDept = new ProcessDept();
            String deptId = UUID.randomUUID().toString();
            processDept.setProcessDeptId(deptId);
            processDept.setDeptCode(workerType.equals("3") ? integrationProcessData.getDeptCode() : integrationProcessData.getConfirmDeptCode());
            processDept.setDeptName(workerType.equals("3") ? integrationProcessData.getDeptName() : integrationProcessData.getConfirmDeptName());
            insertDeptList.add(processDept);
            //2.作业包数据
            ProcessPacket processPacket = new ProcessPacket();
            String packetId = UUID.randomUUID().toString();
            processPacket.setProcessPacketId(packetId);
            processPacket.setPacketType("6");
            processPacket.setPacketCode(integrationProcessData.getItemCode());
            processPacket.setPacketName(integrationProcessData.getItemName());
            insertPacketList.add(processPacket);
            //3.辆序表数据
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
            //4.添加作业人员表数据
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
                    //添加作业过程记录表数据
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
                    //添加确认辆序表数据
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
     * @author: 冯帅
     * @Date: 2021/5/8
     * @Description: 删除无修程作业过程
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public boolean delIntegration(IntegrationProcessData integrationProcessData) {
        //获取当前用户
        ShiroUser currentUser = ShiroKit.getUser();
        //当前时间
        Date currentDate = new Date();
        //1.删除辆序
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
        //删除作业人员
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
        //删除作业记录
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
        //1.取检修任务中台获取任务
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
        //2.过滤掉已经全部确认过的车组
        //2.1从数据库中获取确认数据
        ProcessData queryModel = new ProcessData();
        queryModel.setUnitCode(unitCode);
        queryModel.setDayPlanId(dayPlanId);
        queryModel.setRepairType("6");
        queryModel.setPageNum(1);
        queryModel.setPageSize(Integer.MAX_VALUE);
        List<IntegrationProcessData> integrationList = this.getIntegrationList(queryModel);
        //3.将作业任务按照作业包分组
        if (!CollectionUtils.isEmpty(ztTaskPacketEntityList)) {
            Map<IntegrationGroupKey, List<ZtTaskPacketEntity>> groupList = ztTaskPacketEntityList.stream().collect(Collectors.groupingBy(v -> {
                IntegrationGroupKey key = new IntegrationGroupKey();
                key.setTrainsetId(v.getTrainsetId());
                key.setTrainsetName(v.getTrainsetName());
                return key;
            }));
            groupList.forEach((groupKey, groupItem) -> {
                String trainsetId = groupKey.getTrainsetId();
                //获取该车组所有的项目编码集合
                List<String> taskItemCodeList = groupItem.stream().map(t -> t.getPacketCode()).collect(Collectors.toList());
                //获取数据库中已确认了的项目编码集合
                if (!CollectionUtils.isEmpty(integrationList)) {
                    List<String> integrationItemCodeList = integrationList.stream().filter(v->v.getTrainsetId().equals(groupKey.getTrainsetId())).map(t ->t.getItemCode()).collect(Collectors.toList());
                    //求差集
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
     * @author: 冯帅
     * @Date: 2021/12/10
     * @Description: 无修程作业过程作业任务集合
     */
    @Override
    public List<ZtTaskPacketEntity> getIntegrationTaskList(String dayPlanId, String trainsetId, String repairDeptCode, String unitCode, String lstPacketTypeCode) {
        List<Map<String, String>> resList = new ArrayList<>();
        //1.取检修任务中台获取任务
        List<String> packetTypeCodeList = new ArrayList<>();
        packetTypeCodeList.add("6");
        String packetTypeStrs = String.join(",", packetTypeCodeList);
        List<ZtTaskPacketEntity> ztTaskPacketEntityList = iRemoteService.getPacketTaskByCondition(dayPlanId, trainsetId, packetTypeStrs, "", unitCode).stream().collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(ztTaskPacketEntityList)) {
            //2.过滤掉已经全部确认过的车组
            //2.1从数据库中获取确认数据
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


    /**-----------------------------------------------------公用-------------------------------------------------------------*/

    /**
     * @author: 冯帅
     * @Date: 2021/5/8
     * @Description: 插入作业时间记录表
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public boolean addProcessTimeRecordList(List<ProcessTimeRecord> processTimeRecordList) {
        return processTimeRecordService.insertBatch(processTimeRecordList);
    }

    /**
     * @author: 冯帅
     * @Date: 2021/5/8
     * @Description: 插入作业过程定位表
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public boolean addProcessLocationList(List<ProcessLocation> processLocationList) {
        return processLocationService.insertBatch(processLocationList);
    }

    /**
     * @author: 冯帅
     * @Date: 2021/5/8
     * @Description: 插入作业过程图片表
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public boolean addProcessPicList(List<ProcessPic> processPicList) {
        return processPicService.insertBatch(processPicList);
    }
}