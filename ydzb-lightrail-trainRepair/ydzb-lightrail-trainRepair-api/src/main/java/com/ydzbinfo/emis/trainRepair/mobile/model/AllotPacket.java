package com.ydzbinfo.emis.trainRepair.mobile.model;

import lombok.Data;

import java.util.List;

@Data
public class AllotPacket {

    /**
     * 包id
     */
    private String packetCode;

    /**
     * 包名称
     */
    private String packetName;


    /**
     * 项目集合
     */
    private List<AllotItem> allotItemList;

}
