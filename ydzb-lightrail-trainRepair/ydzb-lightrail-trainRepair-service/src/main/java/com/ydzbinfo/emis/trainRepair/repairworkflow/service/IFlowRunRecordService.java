package com.ydzbinfo.emis.trainRepair.repairworkflow.service;


import com.baomidou.mybatisplus.service.IService;
import com.ydzbinfo.emis.trainRepair.repairworkflow.model.FlowRunRecordInfo;
import com.ydzbinfo.emis.trainRepair.repairworkflow.querymodel.FlowRunRecord;

import java.util.Date;
import java.util.List;

/**
 * <p>
 * 流程运行记录表 服务类
 * </p>
 *
 * @author gaohan
 * @since 2021-05-13
 */
public interface IFlowRunRecordService extends IService<FlowRunRecord> {
    /**
     * 获取日计划下被驳回/强制结束的流程信息
     */
    List<FlowRunRecordInfo> getFlowRunRecordsByForceEnd(String unitCode,String dayPlanId,String flowRunId, Date startDate, Date endDate);
}
