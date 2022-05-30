package com.ydzbinfo.emis.trainRepair.workprocess.model;

import com.ydzbinfo.emis.trainRepair.workprocess.querymodel.ProcessTimeRecord;

public class ProcessTimeRecordWithWorkerInfo extends ProcessTimeRecord {

    private String workerId;

    private String workerName;

    private String workerType;

    /**
     * 对应作业过程的主键
     */
    private String mainProcessId;

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

    public String getMainProcessId() {
        return mainProcessId;
    }

    public void setMainProcessId(String mainProcessId) {
        this.mainProcessId = mainProcessId;
    }
}
