package com.ydzbinfo.emis.trainRepair.taskAllot.querymodel;

import com.alibaba.fastjson.annotation.JSONField;
import com.ydzbinfo.emis.trainRepair.common.querymodel.ITaskPacketEntityBase;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @author: fengshuai
 * @Date: 2021/3/31
 * @Description: 派工作业包实体（中台服务）
 */
@Data
public class TaskAllotPacketEntity implements ITaskPacketEntityBase {


    /**
     * 派工部门表主键
     */
    private String taskAllotDeptId;

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
     * 派工包表主键
     */
    private String taskAllotPacketId;

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
     * 作业包任务ID
     */
    private String taskId;

    /**
     * 项目集合
     */
    private List<TaskAllotItemEntity> taskAllotItemEntityList;

    @Override
    public String getPacketTypeCode() {
        return this.getPacketType();
    }

    @Override
    public void setPacketTypeCode(String packetTypeCode) {
        this.setPacketType(packetTypeCode);
    }

    @Override
    public String getRepairDeptCode() {
        return this.getDeptCode();
    }

    @Override
    public void setRepairDeptCode(String repairDeptCode) {
        this.setDeptCode(repairDeptCode);
    }

    @Override
    public String getRepairDeptName() {
        return this.getDeptName();
    }

    @Override
    public void setRepairDeptName(String repairDeptName) {
        this.setDeptName(repairDeptName);
    }

    @Override
    public String getDepotCode() {
        return this.getUnitCode();
    }

    @Override
    public void setDepotCode(String depotCode) {
        this.setUnitCode(depotCode);
    }
}
