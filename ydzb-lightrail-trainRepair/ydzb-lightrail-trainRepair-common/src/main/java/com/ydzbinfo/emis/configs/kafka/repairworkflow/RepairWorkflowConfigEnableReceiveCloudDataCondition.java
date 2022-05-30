package com.ydzbinfo.emis.configs.kafka.repairworkflow;

import com.ydzbinfo.emis.configs.kafka.SpringCloudStreamModuleEnum;
import com.ydzbinfo.emis.utils.SpringCloudStreamUtil;

/**
 * @author 张天可
 * @since 2021/11/25
 */
public class RepairWorkflowConfigEnableReceiveCloudDataCondition extends SpringCloudStreamUtil.EnableReceiveCloudDataConditionBase {
    @Override
    protected SpringCloudStreamModuleEnum getModule() {
        return SpringCloudStreamModuleEnum.REPAIR_WORKFLOW_CONFIG;
    }
}
