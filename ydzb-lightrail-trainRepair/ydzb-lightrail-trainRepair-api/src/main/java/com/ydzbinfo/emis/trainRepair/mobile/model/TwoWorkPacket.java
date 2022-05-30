package com.ydzbinfo.emis.trainRepair.mobile.model;

import lombok.Data;

import java.util.List;

/**
 * Description:     二级修返回的作业包
 * Author: wuyuechang
 * Create Date Time: 2021/5/14 10:10
 * Update Date Time: 2021/5/14 10:10
 *
 * @see
 */
@Data
public class TwoWorkPacket {

    /**
     * 作业包CODE
     */
    private String packetCode;
    /**
     * 作业包NAME
     */
    private String packetName;
    /**
     * 作业包类型
     */
    private String packetType;

    /**
     * 二级修返回项目列表
     */
    private List<TwoWorkCarPart> twoWorkCarParts;
}
