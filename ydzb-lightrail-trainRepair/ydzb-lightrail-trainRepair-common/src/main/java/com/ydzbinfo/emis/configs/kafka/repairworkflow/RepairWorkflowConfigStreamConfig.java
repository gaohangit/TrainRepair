package com.ydzbinfo.emis.configs.kafka.repairworkflow;

import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

/**
 * 流程配置功能的数据同步配置
 *
 * @author 张天可
 */
public class RepairWorkflowConfigStreamConfig {

    @Configuration
    @Conditional(RepairWorkflowConfigEnableReceiveCloudDataCondition.class)
    @EnableBinding(RepairWorkflowConfigMqSink.class)
    public static class ReceiverConfig {
    }

    @Configuration
    @Conditional(RepairWorkflowConfigEnableSendCloudDataCondition.class)
    @EnableBinding(RepairWorkflowConfigMqSource.class)
    public static class SenderConfig {
    }
}
