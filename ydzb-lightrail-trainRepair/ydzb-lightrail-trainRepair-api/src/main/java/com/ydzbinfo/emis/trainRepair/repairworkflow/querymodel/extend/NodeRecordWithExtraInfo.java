package com.ydzbinfo.emis.trainRepair.repairworkflow.querymodel.extend;

import com.ydzbinfo.emis.trainRepair.repairworkflow.querymodel.NodeRecord;
import com.ydzbinfo.emis.trainRepair.repairworkflow.querymodel.NodeRecordExtraInfo;
import com.ydzbinfo.emis.utils.entity.ToStringUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 节点记录信息携带额外信息
 *
 * @author 张天可
 * @since 2021-03-31
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class NodeRecordWithExtraInfo extends NodeRecord {

    /**
     * 节点记录额外信息
     */
    private List<NodeRecordExtraInfo> nodeRecordExtraInfoList;

    @Override
    public String toString() {
        return ToStringUtil.toString(this);
    }
}

