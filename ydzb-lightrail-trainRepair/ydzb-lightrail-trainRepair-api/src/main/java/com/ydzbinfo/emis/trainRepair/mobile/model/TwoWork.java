package com.ydzbinfo.emis.trainRepair.mobile.model;

import com.ydzbinfo.emis.trainRepair.workprocess.querymodel.RfidCardSummary;
import com.ydzbinfo.emis.trainRepair.trainsetPostion.querymodel.TrainsetPositionEntity;
import lombok.Data;

import java.util.List;

/**
 * Description:     二级修作业过程返回数据
 * Author: wuyuechang
 * Create Date Time: 2021/5/7 17:12
 * Update Date Time: 2021/5/7 17:12
 *
 * @see
 */

@Data
public class TwoWork {

    /**
     * 车组id
     */
    private String trainsetId;

    /**
     * 车组名称
     */
    private String trainsetName;

    /**
     * 车型
     */
    private String trainsetType;

    /**
     * 日计划id
     */
    private String dayPlanId;

    /**
     * 股道位列
     */
    private String planRepairTrack;

    /**
     * 动车位置实体
     */
    private TrainsetPositionEntity trainsetPositionEntity;

    /**
     * 派工包
     */
    private List<TwoWorkPacket> packetList;

    /**
     * 时间集合
     */
    private List<RfidCardSummary> rfidCardSummaries;
}
