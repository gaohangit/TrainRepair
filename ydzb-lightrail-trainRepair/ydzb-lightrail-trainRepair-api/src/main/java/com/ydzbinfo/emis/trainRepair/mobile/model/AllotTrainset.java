package com.ydzbinfo.emis.trainRepair.mobile.model;

import lombok.Data;

import java.util.List;

@Data
public class AllotTrainset {

    /**
     * 车组id
     */
    private String trainsetId;

    /**
     * 车组名称
     */
    private String trainsetName;


    /**
     * 包集合
     */
    private List<AllotPacket> allotPacketList;

}
