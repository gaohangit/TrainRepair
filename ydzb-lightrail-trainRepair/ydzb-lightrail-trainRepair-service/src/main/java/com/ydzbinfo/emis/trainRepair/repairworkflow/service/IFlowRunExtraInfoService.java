package com.ydzbinfo.emis.trainRepair.repairworkflow.service;


import com.baomidou.mybatisplus.service.IService;
import com.ydzbinfo.emis.trainRepair.repairworkflow.querymodel.FlowRunExtraInfo;

/**
 * <p>
 * 流程运行额外信息表 服务类
 * </p>
 *
 * @author gaohan
 * @since 2021-05-14
 */
public interface IFlowRunExtraInfoService extends IService<FlowRunExtraInfo> {
    void addFlowRunExtraInfo(FlowRunExtraInfo flowRunExtraInfo);
}
