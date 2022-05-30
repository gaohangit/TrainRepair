package com.ydzbinfo.emis.configs.kafka.taskallot;

import com.ydzbinfo.emis.configs.kafka.BaseMqSink;
import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;

/**
 * @author 冯帅
 * @modifier 张天可
 */
public interface TaskAllotConfigMqSink extends BaseMqSink {

    String POSTCONFIG_INPUT = "postconfig_input";

    @Input(POSTCONFIG_INPUT)
    SubscribableChannel postconfigChannel();

    String TASKALLOTONECONFIG_INPUT = "taskallotoneconfig_input";

    @Input(TASKALLOTONECONFIG_INPUT)
    SubscribableChannel taskallotoneconfigChannel();

    String TASKALLOTTWOCINFIG_INPUT = "taskallottwoconfig_input";

    @Input(TASKALLOTTWOCINFIG_INPUT)
    SubscribableChannel taskallottwoconfigChannel();
}
