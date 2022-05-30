package com.ydzbinfo.emis.configs.kafka.workcriterion;

import com.ydzbinfo.emis.configs.kafka.SpringCloudStreamModuleEnum;
import com.ydzbinfo.emis.utils.SpringCloudStreamUtil;

/**
 * @author 张天可
 * @since 2021/11/25
 */
public class WorkCriterionEnableSendCloudDataCondition extends SpringCloudStreamUtil.EnableSendCloudDataConditionBase {
    @Override
    protected SpringCloudStreamModuleEnum getModule() {
        return SpringCloudStreamModuleEnum.WORK_CRITERION;
    }
}
