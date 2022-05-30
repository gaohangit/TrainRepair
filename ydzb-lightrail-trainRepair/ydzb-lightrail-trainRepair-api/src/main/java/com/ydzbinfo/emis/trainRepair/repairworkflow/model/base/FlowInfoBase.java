package com.ydzbinfo.emis.trainRepair.repairworkflow.model.base;

import com.ydzbinfo.emis.trainRepair.repairworkflow.model.FlowParallelSection;
import com.ydzbinfo.emis.trainRepair.repairworkflow.model.PairNodeInfo;
import com.ydzbinfo.emis.trainRepair.repairworkflow.model.SubflowInfo;
import com.ydzbinfo.emis.trainRepair.repairworkflow.querymodel.base.BaseFlow;
import com.ydzbinfo.emis.trainRepair.repairworkflow.querymodel.extend.NodeVectorWithExtraInfo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 基础流程配置信息
 *
 * @author 张天可
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class FlowInfoBase extends BaseFlow {

    /**
     * 是否可用, 用于临时保存尚未配置完整的流程
     */
    private Boolean usable;

    /**
     * 是否已经逻辑删除
     */
    private Boolean deleted;

    /**
     * 是否默认
     */
    private Boolean defaultType;

    /**
     * 条件表主键
     */
    private String conditionId;

    /**
     * 流程节点向量列表
     */
    private List<NodeVectorWithExtraInfo> nodeVectors;

    /**
     * 当前流程节点子流程列表
     */
    private List<SubflowInfo> subflowInfoList;

    /**
     * 并行区段信息列表
     */
    private List<FlowParallelSection> parallelSections;

    /**
     * 成对节点信息，为成对节点id的二维数组
     */
    private List<PairNodeInfo> pairNodeInfo;

}
