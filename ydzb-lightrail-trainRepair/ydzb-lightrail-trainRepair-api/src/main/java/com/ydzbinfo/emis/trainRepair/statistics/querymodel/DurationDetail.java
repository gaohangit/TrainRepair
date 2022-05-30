package com.ydzbinfo.emis.trainRepair.statistics.querymodel;

import lombok.Data;

import java.util.Date;

/**
 * Description:
 * Author: 吴跃常
 * Create Date Time: 2021/6/23 16:36
 * Update Date Time: 2021/6/23 16:36
 *
 * @see
 */
@Data
public class DurationDetail {

    /**
     * 项目名称
     */
    private String itemName;

    /**
     * 班组名称
     */
    private String deptName;

    /**
     * 作业人员
     */
    private String workName;

    /**
     * 开始时间
     */
    private Date startTime;

    /**
     * 结束时间
     */
    private Date endTime;

    /**
     * 作业时长
     */
    private Long duration;
}
