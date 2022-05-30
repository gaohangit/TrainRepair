package com.ydzbinfo.emis.trainRepair.taskAllot.model;

import java.util.List;

public class QueryTrainset {

    /**
     * 车组ID
     */
    private String trainsetId;

    /**
     * 车组名称
     */
    private String trainsetName;

    /**
     * 查询任务集合
     */
    private List<QueryWorkTask> queryWorkTaskList;

    public String getTrainsetId() {
        return trainsetId;
    }

    public void setTrainsetId(String trainsetId) {
        this.trainsetId = trainsetId;
    }

    public String getTrainsetName() {
        return trainsetName;
    }

    public void setTrainsetName(String trainsetName) {
        this.trainsetName = trainsetName;
    }

    public List<QueryWorkTask> getQueryWorkTaskList() {
        return queryWorkTaskList;
    }

    public void setQueryWorkTaskList(List<QueryWorkTask> queryWorkTaskList) {
        this.queryWorkTaskList = queryWorkTaskList;
    }

    @Override
    public String toString() {
        return "QueryTrainset{" +
                "trainsetId='" + trainsetId + '\'' +
                ", trainsetName='" + trainsetName + '\'' +
                ", queryWorkTaskList=" + queryWorkTaskList +
                '}';
    }
}
