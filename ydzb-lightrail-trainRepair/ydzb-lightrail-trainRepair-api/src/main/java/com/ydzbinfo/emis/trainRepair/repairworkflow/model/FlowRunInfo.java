package com.ydzbinfo.emis.trainRepair.repairworkflow.model;

import lombok.Data;

import java.util.List;

/**
 * @author 高晗
 * @description 流程运行结果和异常流程提示
 * @createDate 2021/11/11 16:04
 **/
@Data
public class FlowRunInfo {
    private String errorFlowFunInfos;
    private List<FlowRunInfoGroup> flowRunInfoGroups;
}
