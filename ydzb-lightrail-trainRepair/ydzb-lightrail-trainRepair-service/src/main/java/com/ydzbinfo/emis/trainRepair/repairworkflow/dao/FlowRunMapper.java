package com.ydzbinfo.emis.trainRepair.repairworkflow.dao;


import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.ydzbinfo.emis.trainRepair.common.model.WorkTeam;
import com.ydzbinfo.emis.trainRepair.repairworkflow.model.RuntimeRole;
import com.ydzbinfo.emis.trainRepair.repairworkflow.querymodel.FlowRun;
import com.ydzbinfo.emis.trainRepair.repairworkflow.querymodel.extend.FlowRunWithExtraInfo;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * <p>
 * 流程运行表 Mapper 接口
 * </p>
 *
 * @author gaohan
 * @since 2021-05-13
 */
public interface FlowRunMapper extends BaseMapper<FlowRun> {
    /**
     * 流程运行和流程运行额外信息列表
     *
     * @return
     */
    List<FlowRunWithExtraInfo> getFlowRunWithExtraInfos(@Param("dayPlanIds") List<String> dayPlanIds, @Param("flowTypeCodes") List<String> flowTypeCodes, @Param("flowRunId") String flowRunId, @Param("trainsetId") String trainsetId, @Param("flowRunState") String flowRunState, @Param("startDate") Date startDate, @Param("endDate") Date endDate, @Param("flowId") String flowId, @Param("flowRunIds") String[] flowRunIds, @Param("unitCode") String unitCode, @Param("dayPlanCode") String dayPlanCode);

    /**
     * 查询派工岗位(作业流程处理使用)
     *
     * @return
     */
    List<RuntimeRole> getTaskPostList(@Param("dayPlanId") String dayPlanId, @Param("unitCode") String unitCode, @Param("staffId") String staffId, @Param("trainsetId") String trainsetId);

    /**
     * 获取故障转关键作业的故障id
     */
    Set<String> getFaultIdList(@Param("unitCode") String unitCode);

    /**
     * 获取指定运用所、指定日计划、指定车组、指定人所派的班组列表
     *
     * @param dayPlanId
     * @param unitCode
     * @param staffId
     * @param trainsetIds
     * @return
     */
    List<WorkTeam> getTaskPostWorkTeamList(@Param("unitCode") String unitCode, @Param("dayPlanId") String dayPlanId, @Param("trainsetIds") String[] trainsetIds, @Param("staffId") String staffId);

    /**
     * 某段时间里的流程运行
     */
    List<String> getFlowRunList(@Param("startDataTime") Date startTime, @Param("endDataTime") Date endTime, @Param("unitCode") String unitCode, @Param("flowCode") String flowCode);

    /**
     * 查询岗位信息
     */
    List<RuntimeRole> getPostByStaffId(@Param("unitCode") String unitCode, @Param("staffId") String staffId);

}
