package com.ydzbinfo.emis.trainRepair.repairworkflow.dao;


import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.ydzbinfo.emis.trainRepair.repairworkflow.querymodel.FlowRunRecord;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * <p>
 * 流程运行记录表 Mapper 接口
 * </p>
 *
 * @author gaohan
 * @since 2021-05-13
 */
public interface FlowRunRecordMapper extends BaseMapper<FlowRunRecord> {

    List<FlowRunRecord> getFlowRunRecordsByForceEnd(@Param("unitCode") String unitCode, @Param("dayPlanId") String dayPlanId, @Param("flowRunId") String flowRunId, @Param("startDate") Date startDate, @Param("endDate") Date endDate);

}
