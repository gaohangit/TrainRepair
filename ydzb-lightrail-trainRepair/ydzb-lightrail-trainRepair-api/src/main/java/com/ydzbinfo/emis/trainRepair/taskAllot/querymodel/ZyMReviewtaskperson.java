package com.ydzbinfo.emis.trainRepair.taskAllot.querymodel;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 复核记录单人员分配表
 *
 * @author 史艳涛
 * @since 2022-01-18
 */
@Data
@TableName("ZY_M_REVIEWTASKPERSON")
public class ZyMReviewtaskperson {

    /**
     * 复核任务记录单人员分配表ID(主键ID)
     */
    @TableField("S_REVIEWTASKPERSONID")
    private String reviewtaskpersonid;

    /**
     * 复核任务记录单总表ID
     */
    @TableField("S_REVIEWTASKSUMMARYID")
    private String reviewtasksummaryid;

    /**
     * 复核任务单内复核项目ID
     */
    @TableField("S_REVIEWTASKITEMID")
    private String reviewtaskitemid;

    /**
     * 日计划ID
     */
    @TableField("S_DAYPLANID")
    private String dayplanid;

    /**
     * 检修工长职工编码
     */
    @TableField("S_WORKERSTAFFCODE")
    private String workerstaffcode;

    /**
     * 检修工长职工姓名
     */
    @TableField("S_WORKERSTAFFNAME")
    private String workerstaffname;

    /**
     * 被分配人员编码
     */
    @TableField("S_PERSONCODE")
    private String personcode;

    /**
     * 被分配人员名称
     */
    @TableField("S_PERSONNAME")
    private String personname;

    /**
     * 创建数据时间
     */
    @TableField("D_CREATETIME")
    private Date createtime;

    /**
     * 唯一发行码
     */
    @TableField("S_PUBLISHCODE")
    private String publishcode;

    /**
     * 备注信息
     */
    @TableField("S_REMARKS")
    private String remarks;
}
