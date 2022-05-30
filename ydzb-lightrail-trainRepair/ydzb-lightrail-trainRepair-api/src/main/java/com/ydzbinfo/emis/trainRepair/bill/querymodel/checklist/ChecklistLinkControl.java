package com.ydzbinfo.emis.trainRepair.bill.querymodel.checklist;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import java.io.Serializable;

import com.ydzbinfo.emis.trainRepair.bill.general.base.BillCellControlGeneral;
import com.ydzbinfo.emis.trainRepair.bill.general.base.IBillCellControlGeneral;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 单据明细内容卡控表
 * </p>
 *
 * @author 张天可
 * @since 2021-08-27
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("XZY_M_CHECKLISTLINKCONTROL")
public class ChecklistLinkControl extends BillCellControlGeneral implements Serializable {

    /**
     * 主键
     */
    @TableId("S_ID")
    private String id;

    /**
     * XZY_M_CHECKLISTDETAIL表主键
     */
    @TableField("S_CONTENTID")
    private String contentId;

    /**
     * 回填条件默认内容
     */
    @TableField("S_CONTENT")
    private String content;

    /**
     * 回填条件属性编码
     */
    @TableField("S_ATTRIBUTECODE")
    private String attributeCode;

    @TableField("S_CHECKLISTSUMMARYID")
    private String checklistSummaryId;
}
