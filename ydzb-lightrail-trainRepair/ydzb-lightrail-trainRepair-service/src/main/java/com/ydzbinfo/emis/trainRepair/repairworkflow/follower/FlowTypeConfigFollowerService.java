package com.ydzbinfo.emis.trainRepair.repairworkflow.follower;

import com.ydzbinfo.emis.configs.kafka.repairworkflow.RepairWorkflowConfigEnableReceiveCloudDataCondition;
import com.ydzbinfo.emis.configs.kafka.repairworkflow.RepairWorkflowConfigMqSink;
import com.ydzbinfo.emis.trainRepair.repairworkflow.constant.FlowTypeConfigHeaderConstant;
import com.ydzbinfo.emis.trainRepair.repairworkflow.model.FlowTypeInfoWithPackets;
import com.ydzbinfo.emis.trainRepair.repairworkflow.service.IExtraFlowTypePacketService;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * @author 高晗
 * @description 监听流程类型配置数据推送
 * @createDate 2021/11/16 14:37
 **/
@Component
@Conditional(RepairWorkflowConfigEnableReceiveCloudDataCondition.class)
public class FlowTypeConfigFollowerService {
    @Resource
    IExtraFlowTypePacketService flowTypePacketService;
    @Transactional
    @StreamListener(target = RepairWorkflowConfigMqSink.FLOWTYPECONFIG_INPUT, condition = "headers['operateType']=='" + FlowTypeConfigHeaderConstant.UPDATE + "'")
    public void updFlowTypeConfigData(FlowTypeInfoWithPackets flowTypeInfoWithPackets) {
        flowTypePacketService.sendSetExtraFlowTypePacket(flowTypeInfoWithPackets);
    }
}
