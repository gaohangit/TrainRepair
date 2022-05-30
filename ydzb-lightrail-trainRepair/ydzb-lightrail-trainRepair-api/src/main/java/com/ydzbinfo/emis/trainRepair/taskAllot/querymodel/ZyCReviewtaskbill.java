package com.ydzbinfo.emis.trainRepair.taskAllot.querymodel;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 复核记录单项目配置表
 *
 * @author 史艳涛
 * @since 2022-01-18
 */
@Data
@TableName("ZY_C_REVIEWTASKBILL")
public class ZyCReviewtaskbill {

    /**
     * 主键
     */
    @TableField("S_ID")
    private String id;

    /**
     * 超限项目ID
     */
    @TableField("S_CHECKITEMID")
    private String checkitemid;

    /**
     * 类型  0--轮对  1--受电弓
     */
    @TableField("S_TYPE")
    private String type;

    /**
     * 单据类型    Default = 0,InternalSpur = 1,DetectionQR = 2,Coaxial = 3,SkateHight = 4,SkateAbrasion = 5,KissPressure = 6
     */
    @TableField("S_BILLTYPE")
    private String billtype;

    /**
     * 创建人CODE
     */
    @TableField("S_CREATEUSERID")
    private String createuserid;

    /**
     * 创建人名称
     */
    @TableField("S_CREATEUSERNAME")
    private String createusername;

    /**
     * 创建时间
     */
    @TableField("D_CREATETIME")
    private Date createtime;

    /**
     * 删除时间
     */
    @TableField("D_DELETETIME")
    private Date deletetime;

    /**
     * 启用情况  1--启用  0--不启用
     */
    @TableField("C_USEFLAG")
    private String useflag;

    /**
     * 删除人CODE
     */
    @TableField("S_DELETEUSERID")
    private String deleteuserid;

    /**
     * 删除人名称
     */
    @TableField("S_DELETEUSERNAME")
    private String deleteusername;

    /**
     * 超限项目名称
     */
    @TableField("S_CHECKITEMNAME")
    private String checkitemname;
}
