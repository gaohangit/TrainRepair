package com.ydzbinfo.emis.trainRepair.repairworkflow.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.ydzbinfo.emis.trainRepair.repairworkflow.model.QueryFlowModel;
import com.ydzbinfo.emis.trainRepair.repairworkflow.querymodel.Flow;
import com.ydzbinfo.emis.trainRepair.repairworkflow.querymodel.extend.FlowWithExtraInfo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 流程表 Mapper 接口
 * </p>
 *
 * @author gaohan
 * @since 2021-04-02
 */
public interface FlowMapper extends BaseMapper<Flow> {
    /**
     * 查询流程配置
     * @param queryFlowModel 查询条件
     */
    List<FlowWithExtraInfo> getFlowWithExtraInfoList(QueryFlowModel queryFlowModel);
    /**
     * 查询本组(同条件)最早流程
     */
    Flow getFlowFirstName(@Param("flowId") String flowId,@Param("conditionId") String conditionId);

    Flow getFlowFirstDefaultName(@Param("condition") String condition, @Param("flowId") String flowId);

    List<Flow> getFlowListByFlow(Flow flow);

    /**
     * 查询同条件下所有的流程
     * @param flowId
     * @return
     */
    List<Flow> getConditionFlowsByFlowId(@Param("flowId") String flowId, @Param("unitCode") String unitCode);
}
