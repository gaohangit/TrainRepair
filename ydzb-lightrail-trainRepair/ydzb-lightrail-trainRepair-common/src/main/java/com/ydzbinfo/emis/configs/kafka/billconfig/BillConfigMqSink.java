package com.ydzbinfo.emis.configs.kafka.billconfig;

import com.ydzbinfo.emis.configs.kafka.BaseMqSink;
import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;

/**
 * @author 张天可
 */
public interface BillConfigMqSink extends BaseMqSink {

    String TEMPLATESUMMARY_INPUT = "templatesummary_input";

    @Input(TEMPLATESUMMARY_INPUT)
    SubscribableChannel templatesummaryChannel();

    String TEMPLATEPROCESS_INPUT = "templateprocess_input";

    @Input(TEMPLATEPROCESS_INPUT)
    SubscribableChannel templateprocessChannel();

    String BASETEMPLATE_INPUT = "basetemplate_input";

    @Input(BASETEMPLATE_INPUT)
    SubscribableChannel basetemplateChannel();

    String SSJSONFILE_INPUT = "ssjsonfile_input";

    @Input(SSJSONFILE_INPUT)
    SubscribableChannel ssjsonfileChannel();
}
