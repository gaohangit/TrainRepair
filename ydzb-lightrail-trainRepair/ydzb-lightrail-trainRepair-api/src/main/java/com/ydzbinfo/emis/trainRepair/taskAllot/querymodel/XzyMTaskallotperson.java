package com.ydzbinfo.emis.trainRepair.taskAllot.querymodel;

import com.baomidou.mybatisplus.activerecord.Model;

import java.io.Serializable;
import java.util.List;

/**
 * <p>
 * 
 * </p>
 *
 * @author duanzefan
 * @since 2020-09-11
 */
public class XzyMTaskallotperson extends Model<XzyMTaskallotperson> {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    private String taskAllotPersonId;

    /**
     * 作业人员ID
     */
    private String workerID;

    /**
     * 作业人员姓名
     */
    private String workerName;

    /**
     *作业人员类型
     */
    private String workerType;

    /**
     * 派工部件辆序表主键
     */
    private String processId;

    /**
     * 派工人员岗位集合
     */
    private List<TaskAllotPersonPostEntity> taskAllotPersonPostEntityList;

    public String getTaskAllotPersonId() {
        return taskAllotPersonId;
    }

    public void setTaskAllotPersonId(String taskAllotPersonId) {
        this.taskAllotPersonId = taskAllotPersonId;
    }

    public String getWorkerID() {
        return workerID;
    }

    public void setWorkerID(String workerID) {
        this.workerID = workerID;
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

    public List<TaskAllotPersonPostEntity> getTaskAllotPersonPostEntityList() {
        return taskAllotPersonPostEntityList;
    }

    public void setTaskAllotPersonPostEntityList(List<TaskAllotPersonPostEntity> taskAllotPersonPostEntityList) {
        this.taskAllotPersonPostEntityList = taskAllotPersonPostEntityList;
    }

    @Override
    public String toString() {
        return "XzyMTaskallotperson{" +
                "taskAllotPersonId='" + taskAllotPersonId + '\'' +
                ", workerID='" + workerID + '\'' +
                ", workerName='" + workerName + '\'' +
                ", workerType='" + workerType + '\'' +
                ", processId='" + processId + '\'' +
                ", taskAllotPersonPostEntityList=" + taskAllotPersonPostEntityList +
                '}';
    }

    @Override
    protected Serializable pkVal() {
        return this.taskAllotPersonId;
    }
}
