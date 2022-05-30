package com.ydzbinfo.emis.trainRepair.repairworkflow.service;

import com.baomidou.mybatisplus.service.IService;
import com.ydzbinfo.emis.trainRepair.repairworkflow.model.FlowInfo;
import com.ydzbinfo.emis.trainRepair.repairworkflow.model.FlowInfoDispose;
import com.ydzbinfo.emis.trainRepair.repairworkflow.model.QueryFlowModel;
import com.ydzbinfo.emis.trainRepair.repairworkflow.model.QueryFlowModelGeneral;
import com.ydzbinfo.emis.trainRepair.repairworkflow.querymodel.Flow;
import com.ydzbinfo.emis.trainRepair.repairworkflow.querymodel.extend.FlowWithExtraInfo;
import lombok.Data;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 流程表 服务类
 * </p>
 *
 * @author gaohan
 * @since 2021-04-02
 */
public interface IFlowService extends IService<Flow> {
    /**
     * 查询作业流程配置
     *
     * @return
     */
    List<FlowInfo> getFlowInfoList(QueryFlowModelGeneral queryFlowModelGeneral);

    /**
     * 查询流程和流程额外信息
     *
     * @return
     */
    List<FlowWithExtraInfo> getFlowWithExtraInfoList(QueryFlowModel queryFlowModel);

    /**
     * 查询详细流程配置
     */
    FlowInfo getFlowInfoByFlowId(String flowId);

    @Data
    class SetTaskFlowConfigResult {
        String id;
        String resultInfo;
        String warningInfo;
        //是否需要确认取消发布流程
        Boolean needCancelUsable = false;
        String needCancelUsableMessage;
    }

    /**
     * 修改详细流程
     *
     * @return
     */
    SetTaskFlowConfigResult setTaskFlowConfig(FlowInfoDispose flowInfoDispose, boolean newIdUseFlowInfo);

    SetTaskFlowConfigResult sendSetTaskFlowConfig(FlowInfoDispose flowInfoDispose, boolean newIdUseFlowInfo);

    /**
     * 修改流程表
     */
    void updFlowById(Flow flow);

    /**
     * 新增流程表
     */
    void addFlow(Flow flow);

    /**
     * 删除流程信息
     *
     * @param flowInfo
     */
    String deleteFlowInfoAndRepairConditionDefaultType(FlowInfo flowInfo);

    /**
     * 删除流程信息前提示信息
     *
     * @param flowId
     */
    String getDelTaskFlowConfigWarningInfo(String flowId);

    String sendDeleteFlowInfoAndRepairConditionDefaultType(FlowInfo flowInfo);

    /**
     * 删除流程信息
     */
    void delFlowInfoById(String flowId);

    /**
     * 查询本组(同条件)最早流程
     */
    Flow getFirstFlowByCondition(String flowId,String condition);

    /**
     * 查询流程表
     */
    Flow getFlowByFlowId(String flowId);

    /**
     * 校验流程配置
     *
     * @return
     */
    void verifyFlowType(String unitCode);

    Flow getFlowFirstDefaultName(@Param("condition") String condition, @Param("flowId") String flowId);

    List<FlowWithExtraInfo> getFlowByFlowTypeCode(String unitCode, String flowTypeCode, boolean queryPastFlow, String flowPageCode);

    /**
     * 修改为已发布状态
     *
     * @param flowId
     */
    void setFlowUsable(String flowId);

    void sendSetFlowUsable(String flowId);

    /**
     * 修改为默认状态
     *
     * @param flowId
     * @return
     */
    String setDefaultFlowConfig(String flowId);

    String sendSetDefaultFlowConfig(String flowId);

    /**
     * 查询同组其他流程
     */
    List<Flow> getConditionFlowsByFlowId(String flowId, String unitCode);
}
