package com.ydzbinfo.emis.trainRepair.mobile.model;

import lombok.Data;

@Data
public class PlanTrainset {

    /**
     * 车组id
     */
    private String trainsetId;

    /**
     * 车组名称
     */
    private String trainsetName;
    /**
     * 派工状态名称
     */
    private String allotStateName;


}
