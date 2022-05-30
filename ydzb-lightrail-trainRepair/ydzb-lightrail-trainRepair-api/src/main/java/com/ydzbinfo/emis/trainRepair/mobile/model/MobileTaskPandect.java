package com.ydzbinfo.emis.trainRepair.mobile.model;

import com.ydzbinfo.emis.trainRepair.trainsetPostion.querymodel.TrackPowerEntity;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * Description: 手持机任务总单返回对象
 * Author: wuyuechang
 * Create Date Time: 2021/4/26 14:39
 * Update Date Time: 2021/4/26 14:39
 *
 * @see
 */
@Data
public class MobileTaskPandect{

    /**
     * 车组id
     */
    private String trainsetId;

    /**
     * 车组名称
     */
    private String trainsetName;

    /**
     * 班组编码
     */
    private String repairDeptCode;

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
     * 列位供断电信息
     */
    private List<TrackPowerEntity> trackPowerEntityList;


    /**
     * 修程下含有的包
     */
    private List<MainCycPacket> mainCycPacket;

    /**
     * 作业包含人员集合
     */
    private List<PacketPersonnel> packetList;
}
