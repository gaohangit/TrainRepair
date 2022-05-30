package com.ydzbinfo.emis.trainRepair.mobile.oftenfunction.service.impl;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.ydzbinfo.emis.trainRepair.common.model.ConfigParamsModel;
import com.ydzbinfo.emis.trainRepair.common.querymodel.XzyCConfig;
import com.ydzbinfo.emis.trainRepair.common.service.IRepairMidGroundService;
import com.ydzbinfo.emis.trainRepair.mobile.model.HomeAllotTask;
import com.ydzbinfo.emis.trainRepair.mobile.model.MobileLoginTaskEntity;
import com.ydzbinfo.emis.trainRepair.mobile.oftenfunction.dao.PhoneOftenFunctionMapper;
import com.ydzbinfo.emis.trainRepair.mobile.oftenfunction.service.IPhoneOftenFunctionService;
import com.ydzbinfo.emis.trainRepair.mobile.querymodel.PhoneOftenFunction;
import com.ydzbinfo.emis.trainRepair.taskAllot.querymodel.TaskAllotPacketEntity;
import com.ydzbinfo.emis.trainRepair.taskAllot.querymodel.XzyMTaskallotpacket;
import com.ydzbinfo.emis.trainRepair.taskAllot.service.XzyMTaskallotpacketService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author 韩旭
 * @since 2021-04-07
 */
@Service
public class PhoneOftenFunctionServiceImpl extends ServiceImpl<PhoneOftenFunctionMapper, PhoneOftenFunction> implements IPhoneOftenFunctionService {

    @Resource
    private PhoneOftenFunctionMapper phoneOftenFunctionMapper;

    @Autowired
    private IRepairMidGroundService repairMidGroundService;

    @Autowired
    XzyMTaskallotpacketService taskallotpacketService;

    @Autowired
    IRepairMidGroundService midGroundService;


    protected static final Logger logger = LoggerFactory.getLogger(PhoneOftenFunctionServiceImpl.class);


    /**
     * 是否可用  1--可用 0--不可用
     */
    private static final String FLAG = "1";

    @Override
    public HomeAllotTask getMobileLoginUserRepairTask(String unitCode, String dayPlanID, String repairType, List<String> trainsetIDList, String deptCode, String staffID) {
        List<MobileLoginTaskEntity> mobileLoginTaskEntities = new ArrayList<>();
        //获取派工任务
//        JSONObject repairTask = new JSONObject();
        List<TaskAllotPacketEntity> taskAllotData = new ArrayList<>();
        List<String> deptCodeList = new ArrayList<>();
        deptCodeList.add(deptCode);
        try {
            long startNow = System.currentTimeMillis();
            taskAllotData = midGroundService.getTaskAllotData(unitCode, dayPlanID, null, trainsetIDList, deptCodeList,Collections.singletonList(staffID), null);

//            repairTask = taskallotpacketService.getRepairTask(unitCode, dayPlanID, deptCode, null, "1");
            long endNow = System.currentTimeMillis();
            logger.info("获取派工任务执行时间:{}", endNow-startNow);
        } catch (Exception e) {
            logger.error("获取派工数据接口异常", e);
        }
//        if(repairTask.getIntValue("code") == 0){
//            throw new RuntimeException("获取派工任务失败");
//        }
//        JSONArray data = repairTask.getJSONArray("data");
        //List<AllotData> allotDataList = JSONArray.parseArray(data.toJSONString(), AllotData.class);
        ConfigParamsModel configParamsModel = new ConfigParamsModel();
        configParamsModel.setType("7");
        configParamsModel.setName("HomeAllotTask");
        List<XzyCConfig> xzyCConfigs = midGroundService.getXzyCConfigs(configParamsModel);
        //获取当前用户的id
        taskAllotData.forEach(allotData -> {
            allotData.getTaskAllotItemEntityList().forEach(taskAllotItemEntity -> {
                taskAllotItemEntity.getTaskAllotPersonEntityList().forEach(work -> {
                    //只获取当前用户的派工任务
                    if (work.getWorkerId().equals(staffID)){
                        String trainsetId = allotData.getTrainsetId();
                        String trainsetName = allotData.getTrainsetName();
                        String staskId = allotData.getTaskId();
                        String packetCode = allotData.getPacketCode();
                        String packetName = allotData.getPacketName();
                        String packetType = allotData.getPacketType();
                        String taskAllotPacketId = allotData.getTaskAllotPacketId();
                        String mainCyc = allotData.getMainCyc();

                        List<MobileLoginTaskEntity> collect = mobileLoginTaskEntities.stream().filter(t -> t.getTrainsetID().equals(trainsetId)).collect(Collectors.toList());
                        if (CollectionUtils.isEmpty(collect)){
                            MobileLoginTaskEntity mobileLoginTaskEntity = new MobileLoginTaskEntity();
                            //车组信息
                            mobileLoginTaskEntity.setTrainsetID(trainsetId);
                            mobileLoginTaskEntity.setTrainsetName(trainsetName);
                            //作业包信息
                            List<XzyMTaskallotpacket> xzyMTaskallotpackets = new ArrayList<>();
                            XzyMTaskallotpacket xzyMTaskallotpacket = getXzyMTaskallotpacket(staskId, packetCode, packetName, packetType, taskAllotPacketId, mainCyc);
                            xzyMTaskallotpackets.add(xzyMTaskallotpacket);
                            mobileLoginTaskEntity.setPacketList(xzyMTaskallotpackets);
                            mobileLoginTaskEntities.add(mobileLoginTaskEntity);
                        }else {
                            MobileLoginTaskEntity taskEntity = collect.get(0);
                            logger.trace("mobileLoginTaskEntity:{}", taskEntity);
                            List<XzyMTaskallotpacket> packetList = taskEntity.getPacketList();
                            logger.trace("packetList:{}", packetList);
                            if (packetList.stream().noneMatch(t->t.getTaskAllotPacketId().equals(taskAllotPacketId))){
                                XzyMTaskallotpacket xzyMTaskallotpacket = getXzyMTaskallotpacket(staskId, packetCode, packetName, packetType, taskAllotPacketId, mainCyc);
                                packetList.add(xzyMTaskallotpacket);
                            }
                        }
                    }
                });
            });
        });
        //获取车组名称
        String trainsetNames = mobileLoginTaskEntities.stream().map(MobileLoginTaskEntity::getTrainsetName).collect(Collectors.joining(","));
        logger.trace("集合中的车组名称:{}", trainsetNames);

        // List<TrainsetPostionCur> trainsetPostIon = new ArrayList<>();
        // try {
        //   trainsetPostIon = repairMidGroundService.getTrainsetPosition(unitCode, null, trainsetNames);
        // } catch (Exception e) {
        //     logger.error("获取车组位置接口异常：getTrainsetPostIon", e);
        // }
        // logger.trace("获取的股道信息:{}", trainsetPostIon);
        // //获取股道位列名称
        // for (MobileLoginTaskEntity mobileLoginTaskEntity : mobileLoginTaskEntities) {
        //     for (TrainsetPostionCur trainsetPostionCur : trainsetPostIon) {
        //         if (mobileLoginTaskEntity.getTrainsetID().equals(trainsetPostionCur.getTrainsetId())
        //                 && mobileLoginTaskEntity.getTrainsetName().equals(trainsetPostionCur.getTrainsetName())){
        //             StringBuilder stringBuilder = new StringBuilder();
        //             stringBuilder.append(trainsetPostionCur.getTrackName()).append("-").append(trainsetPostionCur.getHeadDirectionPla());
        //             //判断位列code是否一致，不一致则添加车尾位列
        //             if (!trainsetPostionCur.getHeadDirectionPlaCode().equals(trainsetPostionCur.getTailDirectionPlaCode())){
        //                 stringBuilder.append("(").append(trainsetPostionCur.getTailDirectionPla()).append(")");
        //             }
        //             mobileLoginTaskEntity.setTrackPlaceName(stringBuilder.toString());
        //         }
        //     }
        // }
        HomeAllotTask homeAllotTask = new HomeAllotTask();
        if (!CollectionUtils.isEmpty(xzyCConfigs)){
            homeAllotTask.setHomeAllotTask(xzyCConfigs.get(0).getParamValue());
        }
        homeAllotTask.setMobileLoginTaskEntities(mobileLoginTaskEntities);
        return homeAllotTask;
    }

    private XzyMTaskallotpacket getXzyMTaskallotpacket(String staskId, String packetCode, String packetName, String packetType, String taskAllotPacketId, String mainCyc) {
        XzyMTaskallotpacket xzyMTaskallotpacket = new XzyMTaskallotpacket();
        xzyMTaskallotpacket.setTaskAllotPacketId(taskAllotPacketId);
        xzyMTaskallotpacket.setPacketCode(packetCode);
        xzyMTaskallotpacket.setPacketName(packetName);
        xzyMTaskallotpacket.setPacketType(packetType);
        xzyMTaskallotpacket.setStaskId(staskId);
        xzyMTaskallotpacket.setMainCyc(mainCyc);
        return xzyMTaskallotpacket;
    }

    @Override
    public List<String> getOftenFunction(String staffId,String type) {
        return phoneOftenFunctionMapper.getOftenFunction(staffId,type);
    }

    @Override
    @Transactional
    public void setOftenFunction(String staffID, String staffName, String code, List<String> moduleIDList) {
        //删除原来的常用功能
        phoneOftenFunctionMapper.delOftenFunction(staffID, staffName, new Date(), code);

        //如果没有选择新功能则不添加
        if (!CollectionUtils.isEmpty(moduleIDList)){
            List<PhoneOftenFunction> phoneOftenFunctionList = new ArrayList<>();
            for (String moduleID : moduleIDList) {
                PhoneOftenFunction often = new PhoneOftenFunction();
                UUID uuid = UUID.randomUUID();
                often.setId(uuid.toString());
                often.setStaffId(staffID);
                often.setPhoneModuleId(moduleID);
                often.setCreateTime(new Date());
                often.setCreateUserCode(code);
                often.setCreateUserName(staffName);
                often.setFlag(FLAG);
                phoneOftenFunctionList.add(often);
            }
            phoneOftenFunctionMapper.setOftenFunction(phoneOftenFunctionList);
        }
    }
}
