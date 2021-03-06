package com.ydzbinfo.emis.trainRepair.taskAllot.service.impl;


import com.alibaba.fastjson.JSONObject;
import com.jxdinfo.hussar.common.userutil.UserUtil;
import com.jxdinfo.hussar.core.shiro.ShiroKit;
import com.jxdinfo.hussar.core.shiro.ShiroUser;
import com.ydzbinfo.emis.trainRepair.bill.fillback.service.IAbnormalHandlerLogService;
import com.ydzbinfo.emis.trainRepair.bill.querymodel.checklist.AbnormalHandlerLog;
import com.ydzbinfo.emis.trainRepair.common.model.ConfigParamsModel;
import com.ydzbinfo.emis.trainRepair.common.querymodel.XzyCConfig;
import com.ydzbinfo.emis.trainRepair.common.service.IRepairMidGroundService;
import com.ydzbinfo.emis.trainRepair.systemmanagent.messagemanagent.model.MessageInfo;
import com.ydzbinfo.emis.trainRepair.systemmanagent.messagemanagent.model.MessageRec;
import com.ydzbinfo.emis.trainRepair.taskAllot.dao.TrainsetPostionMapper;
import com.ydzbinfo.emis.trainRepair.taskAllot.dao.XzyMTaskAllotDeptMapper;
import com.ydzbinfo.emis.trainRepair.taskAllot.dao.XzyMTaskAllotPersonMapper;
import com.ydzbinfo.emis.trainRepair.taskAllot.dao.XzyMTaskcarpartMapper;
import com.ydzbinfo.emis.trainRepair.taskAllot.model.GroupModel;
import com.ydzbinfo.emis.trainRepair.taskAllot.model.PerSonNelModel;
import com.ydzbinfo.emis.trainRepair.taskAllot.model.PostModel;
import com.ydzbinfo.emis.trainRepair.taskAllot.querymodel.*;
import com.ydzbinfo.emis.trainRepair.taskAllot.service.ITaskAllotPersonPostService;
import com.ydzbinfo.emis.trainRepair.taskAllot.service.PerSonNelService;
import com.ydzbinfo.emis.trainRepair.taskAllot.service.XzyMTaskcarpartService;
import com.ydzbinfo.emis.utils.CustomServiceNameEnum;
import com.ydzbinfo.emis.utils.RestRequestKitUseLogger;
import com.ydzbinfo.emis.utils.ServiceNameEnum;
import com.ydzbinfo.emis.utils.result.RestResultGeneric;
import com.ydzbinfo.hussar.system.bsp.organ.SysStaff;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class XzyMTaskcarpartServiceImpl implements XzyMTaskcarpartService {

    private final static Logger logger = LoggerFactory.getLogger(XzyMTaskcarpartServiceImpl.class);
    private final String messageServiceId = ServiceNameEnum.MessageService.getId();

    //????????????
    private final String midGroundServiceId = CustomServiceNameEnum.MidGroundService.getId();

    @Autowired
    XzyMTaskcarpartMapper taskcarpartMapper;
    @Autowired
    XzyMTaskAllotPersonMapper taskAllotPersonMapper;
    @Autowired
    XzyMTaskAllotDeptMapper taskAllotDeptMapper;
    @Autowired
    TrainsetPostionMapper trainsetPostionMapper;
    @Autowired
    ITaskAllotPersonPostService iTaskAllotPersonPostService;
    @Resource
    private PerSonNelService perSonNelService;
    @Autowired
    IAbnormalHandlerLogService abnormalHandlerLogService;

    @Autowired
    private IRepairMidGroundService midGroundService;

    @Override
    public List<XzyMTaskcarpart> getTaskAllotListByPacket(String packetId) {
        return taskcarpartMapper.getTaskAllotListByPacket(packetId);
    }

    @Data
    public static class TempKey{
        private String keyCode;
        private String keyName;
    }


    public boolean setTaskAllotData(List<XzyMTaskallotpacket> xzyMTaskallotpacketList, List<XzyMTaskcarpart> taskcarpartList) {
        boolean res = true;
        //??????????????????
        ShiroUser currentUser = ShiroKit.getUser();
        //??????????????????
        Date currentDate = new Date();
        String deptID = UUID.randomUUID().toString();
        XzyMTaskallotdept taskallotdept = new XzyMTaskallotdept();
        //??????????????????
        List<GroupModel> groupModels = perSonNelService.getPerSonNelList(null, null);
        //1.????????????????????????????????????
        List<TaskAllotPacketEntity> taskAllotPacketEntityList = new ArrayList<>();//??????????????????
        for (XzyMTaskallotpacket packetItem : xzyMTaskallotpacketList) {
            TaskAllotPacketEntity packetEntity = new TaskAllotPacketEntity();
            packetEntity.setMainCyc(packetItem.getMainCyc());
            packetEntity.setPacketCode(packetItem.getPacketCode());
            packetEntity.setPacketName(packetItem.getPacketName());
            packetEntity.setPacketType(packetItem.getPacketType());
            packetEntity.setTaskId(packetItem.getStaskId());
            packetEntity.setRecordCode(currentUser.getStaffId());
            packetEntity.setRecordName(currentUser.getName());
            packetEntity.setRecordTime(currentDate);
            List<TaskAllotItemEntity> taskAllotItemEntityList = new ArrayList<>();//?????????????????????
            List<XzyMTaskcarpart> partItemList = taskcarpartList.stream().filter(t -> t.getTaskAllotPacketId().equals(packetItem.getTaskAllotPacketId())).collect(Collectors.toList());
            String packetType = packetItem.getPacketType();
            String repairMode = "";//????????????
            if (packetType.equals("16")) {
                repairMode = "2";
            } else if (packetType.equals("17")) {
                repairMode = "1";
            } else {
                repairMode = "0";
            }
            for (XzyMTaskcarpart partItem : partItemList) {
                //???????????????????????????????????????
                packetEntity.setTrainsetId(partItem.getTrainsetId());
                packetEntity.setTrainsetName(partItem.getTrainsetName());
                packetEntity.setUnitCode(partItem.getUnitCode());
                packetEntity.setUnitName(partItem.getUnitName());
                packetEntity.setDayPlanId(partItem.getDayPlanID());
                packetEntity.setDeptCode(partItem.getXzyMTaskallotdept().getDeptCode());
                packetEntity.setDeptName(partItem.getXzyMTaskallotdept().getDeptName());
                //??????????????????-????????????
                TaskAllotItemEntity taskAllotItemEntity = new TaskAllotItemEntity();
                taskAllotItemEntity.setRemark(partItem.getRemark());
                taskAllotItemEntity.setPartType(partItem.getPartType());
                taskAllotItemEntity.setPartName(partItem.getPartName());
                taskAllotItemEntity.setPartPosition(partItem.getPartPosition());
                taskAllotItemEntity.setTaskItemId(partItem.getTaskItemId());
                taskAllotItemEntity.setRepairMode(partItem.getRepairMode());
                taskAllotItemEntity.setCarNo(partItem.getCarNo());
                taskAllotItemEntity.setTrainsetType(partItem.getTrainsetType());
                taskAllotItemEntity.setItemType(partItem.getRepairType());
                taskAllotItemEntity.setRepairMode(repairMode);
//                taskAllotItemEntity.setPublishCode();
                taskAllotItemEntity.setArrageType(partItem.getArrageType());
                taskAllotItemEntity.setItemCode(partItem.getItemCode());
                taskAllotItemEntity.setItemName(partItem.getItemName());
                taskAllotItemEntity.setPartPosition(partItem.getPartPosition());
                taskAllotItemEntity.setLocationCode(partItem.getLocationCode());
                taskAllotItemEntity.setLocationName(partItem.getLocationName());

                List<TaskAllotPersonEntity> taskAllotPersonEntityList = new ArrayList<>();//?????????????????????
                List<XzyMTaskallotperson> workerList = partItem.getWorkerList();
                for (XzyMTaskallotperson personItem : workerList) {
                    TaskAllotPersonEntity personEntity = new TaskAllotPersonEntity();
                    personEntity.setWorkerId(personItem.getWorkerID());
                    personEntity.setWorkerName(personItem.getWorkerName());
                    personEntity.setWorkerType(personItem.getWorkerType());
                    personEntity.setTaskAllotPersonPostEntityList(personItem.getTaskAllotPersonPostEntityList());
                    taskAllotPersonEntityList.add(personEntity);
                }
                //????????????????????????????????????????????????
                taskAllotItemEntity.setTaskAllotPersonEntityList(taskAllotPersonEntityList);
                //??????????????????????????????
                taskAllotItemEntityList.add(taskAllotItemEntity);
            }
            //?????????????????????????????????
            packetEntity.setTaskAllotItemEntityList(taskAllotItemEntityList);
            taskAllotPacketEntityList.add(packetEntity);
        }
        if(!CollectionUtils.isEmpty(taskAllotPacketEntityList)){
            //2.??????????????????????????????
            RestResultGeneric addRes = midGroundService.setTaskAllotData(taskAllotPacketEntityList);
            if(!ObjectUtils.isEmpty(addRes)&&!ObjectUtils.isEmpty(addRes.getCode())&&!ObjectUtils.isEmpty(1 == addRes.getCode().getValue())){
                //4.?????????????????????????????????????????????
                List<AbnormalHandlerLog> insertAbnormalHandlerLogList = new ArrayList();
                taskAllotPacketEntityList.forEach(packetItem->{
                    List<TaskAllotItemEntity> taskAllotItemEntityList = packetItem.getTaskAllotItemEntityList();
                    AbnormalHandlerLog insertHandlerLog = new AbnormalHandlerLog();
                    insertHandlerLog.setDayPlanId(packetItem.getDayPlanId());
                    insertHandlerLog.setTrainsetName(packetItem.getTrainsetName());
                    insertHandlerLog.setMainCyc(packetItem.getMainCyc());
                    insertHandlerLog.setPacketCode(packetItem.getPacketCode());
                    insertHandlerLog.setPacketName(packetItem.getPacketName());
                    insertHandlerLog.setStaffCode(currentUser.getStaffId());
                    insertHandlerLog.setStaffName(currentUser.getName());
                    insertHandlerLog.setCreateTime(currentDate);
                    insertHandlerLog.setHandlerType("0");
                    insertHandlerLog.setIpAddress(currentUser.getRemoteAddress());
                    insertHandlerLog.setUseFlag("0");
                    if(packetItem.getPacketTypeCode().equals("1")&&"1".equals(packetItem.getMainCyc())){//?????????
                        insertHandlerLog.setAbnormalHandlerLogId(UUID.randomUUID().toString());
                        insertHandlerLog.setItemCode(packetItem.getPacketCode());
                        insertHandlerLog.setItemName(packetItem.getPacketName());
                        insertAbnormalHandlerLogList.add(insertHandlerLog);
                    } else if(packetItem.getPacketTypeCode().equals("1")&&"2".equals(packetItem.getMainCyc())){//?????????
                        Map<TempKey, List<TaskAllotItemEntity>> groupMap = Optional.ofNullable(taskAllotItemEntityList).orElseGet(ArrayList::new).stream().collect(Collectors.groupingBy(v -> {
                            TempKey tempKey = new TempKey();
                            tempKey.setKeyCode(v.getItemCode());
                            tempKey.setKeyName(v.getItemName());
                            return tempKey;
                        }));
                        Optional.ofNullable(groupMap).orElseGet(HashMap::new).forEach(((tempKey, taskAllotItemEntities) -> {
                            AbnormalHandlerLog insertHandlerLog2 = new AbnormalHandlerLog();
                            BeanUtils.copyProperties(insertHandlerLog,insertHandlerLog2);
                            insertHandlerLog2.setAbnormalHandlerLogId(UUID.randomUUID().toString());
                            insertHandlerLog2.setItemCode(tempKey.getKeyCode());
                            insertHandlerLog2.setItemName(tempKey.getKeyName());
                            insertAbnormalHandlerLogList.add(insertHandlerLog2);
                        }));
                    } else if(packetItem.getPacketTypeCode().equals("-1")&&"5".equals(packetItem.getMainCyc())){//????????????
                        Optional.ofNullable(taskAllotItemEntityList).orElseGet(ArrayList::new).stream().forEach(v->{
                            AbnormalHandlerLog insertHandlerLog3 = new AbnormalHandlerLog();
                            BeanUtils.copyProperties(insertHandlerLog,insertHandlerLog3);
                            insertHandlerLog3.setAbnormalHandlerLogId(UUID.randomUUID().toString());
                            insertHandlerLog3.setItemCode(v.getItemCode());
                            insertHandlerLog3.setItemName(v.getItemName());
                            insertAbnormalHandlerLogList.add(insertHandlerLog3);
                        });
                    }
                });
                //?????????????????????????????????
                if(!CollectionUtils.isEmpty(insertAbnormalHandlerLogList)){
                    abnormalHandlerLogService.insertBatch(insertAbnormalHandlerLogList);
                }
            }else{
                res = false;
            }
        }
        return res;
    }

    //???????????????????????????emis??????????????????
    public boolean getWriteEmisTaskAllot(){
        boolean res = false;
        ConfigParamsModel configParamsModel = new ConfigParamsModel();
        configParamsModel.setType("2");
        configParamsModel.setName("WriteEmisTaskAllot");
        List<XzyCConfig> configs = midGroundService.getXzyCConfigs(configParamsModel);
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

    @Override
    public int setTaskAllot(List<XzyMTaskcarpart> taskcarpartList) {
        int res = 0;
        int count = 0;
        ShiroUser shiroUser = ShiroKit.getUser();
        String deptID = UUID.randomUUID().toString();
        for (XzyMTaskcarpart taskcarpart : taskcarpartList) {
            count++;

            XzyMTaskallotdept taskallotdept = new XzyMTaskallotdept();
            BeanUtils.copyProperties(taskcarpart.getXzyMTaskallotdept(), taskallotdept);
            taskallotdept.setTaskAllotDeptId(deptID);
            if (count == 1) {
                res = taskAllotDeptMapper.setTaskAllotDept(taskallotdept);
            }
            XzyMTaskcarpart xzyMTaskcarpart = new XzyMTaskcarpart();
            BeanUtils.copyProperties(taskcarpart, xzyMTaskcarpart);
            xzyMTaskcarpart.setProcessId(UUID.randomUUID().toString());
            xzyMTaskcarpart.setTaskAllotDeptId(taskallotdept.getTaskAllotDeptId());
            xzyMTaskcarpart.setRecorderName(shiroUser.getName());
            xzyMTaskcarpart.setRecorderCode(shiroUser.getStaffId());
            res = taskcarpartMapper.setTaskAllot(xzyMTaskcarpart);
            //????????????????????????????????????
            List<SysStaff> userList = UserUtil.getStaffListByDeptIncludePartTimeDept(taskallotdept.getDeptCode());
            if (res > 0) {
                //????????????????????????
                MessageInfo messageInfo = new MessageInfo();
                List<MessageRec> recList = new ArrayList<>();
                messageInfo.setRecType("1");
                messageInfo.setType("??????");
                messageInfo.setContent(shiroUser.getName() + "????????????????????????" + taskcarpart.getItemName() + "??????????????????");
                messageInfo.setUserName(shiroUser.getName());
                messageInfo.setUserId(shiroUser.getAccount());
                messageInfo.setAppCode("M002");
                messageInfo.setAppName("??????");
                messageInfo.setModuleCode("0191");
                messageInfo.setModuleName("??????");
                messageInfo.setTargetUrl("/");
                messageInfo.setSubject("????????????");
                messageInfo.setExpires("7");
                //????????????????????????????????????
                for (XzyMTaskallotperson taskallotperson : taskcarpart.getWorkerList()) {
                    XzyMTaskallotperson xzyMTaskallotperson = new XzyMTaskallotperson();
                    BeanUtils.copyProperties(taskallotperson, xzyMTaskallotperson);
                    xzyMTaskallotperson.setProcessId(xzyMTaskcarpart.getProcessId());
                    xzyMTaskallotperson.setTaskAllotPersonId(UUID.randomUUID().toString());
                    res = taskAllotPersonMapper.setTaskAllotPerson(xzyMTaskallotperson);

                    //???????????? ?????????????????????
                    List<GroupModel> groupModels = perSonNelService.getPerSonNelList(null, xzyMTaskallotperson.getWorkerID());
                    for (GroupModel groupModel : groupModels) {
                        for (PerSonNelModel perSonNelModel : groupModel.getPerSonNelModels()) {
                            for (PostModel postModel : perSonNelModel.getPostModelList()) {
                                TaskAllotPersonPost taskAllotPersonPost = new TaskAllotPersonPost();
                                taskAllotPersonPost.setTaskAllotPersonId(xzyMTaskallotperson.getTaskAllotPersonId());
                                taskAllotPersonPost.setPostId(postModel.getPostId());
                                taskAllotPersonPost.setPostName(postModel.getPostName());
                                iTaskAllotPersonPostService.addTaskAllotPersonPost(taskAllotPersonPost);
                            }
                        }
                    }


                    //??????????????????????????????????????????????????????????????????????????????????????????????????????
                    for (SysStaff user : userList) {
                        if (xzyMTaskallotperson.getWorkerID().equals(user.getStaffId())) {
                            MessageRec messageRec = new MessageRec();
                            messageRec.setUserId(user.getUserAccount());
                            messageRec.setUserName(user.getName());
                            recList.add(messageRec);
                        }
                    }
                }
                messageInfo.setRecList(recList);
                if (xzyMTaskcarpart.getRepairType().equals("5")) {
                    MessageInfo faultMessageInfo = new MessageInfo();
                    BeanUtils.copyProperties(messageInfo, faultMessageInfo);
                    faultMessageInfo.setContent(shiroUser.getName() + "????????????????????????????????????" + taskcarpart.getItemName() + "????????????????????????");
                    logger.info("??????????????????????????????????????????...");
                    try {
                        JSONObject sendMessageRes = new RestRequestKitUseLogger<JSONObject>(messageServiceId, logger) {
                        }.postObject("/message/addMessage", messageInfo);
                        logger.info(sendMessageRes.toString());
                    } catch (Exception e) {
                        logger.error("????????????????????????????????????");
                        logger.info(recList.toString());
                    }
                    logger.info("??????????????????????????????");
                }
                logger.info("??????????????????????????????????????????...");
                try {
                    JSONObject sendMessageRes = new RestRequestKitUseLogger<JSONObject>(messageServiceId, logger) {
                    }.postObject("/message/addMessage", messageInfo);
                    logger.info(sendMessageRes.toString());
                } catch (Exception e) {
                    logger.error("????????????????????????????????????");
                    logger.info(recList.toString());
                }
                logger.info("??????????????????????????????");
                //?????????????????????????????????
//                if(res > 0&&count==1){
//                    res = taskAllotDeptMapper.setTaskAllotDept(taskallotdept);
//                }
            }
        }
        return res;
    }

    @Override
    public List<XzyMTaskcarpart> getCarPartListByItemCodeList(List<String> itemCodeList){
        return taskcarpartMapper.getCarPartListByItemCodeList(itemCodeList);
    }


    @Override
    public List<XzyMTaskcarpart> getCarPartListByParam(Map<String, String> map) {
        return taskcarpartMapper.getCarPartListByParam(map);
    }

    @Override
    public int deleteAll(String unitCode, String deptCode) {
        return taskcarpartMapper.deleteAll(unitCode, deptCode);
    }

    @Override
    public int deleteTaskCarPatrs(Map<String, String> map) {
        return taskcarpartMapper.deleteTaskCarPatrs(map);
    }

    @Override
    public List<XzyMTaskcarpart> getCarPartLists(Map<String, String> map) {
        return taskcarpartMapper.getCarPartLists(map);
    }

    @Override
    public List<XzyMTaskcarpart> getTaskAllotListByPacketIds(List<String> taskAllotPacketIds) {
        return taskcarpartMapper.getTaskAllotListByPacketIds(taskAllotPacketIds);
    }

}
