package com.ydzbinfo.emis.trainRepair.repairworkflow.utils;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 处理结果展示
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class NodeDisposeResult extends NodeDisposeCheckResult {

    /**
    * 流程是否完成
    **/
    private Boolean flowRunFinished;

    /**
     * 流程运行id
     */
    private String flowRunId;

}
