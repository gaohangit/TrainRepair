package com.ydzbinfo.emis.common.taskAllot.service.impl;

import com.ydzbinfo.emis.common.general.service.ICommonService;
import com.ydzbinfo.emis.common.taskAllot.service.*;
import com.ydzbinfo.emis.common.taskAllot.service.impl.TaskPacketReview.TaskPacketReviewItem;
import com.ydzbinfo.emis.trainRepair.common.model.ConfigParamsModel;
import com.ydzbinfo.emis.trainRepair.common.querymodel.XzyCConfig;
import com.ydzbinfo.emis.trainRepair.common.service.IRemoteService;
import com.ydzbinfo.emis.trainRepair.constant.ArrageTypeEnum;
import com.ydzbinfo.emis.trainRepair.constant.MainCycEnum;
import com.ydzbinfo.emis.trainRepair.constant.PacketTypeEnum;
import com.ydzbinfo.emis.trainRepair.remotemodel.resume.TrainsetInfo;
import com.ydzbinfo.emis.trainRepair.remotemodel.trainuse.ZtTaskPacketEntity;
import com.ydzbinfo.emis.trainRepair.taskAllot.model.PlacePartTaskCars;
import com.ydzbinfo.emis.trainRepair.taskAllot.querymodel.*;
import com.ydzbinfo.emis.utils.CacheUtil;
import com.ydzbinfo.emis.utils.UserUtil;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.util.*;
import java.util.stream.Collectors;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * <p>
 *  派工时向EMIS写入派工数据
 * </p>
 *
 * @author 史艳涛
 * @since 2022-01-14
 */
@Service
public class TaskPacketItem {

    protected static final Logger logger = getLogger(TaskPacketItem.class);
    @Autowired
    IZyMTaskitemallotService zyMTaskitemallotService;
    @Autowired
    IZyMPlaceparttaskService zyMPlaceparttaskService;
    @Autowired
    IZyMRepairpersonallotCurService repairpersonallotCurService;
    @Autowired
    IPzMTaskApplyService pzMTaskApplyService;
    @Autowired
    IPzMTaskApplyPositionService pzMTaskApplyPositionService;
    @Autowired
    ICommonService commonService;
    @Autowired
    TaskPacketReviewItem taskPacketReviewItem;
    @Autowired
    private IRemoteService iRemoteService;

    /**
     * 是否向EMIS写入机检一级修派工数据
     * @return
     */
    private boolean getWriteEmisTechniqueTaskAllot(){
        boolean res = false;
        ConfigParamsModel configParamsModel = new ConfigParamsModel();
        configParamsModel.setType("2");
        configParamsModel.setName("WriteEmisTaskAllot");
        List<XzyCConfig> configs = commonService.getXzyCConfigs(configParamsModel);
        if(!CollectionUtils.isEmpty(configs)){
            XzyCConfig xzyCConfig = configs.stream().findFirst().orElse(null);
            if(!ObjectUtils.isEmpty(xzyCConfig)){
                String paramValue = xzyCConfig.getParamValue();
                if("1".equals(paramValue)){
                    res=true;
                }
            }
        }
        return res;
    }

    /**
     * 是否向EMIS写入派工数据
     * @return
     */
    private boolean getWriteEmisTaskAllot(){
        boolean res = false;
        ConfigParamsModel configParamsModel = new ConfigParamsModel();
        configParamsModel.setType("2");
        configParamsModel.setName("WriteEmisAllTaskAllot");
        List<XzyCConfig> configs = commonService.getXzyCConfigs(configParamsModel);
        if(!CollectionUtils.isEmpty(configs)){
            XzyCConfig xzyCConfig = configs.stream().findFirst().orElse(null);
            if(!ObjectUtils.isEmpty(xzyCConfig)){
                String paramValue = xzyCConfig.getParamValue();
                if("1".equals(paramValue)){
                    res=true;
                }
            }
        }
        return res;
    }

    /**
     * 写入机检一级修派工数据
     * @param taskPacketEntityList 计划数据
     */
    public void setTechniqueTask(List<ZtTaskPacketEntity> taskPacketEntityList){
        try {
            boolean isWrite = getWriteEmisTechniqueTaskAllot();
            if (isWrite) {
                List<ZyMTaskitemallot> lstTaskItemAllots = new ArrayList<ZyMTaskitemallot>();
                List<ZyMPlaceparttask> lstPlaceParts = new ArrayList<ZyMPlaceparttask>();
                for (ZtTaskPacketEntity t : taskPacketEntityList) {
                    //如果已经派工过，则不需要再派工
                    List<ZyMTaskitemallot> result = getTaskItemAllots(t.getDayPlanId(), t.getRepairDeptCode());
                    List<ZyMTaskitemallot> ts = result.stream().filter(f -> f.getTrainsetid().equals(t.getTrainsetName())).collect(Collectors.toList());
                    if (ts.size() > 0) {
                        continue;
                    }
                    //创建派工信息
                    ZyMTaskitemallot taskitemallot = createTaskItemAllot(t);
                    ZyMPlaceparttask placeparttask = createPlacePartTask(taskitemallot);
                    placeparttask.setTrainsetcar("全部");
                    lstTaskItemAllots.add(taskitemallot);
                    lstPlaceParts.add(placeparttask);
                }
                setTaskItemPlacePart(lstTaskItemAllots, lstPlaceParts);
            }
        }catch (Exception e){
            logger.error("写入机检一级修派工数据时引发异常："+e.toString());
        }
    }

    /**
     * 中台写入派工数据，同时向旧派工写入数据
     * @param lstTaskAllotPackets 派工数据
     */
    public void setTaskAllotItems(List<TaskAllotPacketEntity> lstTaskAllotPackets) {
        try {
            boolean isWrite = getWriteEmisTaskAllot();
            if(isWrite){
                List<ZyMTaskitemallot> lstTaskItems = new ArrayList<ZyMTaskitemallot>();
                List<ZyMPlaceparttask> lstPlaceParts = new ArrayList<ZyMPlaceparttask>();
                List<String> lstTrainSet = lstTaskAllotPackets.stream().map(TaskAllotPacketEntity::getTrainsetId).collect(Collectors.toList());
                lstTrainSet = lstTrainSet.stream().distinct().collect(Collectors.toList());
                //循环车组ID
                for(String trainsetid : lstTrainSet){
                    List<PlacePartTaskCars> cars = getDistinctCars(trainsetid);
                    //获取指定车组ID下所有的作业包
                    List<TaskAllotPacketEntity> packetEntities = lstTaskAllotPackets.stream().filter(f->f.getTrainsetId().equals(trainsetid)).collect(Collectors.toList());
                    //循环作业包
                    for(TaskAllotPacketEntity packetEntity : packetEntities){
                        String maincyc = packetEntity.getMainCyc();             //修程
                        String packetType = packetEntity.getPacketType();       //作业包类型

                        List<ZyMTaskitemallot> lstArray = getTaskItemAllotArray(packetEntity.getDayPlanId(), packetEntity.getTrainsetName());

                        //人检一级修
                        if(maincyc.equals(MainCycEnum.MAIN_CYC_1.getValue()) && packetType.equals(PacketTypeEnum.NORMAL.getValue())){
                            for(PlacePartTaskCars car : cars){
                                ZyMTaskitemallot taskitemallot = null;
                                List<ZyMTaskitemallot> ts = lstArray.stream().filter(f->f.getSppacketcode().equals(packetEntity.getPacketCode()) && f.getDistinctcars().equals(car.getDistinctCars())).collect(Collectors.toList());
                                if(ts.size() > 0){
                                    taskitemallot = ts.get(0);
                                }else {
                                    taskitemallot = createTaskItemAllot(packetEntity, maincyc, packetType);
                                    lstTaskItems.add(taskitemallot);
                                }
                                if(taskitemallot != null){
                                    taskitemallot.setDistinctcars(car.getDistinctCars());
                                    taskitemallot.setSplpacketitemcode("");
                                    taskitemallot.setRemark(taskitemallot.getSppacketname());
                                    ZyMPlaceparttask placeparttask = createPlacePartTask(taskitemallot, packetEntity.getTaskAllotItemEntityList().get(0));
                                    if(placeparttask != null){
                                        placeparttask.setTrainsetcar(car.getTrainsetCar());
                                        lstPlaceParts.add(placeparttask);
                                    }
                                }
                            }
                            continue;
                        }
//                        //机检一级修
//                        if(maincyc.equals(MainCycEnum.MAIN_CYC_1.getValue()) && packetType.equals(PacketTypeEnum.MACHINE_CHECK.getValue())) {
//                            ZyMTaskitemallot taskitemallot = createTaskItemAllot(packetEntity, maincyc, packetType);
//                            if(taskitemallot != null){
//                                ZyMPlaceparttask placeparttask = createPlacePartTask(taskitemallot, packetEntity.getTaskAllotItemEntityList().get(0));
//                                if(placeparttask != null){
//                                    placeparttask.setTrainsetcar("全部");
//                                    lstTaskItems.add(taskitemallot);
//                                    lstPlaceParts.add(placeparttask);
//                                }
//                            }
//                            continue;
//                        }
                        //预防性二级修或滤网任务
                        if((maincyc.equals(MainCycEnum.MAIN_CYC_2.getValue()) && packetType.equals(PacketTypeEnum.NORMAL.getValue())) ||
                                (maincyc.equals(MainCycEnum.MAIN_CYC_TEMP.getValue()) && packetType.equals(PacketTypeEnum.SIEVE.getValue()))){
                            for(TaskAllotItemEntity item : packetEntity.getTaskAllotItemEntityList()){
                                for(PlacePartTaskCars car : cars){
                                    ZyMTaskitemallot taskitemallot = null;
                                    List<ZyMTaskitemallot> ts = lstArray.stream().filter(f->f.getSppacketcode().equals(packetEntity.getPacketCode()) &&
                                            f.getSplpacketitemcode().equals(item.getItemCode()) &&
                                            f.getDistinctcars().equals(car.getDistinctCars())).collect(Collectors.toList());
                                    if(ts.size() > 0){
                                        taskitemallot = ts.get(0);
                                    }else{
                                        //判断改数据是否存在
                                        ts = lstTaskItems.stream().filter(f->f.getTrainsetid().equals(packetEntity.getTrainsetName()) &&
                                                f.getSppacketcode().equals(packetEntity.getPacketCode()) &&
                                                f.getSplpacketitemcode().equals(item.getItemCode()) &&
                                                f.getDistinctcars().equals(car.getDistinctCars())).collect(Collectors.toList());
                                        if(ts.size() == 0){
                                            taskitemallot = createTaskItemAllot(packetEntity, maincyc, packetType);
                                            taskitemallot.setSplpacketitemcode(item.getItemCode());
                                            taskitemallot.setSplpacketitemname(item.getItemName());
                                            taskitemallot.setDistinctcars(car.getDistinctCars());
                                            taskitemallot.setCarnoflag("1");
                                            lstTaskItems.add(taskitemallot);
                                        }else {
                                            taskitemallot = ts.get(0);
                                        }
                                    }
                                    if(taskitemallot != null){
                                        ZyMPlaceparttask placeparttask = createPlacePartTask(taskitemallot, item);
                                        if(placeparttask != null){
                                            lstPlaceParts.add(placeparttask);
                                        }
                                    }
                                }
                            }
                            continue;
                        }
                        //故障
                        if(maincyc.equals(MainCycEnum.MAIN_CYC_TEMP.getValue()) && packetType.equals(PacketTypeEnum.FAULT.getValue())) {
                            for (TaskAllotItemEntity item : packetEntity.getTaskAllotItemEntityList()) {
                                ZyMTaskitemallot taskitemallot = null;
                                List<ZyMTaskitemallot> ts = lstArray.stream().filter(f->f.getSppacketcode().equals(packetEntity.getPacketCode()) &&
                                                f.getSplpacketitemcode().equals(item.getItemCode())).collect(Collectors.toList());
                                if(ts.size() >0){
                                    taskitemallot = ts.get(0);
                                }else{
                                    taskitemallot = createTaskItemAllot(packetEntity, maincyc, packetType);
                                    taskitemallot.setSplpacketitemcode(item.getItemCode());
                                    taskitemallot.setSplpacketitemname(item.getItemName());
                                    lstTaskItems.add(taskitemallot);
                                }
                                if (taskitemallot != null) {
                                    ZyMPlaceparttask placeparttask = createPlacePartTask(taskitemallot, item);
                                    if (placeparttask != null) {
                                        lstPlaceParts.add(placeparttask);
                                    }
                                }
                            }
                            continue;
                        }
                        //协同任务
                        if(maincyc.equals(MainCycEnum.MAIN_CYC_TEMP.getValue()) && packetType.equals(PacketTypeEnum.TEAMWORK.getValue())){                //协同任务
                            ZyMTaskitemallot taskitemallot = null;
                            List<ZyMTaskitemallot> ts = lstArray.stream().filter(f->f.getSppacketcode().equals(packetEntity.getPacketCode()) &&
                                    f.getMaincyc().equals(MainCycEnum.MAIN_CYC_TEMP.getValue()) &&
                                    f.getPackettypecode().equals(PacketTypeEnum.TEAMWORK.getValue())).collect(Collectors.toList());
                            if(ts.size() > 0){
                                taskitemallot = ts.get(0);
                            }else{
                                taskitemallot = createTaskItemAllot(packetEntity, maincyc, packetType);
                                taskitemallot.setSplpacketitemcode("");
                                lstTaskItems.add(taskitemallot);
                            }
                            if(taskitemallot != null){
                                ZyMPlaceparttask placeparttask = createPlacePartTask(taskitemallot, packetEntity.getTaskAllotItemEntityList().get(0));
                                if(placeparttask != null){
                                    placeparttask.setTrainsetcar("全部");
                                    lstPlaceParts.add(placeparttask);
                                }
                            }
                            continue;
                        }
                        //临时任务/临时镟轮
                        if(maincyc.equals(MainCycEnum.MAIN_CYC_TEMP.getValue()) && packetType.equals(PacketTypeEnum.TEMPORARY.getValue())){
                            List<String> itemcodes = packetEntity.getTaskAllotItemEntityList().stream().map(TaskAllotItemEntity::getItemCode).collect(Collectors.toList());
                            itemcodes = itemcodes.stream().distinct().collect(Collectors.toList());
                            for(String itemcode : itemcodes){
                                List<TaskAllotItemEntity> items = packetEntity.getTaskAllotItemEntityList().stream().filter(f->f.getItemCode().equals(itemcode)).collect(Collectors.toList());
                                String arrageType = items.get(0).getArrageType();
                                if(arrageType.equals(ArrageTypeEnum.ARRAGE_CARNO.getValue())){
                                    ZyMTaskitemallot taskitemallot = null;
                                    List<ZyMTaskitemallot> ts = lstArray.stream().filter(f->f.getSppacketcode().equals(packetEntity.getPacketCode()) &&
                                            f.getMaincyc().equals(MainCycEnum.MAIN_CYC_TEMP.getValue()) &&
                                            f.getPackettypecode().equals(PacketTypeEnum.TEMPORARY.getValue())).collect(Collectors.toList());
                                    if(ts.size() > 0){
                                        taskitemallot = ts.get(0);
                                    }else{
                                        taskitemallot = createTaskItemAllot(packetEntity, maincyc, packetType);
                                        lstTaskItems.add(taskitemallot);
                                    }
                                    if(taskitemallot != null){
                                        ZyMPlaceparttask placeparttask = createPlacePartTask(taskitemallot, packetEntity.getTaskAllotItemEntityList().get(0));
                                        if(placeparttask != null){
                                            placeparttask.setTrainsetcar("全部");
                                            lstPlaceParts.add(placeparttask);
                                        }
                                    }
                                }
                                else if(arrageType.equals(ArrageTypeEnum.ARRAGE_PART.getValue())){
                                    for(TaskAllotItemEntity item : items){
                                        for(PlacePartTaskCars car : cars){
                                            ZyMTaskitemallot taskitemallot = null;
                                            List<ZyMTaskitemallot> ts = lstArray.stream().filter(f->f.getSppacketcode().equals(packetEntity.getPacketCode()) &&
                                                    f.getSplpacketitemcode().equals(item.getItemCode()) &&
                                                    f.getDistinctcars().equals(car.getDistinctCars())).collect(Collectors.toList());
                                            if(ts.size() > 0){
                                                taskitemallot = ts.get(0);
                                            }else{
                                                //判断改数据是否存在
                                                ts = lstTaskItems.stream().filter(f->f.getTrainsetid().equals(packetEntity.getTrainsetName()) &&
                                                        f.getSppacketcode().equals(packetEntity.getPacketCode()) &&
                                                        f.getSplpacketitemcode().equals(item.getItemCode()) &&
                                                        f.getDistinctcars().equals(car.getDistinctCars())).collect(Collectors.toList());
                                                if(ts.size() == 0){
                                                    taskitemallot = createTaskItemAllot(packetEntity, maincyc, packetType);
                                                    taskitemallot.setSplpacketitemcode(item.getItemCode());
                                                    taskitemallot.setSplpacketitemname(item.getItemName());
                                                    taskitemallot.setDistinctcars(car.getDistinctCars());
                                                    taskitemallot.setCarnoflag("1");
                                                    lstTaskItems.add(taskitemallot);
                                                }
                                            }
                                            if(taskitemallot != null){
                                                ZyMPlaceparttask placeparttask = createPlacePartTask(taskitemallot, item);
                                                if(placeparttask != null){
                                                    lstPlaceParts.add(placeparttask);
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            continue;
                        }
                        //PHM故障修
                        if(maincyc.equals(MainCycEnum.MAIN_CYC_TEMP.getValue()) && packetType.equals(PacketTypeEnum.PHM_FAULT.getValue())) {
                            for (TaskAllotItemEntity item : packetEntity.getTaskAllotItemEntityList()) {
                                ZyMTaskitemallot taskitemallot = null;
                                List<ZyMTaskitemallot> ts = lstTaskItems.stream().filter(f->f.getSplpacketitemcode().equals(item.getItemCode())).collect(Collectors.toList());
                                if(ts.size() ==0){
                                    taskitemallot = createTaskItemAllot(packetEntity, maincyc, packetType);
                                    taskitemallot.setSplpacketitemcode(item.getItemCode());
                                    taskitemallot.setSplpacketitemname(item.getItemName());
                                    taskitemallot.setAssigntaskstate(16);
                                    lstTaskItems.add(taskitemallot);
                                }else{
                                    taskitemallot = ts.get(0);
                                }
                                if (taskitemallot != null) {
                                    ZyMPlaceparttask placeparttask = createPlacePartTask(taskitemallot, item);
                                    if (placeparttask != null) {
                                        placeparttask.setAssigntaskstate(16);
                                        lstPlaceParts.add(placeparttask);
                                    }
                                }
                            }
                            continue;
                        }
                        //PHM预测修
                        if(maincyc.equals(MainCycEnum.MAIN_CYC_TEMP.getValue()) && packetType.equals(PacketTypeEnum.PHM_PREVENTION.getValue())){
                            for(TaskAllotItemEntity item : packetEntity.getTaskAllotItemEntityList()){
                                for(PlacePartTaskCars car : cars){
                                    ZyMTaskitemallot taskitemallot = null;
                                    List<ZyMTaskitemallot> ts = lstArray.stream().filter(f->f.getSppacketcode().equals(packetEntity.getPacketCode()) &&
                                            f.getSplpacketitemcode().equals(item.getItemCode()) &&
                                            f.getDistinctcars().equals(car.getDistinctCars())).collect(Collectors.toList());
                                    if(ts.size() > 0){
                                        taskitemallot = ts.get(0);
                                    }else{
                                        //判断改数据是否存在
                                        ts = lstTaskItems.stream().filter(f->f.getTrainsetid().equals(packetEntity.getTrainsetName()) &&
                                                f.getSppacketcode().equals(packetEntity.getPacketCode()) &&
                                                f.getSplpacketitemcode().equals(item.getItemCode()) &&
                                                f.getDistinctcars().equals(car.getDistinctCars())).collect(Collectors.toList());
                                        if(ts.size() == 0){
                                            taskitemallot = createTaskItemAllot(packetEntity, maincyc, packetType);
                                            taskitemallot.setSplpacketitemcode(item.getItemCode());
                                            taskitemallot.setSplpacketitemname(item.getItemName());
                                            taskitemallot.setDistinctcars(car.getDistinctCars());
                                            taskitemallot.setCarnoflag("1");
                                            taskitemallot.setAssigntaskstate(16);
                                            lstTaskItems.add(taskitemallot);
                                        }else {
                                            taskitemallot = ts.get(0);
                                        }
                                    }
                                    if(taskitemallot != null){
                                        ZyMPlaceparttask placeparttask = createPlacePartTask(taskitemallot, item);
                                        if(placeparttask != null){
                                            placeparttask.setAssigntaskstate(16);
                                            lstPlaceParts.add(placeparttask);
                                        }
                                    }
                                }
                            }
                            continue;
                        }
                        //轮对受电弓复核任务
                        if(maincyc.equals(MainCycEnum.MAIN_CYC_TEMP.getValue()) && packetType.equals(PacketTypeEnum.RECHECK.getValue())){

                            continue;
                        }
                    }
                }
                setTaskItemPlacePart(lstTaskItems, lstPlaceParts);
            }
        } catch (Exception e) {
            logger.error("中台写入派工数据，同时向旧派工写入数据时引发异常：" + e.toString());
        }
    }

    /**
     * 创建派工数据
     * @param p 作业包类型
     * @param mainCyc 修程
     * @param packetType 作业包类型
     * @return
     */
    private ZyMTaskitemallot createTaskItemAllot(TaskAllotPacketEntity p, String mainCyc, String packetType){
        ZyMTaskitemallot taskitemallot = null;
        try{
            taskitemallot = new ZyMTaskitemallot();
            taskitemallot.setTaskitemallotid(UUID.randomUUID().toString());
            taskitemallot.setDayplanid(p.getDayPlanId());
            taskitemallot.setTrainsetid(p.getTrainsetName());
            taskitemallot.setDeptcode(p.getRepairDeptCode());
            taskitemallot.setItemaggregationcode(p.getPacketCode());
            taskitemallot.setItemaggregationname(p.getPacketName());
            taskitemallot.setItemaggregationtype("0");
            taskitemallot.setAllottaskmode("0");
            taskitemallot.setMaincyc(mainCyc);
            taskitemallot.setDistinctcars("0");
            taskitemallot.setPackettypecode(packetType);
            taskitemallot.setSppacketcode(p.getPacketCode());
            taskitemallot.setSppacketname(p.getPacketName());
            taskitemallot.setSplpacketitemcode(p.getPacketCode());
            taskitemallot.setSplpacketitemname(p.getPacketName());
            taskitemallot.setBranchtype("0");
            taskitemallot.setPublishcode(UUID.randomUUID().toString());
            taskitemallot.setCarnoflag("2");
            taskitemallot.setAssigntaskstate(8);
            taskitemallot.setSyncflag("");
            taskitemallot.setRemark("");
            taskitemallot.setTaskallottime(new Date());
        }catch (Exception e){
            taskitemallot = null;
            logger.error("创建派工数据数据时引发异常",e);
        }
        return taskitemallot;
    }

    /**
     * 创建辆序部件数据
     * @param taskitemallot 派工数据主表
     * @param t 项目表
     * @return
     */
    private ZyMPlaceparttask createPlacePartTask(ZyMTaskitemallot taskitemallot, TaskAllotItemEntity t){
        ZyMPlaceparttask placeparttask = null;
        try{
            placeparttask = new ZyMPlaceparttask();
            placeparttask.setPlacepartid(UUID.randomUUID().toString());
            placeparttask.setTrainsetcar(t.getCarNo());
            if(taskitemallot.getPackettypecode().equals(PacketTypeEnum.PHM_FAULT.getValue())){

                List<PzMTaskApply> taskApplies = getTaskApply(t.getItemCode());
                List<PzMTaskApplyPosition> taskApplyPositions = getTaskApplyPosition(t.getItemCode());
                List<PzMTaskApplyPosition> tps = taskApplyPositions.stream().filter(f->f.getCar().equals(t.getCarNo())).collect(Collectors.toList());
                if(taskApplies.size() > 0 && tps.size() > 0){
                    PzMTaskApplyPosition applyPosition = null;
                    if(tps.size() > 1){
                        for(PzMTaskApplyPosition tp : tps){
                            if(tp.getCar().equals(t.getCarNo()) && tp.getPositioncode().equals(t.getPartPosition())){
                                applyPosition = tp;
                                break;
                            }
                        }
                    }else{
                        applyPosition = tps.get(0);
                    }
                    String partName = String.format("%s车%s%s位",applyPosition.getCar(), applyPosition.getPositionname(), applyPosition.getLocationcode());
                    placeparttask.setPartposition(t.getCarNo());
                    placeparttask.setPartpositionnote(partName);
                    placeparttask.setTrainsetstructureid(applyPosition.getPositioncode());
                    placeparttask.setTrainsetstructuretext(applyPosition.getPositionname());
                    placeparttask.setKeyparttypeid(applyPosition.getKeyparttypeid());
                    placeparttask.setDayplanid(taskitemallot.getDayplanid());
                    placeparttask.setPublishcode(taskitemallot.getPublishcode());
                    placeparttask.setParttypename("");
                    placeparttask.setSerialnum(applyPosition.getLocationcode());
                    placeparttask.setBureaunumno(applyPosition.getLocationname());
                    placeparttask.setRemark("");
                }
            }else{
                if(t.getArrageType().equals(ArrageTypeEnum.ARRAGE_PART.getValue())){
                    String location = t.getPartName();
                    location = location.replace(String.format("%s车", t.getCarNo()), "");
                    location = location.replace("车轴", "");
                    location = location.replace("车轮", "");
                    location = location.replace("位", "");
                    placeparttask.setPartposition(location);
                }else{
                    placeparttask.setPartposition(t.getPartPosition());
                }
                placeparttask.setPartpositionnote(t.getPartName());
                placeparttask.setTrainsetstructureid("");
                placeparttask.setTrainsetstructuretext("");
                placeparttask.setKeyparttypeid("");
                placeparttask.setDayplanid(taskitemallot.getDayplanid());
                placeparttask.setPublishcode(taskitemallot.getPublishcode());
                placeparttask.setParttypename("");
                placeparttask.setSerialnum("");
                placeparttask.setBureaunumno("");
                placeparttask.setRemark("");
            }
            placeparttask.setBranchtype("0");
            placeparttask.setTaskitemallotid(taskitemallot.getTaskitemallotid());
            placeparttask.setTaskallottime(new Date());
            placeparttask.setWorkerstuffcode(UserUtil.getUserInfo().getStaffId());
            placeparttask.setWorderstuffname(UserUtil.getUserInfo().getName());
            placeparttask.setAssigntaskstate(8);
            List<String> lstStaffCodes = new ArrayList<String>();
            List<String> lstPersonCodes = new ArrayList<String>();
            List<String> lstPersonNames = new ArrayList<String>();
            List<ZyMRepairpersonallotCur> lstPersons = getRepairPersonAllotCurList(taskitemallot.getDeptcode());
            for(TaskAllotPersonEntity personEntity : t.getTaskAllotPersonEntityList()){
                List<ZyMRepairpersonallotCur> cs = lstPersons.stream().filter(f->f.getStuffcode().equals(personEntity.getWorkerId())).collect(Collectors.toList());
                for(ZyMRepairpersonallotCur c : cs){
                    if(!lstStaffCodes.contains(c.getStuffcode())){
                        lstPersonCodes.add(c.getRepairpersonallotcurid());
                        lstPersonNames.add(c.getStuffname());
                        lstStaffCodes.add(c.getStuffcode());
                    }
                }
            }
            String personCode = String.join(",", lstPersonCodes);
            String personName = String.join(",", lstPersonNames);
            placeparttask.setPersonnamecode(personCode);
            placeparttask.setPersonname(personName);
        }catch (Exception e){
            placeparttask = null;
            logger.error("创建EMIS派工信息辆序部件表时引发异常：" + e.toString());
        }
        return placeparttask;
    }

    /**
     * 创建派工数据
     * @param t
     * @return
     */
    private ZyMTaskitemallot createTaskItemAllot(ZtTaskPacketEntity t){
        ZyMTaskitemallot taskitemallot = null;
        try{
            taskitemallot = new ZyMTaskitemallot();
            taskitemallot.setTaskitemallotid(UUID.randomUUID().toString());
            taskitemallot.setDayplanid(t.getDayPlanId());
            taskitemallot.setTrainsetid(t.getTrainsetName());
            taskitemallot.setDeptcode(t.getRepairDeptCode());
            taskitemallot.setItemaggregationcode(t.getPacketCode());
            taskitemallot.setItemaggregationname(t.getPacketName());
            taskitemallot.setItemaggregationtype("0");
            taskitemallot.setAllottaskmode("0");
            taskitemallot.setMaincyc(MainCycEnum.MAIN_CYC_1.getValue());
            taskitemallot.setDistinctcars("0");
            taskitemallot.setSplpacketitemcode("");
            taskitemallot.setSplpacketitemname(t.getPacketName());
            taskitemallot.setPackettypecode(PacketTypeEnum.MACHINE_CHECK.getValue());
            taskitemallot.setSppacketcode(t.getPacketCode());
            taskitemallot.setSppacketname(t.getPacketName());
            taskitemallot.setBranchtype("0");
            taskitemallot.setPublishcode(UUID.randomUUID().toString());
            taskitemallot.setCarnoflag("2");
            taskitemallot.setAssigntaskstate(8);
            taskitemallot.setSyncflag("");
            taskitemallot.setRemark("");
            taskitemallot.setTaskallottime(new Date());
        }catch (Exception e){
            taskitemallot = null;
            logger.error("创建EMIS派工信息主表时引发异常：" + e.toString());
        }
        return taskitemallot;
    }

    /**
     * 创建辆序部件数据
     * @param taskitemallot 派工数据主表
     * @return
     */
    private ZyMPlaceparttask createPlacePartTask(ZyMTaskitemallot taskitemallot){
        ZyMPlaceparttask placeparttask = null;
        try{
            placeparttask = new ZyMPlaceparttask();
            placeparttask.setPlacepartid(UUID.randomUUID().toString());
            placeparttask.setPartposition("");
            placeparttask.setPartpositionnote("");
            placeparttask.setTrainsetstructureid("");
            placeparttask.setTrainsetstructuretext("");
            placeparttask.setKeyparttypeid("");
            placeparttask.setDayplanid(taskitemallot.getDayplanid());
            placeparttask.setPublishcode(taskitemallot.getPublishcode());
            placeparttask.setParttypename("");
            placeparttask.setSerialnum("");
            placeparttask.setBureaunumno("");
            placeparttask.setRemark("");
            placeparttask.setBranchtype("0");
            placeparttask.setTaskitemallotid(taskitemallot.getTaskitemallotid());
            placeparttask.setTaskallottime(new Date());
            placeparttask.setWorkerstuffcode("system");
            placeparttask.setWorderstuffname("system");
            placeparttask.setAssigntaskstate(8);
            List<String> lstStaffCodes = new ArrayList<String>();
            List<String> lstPersonCodes = new ArrayList<String>();
            List<String> lstPersonNames = new ArrayList<String>();
            List<ZyMRepairpersonallotCur> lstPersons = getRepairPersonAllotCurList(taskitemallot.getDeptcode());
            for(ZyMRepairpersonallotCur c : lstPersons){
                if(!lstStaffCodes.contains(c.getStuffcode())){
                    lstPersonCodes.add(c.getRepairpersonallotcurid());
                    lstPersonNames.add(c.getStuffname());
                    lstStaffCodes.add(c.getStuffcode());
                }
                if(lstStaffCodes.size() >= 20){
                    break;
                }
            }
            String personCode = String.join(",", lstPersonCodes);
            String personName = String.join(",", lstPersonNames);
            placeparttask.setPersonnamecode(personCode);
            placeparttask.setPersonname(personName);
        }catch (Exception e){
            placeparttask = null;
            logger.error("创建EMIS派工信息辆序部件表时引发异常：" + e.toString());
        }
        return placeparttask;
    }

    /**
     * 写入派工和辆序部件数据
     * @param lstTaskItemAllots 派工数据
     * @param lstPlaceParts 辆序部件数据
     */
    @Transactional
    void setTaskItemPlacePart(List<ZyMTaskitemallot> lstTaskItemAllots, List<ZyMPlaceparttask> lstPlaceParts){
        try {
            if(lstTaskItemAllots.size() > 0 && lstPlaceParts.size() >0){
                for (ZyMTaskitemallot t : lstTaskItemAllots) {
                    zyMPlaceparttaskService.deleteByParam(t.getDayplanid(), t.getTrainsetid(), t.getDeptcode(), t.getSppacketcode(), t.getSplpacketitemcode());
                }
                zyMTaskitemallotService.deleteByParam(lstTaskItemAllots);
                zyMTaskitemallotService.insertBatch(lstTaskItemAllots);
                zyMPlaceparttaskService.insertBatch(lstPlaceParts);
            }else if(lstTaskItemAllots.size() == 0 && lstPlaceParts.size() >0){
                List<String> lstDayPlanIds = lstPlaceParts.stream().map(m->m.getDayplanid()).distinct().collect(Collectors.toList());
                for(String dayplanid : lstDayPlanIds){
                    List<String> lstTaskItemAllotIds = lstPlaceParts.stream().filter(f->f.getDayplanid().equals(dayplanid)).map(m->m.getTaskitemallotid()).distinct().collect(Collectors.toList());
                    for(String taskitemallotid : lstTaskItemAllotIds){
                        zyMPlaceparttaskService.deleteByTaskId(dayplanid, taskitemallotid);
                    }
                }
                zyMPlaceparttaskService.insertBatch(lstPlaceParts);
            }
        }catch (Exception e){
            logger.error("写入派工和辆序部件数据时引发异常：" + e.toString());
        }
    }

    /**
     * 获取PHM信息
     * @param applyId PHM数据源主键ID（项目编码）
     * @return
     */
    private List<PzMTaskApply> getTaskApply(String applyId){
        return CacheUtil.getDataUseThreadCache("TaskPacketItem.getTaskApply." + applyId, () -> {
            List<PzMTaskApply> lstTaskApplys = new ArrayList<PzMTaskApply>();
            try {
                HashMap<String, Object> deptParam = new HashMap<>();
                deptParam.put("S_APPLYID", applyId);
                lstTaskApplys = pzMTaskApplyService.selectByMap(deptParam);
            }catch (Exception e){
                lstTaskApplys = new ArrayList<PzMTaskApply>();
                logger.error("获取PHM信息[PzMTaskApply]时失败：ApplyID：" + applyId + "；引发异常：" + e.toString());
            }
            return lstTaskApplys;
        });
    }

    /**
     * 获取PHM信息
     * @param applyId PHM数据源主键ID（项目编码）
     * @return
     */
    private List<PzMTaskApplyPosition> getTaskApplyPosition(String applyId){
        return CacheUtil.getDataUseThreadCache("TaskPacketItem.getTaskApplyPosition." + applyId, () -> {
            List<PzMTaskApplyPosition> lstTaskApplys = new ArrayList<PzMTaskApplyPosition>();
            try {
                HashMap<String, Object> deptParam = new HashMap<>();
                deptParam.put("S_APPLYID", applyId);
                lstTaskApplys = pzMTaskApplyPositionService.selectByMap(deptParam);
            }catch (Exception e){
                lstTaskApplys = new ArrayList<PzMTaskApplyPosition>();
                logger.error("获取PHM信息[PzMTaskApplyPosition]时失败：ApplyID：" + applyId + "；引发异常：" + e.toString());
            }
            return lstTaskApplys;
        });
    }

    /**
     * 获取人员分配信息
     * @param deptcode 检修班组编码
     * @return 人员分配信息
     */
    private List<ZyMRepairpersonallotCur> getRepairPersonAllotCurList(String deptcode){
        return CacheUtil.getDataUseThreadCache("TaskPacketItem.getRepairPersonAllotCurList_" + deptcode, () -> {
            List<ZyMRepairpersonallotCur> lstPersons = new ArrayList<ZyMRepairpersonallotCur>();
            try {
                HashMap<String, Object> deptParam = new HashMap<>();
                deptParam.put("S_DEPTCODE", deptcode);
                lstPersons = repairpersonallotCurService.selectByMap(deptParam);
            }catch (Exception e){
                lstPersons = new ArrayList<ZyMRepairpersonallotCur>();
                logger.error("获取班组：" + deptcode + "人员分配信息时引发异常：" + e.toString());
            }
            return lstPersons;
        });
    }

    /**
     * 获取派工信息
     * @param dayplanid 日计划ID
     * @param deptcode 班组编码
     * @return
     */
    private List<ZyMTaskitemallot> getTaskItemAllots(String dayplanid, String deptcode){
        return CacheUtil.getDataUseThreadCache("TaskPacketItem.getTaskItemAllots_" + dayplanid + "_" + deptcode, () -> {
            List<ZyMTaskitemallot> result = new ArrayList<ZyMTaskitemallot>();
            try {
                result = zyMTaskitemallotService.selectByCondition(dayplanid, deptcode);
            }catch (Exception e){
                result = new ArrayList<ZyMTaskitemallot>();
                logger.error("获取日计划：" + dayplanid +" 班组：" + deptcode + "的机检派工信息时引发异常：" + e.toString());
            }
            return result;
        });
    }

    /**
     * 获取派工信息
     * @param dayplanid 日计划ID
     * @param trainset 车组名称
     * @return
     */
    private List<ZyMTaskitemallot> getTaskItemAllotArray(String dayplanid, String trainset){
        return CacheUtil.getDataUseThreadCache("TaskPacketItem.getTaskItemAllots_" + dayplanid + "_" + trainset, () -> {
            List<ZyMTaskitemallot> result = new ArrayList<ZyMTaskitemallot>();
            try {
                Map<String, Object> condition = new HashMap<String, Object>();
                condition.put("S_DAYPLANID", dayplanid);
                condition.put("S_TRAINSETID", trainset);
                result = zyMTaskitemallotService.selectByMap(condition);
            }catch (Exception e){
                result = new ArrayList<ZyMTaskitemallot>();
                logger.error("获取日计划：" + dayplanid +" 车组名称：" + trainset + "的派工信息时引发异常：" + e.toString());
            }
            return result;
        });
    }

    /**
     * 根据车组ID获取编组及编组名称
     * @param trainSetId 车组ID
     * @return <编组编码，编组名称>
     */
    public List<PlacePartTaskCars> getDistinctCars(String trainSetId){
        List<PlacePartTaskCars> lstCars = new ArrayList<PlacePartTaskCars>();
        List<String> allCarNos = getTrainMarshlType(trainSetId);
        if(allCarNos.size() <= 8){
            PlacePartTaskCars cars = new PlacePartTaskCars();
            cars.setMarshlType(allCarNos.size());
            cars.setDistinctCars("0");
            lstCars.add(cars);
        }
        else if(allCarNos.size() > 8){
            PlacePartTaskCars cars1 = new PlacePartTaskCars();
            cars1.setMarshlType(allCarNos.size());
            cars1.setDistinctCars("1");
            lstCars.add(cars1);

            PlacePartTaskCars cars2 = new PlacePartTaskCars();
            cars2.setMarshlType(allCarNos.size());
            cars2.setDistinctCars("2");
            lstCars.add(cars2);
        }
        return lstCars;
    }
    /**
     * 根据车组ID获取车组详细编组信息
     * @param trainsetId 车组ID
     * @return 编组集合
     */
    public List<String> getTrainMarshlType(String trainsetId) {
        List<String> lstCarNos = new ArrayList<String>();
        try {
            return CacheUtil.getDataUseThreadCache("TaskAllotCommon.getTrainMarshlType_" + trainsetId, () -> {
                TrainsetInfo trainsetInfo = iRemoteService.getTrainsetDetialInfo(trainsetId);
                int marshalCount = Integer.parseInt(trainsetInfo.getIMarshalcount());
                for (int i = 1; i < marshalCount; i++) {
                    String carNo = String.valueOf(i);
                    if (carNo.length() == 1)
                        carNo = "0" + carNo;
                    lstCarNos.add(carNo);
                }
                lstCarNos.add("00");
                return lstCarNos;
            });
        } catch (Exception e) {
            logger.error("根据车组ID获取车组详细编组信息", e);
        }
        return lstCarNos;
    }
}
