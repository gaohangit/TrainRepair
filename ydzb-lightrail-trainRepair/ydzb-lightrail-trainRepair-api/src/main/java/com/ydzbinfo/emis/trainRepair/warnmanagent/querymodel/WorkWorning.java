package com.ydzbinfo.emis.trainRepair.warnmanagent.querymodel;

import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 *
 * </p>
 *
 * @author 冯帅
 * @since 2021-06-07
 */
@TableName("XZY_M_WORKWORNING")
@Data
public class WorkWorning implements Serializable {


    /**
     * 主键
     */
    @TableId("S_WORNINGID")
    private String worningId;

    /**
     * 辆序
     */
    @TableField("S_CARNO")
    private String carNo;

    /**
     * 预警内容
     */
    @TableField("S_WORNINGCONTENT")
    private String worningContent;

    public static final String S_EFFECTSTATE = "S_EFFECTSTATE";

    /**
     * 生效状态  1--生效  0--失效
     */
    @TableField("S_EFFECTSTATE")
    private String effectState;

    /**
     * 确认状态人员ID
     */
    @TableField("S_EFFECTSTUFFID")
    private String effectStuffId;

    /**
     * 确认状态人员名称
     */
    @TableField("S_EFFECTSTUFFNAME")
    private String effectStuffName;

    /**
     * 确认状态时间
     */
    @TableField("D_EFFECTTIME")
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date effectTime;

    /**
     * 备注
     */
    @TableField("S_REMARK")
    private String remark;

    /**
     * 运用所CODE
     */
    @TableField("S_UNITCODE")
    private String unitCode;

    /**
     * 运用所名称
     */
    @TableField("S_UNITNAME")
    private String unitName;

    /**
     * 项目CODE
     */
    @TableField("S_ITEMCODE")
    private String itemCode;

    /**
     * 项目NAME
     */
    @TableField("S_ITEMNAME")
    private String itemName;

    /**
     * 车型
     */
    @TableField("S_TRAINSETTYPE")
    private String trainsetType;


    public static final String D_CREATETIME = "D_CREATETIME";
    /**
     * 预警时间
     */
    @TableField(D_CREATETIME)
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    /**
     * 作业人员
     */
    @TableField("S_WORKERNAME")
    private String workerName;

    /**
     * 车组号
     */
    @TableField("S_TRAINSETNAME")
    private String trainsetName;

    /**
     * 作业内容
     */
    @TableField("S_WORKCONTENT")
    private String workContent;

    /**
     * 作业班组
     */
    @TableField("S_DEPTNAME")
    private String deptName;
}
