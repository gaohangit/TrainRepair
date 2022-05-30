package com.ydzbinfo.emis.trainRepair.repairworkflow.querymodel.extend;

import com.ydzbinfo.emis.trainRepair.repairworkflow.querymodel.FlowRun;
import com.ydzbinfo.emis.trainRepair.repairworkflow.querymodel.FlowRunExtraInfo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 流程运行信息，携带流程运行额外信息
 * @author 张天可
 * @description
 * @createDate 2021/5/19 15:20
 **/

@Data
@EqualsAndHashCode(callSuper = true)
public class FlowRunWithExtraInfo extends FlowRun {

    /**
     * 流程运行额外信息列表
     */
    List<FlowRunExtraInfo> flowRunExtraInfoList;
}
