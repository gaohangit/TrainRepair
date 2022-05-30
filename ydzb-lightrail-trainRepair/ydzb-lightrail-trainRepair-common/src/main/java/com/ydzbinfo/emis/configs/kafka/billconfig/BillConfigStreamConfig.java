package com.ydzbinfo.emis.configs.kafka.billconfig;

import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

/**
 * 记录单单据配置功能的数据同步配置
 *
 * @author 张天可
 */
@Configuration
public class BillConfigStreamConfig {

    @Configuration
    @Conditional(BillConfigEnableReceiveCloudDataCondition.class)
    @EnableBinding(BillConfigMqSink.class)
    public static class ReceiverConfig {
    }

    @Configuration
    @Conditional(BillConfigEnableSendCloudDataCondition.class)
    @EnableBinding(BillConfigMqSource.class)
    public static class SenderConfig {
    }
}
