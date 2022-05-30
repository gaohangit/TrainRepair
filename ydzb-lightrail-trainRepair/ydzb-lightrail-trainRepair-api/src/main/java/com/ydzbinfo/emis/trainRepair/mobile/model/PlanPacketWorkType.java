package com.ydzbinfo.emis.trainRepair.mobile.model;

import lombok.Data;

@Data
public class PlanPacketWorkType {
    /**
     * 包id
     */
    private String packetCode;

    /**
     * 包名称
     */
    private String packetName;

    /**
     * 包下是否有新项目，如果有新项目，需要把新项目传后台。0：false，1：true  (是否新作业包)
     */
    private String newPacket;

    /**
     * 作业类型编码,（应该展示到哪一层）(派工模式类型)(0，展示到包，1，展示到项目）
     */
    private String workModeTypeCode;

    /**
     * 包下是否有人
     */
    private String isExistPerson;

    /**
     * 修程
     */
    private String workTypeCode;
}
