package com.ydzbinfo.emis.trainRepair.taskAllot.querymodel;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Data;

import java.util.Date;

/**
 * PHM任务源表
 */
@Data
@TableName("PZ_M_TASKAPPLY")
public class PzMTaskApply {

    /**
     * 主键/源编码
     */
    @TableField("S_APPLYID")
    private String applyid;

    /**
     * 数据平台(PHM/TEDS)
     */
    @TableField("S_PLATFORM")
    private String platform;

    /**
     * 类型
     */
    @TableField("S_TYPE")
    private String type;

    /**
     * 名称
     */
    @TableField("S_NAME")
    private String name;

    /**
     * 详细信息
     */
    @TableField("S_DETAILINFO")
    private String detailinfo;

    /**
     * 车组ID
     */
    @TableField("S_TRAINSETID")
    private String trainsetid;

    /**
     * 车次
     */
    @TableField("S_TRAINNO")
    private String trainno;

    /**
     * 所状态
     */
    @TableField("C_DEPTTYPE")
    private String depttype;

    /**
     * 检修所编码
     */
    @TableField("S_REPAIRDEPTCODE")
    private String repairdeptcode;

    /**
     * 担当所编码
     */
    @TableField("S_INCHARGEDEPTCODE")
    private String inchargedeptcode;

    /**
     * 产生时间
     */
    @TableField("D_CREATDATE")
    private Date creatdate;

    /**
     * 状态（0：未安排，1：已安排）
     */
    @TableField("C_STATUS")
    private String status;

    /**
     * 所属单位编码
     */
    @TableField("S_DEPTCODE")
    private String deptcode;

    /**
     * phm编码
     */
    @TableField("S_PHMCODE")
    private String phmcode;

    /**
     * 传输状态（0：未传输，1：已传输）
     */
    @TableField("S_TRANSPORTSTATUS")
    private String transportstatus;

    /**
     * 传输时间
     */
    @TableField("S_TRANSPORTDATE")
    private Date transportdate;

    /**
     * 功能分类编码
     */
    @TableField("S_FUNCTIONTYPECODE")
    private String functiontypecode;

    /**
     * 功能分类名称
     */
    @TableField("S_FUNCTIONTYPENAME")
    private String functiontypename;

    /**
     * 等级
     */
    @TableField("S_LEVEL")
    private String level;
}
