package com.ydzbinfo.emis.configs.kafka.workcriterion;

import com.ydzbinfo.emis.configs.kafka.BaseMqSource;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

/**
 * @author 冯帅
 * @modifier 张天可
 */
public interface WorkCriterionMqSource extends BaseMqSource {

    String WORKCRITERTIONONE_OUTPUT = "workcritertionone_output";

    @Output(WORKCRITERTIONONE_OUTPUT)
    MessageChannel workcritertiononeChannel();
}
