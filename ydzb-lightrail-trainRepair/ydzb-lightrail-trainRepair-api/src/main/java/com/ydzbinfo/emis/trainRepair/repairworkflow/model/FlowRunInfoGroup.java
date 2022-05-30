package com.ydzbinfo.emis.trainRepair.repairworkflow.model;

import lombok.Data;

import java.util.List;

/**
 * @author gaohan
 * @description 完整流程运行信息
 * @createDate 2021/5/14 9:52
 **/
@Data
public class FlowRunInfoGroup {

    private String trainsetId;

    /**
     * 基本流程信息和节点信息
     */
    private List<FlowRunInfoForSimpleShow> flowRunInfoForSimpleShows;


}
