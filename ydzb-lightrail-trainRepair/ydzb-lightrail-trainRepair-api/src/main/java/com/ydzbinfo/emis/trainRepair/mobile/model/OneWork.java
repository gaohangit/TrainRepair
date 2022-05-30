package com.ydzbinfo.emis.trainRepair.mobile.model;

import com.ydzbinfo.emis.trainRepair.repairmanagement.querymodel.XzyCWorkcritertion;
import com.ydzbinfo.emis.trainRepair.workprocess.querymodel.ProcessLocation;
import com.ydzbinfo.emis.trainRepair.workprocess.querymodel.ProcessPic;
import com.ydzbinfo.emis.trainRepair.workprocess.querymodel.RfidCardSummary;
import com.ydzbinfo.emis.trainRepair.trainsetPostion.querymodel.TrainsetPositionEntity;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Description:     一级修作业过程返回数据
 * Author: wuyuechang
 * Create Date Time: 2021/5/7 17:12
 * Update Date Time: 2021/5/7 17:12
 *
 * @see
 */

@Data
public class OneWork {

    /**
     * 车组id
     */
    private String trainsetId;

    /**
     * 车组名称
     */
    private String trainsetName;

    /**
     * 股道位列
     */
    private String planRepairTrack;

    /**
     * 日计划id
     */
    private String dayPlanId;

    /**
     * 作业标准备注
     */
    private String remark;

    /**
     * 1—可以使用，0—不可以使用
     */
    private String isUsePhoto;

    /**
     * 车组辆序集合
     */
    private List<String> trainsetCarNoList;

    /**
     * 作业项目
     */
    private List<OneWorkCarPart> itemList;

    /**
     * 动车位置实体
     */
    private TrainsetPositionEntity trainsetPositionEntity;

    /**
     * 时间集合
     */
    private List<RfidCardSummary> rfidCardSummaries;

    /**
     * 作业标准
     */
    private XzyCWorkcritertion xzyCWorkcritertion = new XzyCWorkcritertion();

    /**
     * 是否可以开始 1是 2否
     */
    private String isBegin = "2";

    /**
     * 图片集合
     */
    private List<ProcessPic> processPics = new ArrayList<>();

    /**
     * 定位信息
     */
    private List<ProcessLocation> processLocations = new ArrayList<>();
}
