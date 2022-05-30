package com.ydzbinfo.emis.trainRepair.mobile.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Description: 修程下含有的包
 * Author: wuyuechang
 * Create Date Time: 2021/4/28 16:01
 * Update Date Time: 2021/4/28 16:01
 *
 * @see
 */
@Data
public class MainCycPacket {

    /**
     * 作业包类型编码
     */
    private String packetTypeCode;

    /**
     * 修程编码
     */
    private String taskRepairCode;

    /**
     * 修程名称
     */
    private String taskRepairName;

    /**
     * 作业包名称集合
     */
    private List<String> packetNames = new ArrayList<>();
}
