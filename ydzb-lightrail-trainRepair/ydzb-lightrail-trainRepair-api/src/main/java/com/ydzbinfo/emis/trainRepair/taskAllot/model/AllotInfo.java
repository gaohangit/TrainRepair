package com.ydzbinfo.emis.trainRepair.taskAllot.model;

import java.util.List;

public class AllotInfo {

    /**
     * 车组ID
     */
    private String trainsetId;

    /**
     * 车组名称
     */
    private String trainsetName;

    /**
     * 车型CODE
     */
    private String trainsetTypeCode;

    /**
     * 车型名称
     */
    private String trainsetTypeName;

    /**
     * 作业包集合
     */
    private List<PacketData> packetListData;

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

    public List<PacketData> getPacketListData() {
        return packetListData;
    }

    public void setPacketListData(List<PacketData> packetListData) {
        this.packetListData = packetListData;
    }
}
