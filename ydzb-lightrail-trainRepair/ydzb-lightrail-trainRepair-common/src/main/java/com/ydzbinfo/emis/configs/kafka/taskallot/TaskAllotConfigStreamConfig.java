package com.ydzbinfo.emis.configs.kafka.taskallot;

import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

/**
 * 派工配置功能的数据同步配置
 *
 * @author 张天可
 */
public class TaskAllotConfigStreamConfig {

    @Configuration
    @Conditional(TaskAllotConfigEnableReceiveCloudDataCondition.class)
    @EnableBinding(TaskAllotConfigMqSink.class)
    public static class ReceiverConfig {
    }

    @Configuration
    @Conditional(TaskAllotConfigEnableSendCloudDataCondition.class)
    @EnableBinding(TaskAllotConfigMqSource.class)
    public static class SenderConfig {
    }
}
