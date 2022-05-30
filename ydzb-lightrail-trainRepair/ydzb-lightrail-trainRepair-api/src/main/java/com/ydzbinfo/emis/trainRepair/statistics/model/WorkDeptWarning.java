package com.ydzbinfo.emis.trainRepair.statistics.model;

import lombok.Data;

/**
 * Description:
 * Author: 吴跃常
 * Create Date Time: 2021/6/25 16:46
 * Update Date Time: 2021/6/25 16:46
 *
 * @see
 */
@Data
public class WorkDeptWarning {

    /**
     * 作业人员
     */
    private String workName;

    /**
     * 作业班组
     */
    private String deptName;

    /**
     * 作业数量
     */
    private int count;
}
