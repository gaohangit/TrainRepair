package com.ydzbinfo.emis.trainRepair.mobile.model;

import lombok.Data;

import java.util.List;

@Data
public class AllotInfo {

    /**
     * 所编码
     */
    private String unitCode;

    /**
     * 所名称
     */
    private String unitName;

    /**
     * 部门编码
     */
    private String deptCode;

    /**
     * 部门名称
     */
    private String deptName;

    /**
     * 班次
     */
    private String dayPlanId;

    /**
     * 人员集合
     */
    private List<PlanWorker> planWorkerList;


    /**
     * 车组集合
     */
    private List<AllotTrainset> allotTrainsetList;


}
