package com.ydzbinfo.emis.trainRepair.repairworkflow.model;

import com.ydzbinfo.emis.trainRepair.repairworkflow.querymodel.NodeVector;
import com.ydzbinfo.emis.trainRepair.repairworkflow.querymodel.base.BaseFlow;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 子流程配置信息
 *
 * @author 张天可
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SubflowInfo extends BaseFlow {

    /**
     * 节点信息列表
     */
    private List<NodeInfo> nodes;

    /**
     * 流程节点向量列表
     */
    private List<NodeVector> nodeVectors;

}
