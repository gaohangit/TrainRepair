package com.ydzbinfo.emis.configs.kafka.workcriterion;

import com.ydzbinfo.emis.configs.kafka.BaseMqSink;
import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;

/**
 * @author 冯帅
 * @modifier 张天可
 */
public interface WorkCriterionMqSink extends BaseMqSink {

    String WORKCRITERTIONONE_INPUT = "workcritertionone_input";

    @Input(WORKCRITERTIONONE_INPUT)
    SubscribableChannel workcritertiononeChannel();
}
