package com.ydzbinfo.emis.common.taskAllot.dao;

import com.ydzbinfo.emis.trainRepair.taskAllot.model.PostModel;
import com.ydzbinfo.emis.trainRepair.taskAllot.model.TaskCarPart;
import com.ydzbinfo.emis.trainRepair.taskAllot.querymodel.TaskAllotPacketEntity;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 作业过程数据处理（中台服务）
 */
public interface XzyMTaskAllotMapper {

    /**
     * 获取派工数据
     */
    List<TaskAllotPacketEntity> getTaskAllotData(@Param("unitCode") String unitCode,@Param("dayPlanId") String dayPlanId,@Param("startDate") String startDate,@Param("endDate") String endDate,@Param("repairType") String repairType,@Param("trainsetTypeList") List<String> trainsetTypeList,@Param("trainsetIdList") List<String> trainsetIdList,@Param("itemCodeList") List<String> itemCodeList,@Param("branchCodeList") List<String> branchCodeList,@Param("workIdList") List<String> workIdList,@Param("packetName")String packetName);

    List<TaskAllotPacketEntity> getTaskAllotDataToPerson(@Param("unitCode") String unitCode,@Param("dayPlanId") String dayPlanId,@Param("startDate") String startDate,@Param("endDate") String endDate,@Param("repairType") String repairType,@Param("trainsetTypeList") List<String> trainsetTypeList,@Param("trainsetIdList") List<String> trainsetIdList,@Param("itemCodeList") List<String> itemCodeList,@Param("branchCodeList") List<String> branchCodeList,@Param("workIdList") List<String> workIdList,@Param("packetName")String packetName);

    List<TaskAllotPacketEntity> getTaskAllotDataToItem(@Param("unitCode") String unitCode,@Param("dayPlanId") String dayPlanId,@Param("repairType") String repairType,@Param("trainsetTypeList") List<String> trainsetTypeList,@Param("trainsetIdList") List<String> trainsetIdList,@Param("itemCodeList") List<String> itemCodeList,@Param("branchCodeList") List<String> branchCodeList,@Param("packetName")String packetName);

    /**
     * 获取派工车组
     */
    List<String> getTaskAllotTrainsetList(@Param("unitCode") String unitCode,@Param("dayPlanId") String dayPlanId,@Param("branchCode") String branchCode,@Param("mainCyc") String mainCyc);

    /**
     * 获取机检一级修派工车组
     */
    List<String> getTaskAllotEquipmentTrainsetList(@Param("unitCode") String unitCode,@Param("dayPlanId") String dayPlanId,@Param("branchCode") String branchCode);

    /**
     * 获取人员-岗位配置
     */
    List<PostModel> getPersonPostList(@Param("unitCode") String unitCode,@Param("deptCode") String deptCode,@Param("branchCode") String branchCode);

    /**
     * 获取需要删除的派工辆序表集合
     */
    List<TaskCarPart> getCarPartList(@Param("dayPlanId") String dayPlanId,@Param("unitCode") String unitCode,@Param("deptCodeList") List<String> deptCodeList);

    /**
     * 获取需要删除的派工人员表主键集合
     */
    List<String> getDelPersonIdList(@Param("delCarPartIdList") List<String> delCarPartIdList);

    /**
     * 获取需要删除的派工人员岗位表主键集合
     */
    List<String> getDelPersonPostIdList(@Param("delPersonIdList") List<String> delPersonIdList);


    /**
     * 批量删除辆序表
     */
    void delPacketList(@Param("delPacketIdList") List<String> delPacketIdList);


    /**
     * 批量删除辆序表
     */
    void delDeptList(@Param("delDeptIdList") List<String> delDeptIdList);


    /**
     * 批量删除辆序表
     */
    void delCarParList(@Param("delCarPartIdList") List<String> delCarPartIdList);

    /**
     * 批量删除人员表
     */
    void delPersonList(@Param("delPersonIdList") List<String> delPersonIdList);

    /**
     * 批量删除人员表
     */
    void delPersonPostList(@Param("delPersonPostIdList") List<String> delPersonPostIdList);
}
