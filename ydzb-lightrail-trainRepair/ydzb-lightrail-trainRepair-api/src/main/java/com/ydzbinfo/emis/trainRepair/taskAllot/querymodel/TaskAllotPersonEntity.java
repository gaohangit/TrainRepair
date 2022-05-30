package com.ydzbinfo.emis.trainRepair.taskAllot.querymodel;

import lombok.Data;

import java.util.List;

/**
 * @author: fengshuai
 * @Date: 2021/3/31
 * @Description: 派工人员实体（中台服务）
 */
@Data
public class TaskAllotPersonEntity {

    /**
     * 主键
     */
    private String sId;

    /**
     * 作业人员
     */
    private String workerId;

    /**
     * 作业人员类型
     */
    private String workerType;

    /**
     * 作业人员姓名
     */
    private String workerName;

    /**
     * 辆序ID
     */
    private String processId;

    /**
     * 人员分组id
     */
    private String branchCode;

    /**
     * 派工人员岗位集合
     */
    private List<TaskAllotPersonPostEntity> taskAllotPersonPostEntityList;
}
