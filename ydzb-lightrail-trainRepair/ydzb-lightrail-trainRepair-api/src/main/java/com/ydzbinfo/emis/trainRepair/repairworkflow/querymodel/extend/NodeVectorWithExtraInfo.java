package com.ydzbinfo.emis.trainRepair.repairworkflow.querymodel.extend;

import com.ydzbinfo.emis.trainRepair.repairworkflow.querymodel.NodeVector;
import com.ydzbinfo.emis.trainRepair.repairworkflow.querymodel.NodeVectorExtraInfo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * @author gaohan
 * @description
 * @createDate 2021/4/12 16:11
 **/
@Data
@EqualsAndHashCode(callSuper = true)
public class NodeVectorWithExtraInfo extends NodeVector {
    /**
     *节点向量额外信息表
     */
    private List<NodeVectorExtraInfo> nodeVectorExtraInfoList;
}
