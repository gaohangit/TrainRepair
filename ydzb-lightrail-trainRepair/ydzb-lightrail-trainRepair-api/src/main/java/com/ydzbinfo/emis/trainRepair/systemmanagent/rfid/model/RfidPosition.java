package com.ydzbinfo.emis.trainRepair.systemmanagent.rfid.model;

import lombok.Data;

import java.util.List;

/**
 * Description: 标签位置返回数据
 *
 * Author: 吴跃常
 * Create Date Time: 2021/7/28 18:34
 * Update Date Time: 2021/7/28 18:34
 *
 * @see
 */
@Data
public class RfidPosition {

    /**
     * 股道编号
     */
    private String trackCode;

    /**
     * 股道名称
     */
    private String trackName;

    /**
     * 列位编号
     */
    private String placeCode;

    /**
     * 列位名称
     */
    private String placeName;

    /**
     * 编组数
     */
    private String carCount;

    /**
     * 车头方向辆序
     */
    private String headDirectionCarNo;

    /**
     * 车尾方向辆序
     */
    private String tailDirectionCarNo;

    /**
     * 标签编号
     */
    private String tId;

    /**
     * 车头方向列位名称
     */
    private String headDirectionName;

    /**
     * 车尾方向列位名称
     */
    private String tailDirectionName;

    /**
     * 主键id
     */
    private String ruleId;

    /**
     * 辆序数量
     */
    private List<Integer> carCounts;
}
