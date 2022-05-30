package com.ydzbinfo.emis.trainRepair.taskAllot.model;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import java.io.Serializable;

/**
 * <p>
 * 
 * </p>
 *
 * @author 冯帅
 * @since 2021-06-05
 */
@TableName("XZY_M_TASKALLOTPERSON")
public class TaskAllotPerson implements Serializable {


    /**
     * 主键
     */
    @TableId("S_TASKALLOTPERSONID")
    private String taskAllotPersonId;

    /**
     * 作业人员ID
     */
    @TableField("S_WORKERID")
    private String workerId;

    /**
     * 作业人员姓名
     */
    @TableField("S_WORKERNAME")
    private String workerName;

    /**
     * 作业人员类型
     */
    @TableField("S_WORKERTYPE")
    private String workerType;

    /**
     * 派工部件辆序表主键
     */
    @TableField("S_PROCESSID")
    private String processId;

    /**
     * 派工唯一编码
     */
    @TableField("S_PUBLISHCODE")
    private String publishCode;

    public String getTaskAllotPersonId() {
        return taskAllotPersonId;
    }

    public void setTaskAllotPersonId(String taskAllotPersonId) {
        this.taskAllotPersonId = taskAllotPersonId;
    }

    public String getWorkerId() {
        return workerId;
    }

    public void setWorkerId(String workerId) {
        this.workerId = workerId;
    }

    public String getWorkerName() {
        return workerName;
    }

    public void setWorkerName(String workerName) {
        this.workerName = workerName;
    }

    public String getWorkerType() {
        return workerType;
    }

    public void setWorkerType(String workerType) {
        this.workerType = workerType;
    }

    public String getProcessId() {
        return processId;
    }

    public void setProcessId(String processId) {
        this.processId = processId;
    }

    public String getPublishCode() {
        return publishCode;
    }

    public void setPublishCode(String publishCode) {
        this.publishCode = publishCode;
    }

    @Override
    public String toString() {
        return "TaskAllotPerson{" +
            "taskAllotPersonId='" + taskAllotPersonId + '\'' +
            ", workerId='" + workerId + '\'' +
            ", workerName='" + workerName + '\'' +
            ", workerType='" + workerType + '\'' +
            ", processId='" + processId + '\'' +
            ", publishCode='" + publishCode + '\'' +
            '}';
    }
}
