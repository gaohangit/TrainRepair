package com.ydzbinfo.emis.trainRepair.repairworkflow.service;


import com.baomidou.mybatisplus.service.IService;
import com.ydzbinfo.emis.trainRepair.repairworkflow.querymodel.FlowRunSwitch;

import java.util.List;

/**
 * <p>
 * 流程运行切换记录表 服务类
 * </p>
 *
 * @author gaohan
 * @since 2021-05-13
 */
public interface IFlowRunSwitchService extends IService<FlowRunSwitch> {
    /**
     * 根据日计划获取切换记录
     */
    List<FlowRunSwitch> getFlowRunSwitchListByDayPlanId(String dayPlanId);

}
