package com.ydzbinfo.emis.trainRepair.taskAllot.model;

public class QueryWorkTask {

    /**
     * 任务Code
     */
    private String workTaskCode;

    /**
     * 任务名称
     */
    private String workTaskName;

    public String getWorkTaskCode() {
        return workTaskCode;
    }

    public void setWorkTaskCode(String workTaskCode) {
        this.workTaskCode = workTaskCode;
    }

    public String getWorkTaskName() {
        return workTaskName;
    }

    public void setWorkTaskName(String workTaskName) {
        this.workTaskName = workTaskName;
    }

    @Override
    public String toString() {
        return "QueryWorkTask{" +
                "workTaskCode='" + workTaskCode + '\'' +
                ", workTaskName='" + workTaskName + '\'' +
                '}';
    }
}
