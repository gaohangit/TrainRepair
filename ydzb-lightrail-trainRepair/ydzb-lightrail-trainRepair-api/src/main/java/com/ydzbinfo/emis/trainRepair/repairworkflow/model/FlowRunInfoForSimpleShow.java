package com.ydzbinfo.emis.trainRepair.repairworkflow.model;

import com.ydzbinfo.emis.trainRepair.repairworkflow.querymodel.base.BaseFlow;
import com.ydzbinfo.emis.trainRepair.repairworkflow.querymodel.extend.FlowRunWithExtraInfo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 流程运行信息，在简单展示（非流程图展示）时使用
 **/
@Data
@EqualsAndHashCode(callSuper = true)
public class FlowRunInfoForSimpleShow extends FlowRunWithExtraInfo {
    /**
     * 排序后的节点列表
     */
    private List<NodeInfoForSimpleShow> nodeList;

    /**
     * 流程基本信息
     */
    private BaseFlow flowConfig;

    /**
     * 流程驳回信息
     */
    private FlowRunRecordInfo flowRunRecordInfo;
}
