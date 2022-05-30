package com.ydzbinfo.emis.trainRepair.taskAllot.model;


import com.ydzbinfo.emis.trainRepair.taskAllot.model.pojo.PacketData;
import lombok.Data;

import java.util.List;

@Data
public class PhoneAllotData {

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
     * 编组数量
     */
    private String marshal;

    /**
     * 派工状态Code  0-未派工  1-已派工  2-部分派工
     */
    private String allotStateCode;

    /**
     * 派工状态名称
     */
    private String allotStateName;

    /**
     * 作业包集合
     */
    private List<PacketData> packetData;
}
