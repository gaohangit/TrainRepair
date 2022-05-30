package com.ydzbinfo.emis.trainRepair.mobile.model;

import com.ydzbinfo.emis.trainRepair.repairmanagement.querymodel.XzyCWorkcritertion;
import com.ydzbinfo.emis.trainRepair.workprocess.querymodel.ProcessPic;
import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Description:     二级修返回部件
 * Author: wuyuechang
 * Create Date Time: 2021/5/14 10:12
 * Update Date Time: 2021/5/14 10:12
 *
 * @see
 */
@Data
public class TwoWorkCarPart {

    /**
     * 日计划ID
     */
    private String dayPlanID;
    /**
     * 车组名称
     */
    private String trainsetName;
    /**
     * 车组ID
     */
    private String trainsetId;
    /**
     * 运用所CODE
     */
    private String unitCode;
    /**
     * 运用所名称
     */
    private String unitName;
    /**
     * 项目CODE
     */
    private String ItemCode;
    /**
     * 项目NAME
     */
    private String ItemName;
    /**
     * 检修类型（项目类型）
     */
    private String repairType;
    /**
     * 项目作业类型   1--部件  0--辆序
     */
    private String arrageType;
    /**
     * 数据来源
     */
    private String dataSource;
    /**
     * 部件类型名称
     */
    private String partType;
    /**
     * 部件位置
     */
    private String partPosition;
    /**
     * 备注
     */
    private String remark;
    /**
     * 检修方式  0--人工检查   1--机器人     2--机检
     */
    private String repairMode;
    /**
     * 部件名称
     */
    private String partName;
    /**
     * 辆序
     */
    private String carNo;
    /**
     * 车型
     */
    private String trainsetType;

    /**
     * 图片集合
     */
    private List<ProcessPic> processPics = new ArrayList<>();

    /**
     * 作业标准
     */
    private XzyCWorkcritertion xzyCWorkcritertion = new XzyCWorkcritertion();

    /**
     * 项目开始时间
     */
    private Date startTime;

    /**
     * 项目完成时间
     */
    private Date finishTime;

    /**
     * 项目时间唯一标识
     */
    private String itemPublished;

    /**
     * 是否已作业 1已作业 2未作业
     * 空-未作业  1-开始 2-暂停 3-继续 4-结束
     */
    private String isWork;
}
