package com.ydzbinfo.emis.configs.kafka.billconfig;

import com.ydzbinfo.emis.configs.kafka.BaseMqSource;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.SubscribableChannel;

/**
 * @author 张天可
 */
public interface BillConfigMqSource extends BaseMqSource {

    String TEMPLATESUMMARY_OUTPUT = "templatesummary_output";

    @Output(TEMPLATESUMMARY_OUTPUT)
    MessageChannel templatesummaryChannel();

    String TEMPLATEPROCESS_OUTPUT = "templateprocess_output";

    @Output(TEMPLATEPROCESS_OUTPUT)
    MessageChannel templateprocessChannel();

    String BASETEMPLATE_OUTPUT = "basetemplate_output";

    @Output(BASETEMPLATE_OUTPUT)
    MessageChannel basetemplateChannel();


    String SSJSONFILE_OUTPUT = "ssjsonfile_output";

    @Output(SSJSONFILE_OUTPUT)
    SubscribableChannel ssjsonfileChannel();
}
