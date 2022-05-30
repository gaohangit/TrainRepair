package com.ydzbinfo.emis.trainRepair.mobile.model;

import lombok.Data;

import java.io.Serializable;

/**
 * <p>
 * 
 * </p>
 *
 * @author 吴跃常
 * @since 2021-05-12
 */
@Data
public class ProcessPersonInfo implements Serializable {


    /**
     * 主键
     */
    private String processPersonId;

    /**
     * 作业人员ID
     */
    private String workerId;

    /**
     * 作业人员姓名
     */
    private String workerName;

    /**
     * 作业人员类型（1--检修，2--质检，3-无修程任务完成，4-无修程任务确认）
     */
    private String workerType;

    /**
     * 项目时间唯一标识
     */
    private String itemPublcished;

    /**
     * 作业过程辆序部件表主键
     */
    private String processId;

    /**
     * 项目code
     */
    private String itemCode;
}
