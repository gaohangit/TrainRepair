package com.ydzbinfo.emis.trainRepair.workProcessMonitor.model;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author: 冯帅
 * @Date: 2021/5/26 15:00
 * @Description:
 */
@Data
public class FaultItem implements Serializable {

    /**
     * 故障id
     */
    private String  faultId;

    /**
     * 故障所在辆序
     */
    private String faultCarNo;

    /**
     * 车次
     */
    private String trainNo;

    /**
     * 车组id
     */
    private String trainsetId;

    /**
     * 车组名称
     */
    private String trainsetName;

    /**
     * 故障名称
     */
    private String faultName;

    /**
     * 故障现象描述
     */
    private String faultDescription;

    /**
     * 处理人（最后一次）
     */
    private String dealFaultMan;

    /**
     * 故障处理结果编码（最后一次）  0-待处理 1-已处理 2-未处理
     */
    private String dealWithCode;

    /**
     * 故障处理结果（最后一次） 待处理  已处理  未处理
     */
    private String dealWithDesc;

    /**
     * 故障等级
     */
    private String faultGrade;

    /**
     * 发现人（最早一次）
     */
    private String findFaultMan;

    /**
     * 故障发现日期（最早一次）
     */
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date findFaultTime;

    /**
     * 处理日期（最后一次）
     */
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date dealDate;
}
