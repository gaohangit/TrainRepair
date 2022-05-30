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

    //中台服务
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
        //获取当前用户
        ShiroUser currentUser = ShiroKit.getUser();
        //获取当前时间
        Date currentDate = new Date();
        String deptID = UUID.randomUUID().toString();
        XzyMTaskallotdept taskallotdept = new XzyMTaskallotdept();
        //查询岗位配置
        List<GroupModel> groupModels = perSonNelService.getPerSonNelList(null, null);
        //1.组织中台所需要的结构数据
        List<TaskAllotPacketEntity> taskAllotPacketEntityList = new ArrayList<>();//包集合—中台
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
            List<TaskAllotItemEntity> taskAllotItemEntityList = new ArrayList<>();//项目集合—中台
            List<XzyMTaskcarpart> partItemList = taskcarpartList.stream().filter(t -> t.getTaskAllotPacketId().equals(packetItem.getTaskAllotPacketId())).collect(Collectors.toList());
            String packetType = packetItem.getPacketType();
            String repairMode = "";//检修方式
            if (packetType.equals("16")) {
                repairMode = "2";
            } else if (packetType.equals("17")) {
                repairMode = "1";
            } else {
                repairMode = "0";
            }
            for (XzyMTaskcarpart partItem : partItemList) {
                //把中台派工包的实体信息补全
                packetEntity.setTrainsetId(partItem.getTrainsetId());
                packetEntity.setTrainsetName(partItem.getTrainsetName());
                packetEntity.setUnitCode(partItem.getUnitCode());
                packetEntity.setUnitName(partItem.getUnitName());
                packetEntity.setDayPlanId(partItem.getDayPlanID());
                packetEntity.setDeptCode(partItem.getXzyMTaskallotdept().getDeptCode());
                packetEntity.setDeptName(partItem.getXzyMTaskallotdept().getDeptName());
                //中台派工项目-辆序数据
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

                List<TaskAllotPersonEntity> taskAllotPersonEntityList = new ArrayList<>();//人员集合—中台
                List<XzyMTaskallotperson> workerList = partItem.getWorkerList();
                for (XzyMTaskallotperson personItem : workerList) {
                    TaskAllotPersonEntity personEntity = new TaskAllotPersonEntity();
                    personEntity.setWorkerId(personItem.getWorkerID());
                    personEntity.setWorkerName(personItem.getWorkerName());
                    personEntity.setWorkerType(personItem.getWorkerType());
                    personEntity.setTaskAllotPersonPostEntityList(personItem.getTaskAllotPersonPostEntityList());
                    taskAllotPersonEntityList.add(personEntity);
                }
                //中台项目实体设置派工人员集合属性
                taskAllotItemEntity.setTaskAllotPersonEntityList(taskAllotPersonEntityList);
                //添加到项目实体集合中
                taskAllotItemEntityList.add(taskAllotItemEntity);
            }
            //添加到派工包实体集合中
            packetEntity.setTaskAllotItemEntityList(taskAllotItemEntityList);
            taskAllotPacketEntityList.add(packetEntity);
        }
        if(!CollectionUtils.isEmpty(taskAllotPacketEntityList)){
            //2.调用中台接口插入数据
            RestResultGeneric addRes = midGroundService.setTaskAllotData(taskAllotPacketEntityList);
            if(!ObjectUtils.isEmpty(addRes)&&!ObjectUtils.isEmpty(addRes.getCode())&&!ObjectUtils.isEmpty(1 == addRes.getCode().getValue())){
                //4.如果派工成功，则插入派工日志表
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
                    if(packetItem.getPacketTypeCode().equals("1")&&"1".equals(packetItem.getMainCyc())){//一级修
                        insertHandlerLog.setAbnormalHandlerLogId(UUID.randomUUID().toString());
                        insertHandlerLog.setItemCode(packetItem.getPacketCode());
                        insertHandlerLog.setItemName(packetItem.getPacketName());
                        insertAbnormalHandlerLogList.add(insertHandlerLog);
                    } else if(packetItem.getPacketTypeCode().equals("1")&&"2".equals(packetItem.getMainCyc())){//二级修
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
                    } else if(packetItem.getPacketTypeCode().equals("-1")&&"5".equals(packetItem.getMainCyc())){//故障任务
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
                //往数据库中插入日志数据
                if(!CollectionUtils.isEmpty(insertAbnormalHandlerLogList)){
                    abnormalHandlerLogService.insertBatch(insertAbnormalHandlerLogList);
                }
            }else{
                res = false;
            }
        }
        return res;
    }

    //获取派工之后是否往emis中写数据配置
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
            //获取班组中所有的人员信息
            List<SysStaff> userList = UserUtil.getStaffListByDeptIncludePartTimeDept(taskallotdept.getDeptCode());
            if (res > 0) {
                //构建通知消息信息
                MessageInfo messageInfo = new MessageInfo();
                List<MessageRec> recList = new ArrayList<>();
                messageInfo.setRecType("1");
                messageInfo.setType("通知");
                messageInfo.setContent(shiroUser.getName() + "给你分配了任务：" + taskcarpart.getItemName() + "，请及时查看");
                messageInfo.setUserName(shiroUser.getName());
                messageInfo.setUserId(shiroUser.getAccount());
                messageInfo.setAppCode("M002");
                messageInfo.setAppName("派工");
                messageInfo.setModuleCode("0191");
                messageInfo.setModuleName("检修");
                messageInfo.setTargetUrl("/");
                messageInfo.setSubject("派工通知");
                messageInfo.setExpires("7");
                //往数据库写入派工人员信息
                for (XzyMTaskallotperson taskallotperson : taskcarpart.getWorkerList()) {
                    XzyMTaskallotperson xzyMTaskallotperson = new XzyMTaskallotperson();
                    BeanUtils.copyProperties(taskallotperson, xzyMTaskallotperson);
                    xzyMTaskallotperson.setProcessId(xzyMTaskcarpart.getProcessId());
                    xzyMTaskallotperson.setTaskAllotPersonId(UUID.randomUUID().toString());
                    res = taskAllotPersonMapper.setTaskAllotPerson(xzyMTaskallotperson);

                    //增加岗位 根据人查到岗位
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


                    //取出派工人员的用户信息，放入消息通知信息中，用于向该用户发送消息通知
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
                    faultMessageInfo.setContent(shiroUser.getName() + "给你分配了故障处理任务：" + taskcarpart.getItemName() + "，请及时回填故障");
                    logger.info("准备调用消息通知接口发送消息...");
                    try {
                        JSONObject sendMessageRes = new RestRequestKitUseLogger<JSONObject>(messageServiceId, logger) {
                        }.postObject("/message/addMessage", messageInfo);
                        logger.info(sendMessageRes.toString());
                    } catch (Exception e) {
                        logger.error("新增消息通知接口调用失败");
                        logger.info(recList.toString());
                    }
                    logger.info("消息通知接口调用完成");
                }
                logger.info("准备调用消息通知接口发送消息...");
                try {
                    JSONObject sendMessageRes = new RestRequestKitUseLogger<JSONObject>(messageServiceId, logger) {
                    }.postObject("/message/addMessage", messageInfo);
                    logger.info(sendMessageRes.toString());
                } catch (Exception e) {
                    logger.error("新增消息通知接口调用失败");
                    logger.info(recList.toString());
                }
                logger.info("消息通知接口调用完成");
                //班组只需要添加一次即可
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
