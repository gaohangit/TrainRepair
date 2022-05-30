package com.ydzbinfo.emis.common.workprocess.service.impl;

import com.ydzbinfo.emis.common.workprocess.dao.WorkProcessMidMapper;
import com.ydzbinfo.emis.common.workprocess.service.*;
import com.ydzbinfo.emis.trainRepair.workprocess.model.*;
import com.ydzbinfo.emis.trainRepair.workprocess.model.base.BaseProcessData;
import com.ydzbinfo.emis.trainRepair.workprocess.querymodel.*;
import com.ydzbinfo.emis.utils.StringUtils;
import com.ydzbinfo.emis.utils.mybatisplus.MybatisPlusUtils;
import com.ydzbinfo.emis.utils.mybatisplus.param.ColumnParam;
import com.ydzbinfo.emis.utils.mybatisplus.param.ColumnParamUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.*;
import java.util.stream.Collectors;

import static com.ydzbinfo.emis.utils.mybatisplus.MybatisPlusUtils.eqParam;

/**
 * @author: 冯帅
 * @Date: 2021-07-01
 * @Description: 作业过程中台服务
 */
@Service
public class WorkProcessMidServiceImpl implements IWorkProcessMidService {

    protected static final Logger logger = LoggerFactory.getLogger(WorkProcessMidServiceImpl.class);

    @Autowired
    IRfidCardSummaryService rfidCardSummaryService;

    @Autowired
    IProcessPacketService processPacketService;

    @Autowired
    IProcessDeptService processDeptService;

    @Autowired
    IProcessCarPartService processCarPartService;

    @Autowired
    IProcessPersonService processPersonService;

    @Autowired
    IProcessTimeRecordService processTimeRecordService;

    @Autowired
    IProcessPersonConfirmService processPersonConfirmService;

    @Autowired
    WorkProcessMidMapper workProcessMidMapper;


    /**
     * 查询作业过程操作总表
     */
    @Override
    public List<ProcessSummaryEntity> getSummaryList(ProcessSummaryEntity processSummaryEntity) {
        List<ProcessSummaryEntity> summaryList = new ArrayList<>();
        if (processSummaryEntity != null) {
            List<RfidCardSummary> rfidCardSummaryList = MybatisPlusUtils.selectList(
                rfidCardSummaryService,
                getColumnParams(processSummaryEntity)
            );
            if (rfidCardSummaryList != null && rfidCardSummaryList.size() > 0) {
                summaryList = rfidCardSummaryList.stream().map(rfidItem -> {
                    ProcessSummaryEntity summaryEntity = new ProcessSummaryEntity();
                    BeanUtils.copyProperties(rfidItem, summaryEntity);
                    return summaryEntity;
                }).collect(Collectors.toList());
            }
        }
        return summaryList;
    }

    /**
     * 添加作业过程操作总表
     */
    @Override
    public boolean addSummary(List<ProcessSummaryEntity> processSummaryEntityList) {
        List<RfidCardSummary> rfidCardSummaryList = new ArrayList<>();
        processSummaryEntityList.stream().map(summaryEntityItem -> {
            RfidCardSummary rfidCardSummary = new RfidCardSummary();
            BeanUtils.copyProperties(summaryEntityItem, rfidCardSummary);
            if(ObjectUtils.isEmpty(summaryEntityItem.getCritertionId())){
                logger.info("中台addSummary参数有问题，前端没有传过来作业标准id-"+new Date());
            }
            rfidCardSummary.setCritertionId(summaryEntityItem.getCritertionId());
            rfidCardSummary.setFlag("1");
            rfidCardSummary.setCreateUserCode(summaryEntityItem.getRecorderCode());
            rfidCardSummary.setCreateUserName(summaryEntityItem.getRecorderName());
            rfidCardSummary.setRecordTime(summaryEntityItem.getRecordTime());
            rfidCardSummary.setUnitCode(summaryEntityItem.getUnitCode());
            rfidCardSummary.setTrainsetType(summaryEntityItem.getTrainsetType());
            rfidCardSummary.setDataSource(summaryEntityItem.getDataSource());
            rfidCardSummaryList.add(rfidCardSummary);
            return summaryEntityItem;
        }).collect(Collectors.toList());
        boolean flag = rfidCardSummaryService.insertBatch(rfidCardSummaryList);
        return flag;
    }

    /**
     * 添加或者更新作业过程操作总表
     */
    @Override
    public boolean addOrUpdateSummary(List<ProcessSummaryEntity> processSummaryEntityList){
        List<RfidCardSummary> rfidCardSummaryList = new ArrayList<>();
        processSummaryEntityList.stream().map(summaryEntityItem -> {
            RfidCardSummary rfidCardSummary = new RfidCardSummary();
            BeanUtils.copyProperties(summaryEntityItem, rfidCardSummary);
            if(ObjectUtils.isEmpty(summaryEntityItem.getCritertionId())){
                logger.info("中台addOrUpdateSummary参数有问题，前端没有传过来作业标准id-"+new Date());
            }
            rfidCardSummary.setCritertionId(summaryEntityItem.getCritertionId());
            rfidCardSummary.setFlag("1");
            rfidCardSummary.setCreateUserCode(summaryEntityItem.getRecorderCode());
            rfidCardSummary.setCreateUserName(summaryEntityItem.getRecorderName());
            rfidCardSummary.setRecordTime(summaryEntityItem.getRecordTime());
            rfidCardSummary.setUnitCode(summaryEntityItem.getUnitCode());
            rfidCardSummary.setTrainsetType(summaryEntityItem.getTrainsetType());
            rfidCardSummary.setDataSource(summaryEntityItem.getDataSource());
            rfidCardSummaryList.add(rfidCardSummary);
            return summaryEntityItem;
        }).collect(Collectors.toList());
        boolean flag = rfidCardSummaryService.insertOrUpdateBatch(rfidCardSummaryList);
        return flag;
    }

    /**
     * 删除作业过程操作总表
     */
    @Override
    public boolean delSummary(ProcessSummaryEntity processSummaryEntity) {
        if (processSummaryEntity != null) {
            RfidCardSummary updateModel = new RfidCardSummary();
            updateModel.setDelUserCode(processSummaryEntity.getRecorderCode());
            updateModel.setDelUserName(processSummaryEntity.getRecorderName());
            updateModel.setDelTime(new Date());
            updateModel.setFlag("0");
            return MybatisPlusUtils.update(
                rfidCardSummaryService,
                updateModel,
                getColumnParams(processSummaryEntity)
            );
        }
        return false;
    }

    private ColumnParam<RfidCardSummary>[] getColumnParams(ProcessSummaryEntity processSummaryEntity) {
        return ColumnParamUtil.filterBlankParams(
            eqParam(RfidCardSummary::getDayPlanId, processSummaryEntity.getDayPlanId()),
            eqParam(RfidCardSummary::getTrainsetId, processSummaryEntity.getTrainsetId()),
            eqParam(RfidCardSummary::getItemCode, processSummaryEntity.getItemCode()),
            eqParam(RfidCardSummary::getItemName, processSummaryEntity.getItemName()),
            eqParam(RfidCardSummary::getStuffId, processSummaryEntity.getStuffId()),
            eqParam(RfidCardSummary::getStuffName, processSummaryEntity.getStuffName()),
            eqParam(RfidCardSummary::getDeptCode, processSummaryEntity.getDeptCode()),
            eqParam(RfidCardSummary::getDeptName, processSummaryEntity.getDeptName()),
            eqParam(RfidCardSummary::getTimeTag, processSummaryEntity.getTimeTag())
        );
    }

    /**
     * 获取作业过程
     */
    @Override
    public List<ProcessPacketEntity> getWorkProcessList(BaseProcessData baseProcessData) {
        List<ProcessPacketEntity> resList = new ArrayList<>();
        resList = workProcessMidMapper.getWorkProcessList(baseProcessData);
        return resList;
    }

    /**
     * 添加作业过程
     */
    @Override
    public boolean addWorkProcess(List<ProcessPacketEntity> processPacketEntityList) {
        List<ProcessPacket> insertPacketList = new ArrayList<>();
        List<ProcessDept> insertDeptList = new ArrayList<>();
        List<ProcessCarPart> insertCarPartList = new ArrayList<>();
        List<ProcessPerson> insertPersonList = new ArrayList<>();
        List<ProcessTimeRecord> insertTimeRecordList = new ArrayList<>();
        List<ProcessPersonConfirm> insertPersonConfirmList = new ArrayList<>();
        Set<ProcessPacket> insertPacketSet = new HashSet<>();
        Set<ProcessDept> insertDeptSet = new HashSet<>();
        String packetId = "";
        String deptId = "";
        String carPartId = "";
        String personId = "";
        String timeRecordId = "";
        String personConfirmId = "";
        for (ProcessPacketEntity packetEntity : processPacketEntityList) {
            String recordUserCode = packetEntity.getRecordCode();
            String recordUserName = packetEntity.getRecordName();
            Date recordTime = packetEntity.getRecordTime();
            //1.作业包
            ProcessPacket insertPacket = new ProcessPacket();
            BeanUtils.copyProperties(packetEntity, insertPacket);
            packetId = UUID.randomUUID().toString();
            insertPacket.setProcessPacketId(packetId);
            //2.作业部门
            ProcessDept insertDept = new ProcessDept();
            BeanUtils.copyProperties(packetEntity, insertDept);
            deptId = UUID.randomUUID().toString();
            insertDept.setProcessDeptId(deptId);
            //3.根据运用所、日计划、项目编码、车组ID、辆序查询辆序表，如果有数据则将主键取出来，否则插入
            //查询辆序表中是否有数据
            List<ProcessCarPart> processCarParts = MybatisPlusUtils.selectList(
                processCarPartService,
                ColumnParamUtil.filterBlankParams(
                    eqParam(ProcessCarPart::getFlag, "1"),
                    eqParam(ProcessCarPart::getDayPlanId, packetEntity.getDayPlanId()),
                    eqParam(ProcessCarPart::getUnitCode, packetEntity.getUnitCode()),
                    eqParam(ProcessCarPart::getTrainsetId, packetEntity.getTrainsetId())
                )
            );
            List<ProcessCarPartEntity> carPartEntitieList = packetEntity.getProcessCarPartEntityList();
            if (carPartEntitieList != null && carPartEntitieList.size() > 0) {
                for (ProcessCarPartEntity carPartEntity : carPartEntitieList) {
                    List<ProcessCarPart> filterCarParts = new ArrayList<>();
                    if (processCarParts != null && processCarParts.size() > 0) {
                        if (StringUtils.isNotBlank(carPartEntity.getPartName())) {
                            filterCarParts = processCarParts.stream().filter(t -> t.getItemCode().equals(carPartEntity.getItemCode()) && carPartEntity.getCarNo().equals(t.getCarNo()) && carPartEntity.getPartName().equals(t.getPartName())).collect(Collectors.toList());
                        } else {
                            filterCarParts = processCarParts.stream().filter(t -> t.getItemCode().equals(carPartEntity.getItemCode()) && carPartEntity.getCarNo().equals(t.getCarNo())).collect(Collectors.toList());
                        }
                    }
                    if (filterCarParts.size() > 0) {//辆序表中已有数据，将辆序表的主键、作业包主键、作业部门主键取出来
                        ProcessCarPart carPartSel = filterCarParts.stream().findFirst().get();
                        packetId = carPartSel.getProcessPacketId();
                        deptId = carPartSel.getProcessDeptId();
                        carPartId = carPartSel.getProcessId();
                        insertPacket.setProcessPacketId(packetId);
                        insertDept.setProcessDeptId(deptId);
                    } else {//辆序表无数据，则插入数据（作业包表、作业部门表、辆序表、作业人员表、作业过程记录表、作业人员确认表）
                        insertPacketSet.add(insertPacket);
                        insertDeptSet.add(insertDept);
                        ProcessCarPart insertCarPart = new ProcessCarPart();
                        BeanUtils.copyProperties(carPartEntity, insertCarPart);
                        carPartId = UUID.randomUUID().toString();
                        insertCarPart.setProcessId(carPartId);
                        insertCarPart.setProcessPacketId(packetId);
                        insertCarPart.setProcessDeptId(deptId);
                        insertCarPart.setDayPlanId(packetEntity.getDayPlanId());
                        insertCarPart.setTrainsetId(packetEntity.getTrainsetId());
                        insertCarPart.setTrainsetName(packetEntity.getTrainsetName());
//                        insertCarPart.setTrainsetType();
                        insertCarPart.setUnitCode(packetEntity.getUnitCode());
                        insertCarPart.setUnitName(packetEntity.getUnitName());
                        insertCarPart.setRecorderCode(recordUserCode);
                        insertCarPart.setRecorderName(recordUserName);
                        insertCarPart.setRecordTime(recordTime);
                        insertCarPart.setRepairType(carPartEntity.getItemType());
                        insertCarPart.setFlag("1");
                        insertCarPartList.add(insertCarPart);
                    }
                    //4.插入作业人员表数据
                    List<ProcessPersonEntity> personEntityList = carPartEntity.getProcessPersonEntityList();
                    if (personEntityList != null && personEntityList.size() > 0) {
                        //查询人员表，如果人员表中已有数据，则将人员表的主键取出来
                        List<ProcessPerson> personSelList = MybatisPlusUtils.selectList(
                            processPersonService,
                            eqParam(ProcessPerson::getFlag, "1"),
                            eqParam(ProcessPerson::getProcessId, carPartId)
                        );
                        for (ProcessPersonEntity personEntity : personEntityList) {
                            List<ProcessPerson> filterPersonList = new ArrayList<>();
                            if (personSelList != null && personSelList.size() > 0) {
                                filterPersonList = personSelList.stream().filter(t -> t.getWorkerId().equals(personEntity.getWorkerId())).collect(Collectors.toList());
                            }
                            if (filterPersonList.size() > 0) {//说明人员表中已有此人的作业数据，此时则将人员表主键取出来
                                personId = filterPersonList.stream().findFirst().get().getProcessPersonId();
                            } else {
                                ProcessPerson insertPerson = new ProcessPerson();
                                BeanUtils.copyProperties(personEntity, insertPerson);
                                personId = UUID.randomUUID().toString();
                                insertPerson.setProcessPersonId(personId);
                                insertPerson.setProcessId(carPartId);
                                insertPerson.setFlag("1");
                                insertPerson.setRecorderCode(recordUserCode);
                                insertPerson.setRecorderName(recordUserName);
                                insertPerson.setRecordTime(recordTime);
                                insertPersonList.add(insertPerson);
                            }
                            //5.插入作业过程记录表
                            List<ProcessTimeRecordEntity> timeRecordEntityList = personEntity.getProcessTimeRecordEntityList();
                            if (timeRecordEntityList != null && timeRecordEntityList.size() > 0) {
                                for (ProcessTimeRecordEntity timeRecordEntity : timeRecordEntityList) {
                                    ProcessTimeRecord insertTimeRecord = new ProcessTimeRecord();
                                    timeRecordId = UUID.randomUUID().toString();
                                    insertTimeRecord.setProcessId(timeRecordId);
                                    insertTimeRecord.setProcessPersonId(personId);
                                    insertTimeRecord.setFlag("1");
                                    insertTimeRecord.setTime(timeRecordEntity.getTime());
                                    insertTimeRecord.setItemTimeState(timeRecordEntity.getItemTimeState());
                                    insertTimeRecord.setRecorderCode(recordUserCode);
                                    insertTimeRecord.setRecorderName(recordUserName);
                                    insertTimeRecord.setRecordTime(recordTime);
                                    insertTimeRecordList.add(insertTimeRecord);
                                }
                            }
                            //6.插入作业确认辆序数据
                            if (personEntity.getCarNoCount() != null && !personEntity.getCarNoCount().equals("")) {
                                //将原有数据删除
                                MybatisPlusUtils.delete(
                                    processPersonConfirmService,
                                    eqParam(ProcessPersonConfirm::getProcessPersonId, personId)
                                );
                                //插入新数据
                                ProcessPersonConfirm insertPersonConfirm = new ProcessPersonConfirm();
                                String confirmId = UUID.randomUUID().toString();
                                insertPersonConfirm.setProcessPersonId(personId);
                                insertPersonConfirm.setCarNoCount(personEntity.getCarNoCount());
                                insertPersonConfirm.setConfirmId(confirmId);
                                insertPersonConfirmList.add(insertPersonConfirm);
                            }
                        }
                    }
                }
            }
        }
        //插入作业包数据
        for (ProcessPacket packet : insertPacketSet) {
            insertPacketList.add(packet);
        }
        if (insertPacketList.size() > 0) {
            processPacketService.insertOrUpdateBatch(insertPacketList);
        }
        //插入作业部门数据
        for (ProcessDept dept : insertDeptSet) {
            insertDeptList.add(dept);
        }
        if (insertDeptList.size() > 0) {
            processDeptService.insertOrUpdateBatch(insertDeptList);
        }
        //插入作业辆序数据
        if (insertCarPartList.size() > 0) {
            processCarPartService.insertBatch(insertCarPartList);
        }
        //插入作业人员数据
        if (insertPersonList.size() > 0) {
            processPersonService.insertBatch(insertPersonList);
        }
        //插入作业时间记录数据
        if (insertTimeRecordList.size() > 0) {
            processTimeRecordService.insertBatch(insertTimeRecordList);
        }
        //插入确认辆序数据
        if (insertPersonConfirmList.size() > 0) {
            processPersonConfirmService.insertBatch(insertPersonConfirmList);
        }
        return true;
    }
}
