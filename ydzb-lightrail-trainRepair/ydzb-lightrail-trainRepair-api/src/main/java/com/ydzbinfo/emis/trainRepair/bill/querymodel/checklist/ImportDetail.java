package com.ydzbinfo.emis.trainRepair.bill.querymodel.checklist;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 表 XZY_M_IMPORT_DETAIL
 * @author 史艳涛
 * @since 2021-11-18
 */
@Data
@TableName("XZY_M_IMPORT_DETAIL")
public class ImportDetail {

    /**
     * 主键ID
     */
    @TableId("S_ID")
    private String id;

    /**
     * 导入记录主表ID
     */
    @TableField("S_IMPORT_RECORDID")
    private String importRecordId;

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
     * 辆序
     */
    @TableField("S_CARNO")
    private String carNo;

    /**
     * 轴位
     */
    @TableField("S_AXLEPOSITION")
    private String axlePosition;

    /**
     * 设备数据ID
     */
    @TableField("S_DEVICEDATAID")
    private String deviceDataId;

    /**
     * 导入时间
     */
    @TableField("D_IMPORTIME")
    private Date ImportTime;

    /**
     * 备注信息
     */
    @TableField("S_REMARKS")
    private String remarks;

}
