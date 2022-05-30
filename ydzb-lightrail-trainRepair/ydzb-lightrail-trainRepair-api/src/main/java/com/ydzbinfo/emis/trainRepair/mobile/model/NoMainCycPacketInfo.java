package com.ydzbinfo.emis.trainRepair.mobile.model;

import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * Description:     无修程包信息
 * Author: wuyuechang
 * Create Date Time: 2021/5/13 15:39
 * Update Date Time: 2021/5/13 15:39
 *
 * @see
 */
@Data
public class NoMainCycPacketInfo {

    /**
     * 包名称
     */
    private String packetName;

    /**
     * 包Code
     */
    private String packetCode;

    /**
     * 包类型
     */
    private String packetType;

    /**
     * 项目时间唯一标识
     */
    private String itemPublicShed;

    /**
     * 确认辆序
     */
    private String carNo;

    /**
     * 确认时间
     */
    private Date time;

    /**
     * 无修程确认人员
     */
    private List<NoMainCycPersonInfo> noMainCycPersonInfos;
}
