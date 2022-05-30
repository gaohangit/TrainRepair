package com.ydzbinfo.emis.trainRepair.trainMonitor.model;

import com.ydzbinfo.emis.trainRepair.workProcessMonitor.model.FaultItem;
import lombok.Data;

import java.util.List;

/**
 * @author gaohan
 * @description
 * @createDate 2021/3/11 13:58
 **/
@Data
public class RepairTask {
    //车组号
    private String trainsetId;

    private Boolean dayPlan;

    //一级修
    private Boolean repairOne;

    //二级修
    private Boolean repairTwo;

    //故障
    private int faultNumber;

    //故障列表
    private List<FaultItem> faultItemList;

    //临修
    private int temporaryNumber;

    //滤网
    private Boolean filterNumber;

    //关键作业
    private int planLessKeyNumber;
    //关键作业是否当前班次
    private Boolean planLessKeyDay;

    //关键作业流程配置id
    private List<String> planLessKeyFlowId;

    //整备作业
    private FlowWithFlowRun hostLing;

    //机检
    private FlowWithFlowRun machineRepair;

    //漩轮作业
    private FlowWithFlowRun millWheel;



}
