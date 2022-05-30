package com.ydzbinfo.emis.trainRepair.repairworkflow.querymodel.extend;

import com.ydzbinfo.emis.trainRepair.repairworkflow.querymodel.Node;
import com.ydzbinfo.emis.trainRepair.repairworkflow.querymodel.NodeExtraInfo;
import com.ydzbinfo.emis.utils.entity.ToStringUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 节点信息携带额外信息
 *
 * @author 张天可
 * @since 2021-03-31
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class NodeWithExtraInfo extends Node {

    /**
     * 节点额外信息
     */
    private List<NodeExtraInfo> nodeExtraInfoList;

    @Override
    public String toString() {
        return ToStringUtil.toString(this);
    }
}

