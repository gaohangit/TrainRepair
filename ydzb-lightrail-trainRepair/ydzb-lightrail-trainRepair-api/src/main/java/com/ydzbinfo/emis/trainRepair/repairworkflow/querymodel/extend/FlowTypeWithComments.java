package com.ydzbinfo.emis.trainRepair.repairworkflow.querymodel.extend;

import com.ydzbinfo.emis.trainRepair.repairworkflow.querymodel.FlowType;
import com.ydzbinfo.emis.trainRepair.repairworkflow.querymodel.FlowTypeComment;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * @author gaohan
 * @description
 * @createDate 2021/5/8 15:54
 **/
@Data
@EqualsAndHashCode(callSuper = true)
public class FlowTypeWithComments extends FlowType {
    //流程类型描述
    List<FlowTypeComment> flowTypeComments;
}
