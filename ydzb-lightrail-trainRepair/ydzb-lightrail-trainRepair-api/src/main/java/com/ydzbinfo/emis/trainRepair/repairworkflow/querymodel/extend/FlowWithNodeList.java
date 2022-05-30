package com.ydzbinfo.emis.trainRepair.repairworkflow.querymodel.extend;

import com.ydzbinfo.emis.trainRepair.repairworkflow.querymodel.NodeVector;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 流程信息携带节点信息
 *
 * @author 张天可
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class FlowWithNodeList extends FlowWithExtraInfo {
    /**
     * 流程节点列表
     */
    private List<NodeWithExtraInfo> nodeWithExtraInfoList;

    /**
     * 流程节点向量列表
     */
    private List<NodeVector> nodeVectors;
}
