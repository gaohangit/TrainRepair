package com.ydzbinfo.emis.trainRepair.taskAllot.model;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class QueryTaskAllot implements Serializable {
    /**
     * 运用所编号
     */
    private String unitCode;

    /**
     * 运用所名称
     */
    private String unitName;

    /**
     * 日计划ID
     */
    private String dayPlanId;

    /**
     * 班组编码
     */
    private String deptCode;

    /**
     * 班组名称
     */
    private String deptName;

    /**
     * 作业包编码
     */
    private String packetCode;

    /**
     * 作业包类型
     */
    private String packetType;

    /**
     * 作业包名称
     */
    private String packetName;

    /**
     * 项目类型
     */
    private String itemType;

    /**
     *作业项目编码
     */
    private String itemCode;

    /**
     *作业项目名称
     */
    private String itemName;

    /**
     *派工人员ID
     */
    private String workerId;


    /**
     *派工人员名称
     */
    private String workerName;

    /**
     *派工人员名称list集合
     */
    private List<String> workerNameList;

    /**
     * 作业类型
     */
    private String repairType;

    /**
     *辆序集合
     */
    private List<String> carNos;

    /**
     *辆序字符串集合
     */
    private String carNoStrs;

    /**
     * 车组名称
     */
    private String trainsetName;

    /**
     * 车组Id
     */
    private String trainsetId;

}
