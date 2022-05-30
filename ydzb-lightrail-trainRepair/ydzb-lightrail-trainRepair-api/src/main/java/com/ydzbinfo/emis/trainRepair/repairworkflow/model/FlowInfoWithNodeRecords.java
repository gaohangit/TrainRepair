package com.ydzbinfo.emis.trainRepair.repairworkflow.model;

import com.ydzbinfo.emis.trainRepair.repairworkflow.model.base.FlowInfoBase;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 流程配置信息，携带节点状态信息和节点记录信息
 *
 * @author 张天可
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class FlowInfoWithNodeRecords extends FlowInfoBase {

    /**
     * 节点信息列表
     */
    private List<NodeWithRecord> nodes;

}
