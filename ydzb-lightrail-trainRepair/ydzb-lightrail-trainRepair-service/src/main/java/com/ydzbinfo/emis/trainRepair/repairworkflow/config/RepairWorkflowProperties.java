package com.ydzbinfo.emis.trainRepair.repairworkflow.config;

import com.ydzbinfo.emis.utils.TrainRepairPropertiesParent;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * 项目流程配置
 *
 * @author 张天可
 * @since 2021/11/22
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Component
@ConfigurationProperties("ydzb.repair-workflow")
public class RepairWorkflowProperties extends TrainRepairPropertiesParent<RepairWorkflowProperties> {

    /**
     * 自定义整备任务作业的流程类型编码
     */
    String hostlingFlowTypeCode;

    /**
     * 漩轮任务作业的流程类型编码
     */
    String  millWheelFlowTypeCode;

    @PostConstruct
    void checkInit() {
        // checkProperty("hostlingFlowTypeCode", "自定义整备任务作业的流程类型编码");
    }

}
