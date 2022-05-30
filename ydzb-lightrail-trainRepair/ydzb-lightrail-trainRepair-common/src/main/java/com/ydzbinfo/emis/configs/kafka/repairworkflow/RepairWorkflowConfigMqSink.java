package com.ydzbinfo.emis.configs.kafka.repairworkflow;

import com.ydzbinfo.emis.configs.kafka.BaseMqSink;
import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;

/**
 * @author 高晗
 * @modifier 张天可
 */
public interface RepairWorkflowConfigMqSink extends BaseMqSink {

    String FLOWTYPECONFIG_INPUT = "flowtypeconfig_input";

    @Input(FLOWTYPECONFIG_INPUT)
    SubscribableChannel flowTypeConfigChannel();

    String KEYWORKCONFIG_INPUT = "keyworkconfig_input";

    @Input(KEYWORKCONFIG_INPUT)
    SubscribableChannel keyWorkConfigChannel();

    String FLOWCONFIG_INPUT = "flowconfig_input";

    @Input(FLOWCONFIG_INPUT)
    SubscribableChannel flowConfigChannel();
}
