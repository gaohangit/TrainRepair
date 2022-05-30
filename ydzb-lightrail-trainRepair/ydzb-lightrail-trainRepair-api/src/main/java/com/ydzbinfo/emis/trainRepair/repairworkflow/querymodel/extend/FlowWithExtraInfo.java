package com.ydzbinfo.emis.trainRepair.repairworkflow.querymodel.extend;

import com.ydzbinfo.emis.trainRepair.repairworkflow.querymodel.Flow;
import com.ydzbinfo.emis.trainRepair.repairworkflow.querymodel.FlowExtraInfo;
import com.ydzbinfo.emis.utils.entity.ToStringUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 流程信息携带额外信息
 *
 * @author 张天可
 * @since 2021-03-31
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class FlowWithExtraInfo extends Flow {

    /**
     * 流程额外信息
     */
    private List<FlowExtraInfo> flowExtraInfoList;

    @Override
    public String toString() {
        return ToStringUtil.toString(this);
    }
}
