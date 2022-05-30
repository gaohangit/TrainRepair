package com.ydzbinfo.emis.configs.kafka.taskallot;

import com.ydzbinfo.emis.configs.kafka.BaseMqSource;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

/**
 * @author 冯帅
 * @modifier 张天可
 */
public interface TaskAllotConfigMqSource extends BaseMqSource {

    String POSTCONFIG_OUTPUT = "postconfig_output";

    @Output(POSTCONFIG_OUTPUT)
    MessageChannel postconfigChannel();

    String TASKALLOTONECONFIG_OUTPUT = "taskallotoneconfig_output";

    @Output(TASKALLOTONECONFIG_OUTPUT)
    MessageChannel taskallotoneconfigChannel();

    String TASKALLOTTWOCONFIG_OUTPUT = "taskallottwoconfig_output";

    @Output(TASKALLOTTWOCONFIG_OUTPUT)
    MessageChannel taskallottwoconfigChannel();
}
