package com.ydzbinfo.emis.trainRepair.taskAllot.querymodel;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 复核记录单主表
 *
 * @author 史艳涛
 * @since 2022-01-18
 */
@Data
@TableName("ZY_M_REVIEWTASKSUMMARY")
public class ZyMReviewtasksummary {

    /**
     * 复核任务记录单总表ID(主键ID)
     */
    @TableField("S_REVIEWTASKSUMMARYID")
    private String reviewtasksummaryid;

    /**
     * 日计划ID
     */
    @TableField("S_DAYPLANID")
    private String dayplanid;

    /**
     * 车组号
     */
    @TableField("S_TRAINSETNAME")
    private String trainsetname;

    /**
     * 复核任务记录单名称
     */
    @TableField("S_REVIEWTASKNAME")
    private String reviewtaskname;

    /**
     * 复核任务类型（0：轮对；1：受电弓）
     */
    @TableField("C_TYPE")
    private String type;

    /**
     * 复核任务子类型（1、内距 2、踏面磨耗、轮缘厚度、QR值、轮径、深度、长度、不圆度、探伤检测 3、同轴、同转向架、同车厢轮径差 4、滑板高度差 5、滑板磨耗到限 6、接触压力）
     */
    @TableField("C_SUBTYPE")
    private String subtype;

    /**
     * 所属检修班组编码
     */
    @TableField("S_DEPTCODE")
    private String deptcode;

    /**
     * 所属检修班组名称
     */
    @TableField("S_DEPTNAME")
    private String deptname;

    /**
     * 记录单编号
     */
    @TableField("S_CHECKLISTNO")
    private String checklistno;

    /**
     * 回填状态（0：未回填；1：部分回填；2：完全回填）
     */
    @TableField("C_BACKFILLSTATE")
    private String backfillstate;

    /**
     * 创建记录单员工编号
     */
    @TableField("S_CREATESTAFFCODE")
    private String createstaffcode;

    /**
     * 创建记录单员工姓名
     */
    @TableField("S_CREATESTAFFNAME")
    private String createstaffname;

    /**
     * 签字工长编号
     */
    @TableField("S_FOREMANSGINCODE")
    private String foremansgincode;

    /**
     * 签字工长姓名
     */
    @TableField("S_FOREMANSGINNAME")
    private String foremansginname;

    /**
     * 签字质检员编号
     */
    @TableField("S_QUALITYINSPECTORSCODE")
    private String qualityinspectorscode;

    /**
     * 签字质检员姓名
     */
    @TableField("S_QUALITYINSPECTORSNAME")
    private String qualityinspectorsname;

    /**
     * 签字调度员编号
     */
    @TableField("S_DISPATCHERCODE")
    private String dispatchercode;

    /**
     * 签字调度员姓名
     */
    @TableField("S_DISPATCHERNAME")
    private String dispatchername;

    /**
     * 统一发行码
     */
    @TableField("S_PUBLISHCODE")
    private String publishcode;

    /**
     * 记录单创建时间
     */
    @TableField("D_CREATETIME")
    private Date createtime;

    /**
     * 记录单更新时间（添加新项目时间）
     */
    @TableField("D_UPDATETIME")
    private Date updatetime;

    /**
     * 备注信息
     */
    @TableField("S_REMARKS")
    private String remarks;
}
