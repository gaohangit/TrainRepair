package com.ydzbinfo.emis.trainRepair.repairworkflow.querymodel.extend;

import com.ydzbinfo.emis.trainRepair.repairworkflow.querymodel.ExtraFlowType;
import com.ydzbinfo.emis.trainRepair.repairworkflow.querymodel.FlowType;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * @author gaohan
 * @description
 * @createDate 2021/4/28 10:28
 **/
@Data
public class FlowTypeWithExtraFlowType extends FlowType {
    /**
     * 额外流程类型表
     */
    private List<ExtraFlowType> extraFlowTypeList;
}
