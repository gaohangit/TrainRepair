package com.ydzbinfo.emis.trainRepair.bill.querymodel.checklist;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 表 XZY_M_IMPORT_RECORD
 * @author 史艳涛
 * @since 2021-11-18
 */
@Data
@TableName("XZY_M_IMPORT_RECORD")
public class ImportRecord {

    /**
     * 主键ID
     */
    @TableId("S_ID")
    private String id;

    /**
     * 日计划ID
     */
    @TableField("S_DAYPLANID")
    private String dayPlanId;

    /**
     * 记录单ID
     */
    @TableField("S_CHECKLISTSUMMARYID")
    private String checkListSummaryId;

    /**
     * 车组ID
     */
    @TableField("S_TRAINSETID")
    private String trainSetId;

    /**
     * 项目编码
     */
    @TableField("S_ITEMCODE")
    private String itemCode;

    /**
     * 项目名称
     */
    @TableField("S_ITEMNAME")
    private String itemName;

    /**
     * 模板类型编码
     */
    @TableField("S_TEMPLATETYPECODE")
    private String tempLateTypeCode;

    /**
     * 导入状态（1：导入成功；0：导入失败）
     */
    @TableField("C_IMPORTSTATE")
    private String importState;

    /**
     * 导入职工编码
     */
    @TableField("S_STAFFCODE")
    private String staffCode;

    /**
     * 导入职工名称
     */
    @TableField("S_STAFFNAME")
    private String staffName;

    /**
     * 导入时间
     */
    @TableField("D_IMPORTTIME")
    private Date importTime;

    /**
     * 备注信息
     */
    @TableField("S_REMARKS")
    private String remarks;
}
