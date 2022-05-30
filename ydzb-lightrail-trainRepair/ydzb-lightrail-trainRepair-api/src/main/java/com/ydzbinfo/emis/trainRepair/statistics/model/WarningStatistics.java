package com.ydzbinfo.emis.trainRepair.statistics.model;

import com.ydzbinfo.emis.trainRepair.warnmanagent.querymodel.WorkWorning;
import lombok.Data;

import java.util.List;

/**
 * Description:
 * Author: 吴跃常
 * Create Date Time: 2021/6/28 9:14
 * Update Date Time: 2021/6/28 9:14
 *
 * @see
 */
@Data
public class WarningStatistics {

    /**
     * 预警人员
     */
    private List<WorkDeptWarning> workPerson;

    /**
     * 预警班组
     */
    private List<WorkDeptWarning> workDept;

    /**
     * 总数
     */
    private Integer total;

    /**
     * 预警明细
     */
    private List<WorkWorning> workWornings;
}
