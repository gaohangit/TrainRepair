package com.ydzbinfo.emis.trainRepair.taskAllot.querymodel;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 复核记录单详细信息表
 *
 * @author 史艳涛
 * @since 2022-01-18
 */
@Data
@TableName("ZY_M_REVIEWTASKDETAIL")
public class ZyMReviewtaskdetail {

    /**
     * 复核任务记录单详细信息表ID(主键ID)
     */
    @TableField("S_REVIEWTASKDETAILID")
   private String reviewtaskdetailid;

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
     * 超限数据ID（c_type = 0 : em_di_wst_analyse s_id; c_type = 1 : em_di_sj_analyse s_id）
     */
    @TableField("S_ANALYSEID")
    private String analyseid;

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
     * 车号
     */
    @TableField("S_CARNAME")
    private String carname;

    /**
     * 超限项目名称
     */
    @TableField("S_ITEMNAME")
    private String itemname;

    /**
     * 轮对位置/弓位
     */
    @TableField("S_POSITION")
    private String position;

    /**
     * 检测值/超限值
     */
    @TableField("S_CHECKVALUE")
    private String checkvalue;

    /**
     * 复核值
     */
    @TableField("S_VALUE")
    private String value;

    /**
     * 在记录单内所属行（下标从0开始）
     */
    @TableField("I_ROWINDEX")
    private int rowindex;

    /**
     * 在记录单内所属列（下标从0开始）
     */
    @TableField("I_COLINDEX")
    private int colindex;

    /**
     * 复核人员工编码
     */
    @TableField("S_REVIEWERCODE")
    private String reviewercode;

    /**
     * 复核人员工姓名
     */
    @TableField("S_REVIEWERNAME")
    private String reviewername;

    /**
     * 复核日期
     */
    @TableField("D_REVIEWDATE")
    private Date reviewdate;

    /**
     * 误差分析
     */
    @TableField("S_ERRORANALYSIS")
    private String erroranalysis;

    /**
     * 处理情况
     */
    @TableField("S_HANDLEINFO")
    private String handleinfo;

    /**
     * 处理人员编码
     */
    @TableField("S_HANDLESTAFFCODE")
    private String handlestaffcode;

    /**
     * 处理人员姓名
     */
    @TableField("S_HANDLESTAFFNAME")
    private String handlestaffname;

    /**
     * 处理时间
     */
    @TableField("D_HANDLEDATE")
    private Date handledate;

    /**
     * 复核项目创建时间
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

    /**
     * 轮位
     */
    @TableField("S_WHEELPOSITION")
    private String wheelposition;

}
