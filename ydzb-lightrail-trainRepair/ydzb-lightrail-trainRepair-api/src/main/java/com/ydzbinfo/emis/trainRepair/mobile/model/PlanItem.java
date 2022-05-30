package com.ydzbinfo.emis.trainRepair.mobile.model;

import lombok.Data;

@Data
public class PlanItem {

    /**
     * 项目编码
     */
    private String itemCode;

    /**
     * 项目名称
     */
    private String itemName;

    /**
     * 项目下是否有人,（0，没有，1，有）
     */
    private String isExistPerson;

    /**
     * 展示项目名称（仅作为一级修时使用）
     */
    private String displayItemName;

    /**
     * 是辆序还是部件（0辆序,1部件）
     */
    private String arrangeType;

    /**
     * 是否可以修改(0,不能，1，可以)
     */
    private String fillType;


    /**
     * 是否新项目（0，不是，1，是）
     */
    private String newItem;





}
