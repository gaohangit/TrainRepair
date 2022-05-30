package com.ydzbinfo.emis.trainRepair.workprocess.model;


import com.alibaba.fastjson.annotation.JSONField;
import com.ydzbinfo.emis.trainRepair.workprocess.querymodel.ProcessPic;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author:
 * @Date: 2021/5/12
 * @Description: 作业过程实体
 * @Modify 冯帅
 */
@Data
public class ProcessData implements Serializable {
    /**
     * 人员表主键
     */
    private String processPersonId;

    /**
     * 作业人员ID
     */
    private String workerId;

    /**
     * 作业人员名称
     */
    private String workerName;

    /**
     * 作业人员类型
     */
    private String workerType;

    /**
     * 作业人员集合
     */
    List<ProcessWorker> processWorkerList;

    /**
     * 辆序部件表主键
     */
    private String processId;

    /**
     * 日计划
     */
    private String dayPlanId;

    /**
     * 车组ID
     */
    private String trainsetId;

    /**
     * 车组名称
     */
    private String trainsetName;

    /**
     * 项目编码
     */
    private String itemCode;

    /**
     * 项目名称
     */
    private String itemName;

    /**
     * 作业标准主键
     */
    private String critertionId;

    /**
     * 检修类型
     */
    private String repairType;

    /**
     * 项目作业类型   1--部件  0--辆序
     */
    private String arrageType;

    /**
     * 车型
     */
    private String trainsetType;

    /**
     * 批次
     */
    private String trainsetSubType;

    /**
     * 辆序
     */
    private String carNo;

    /**
     * 辆序集合
     */
    private List<String> carNos;

    /**
     * 作业过程部门表主键
     */
    private String processDeptId;

    /**
     * 作业过程部门编码
     */
    private String deptCode;

    /**
     * 作业过程部门名称
     */
    private String deptName;

    /**
     * 作业过程包记录主键
     */
    private String processPacketId;

    /**
     * 作业包编码
     */
    private String packetCode;

    /**
     * 作业包名称
     */
    private String packetName;

    /**
     * 开始时间
     */
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date startTime;

    /**
     * 结束时间
     */
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date endTime;

    /**
     * 实际图片数量
     */
    private Integer actualPicCount;

    /**
     * 图片集合
     */
    List<ProcessPic> processPicList;

    /**
     * 当前页码
     */
    private Integer pageNum;

    /**
     * 每页显示条数
     */
    private Integer pageSize;

    /**
     * 辆序数量
     */
    private String carCount;

    /**
     * 所编码
     */
    private String unitCode;

    /**
     * 所名称
     */
    private String unitName;

    /**
     * 手持机或PC，手持机1，PC2
     */
    private String dataSource;
}
