package com.ydzbinfo.emis.trainRepair.workprocess.model;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author: 冯帅
 * @Date: 2021/5/13 15:24
 * @Description: 作业过程一体化实体
 */
@Data
public class IntegrationProcessData implements Serializable {

    /**
     * 运用所编码
     */
    private String unitCode;

    /**
     * 运用所名称
     */
    private String unitName;

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
     * 辆序集合
     */
    private List<String> carNos;

    /**
     * 项目编码
     */
    private String itemCode;

    /**
     * 项目名称
     */
    private String itemName;

    /**
     * 作业班组编码
     */
    private String deptCode;

    /**
     * 作业班组名称
     */
    private String deptName;

    /**
     * 作业人员集合
     */
    List<ProcessWorker> processWorkerList;

    /**
     * 作业辆序数量
     */
    private String workCarCount;

    /**
     * 作业结束时间
     */
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date workEndTime;

    /**
     * 确认班组编码
     */
    private String confirmDeptCode;

    /**
     * 确认班组名称
     */
    private String confirmDeptName;

    /**
     * 确认人员集合
     */
    List<ProcessWorker> processConfirmList;

    /**
     * 确认辆序数量
     */
    private String confirmCarCount;

    /**
     * 确认结束时间
     */
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date confirmEndTime;

    /**
     * 手持机或PC，手持机1，PC2
     */
    private String dataSource;
}
