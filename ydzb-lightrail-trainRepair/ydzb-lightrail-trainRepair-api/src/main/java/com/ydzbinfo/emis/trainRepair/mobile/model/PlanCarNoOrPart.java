package com.ydzbinfo.emis.trainRepair.mobile.model;

import lombok.Data;

@Data
public class PlanCarNoOrPart {


    /**
     * 辆序或者部件名称
     */
    private String carNoOrPartName;

    /**
     * 是否选中(0,没有选中,1，选中)
     */
    private String isChoseState;


}
