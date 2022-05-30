package com.ydzbinfo.emis.trainRepair.workprocess.model;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author: 冯帅
 * @Date: 2021/7/2 09:45
 * @Description: 作业过程包实体—中台
 */
@Data
public class ProcessPacketEntity implements Serializable {

    /**
     * 主键
     */
    private String processPacketId;

    /**
     * 班组编码
     */
    private String deptCode;

    /**
     * 班组名称
     */
    private String deptName;

    /**
     * 车组ID
     */
    private String trainsetId;

    /**
     * 车组名称
     */
    private String trainsetName;

    /**
     * 记录人编码
     */
    private String recordCode;

    /**
     * 记录人名称
     */
    private String recordName;

    /**
     * 记录时间
     */
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date recordTime;

    /**
     * 日计划ID
     */
    private String dayPlanId;

    /**
     * 修程
     */
    private String mainCyc;

    /**
     * 运用所编号
     */
    private String unitCode;

    /**
     * 运用所名称
     */
    private String unitName;

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
     * 作业辆序实体集合
     */
    List<ProcessCarPartEntity> processCarPartEntityList;
}
