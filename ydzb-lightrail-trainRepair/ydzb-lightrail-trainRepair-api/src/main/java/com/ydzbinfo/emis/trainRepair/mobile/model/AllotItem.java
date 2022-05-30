package com.ydzbinfo.emis.trainRepair.mobile.model;

import lombok.Data;

import java.util.List;

@Data
public class AllotItem {

    /**
     * 项目编码
     */
    private String itemCode;

    /**
     * 项目名称
     */
    private String itemName;

    /**
     * 展示项目名称（仅作为一级修时使用）
     */
    private String displayItemName;


    /**
     * 辆序或部件集合
     */
    private List<AllotCarOrPart> allotCarOrPartList;

}
