package com.ydzbinfo.emis.trainRepair.mobile.model;

import lombok.Data;

import java.util.Date;

/**
 * Description:     无修程查询确认人信息对象
 * Author: wuyuechang
 * Create Date Time: 2021/5/13 15:25
 * Update Date Time: 2021/5/13 15:25
 *
 * @see
 */
@Data
public class NoMainCycPersonInfo {

    /**
     * 作业人姓名
     */
    private String workerName;

    /**
     * 部门名称
     */
    private String deptName;

    /**
     * 确认时间
     */
    private Date time;

    /**
     * 项目code
     */
    private String itemCode;

    /**
     * 车组id
     */
    private String trainsetId;

    /**
     * 无修程人员类型 3-无修程任务完成，4-无修程任务确认
     */
    private String workerType;

    /**
     * 辆序
     */
    private String carNo;

    /**
     * 部件名称
     */
    private String partName;

    /**
     * 状态
     */
    private String itemTimeState;
}
