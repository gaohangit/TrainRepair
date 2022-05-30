package com.ydzbinfo.emis.trainRepair.mobile.model;

import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * Description: 任务总单返回对象
 * Author: wuyuechang
 * Create Date Time: 2021/4/26 14:40
 * Update Date Time: 2021/4/26 14:40
 *
 * @see
 */
@Data
public class TrackPowerInfoEntity {

    /**
     * 班组名称
     */
    private String repairDeptName;

    /**
     * 计划检修开始时间
     */
    private Date planRepairBeginTime;

    /**
     * 计划检修结束时间
     */
    private Date planRepairEndTime;

    /**
     * 计划检修股道列位名称
     */
    private String planRepairTrack;

    /**
     * 检修股道编码
     */
    private String repairTrackCode;

    /**
     * 检修股道名称
     */
    private String repairTrackName;

    /**
     * 状态 1有电 2无电
     */
    private String state;

    /**
     * 修程下含有的包
     */
    private List<MainCycPacket> mainCycPacket;

    /**
     * 作业包含人员集合
     */
    private List<PacketPersonnel> packetList;
}
