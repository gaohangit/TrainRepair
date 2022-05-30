package com.ydzbinfo.emis.configs.kafka.repairworkflow;

import com.ydzbinfo.emis.configs.kafka.BaseMqSource;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

/**
 * @author 高晗
 * @modifier 张天可
 */
public interface RepairWorkflowConfigMqSource extends BaseMqSource {

    String FLOWTYPECONFIG_OUTPUT = "flowtypeconfig_output";

    @Output(FLOWTYPECONFIG_OUTPUT)
    MessageChannel flowTypeConfigChannel();

    String KEYWORKCONFIG_OUTPUT = "keyworkconfig_output";

    @Output(KEYWORKCONFIG_OUTPUT)
    MessageChannel keyWorkConfigChannel();

    String FLOWCONFIG_OUTPUT = "flowconfig_output";

    @Output(FLOWCONFIG_OUTPUT)
    MessageChannel flowConfigChannel();
}
