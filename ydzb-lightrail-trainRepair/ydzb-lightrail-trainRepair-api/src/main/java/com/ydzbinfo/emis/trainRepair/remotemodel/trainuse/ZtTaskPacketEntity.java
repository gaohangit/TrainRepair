package com.ydzbinfo.emis.trainRepair.remotemodel.trainuse;

import com.ydzbinfo.emis.trainRepair.common.querymodel.ITaskPacketEntityBase;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 任务包实体
 * </p>
 *
 * @author shenml
 * @since 2020-06-15
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class ZtTaskPacketEntity implements ITaskPacketEntityBase {

    /**
     * 任务包ID
     */
    private String taskId;

    /**
     * 修程编码
     */
    private String taskRepairCode;

    /**
     * 车组ID
     */
    private String trainsetId;

    /**
     * 车组名称
     */
    private String trainsetName;

    /**
     * 包编码
     */
    private String packetCode;

    /**
     * 包名称
     */
    private String packetName;

    /**
     * 作业包类型编码
     */
    private String packetTypeCode;

    /**
     * 配件ID
     */
    private String partId;

    /**
     * 生成时间
     */
    private Date createDate;

    /**
     * 计划开始时间
     */
    private Date beginDate;

    /**
     * 计划结束时间
     */
    private Date endDate;


    /**
     * 班次
     */
    private String dayplanId;

    /**
     * 检修班组编码
     */
    private String repairDeptCode;

    /**
     * 检修班组名称
     */
    private String repairDeptName;

    /**
     * 检修股道编码
     */
    private String repairTrackCode;

    /**
     * 检修股道名称
     */
    private String repairTrackName;

    /**
     * 状态
     */
    private String status;

    /**
     * 备注
     */
    private String remark;

    /**
     * 所属单位编码
     */
    private String depotCode;

    /**
     * lstTaskItemInfo(任务集合)
     */
    private List<ZtTaskItemEntity> lstTaskItemInfo = new ArrayList<>();

    /**
     * 作业人员
     */
    private String workers;

    @Override
    public String getDayPlanId() {
        return this.dayplanId;
    }

    @Override
    public void setDayPlanId(String dayPlanId) {
        this.dayplanId = dayPlanId;
    }

    public String getDayplanId() {
        return this.dayplanId;
    }

    public void setDayplanId(String dayPlanId) {
        this.dayplanId = dayPlanId;
    }
}
