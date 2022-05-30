package com.ydzbinfo.emis.trainRepair.trainMonitor.model;

import lombok.Builder;
import lombok.Data;

/**
 * @author 高晗
 * @description
 * @createDate 2022/4/8 15:07
 **/
@Data
public class FlowWithFlowRun {
    /**
     * 流程id
     */
    private String flowId;

    /**
     * 流程运行id
     */
    private String flowRunId;

    /**
     * 是否触发
     */
    Boolean trigger;


}
