package com.ydzbinfo.emis.trainRepair.workProcess.dao;

import com.ydzbinfo.emis.trainRepair.workprocess.model.*;
import com.ydzbinfo.emis.trainRepair.workprocess.querymodel.ProcessPerson;
import com.ydzbinfo.emis.trainRepair.workprocess.querymodel.RfidCardSummary;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;


public interface WorkProcessMapper {

    /**
     * @author: 冯帅
     * @Date: 2021/5/8
     * @Description: 获取一级修作业列表
     */
    @Deprecated
    List<OneWorkProcessData> getOneWorkProcessList(QueryOneWorkProcessData queryOneWorkProcessData);

    /**
     * 获取总表数据
     *
     * @param queryOneWorkProcessData
     * @return
     */
    List<RfidCardSummary> getRfidCardSummaryList(QueryOneWorkProcessData queryOneWorkProcessData);

    /**
     * 获取辆序数据
     *
     * @param dayPlanId
     * @param trainsetId
     * @param itemCode
     * @param workerId
     * @return
     */
    List<Map<String, Object>> getCarNoList(@Param("dayPlanId") String dayPlanId, @Param("trainsetId") String trainsetId, @Param("itemCode") String itemCode, @Param("workerId") String workerId);


    /**
     * @author: 冯帅
     * @Date: 2021/5/8
     * @Description: 查询出来需要删除的人
     */
    List<ProcessPerson> getDelPersonList(@Param("dayPlanId") String dayPlanId, @Param("trainsetId") String trainsetId, @Param("itemCode") String itemCode, @Param("workerId") String workerId, @Param("repairType") String repairType,@Param("packetCode") String packetCode);

    /**
     * @author: 冯帅
     * @Date: 2021/5/8
     * @Description: 查询二级修列表
     */
    List<TwoWorkProcessData> getTwoWorkProcessList(TwoWorkProcessData twoWorkProcessData);

    /**
     * 查询二级修列表数据
     */
    @Deprecated
    List<TwoWorkProcessData> getTwoWorkProcessList2(TwoWorkProcessData twoWorkProcessData);

    /**
     * 查询二级修列表数据
     */
    List<TwoWorkProcessData> getTwoWorkProcessData(TwoWorkProcess twoWorkProcess);



    /**
     * @author: 冯帅
     * @Date: 2021/5/8
     * @Description: 获取一体化查询列表
     */
    List<ProcessData> getIntegrationList(ProcessData processData);

    /***
     * 获取人员在该班次、班组、车组、作业包  作业的数量
     */
    Integer getWorkCount(@Param("dayplanId") String dayPlanId,@Param("trainsetId") String trainsetId,@Param("packetCode") String packetCode,@Param("deptCode") String deptCode,@Param("workerId") String workerId);
}
