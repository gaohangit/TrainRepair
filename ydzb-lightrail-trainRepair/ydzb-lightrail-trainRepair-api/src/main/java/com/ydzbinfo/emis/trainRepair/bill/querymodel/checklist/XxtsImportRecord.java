package com.ydzbinfo.emis.trainRepair.bill.querymodel.checklist;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 表 ZY_M_XXTSIMPORT_RECORD
 * @author 史艳涛
 * @since 2021-11-17
 */
@Data
@TableName("ZY_M_XXTSIMPORT_RECORD")
public class XxtsImportRecord {

    /**
     * 主键ID
     */
    @TableId("S_ID")
    public String id;

    /**
     * 日计划ID
     */
    @TableField("S_DAYPLANID")
    public String dayPalnId;

    /**
     * 记录单ID
     */
    @TableField("S_CHECKLISTSUMMARYID")
    public String checkListSummaryId;

    /**
     * 车组名称
     */
    @TableField("S_TRAINSETNAME")
    public String trainSetName;

    /**
     * 检修项目名称
     */
    @TableField("S_SPREPAIRITEMNAME")
    public String spRepairItemName;

    /**
     * 导入时间
     */
    @TableField("D_IMPORTTIME")
    public Date importTime;

    /**
     * 单据类型（5：镟修探伤；6：空心轴探伤；7：轮辋轮辐探伤）
     */
    @TableField("S_DOCTYPE")
    public String docType;

    /**
     * 导入结果（1：成功；0：失败）
     */
    @TableField("S_STATE")
    public String state;

    /**
     * 备注信息
     */
    @TableField("S_REMARK")
    public String remarks;

    /**
     * 是否已传输（1：已传输；0：未传输）
     */
    @TableField("C_STATUS")
    public String status;
}
