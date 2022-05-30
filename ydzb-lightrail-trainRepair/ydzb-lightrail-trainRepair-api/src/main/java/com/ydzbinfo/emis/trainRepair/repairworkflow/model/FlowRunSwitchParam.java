package com.ydzbinfo.emis.trainRepair.repairworkflow.model;

import com.ydzbinfo.emis.trainRepair.repairworkflow.querymodel.FlowRun;
import lombok.Data;

/**
 * @author 高晗
 * @description 流程切换参数
 * @createDate 2022/1/11 9:51
 **/
@Data
public class FlowRunSwitchParam {
    /**
     * 流程运行信息
     */
    private FlowRun flowRun;
    /**
     * 切换流程id
     */
    private String targetFlowId;
}
