package com.ydzbinfo.emis.configs.kafka.taskallot;

import com.ydzbinfo.emis.configs.kafka.SpringCloudStreamModuleEnum;
import com.ydzbinfo.emis.utils.SpringCloudStreamUtil;

/**
 * @author 张天可
 * @since 2021/11/25
 */
public class TaskAllotConfigEnableSendCloudDataCondition extends SpringCloudStreamUtil.EnableSendCloudDataConditionBase {
    @Override
    protected SpringCloudStreamModuleEnum getModule() {
        return SpringCloudStreamModuleEnum.TASK_ALLOT_CONFIG;
    }
}
