package com.ydzbinfo.emis.common.taskAllot.service;

import com.ydzbinfo.emis.trainRepair.taskAllot.model.PostModel;
import com.ydzbinfo.emis.trainRepair.taskAllot.model.TaskCarPart;
import com.ydzbinfo.emis.trainRepair.taskAllot.querymodel.TaskAllotPacketEntity;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface XzyMTaskAllotService {


    /**
     * 获取派工数据
     */
    List<TaskAllotPacketEntity> getTaskAllotData(String unitCode,String dayPlanId,String startDate,String endDate,String repairType,List<String> trainsetTypeList,List<String> trainsetIdList,List<String> itemCodeList,List<String> branchCodeList,List<String> workIdList,String packetName);

    List<TaskAllotPacketEntity> getTaskAllotDataToPerson(String unitCode,String dayPlanId,String startDate,String endDate,String repairType,List<String> trainsetTypeList,List<String> trainsetIdList,List<String> itemCodeList,List<String> branchCodeList,List<String> workIdList,String packetName);

    List<TaskAllotPacketEntity> getTaskAllotDataToItem(String unitCode,String dayPlanId,String repairType,List<String> trainsetTypeList,List<String> trainsetIdList,List<String> itemCodeList,List<String> branchCodeList,String packetName);

    /**
     * 获取派工车组
     */
    List<String> getTaskAllotTrainsetList(String unitCode,String dayPlanId,String branchCode,String mainCyc);

    /**
     * 获取机检一级修派工车组
     */
    List<String> getTaskAllotEquipmentTrainsetList(String unitCode,String dayPlanId,String branchCode);

    /**
     * 获取人员-岗位配置
     */
    List<PostModel> getPersonPostList(String unitCode,String deptCode,String branchCode);

    /**
     * 插入派工数据——仅限一个运用所+班次+班组
     */
    boolean setTaskAllotData(List<TaskAllotPacketEntity> packetList);

    /**
     * 插入派工数据——仅限机检一级修
     */
    boolean setTaskAllotDataByDayPlanId(String dayPlanId);

    /**
     * 插入派工数据——增量保存
     */
    boolean setTaskAllotDataIncrement(List<TaskAllotPacketEntity> taskAllotPacketList);


    /**
     * 删除派工数据—根据id
     */
    boolean deleteTaskAllotDataById(List<TaskCarPart> delTaskCarPartList);

    /**
     * 删除派工数据—根据唯一编码
     */
    boolean deleteTaskAllotDataByPublishCode(List<String> publishCodeList);

    /**-----------------------------------------------------------------------------------------------------------*/




    // boolean setTaskAllotDataOverdue(List<TaskAllotPacketEntity> packetList);
    //
    // List<TaskAllotPacketEntity> getTaskAllotDataOverdue(String unitCode,String dayPlanId,String startDate,String endDate,String repairType,List<String> trainsetTypeList,List<String> trainsetIdList,List<String> itemCodeList,List<String> branchCodeList,List<String> workIdList,String packetName);
    //
    // List<TaskAllotPacketEntity> getTaskAllotDataOverdueToPerson(String unitCode,String dayPlanId,String startDate,String endDate,String repairType,List<String> trainsetTypeList,List<String> trainsetIdList,List<String> itemCodeList,List<String> branchCodeList,List<String> workIdList,String packetName);

    // boolean taskAllotConstId(String unitCode,String dayPlanId,String startDate,String endDate,String repairType,List<String> trainsetTypeList,List<String> trainsetIdList,List<String> itemCodeList,List<String> branchCodeList,List<String> workIdList,String packetName);

}
