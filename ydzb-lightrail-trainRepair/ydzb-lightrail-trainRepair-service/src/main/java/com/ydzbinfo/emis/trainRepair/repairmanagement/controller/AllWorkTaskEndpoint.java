package com.ydzbinfo.emis.trainRepair.repairmanagement.controller;

import com.alibaba.fastjson.JSONObject;
import com.ydzbinfo.emis.trainRepair.common.model.ConfigParamsModel;
import com.ydzbinfo.emis.trainRepair.common.querymodel.XzyCConfig;
import com.ydzbinfo.emis.trainRepair.common.service.IRemoteService;
import com.ydzbinfo.emis.trainRepair.common.service.IRepairMidGroundService;
import com.ydzbinfo.emis.trainRepair.remotemodel.trainuse.ShuntPlanModel;
import com.ydzbinfo.emis.trainRepair.remotemodel.trainuse.ZtTaskItemEntity;
import com.ydzbinfo.emis.trainRepair.remotemodel.trainuse.ZtTaskPacketEntity;
import com.ydzbinfo.emis.trainRepair.repairmanagement.model.RepairPlanEntity;
import com.ydzbinfo.emis.trainRepair.repairmanagement.model.RepairTaskItemEntity;
import com.ydzbinfo.emis.trainRepair.repairmanagement.model.RepairTaskPacketEntity;
import com.ydzbinfo.emis.trainRepair.repairmanagement.model.worktask.DispatchEntity;
import com.ydzbinfo.emis.trainRepair.taskAllot.querymodel.XzyMTaskallotdept;
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
import com.ydzbinfo.emis.utils.BaseController;
import com.ydzbinfo.emis.utils.DayPlanUtil;
import com.ydzbinfo.hussar.common.annotion.BussinessLog;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.beanutils.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Description: ????????????
 * Author: wuyuechang
 * Create Date Time: 2021/4/20 8:32
 * Update Date Time: 2021/4/20 8:32
 *
 * @see
 */
@RestController
@RequestMapping("/workTask")
@Api(description = "????????????????????????")
public class AllWorkTaskEndpoint extends BaseController {

    protected static final Logger logger = LoggerFactory.getLogger(AllWorkTaskEndpoint.class);

    @Autowired
    IRepairMidGroundService midGroundService;

    @Autowired
    XzyMTaskcarpartService taskcarpartService;
    @Autowired
    XzyMTaskAllotDeptService taskAllotDeptService;
    @Autowired
    XzyMTaskallotpacketService taskallotpacketService;
    @Autowired
    XzyMTaskAllotPersonService taskAllotPersonService;
    @Autowired
    TrainsetPostionService trainsetPostionService;
    @Autowired
    TrackPowerInfoService trackPowerInfoService;

    @Autowired
    IRemoteService remoteService;

    /**
     * @return
     * @author: wuyuechang
     * @Description: ????????????????????????
     * @param:
     * @date: 2021/4/20 8:32
     */
    @ApiOperation("????????????????????????")
    @PostMapping("/GetAllTask")
    @ResponseBody
    public JSONObject getAllTask(@RequestBody Map<String, String> map) {
        JSONObject result = new JSONObject();
        Date nowdate = new Date();
        String dayplanId = map.get("dayplanId");
        String unitCode = map.get("unitCode");
        String deptCode = map.get("deptCode");
        String workerId = map.get("workerId");
        try {
            if (StringUtils.isEmpty(dayplanId)) {
                dayplanId = DayPlanUtil.getDayPlanId(unitCode);
            }
            //??????????????????????????????
            Map<String, Object> queryMap = new HashMap<>();
            queryMap.put("dayPlanId", dayplanId);
            queryMap.put("deptCode", unitCode);
            List<ZtTaskPacketEntity> taskPacketEntities = remoteService.getPacketTaskByDayplanId(dayplanId, unitCode);
            Map<String, String> queryTaskMap = new HashMap();
            queryTaskMap.put("dayPlanID", dayplanId);
            queryTaskMap.put("unitCodeList", unitCode);
            queryTaskMap.put("deptCode", deptCode);
            queryTaskMap.put("workerId", workerId);
            StringBuilder trainsetNameList = new StringBuilder();
            //???????????????list?????????trainsetName?????????String??????????????????
            for (int i = 0; i < taskPacketEntities.size(); i++) {
                String trainsetName = taskPacketEntities.get(i).getTrainsetName();
                if (i == taskPacketEntities.size() - 1) {
                    trainsetNameList.append(trainsetName);
                } else {
                    trainsetNameList.append(trainsetName);
                    trainsetNameList.append(",");
                }
            }
            queryTaskMap.put("trainsetNameList", trainsetNameList.toString());
            //????????????????????????
            List<TrainsetPositionEntity> trainsetPositionEnties = getTrainsetPostion(unitCode, "", trainsetNameList.toString());
            List<DispatchEntity> res = new ArrayList<>();
            //????????????????????????????????????????????????????????????????????????????????????
            //????????????????????????
            for (TrainsetPositionEntity trainset : trainsetPositionEnties) {
                DispatchEntity taskView = new DispatchEntity();
                map.put("trainsetId", trainset.getTrainsetId());
                //???????????????????????????????????????
                List<XzyMTaskallotpacket> taskallotpacketList = taskallotpacketService.getTaskAllotPackets(map);
                for (XzyMTaskallotpacket packet : taskallotpacketList) {
                    //?????????????????????????????????????????????
                    List<XzyMTaskcarpart> taskcarpartList = taskcarpartService.getTaskAllotListByPacket(packet.getTaskAllotPacketId());
                    for (XzyMTaskcarpart carpart : taskcarpartList) {
                        String processId = carpart.getProcessId();
                        taskView.setDayPlanID(carpart.getDayPlanID());
                        taskView.setUnitCode(carpart.getUnitCode());
                        taskView.setUnitName(carpart.getUnitName());
                        //???????????????????????????????????????????????????????????????
                        List<XzyMTaskallotperson> personList = taskAllotPersonService.getTaskAllotPersonListByProcessId(processId);
                        List<XzyMTaskallotperson> taskallotpersonList = new ArrayList<>();
                        List<String> persons = new ArrayList<>();
                        //????????????????????????
                        for (XzyMTaskallotperson taskallotperson : personList) {
                            String person = taskallotperson.getWorkerName();
                            if (persons.size() < 1) {
                                persons.add(person);
                                taskallotpersonList.add(taskallotperson);
                            } else if (!persons.contains(person)) {
                                persons.add(person);
                                taskallotpersonList.add(taskallotperson);
                            }
                        }
                        List<XzyMTaskallotdept> deptList = taskAllotDeptService.getTaskAllotDeptByCarPart(processId);
                        carpart.setWorkerList(taskallotpersonList);
                        if (deptList != null && deptList.size() > 0) {
                            carpart.setXzyMTaskallotdept(deptList.get(0));
                        }
                    }
                    packet.setTaskcarpartList(taskcarpartList);
                    if (taskcarpartList.size() > 0) {
                        packet.setMainCyc(taskcarpartList.get(0).getMainCyc());
                    }
                }
                taskView.setTaskAllotPacket(taskallotpacketList);
                taskView.setTrainsetId(trainset.getTrainsetId());
                taskView.setTrainsetPositionEntity(trainset);
                taskView.setTaskAllotPacket(taskallotpacketList);
                res.add(taskView);
            }
//            //????????????????????????
//            List<TaskAllotPacketEntity> taskPacketList = new ArrayList<>();
//            for(ProcessCarPartEntity processCarPart : processCarPartEntityList){
//                //?????????id????????????????????????????????????????????????
//                TaskAllotPacketEntity packet = new TaskAllotPacketEntity();
//                for(TrainsetPositionEntity trainset : trainsetPositionEnties){
//                    if(processCarPart.getTrainsetID(controller).equals(trainset.getTrainsetId())){
//                        BeanUtils.copyProperties(processCarPart.getProcessPacket(),packet);
//                        taskPacketList.add(packet);
//                    }
//                }
//                //????????????????????????????????????????????????
//                List<TaskAllotPersonEntity> taskPersonList = new ArrayList<>();
//                for(ProcessPersonEntity processPerson : processCarPart.getProcessPersonList()){
//                    TaskAllotPersonEntity person = new TaskAllotPersonEntity();
//                    person.setWorkerID(processPerson.getWorkerID());
//                    person.setWorkerName(processPerson.getWorkerName());
//                    person.setWorkerType(processPerson.getWorkerType());
//                    taskPersonList.add(person);
//                }
//                packet.setTaskAllotPersonEntities(taskPersonList);
//            }
            result.put("code", 0);
            result.put("data", res);
            result.put("dayPlan", dayplanId);
        } catch (Exception e) {
            logger.error(e.getMessage());
            result.put("code", 1);
            result.put("data", result);
            result.put("message", "????????????");
        }
        return result;
    }

    public List<TrainsetPositionEntity> getTrainsetPostion(String unitCodeList, String trackCodeList, String trainsetNameList) {
        List<TrainsetPositionEntity> trainsetPositionList = new ArrayList<>();
        /*List<TrainsetPositionEntity> trainsetPositionList = new ArrayList<>();*/
        try {
            List<String> trackCodes = new ArrayList<>();
            List<String> trainsetNames = new ArrayList<>();
            List<String> unitCodes = new ArrayList<>();
            //????????????CODE????????????
            if (trackCodeList != null && !trackCodeList.equals("")) {
                trackCodes = Arrays.asList(trackCodeList.split(","));
            }
            //?????????????????????????????????
            if (trainsetNameList != null && !trainsetNameList.equals("")) {
                trainsetNames = Arrays.asList(trainsetNameList.split(","));
            }
            if (unitCodeList != null && !unitCodeList.equals("")) {
                unitCodes = Arrays.asList(unitCodeList.split(","));
            } else {
                return null;
            }
            //????????????????????????????????????
            trainsetPositionList = trainsetPostionService.getTrainsetPostion(trackCodes, trainsetNames, unitCodes);
            List<TrackPowerEntity> trackPowerEntityList = new ArrayList<>();

            //????????????????????????????????????
            if (trainsetPositionList.size() > 0) {
                //??????????????????????????????
                for (TrainsetPositionEntity trainsetPositionEntity : trainsetPositionList) {
                    String trackCode = trainsetPositionEntity.getTrackCode();
                    String unitCode = trainsetPositionEntity.getUnitCode();
                    String trackPlaCode = trainsetPositionEntity.getHeadDirectionPlaCode();
                    if (trainsetPositionEntity.getHeadDirectionPla().equals(trainsetPositionEntity.getTailDirectionPla())) {
                        //???????????????????????????????????????
                        trackPowerEntityList = trackPowerInfoService.getTrackPowerInfoByOne(trackCode, unitCode, trackPlaCode);
                    } else {
                        trackPowerEntityList = trackPowerInfoService.getTrackPowerInfoByOne(trackCode, unitCode, "");
                    }
                    trainsetPositionEntity.setTrackPowerEntityList(trackPowerEntityList);
                }
            }
            //??????????????????
        } catch (Exception e) {
            logger.error("????????????", e);
        }
        return trainsetPositionList;
    }

    public List<XzyCConfig> getXzyCConfigs(ConfigParamsModel configParamsModel) {
        List<XzyCConfig> list = new ArrayList<>();
        try {
            list = midGroundService.getXzyCConfigs(configParamsModel);
        } catch (Exception ex) {
            logger.error("??????????????????");
        }
        return list;
    }

    /**
     * @return
     * @author: wuyuechang
     * @Description: ??????????????????
     * @param:
     * @date: 2021/4/20 8:32
     */
    @ApiOperation("??????????????????")
    @RequestMapping("/getPlanTask")
    @ResponseBody
    @BussinessLog(value = "??????????????????", key = "/workTask/getPlanTask", type = "04")
    public JSONObject getPlanTask(@RequestBody Map<String, String> paramMap) {
        String dayPlanId = paramMap.get("dayPlanId");
        String repairDeptCode = paramMap.get("repairDeptCode");
        String unitCode = paramMap.get("unitCode");
        JSONObject result = new JSONObject();
        try {
            //??????????????????
            List<ZtTaskPacketEntity> taskPacketEntities = remoteService.getPacketTaskByCondition(dayPlanId, null, null, repairDeptCode, unitCode);
            Map<String, List<ZtTaskPacketEntity>> packets = taskPacketEntities.stream().collect(Collectors.groupingBy(ZtTaskPacketEntity::getTrainsetId));
            List<RepairPlanEntity> repairPlanEntities = new ArrayList<>();
            for (String key : packets.keySet()) {
                RepairPlanEntity repairPlanEntity = new RepairPlanEntity();
                List<ZtTaskPacketEntity> packetEntities = packets.get(key);
                List<String> repairTaskPacketEntities = new ArrayList<>();
                for (ZtTaskPacketEntity taskPacketEntity : packetEntities) {
                    RepairTaskPacketEntity repairTaskPacketEntity = new RepairTaskPacketEntity();
                    BeanUtils.copyProperties(repairTaskPacketEntity, taskPacketEntity);
                    List<ZtTaskItemEntity> taskItemEntities = taskPacketEntity.getLstTaskItemInfo();
                    List<RepairTaskItemEntity> repairTaskItemEntities = new ArrayList<>();
                    if (taskItemEntities != null && taskItemEntities.size() > 0) {
                        for (ZtTaskItemEntity taskItemEntity : taskItemEntities) {
                            RepairTaskItemEntity repairTaskItemEntity = new RepairTaskItemEntity();
                            BeanUtils.copyProperties(repairTaskItemEntity, taskItemEntity);
                            repairTaskItemEntities.add(repairTaskItemEntity);
                        }
                    }
                    String packetName = repairTaskPacketEntity.getPacketName();
                    repairTaskPacketEntities.add(packetName);
                    String date = dayPlanId.substring(0, dayPlanId.length() - 3);
                    //??????????????????
                    List<ShuntPlanModel> shuntingPlanModelList = null;
                    try {
                        shuntingPlanModelList = remoteService.getShuntingPlanByCondition(unitCode, taskPacketEntity.getTrainsetId(), date + " 00:00:00", date + " 23:59:59");
                    } catch (Exception e) {
                        logger.info("?????????????????????/shuntplan/getShuntingPlanByCondition????????????");
                    }
                    if (shuntingPlanModelList != null && shuntingPlanModelList.size() > 0) {
                        ShuntPlanModel shuntingPlanModel = shuntingPlanModelList.get(0);
                        repairPlanEntity.setPlanRepairTrack(String.valueOf(shuntingPlanModel.getTrackCode()));
                        repairPlanEntity.setTrackName(shuntingPlanModel.getTrackName());
                        repairPlanEntity.setOutTrainNo(String.valueOf(shuntingPlanModel.getOutRunningInfo().size() == 0 ? null : shuntingPlanModel.getOutRunningInfo().get(0).getTrainNo()));
                        repairPlanEntity.setOutTime(String.valueOf(shuntingPlanModel.getOutRunningInfo().size() == 0 ? null : shuntingPlanModel.getOutRunningInfo().get(0).getInOutTime()));
                        repairPlanEntity.setTrainsetName(shuntingPlanModel.getEmuId());
                        repairPlanEntity.setTrackCode(shuntingPlanModel.getTrackCode());
                    }
                    repairPlanEntity.setDayplanId(dayPlanId);
                    repairPlanEntity.setUnitCode(unitCode);
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    repairPlanEntity.setPlanRepairBeginTime(simpleDateFormat.format(taskPacketEntity.getBeginDate()));
                }
//                Collections.sort(repairTaskPacketEntities,Comparator.comparing(RepairTaskPacketEntity::getPacketTypeCode));
                repairPlanEntity.setRepairPackets(repairTaskPacketEntities);
                repairPlanEntities.add(repairPlanEntity);
            }
            result.put("code", 0);
            result.put("data", repairPlanEntities);
            result.put("msg", "????????????");
        } catch (Exception e) {
            logger.error(e.getMessage());
            result.put("code", 1);
            result.put("data", result);
            result.put("msg", "????????????");
        }
        return result;
    }
}
