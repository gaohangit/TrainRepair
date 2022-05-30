package com.ydzbinfo.emis.trainRepair.repairworkflow.model;

import com.ydzbinfo.emis.trainRepair.repairworkflow.querymodel.NodeRecord;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 节点信息携带节点记录信息
 *
 * @author 张天可
 * @since 2021/7/2
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class NodeWithRecordBase<T extends NodeRecord> extends NodeInfo {
    /**
     * 节点记录信息
     */
    private List<T> nodeRecords;
}
