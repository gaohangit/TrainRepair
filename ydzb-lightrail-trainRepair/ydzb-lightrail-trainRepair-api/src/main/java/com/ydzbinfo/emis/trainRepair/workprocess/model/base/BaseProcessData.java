package com.ydzbinfo.emis.trainRepair.workprocess.model.base;

import lombok.Data;

import java.io.Serializable;

/**
 * @author: 冯帅
 * @Date: 2021/7/6 10:28
 * @Description: 查询实体公用部分
 */
@Data
public class BaseProcessData implements Serializable {

    /**
     * 日计划ID
     */
    private String dayPlanId;

    /**
     * 运用所编码
     */
    private String unitCode;

    /**
     * 运用所名称
     */
    private String unitName;

    /**
     * 作业人员ID
     */
    private String workerId;

    /**
     * 作业人员名称
     */
    protected String workerName;

    /**
     * 车组ID
     */
    private String trainsetId;

    /**
     * 车组名称
     */
    protected String trainsetName;

    /**
     * 车型
     */
    private String trainsetType;

    /**
     * 作业过程部门编码
     */
    private String deptCode;

    /**
     * 作业过程部门名称
     */
    private String deptName;

    /**
     * 作业过程包编码
     */
    private String packetCode;

    /**
     * 作业过程包名称
     */
    private String packetName;
}
