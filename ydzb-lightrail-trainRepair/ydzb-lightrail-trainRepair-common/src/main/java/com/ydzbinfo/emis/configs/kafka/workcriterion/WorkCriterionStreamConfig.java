package com.ydzbinfo.emis.configs.kafka.workcriterion;

import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

/**
 * 派工配置功能的数据同步配置
 *
 * @author 张天可
 */
@Configuration
public class WorkCriterionStreamConfig {

    @Configuration
    @Conditional(WorkCriterionEnableReceiveCloudDataCondition.class)
    @EnableBinding(WorkCriterionMqSink.class)
    public static class ReceiverConfig {
    }

    @Configuration
    @Conditional(WorkCriterionEnableSendCloudDataCondition.class)
    @EnableBinding(WorkCriterionMqSource.class)
    public static class SenderConfig {
    }
}
