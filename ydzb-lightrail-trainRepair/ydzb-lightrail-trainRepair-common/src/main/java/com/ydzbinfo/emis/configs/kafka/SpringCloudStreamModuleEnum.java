package com.ydzbinfo.emis.configs.kafka;

import com.ydzbinfo.emis.configs.kafka.billconfig.BillConfigMqSink;
import com.ydzbinfo.emis.configs.kafka.billconfig.BillConfigMqSource;
import com.ydzbinfo.emis.configs.kafka.repairworkflow.RepairWorkflowConfigMqSink;
import com.ydzbinfo.emis.configs.kafka.repairworkflow.RepairWorkflowConfigMqSource;
import com.ydzbinfo.emis.configs.kafka.taskallot.TaskAllotConfigMqSink;
import com.ydzbinfo.emis.configs.kafka.taskallot.TaskAllotConfigMqSource;
import com.ydzbinfo.emis.configs.kafka.workcriterion.WorkCriterionMqSink;
import com.ydzbinfo.emis.configs.kafka.workcriterion.WorkCriterionMqSource;
import lombok.Getter;

/**
 * 数据同步模块枚举类
 *
 * @author 张天可
 * @since 2021/11/25
 */
@Getter
public enum SpringCloudStreamModuleEnum {
    REPAIR_WORKFLOW_CONFIG(RepairWorkflowConfigMqSource.class, RepairWorkflowConfigMqSink.class),
    TASK_ALLOT_CONFIG(TaskAllotConfigMqSource.class, TaskAllotConfigMqSink.class),
    WORK_CRITERION(WorkCriterionMqSource.class, WorkCriterionMqSink.class),
    BILL_CONFIG(BillConfigMqSource.class, BillConfigMqSink.class),
    ;
    private final Class<? extends BaseMqSource> sourceClass;
    private final Class<? extends BaseMqSink> sinkClass;

    SpringCloudStreamModuleEnum(Class<? extends BaseMqSource> sourceClass, Class<? extends BaseMqSink> sinkClass) {
        this.sourceClass = sourceClass;
        this.sinkClass = sinkClass;
    }
}
