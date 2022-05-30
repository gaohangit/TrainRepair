package com.ydzbinfo.emis.trainRepair.mobile.model;

import lombok.Data;

import java.util.List;

@Data
public class PlanAllotInfo {

    /**
     * 辆序或者部件名称
     */
    private String carNoOrPartName;


    /**
     * 人员名称
     */
    private String planWorkers;
}
