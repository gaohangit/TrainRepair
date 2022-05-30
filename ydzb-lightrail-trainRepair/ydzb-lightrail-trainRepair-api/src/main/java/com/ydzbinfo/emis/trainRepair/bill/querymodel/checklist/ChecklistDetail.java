package com.ydzbinfo.emis.trainRepair.bill.querymodel.checklist;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 记录单详细信息表
 * </p>
 *
 * @author 张天可
 * @since 2021-08-27
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("XZY_M_CHECKLISTDETAIL")
public class ChecklistDetail implements Serializable {

    /**
     * 记录单详细信息表主键
     */
    @TableId("S_ID")
    private String id;

    /**
     * 记录单主表主键
     */
    @TableField("S_CHECKLISTSUMMARYID")
    private String checklistSummaryId;

    /**
     * 数据行
     */
    @TableField("S_ROWID")
    private String rowId;

    /**
     * 数据列
     */
    @TableField("S_COLID")
    private String colId;

    /**
     * 单元格填写内容CODE
     */
    @TableField("S_CODE")
    private String code;

    /**
     * 单元格填写内容
     */
    @TableField("S_VALUE")
    private String value;

    /**
     * 属性编码
     */
    @TableField("S_ATTRIBUTECODE")
    private String attributeCode;

    /**
     * 备注信息
     */
    @TableField("S_REMARKS")
    private String remarks;

    /**
     * 单据最后一次回填时间
     */
    @TableField("D_LASTFILLTIME")
    private Date lastFillTime;

    /**
     * 单据最后一次回填人编码
     */
    @TableField("S_LASTFILLWORKCODE")
    private String lastFillWorkCode;

    /**
     * 单据最后一次回填人名称
     */
    @TableField("S_LASTFILLWORKNAME")
    private String lastFillWorkName;

    /**
     * 单据最后一次回填设备IP地址
     */
    @TableField("S_LASTIPADDRESS")
    private String lastIpAddress;

    /**
     * 单据最后一次回填设备名称
     */
    @TableField("S_LASTHOSTNAME")
    private String lastHostName;

    /**
     * 模板类型编码
     */
    @TableField("S_TEMPLATETYPE")
    private String templateType;

    /**
     * 模板ID
     */
    @TableField("S_TEMPLATEID")
    private String templateId;


}
