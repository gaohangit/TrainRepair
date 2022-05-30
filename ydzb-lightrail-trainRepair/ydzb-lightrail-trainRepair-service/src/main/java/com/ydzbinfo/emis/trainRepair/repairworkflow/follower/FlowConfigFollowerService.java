package com.ydzbinfo.emis.trainRepair.repairworkflow.follower;

import com.ydzbinfo.emis.configs.kafka.repairworkflow.RepairWorkflowConfigEnableReceiveCloudDataCondition;
import com.ydzbinfo.emis.configs.kafka.repairworkflow.RepairWorkflowConfigMqSink;
import com.ydzbinfo.emis.trainRepair.repairworkflow.constant.FlowConfigHeaderConstant;
import com.ydzbinfo.emis.trainRepair.repairworkflow.dao.FlowMarkConfigMapper;
import com.ydzbinfo.emis.trainRepair.repairworkflow.model.FlowInfo;
import com.ydzbinfo.emis.trainRepair.repairworkflow.model.FlowInfoDispose;
import com.ydzbinfo.emis.trainRepair.repairworkflow.querymodel.FlowMarkConfig;
import com.ydzbinfo.emis.trainRepair.repairworkflow.service.IFlowMarkConfigService;
import com.ydzbinfo.emis.trainRepair.repairworkflow.service.IFlowService;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * @author 高晗
 * @description 监听流程配置数据推送
 * @createDate 2021/11/17 10:04
 **/
@Component
@Conditional(RepairWorkflowConfigEnableReceiveCloudDataCondition.class)
public class FlowConfigFollowerService {
    @Resource
    IFlowMarkConfigService flowMarkConfigService;

    @Resource
    IFlowService flowService;

    @Resource
    FlowMarkConfigMapper flowMarkConfigMapper;

    @Transactional
    @StreamListener(target = RepairWorkflowConfigMqSink.FLOWCONFIG_INPUT, condition = "headers['operateType']=='" + FlowConfigHeaderConstant.INSERT_FLOW_MARK_CONFIG + "'")
    public void insertFlowMarkConfig(FlowMarkConfig flowMarkConfig) {
        flowMarkConfigService.sendAddFlowMarkConfig(flowMarkConfig);
    }

    @Transactional
    @StreamListener(target = RepairWorkflowConfigMqSink.FLOWCONFIG_INPUT, condition = "headers['operateType']=='" + FlowConfigHeaderConstant.DELETE_FLOW_MARK_CONFIG + "'")
    public void deleteFlowMarkConfig(String id) {
        flowMarkConfigService.sendDelFlowMarkConfigById(id);
    }

    @Transactional
    @StreamListener(target = RepairWorkflowConfigMqSink.FLOWCONFIG_INPUT, condition = "headers['operateType']=='" + FlowConfigHeaderConstant.SET_FLOW_CONFIG + "'")
    public void setFlowConfig(FlowInfoDispose flowInfoDispose) {
        flowService.sendSetTaskFlowConfig(flowInfoDispose, true);
    }

    @Transactional
    @StreamListener(target = RepairWorkflowConfigMqSink.FLOWCONFIG_INPUT, condition = "headers['operateType']=='" + FlowConfigHeaderConstant.SET_DEFAULT_FLOW_CONFIG + "'")
    public void setDefaultFlowConfig(String flowId) {
        flowService.sendSetDefaultFlowConfig(flowId);
    }

    @Transactional
    @StreamListener(target = RepairWorkflowConfigMqSink.FLOWCONFIG_INPUT, condition = "headers['operateType']=='" + FlowConfigHeaderConstant.SET_FLOW_USABLE + "'")
    public void setFlowUsable(String flowId) {
        flowService.sendSetFlowUsable(flowId);
    }

    @Transactional
    @StreamListener(target = RepairWorkflowConfigMqSink.FLOWCONFIG_INPUT, condition = "headers['operateType']=='" + FlowConfigHeaderConstant.DELETE_FLOW_CONFIG + "'")
    public void deleteFlowConfig(FlowInfo flowInfo) {
        flowService.sendDeleteFlowInfoAndRepairConditionDefaultType(flowInfo);
    }
}
