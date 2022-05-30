package com.ydzbinfo.emis.trainRepair.repairworkflow.service;

import com.baomidou.mybatisplus.service.IService;
import com.ydzbinfo.emis.trainRepair.repairworkflow.querymodel.FlowExtraInfo;

/**
 * <p>
 * 流程额外信息表	如：是否为默认流程，流程选择聚合条件，切换人员聚合条件，触发人员聚合条件 服务类
 * </p>
 *
 * @author gaohan
 * @since 2021-04-02
 */
public interface IFlowExtraInfoService extends IService<FlowExtraInfo> {
    /**
     * 查询等于flowId和CONDITION的vale
     */
    FlowExtraInfo getFlowExtraInfoByFlowId(String flowId);
    /**
     * 删除流程额外信息
     */
    void delFlowExtraInfoByFlowId(String id);

    /**
     * 新增
     */
    void addFlowExtraInfo(FlowExtraInfo flowExtraInfo);
    /**
     * 修改默认状态
     */
    void setFlowDefault(String flowId,String  defaultType);

    /**
     * 查询等于flowId
     */
    FlowExtraInfo getFlowExtraDefaultByFlowId(String flowId);

}
