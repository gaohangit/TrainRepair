package com.ydzbinfo.emis.trainRepair.repairworkflow.model;

import com.ydzbinfo.emis.trainRepair.repairworkflow.querymodel.extend.FlowRunWithExtraInfo;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 流程运行信息，在流程图展示时使用
 *
 * @author 张天可
 * @description
 * @createDate 2021/5/14 15:21
 **/
@Data
@EqualsAndHashCode(callSuper = true)
public class FlowRunInfoForGraphShow extends FlowRunWithExtraInfo {

    /**
     * 流程信息
     */
    private FlowInfoWithNodeRecords flowInfo;

    /**
     * 流程运行额外信息/驳回信息
     */
    private FlowRunRecordInfo flowRunRecordInfo;
}
