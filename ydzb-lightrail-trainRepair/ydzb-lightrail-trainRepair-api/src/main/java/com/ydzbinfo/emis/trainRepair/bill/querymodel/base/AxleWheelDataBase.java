package com.ydzbinfo.emis.trainRepair.bill.querymodel.base;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import lombok.Data;

import java.util.Date;

@Data
public class AxleWheelDataBase {
    /**
     * 主键ID
     */
    @TableId("S_ID")
    public String id;
    /**
     * 镟修记录单ID
     */
    @TableField("S_CHECKLISTSUMMARYID")
    public String checkListSummaryId;
    /**
     * 日计划ID
     */
    @TableField("S_DAYPLANID")
    public String dayPlanId;
    /**
     * 车组ID
     */
    @TableField("S_TRAINSETID")
    public String trainSetId;
    /**
     * 车组名称
     */
    @TableField("S_TRAINSETNAME")
    public String trainSetName;
    /**
     * 辆序
     */
    @TableField("S_CARNO")
    public String carNo;
    /**
     * 轴位
     */
    @TableField("S_AXLELOCATION")
    public String axleLocation;
    /**
     * 端位（1轴：1位侧=1端，2位侧=2端；2轴：1位侧=3端，2位侧=4端；3轴：1位侧=5端，2位侧=6端；4轴：1位侧=7端，2位侧=8端）
     */
    @TableField("S_PARTLOCATION")
    public String partLocation;
    /**
     * 位侧
     */
    @TableField("S_SIDE")
    public String side;
    /**
     * 轴走行公里
     */
    @TableField("S_AXLERUMILES")
    public String axleRumiles;
    /**
     * 镟轮员工编码
     */
    @TableField("S_EDDYSTAFFCODE")
    public String eddyStatffCode;
    /**
     * 镟轮员工名称
     */
    @TableField("S_EDDYSTAFFNAME")
    public String eddyStatffName;
    /**
     * 质检员员工编码
     */
    @TableField("S_INSPECTORSTAFFCODE")
    public String inspectorStaffCode;
    /**
     * 质检员员工名称
     */
    @TableField("S_INSPECTORSTAFFNAME")
    public String inspectorStaffName;
    /**
     * 镟前轮径值
     */
    @TableField("S_XQ")
    public String xq;
    /**
     * 镟后轮径值
     */
    @TableField("S_XH")
    public String xh;
    /**
     * 创建时间
     */
    @TableField("D_CREATTIME")
    public Date createTime;
}
