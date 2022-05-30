package com.ydzbinfo.emis.trainRepair.mobile.taskallot.service;

import com.alibaba.fastjson.JSONObject;
import com.ydzbinfo.emis.trainRepair.mobile.model.*;
import com.ydzbinfo.emis.trainRepair.taskAllot.model.TaskAllotPhoneTrainSetState;
import com.ydzbinfo.emis.trainRepair.taskAllot.querymodel.TaskAllotPacketEntity;

import java.util.List;
import java.util.Map;

/**
 * Description:
 * Author: wuyuechang
 * Create Date Time: 2021/4/23 11:31
 * Update Date Time: 2021/4/23 11:31
 *
 * @see
 */
public interface IPhoneTaskAllotPacketService {

    /**
     * @author: wuyuechang
     * @Description: 手持机获取检修任务
     * @date: 2021/5/18 14:40
     */
    JSONObject getPhoneTaskAllot(String unitCode, String dayPlanID, String deptCode);

    /***
     * @author:冯帅
     * @desc: 手持机获取车组派工状态
     * @date: 2021/9/3
     * @param: []
     * @return: com.alibaba.fastjson.JSONObject
     */
    List<TaskAllotPhoneTrainSetState> getPhoneTaskTrainSetState(String unitCode, String deptCode, String dayPlanId);

    /**
     * @author: wuyuechang
     * @Description: 手持机获取班组信息
     * @date: 2021/5/18 14:40
     */
    Map getMobileGroup(String deptCode);

    /**
     * @author: wuyuechang
     * @Description: 手持机增加派工数据
     * @date: 2021/5/18 14:40
     */
    void setMobileAllotTask(JSONObject param);

    /**
     * @return 手持机获取计划车组信息
     */
    List<PlanTrainset> getPlanTrainset(String dayPlanId, String unitCode, String deptCode);

    /**
     * @return 手持机获取计划包信息
     */
    List<PlanPacket> getPlanPacket(String dayPlanId, String unitCode, String deptCode, String trainsetId);

    /**
     * @return 手持机获取计划项目信息
     */
    List<PlanItem> getPlanItem(String dayPlanId, String unitCode, String deptCode, String trainsetId, String packetCode);

    /**
     * @return 手持机获取计划辆序或部件信息
     */
    List<PlanCarNoOrPart> getPlanCarNoOrPart(String dayPlanId, String unitCode, String deptCode, String trainsetId, String packetCode, String itemCode, String displayItemName);

    /**
     * @return 手持机获取计划派工人员信息
     */
    List<PlanAllotInfo> getPlanAllotInfo(String dayPlanId, String unitCode, String deptCode, String trainsetId, String packetCode, String itemCode, String displayItemName);

    /**
     * 手持机提交派工信息
     */
    List<TaskAllotPacketEntity> submitAllotInfo(String unitCode, String unitName, String deptCode, String deptName, String dayPlanId, List<PlanWorker> planWorkerList, List<AllotTrainset> allotTrainsetList);
}
