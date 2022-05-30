package com.ydzbinfo.emis.trainRepair.repairworkflow.model;

import lombok.Data;

/**
 * 并行区段信息
 *
 * @author 张天可
 */
@Data
public class FlowParallelSection implements IFlowSection {
    /**
     * 并行区段id
     */
    private String id;

    /**
     * 并行区段排序值
     */
    private Integer sort;

    /**
     * 并行区段节点id
     */
    private String[] nodeIds;
}
