package com.ydzbinfo.emis.trainRepair.taskAllot.model;

import java.util.List;

public class QueryTrainType {

    /**
     * 车型Code
     */
    private String trainsetTypeCode;

    /**
     * 车型名称
     */
    private String trainsetTypeName;

    /**
     * 车组集合
     */
    private List<QueryTrainset> queryTrainsetList;

    public String getTrainsetTypeCode() {
        return trainsetTypeCode;
    }

    public void setTrainsetTypeCode(String trainsetTypeCode) {
        this.trainsetTypeCode = trainsetTypeCode;
    }

    public String getTrainsetTypeName() {
        return trainsetTypeName;
    }

    public void setTrainsetTypeName(String trainsetTypeName) {
        this.trainsetTypeName = trainsetTypeName;
    }

    public List<QueryTrainset> getQueryTrainsetList() {
        return queryTrainsetList;
    }

    public void setQueryTrainsetList(List<QueryTrainset> queryTrainsetList) {
        this.queryTrainsetList = queryTrainsetList;
    }

    @Override
    public String toString() {
        return "QueryTrainType{" +
                "trainsetTypeCode='" + trainsetTypeCode + '\'' +
                ", trainsetTypeName='" + trainsetTypeName + '\'' +
                ", queryTrainsetList=" + queryTrainsetList +
                '}';
    }
}
