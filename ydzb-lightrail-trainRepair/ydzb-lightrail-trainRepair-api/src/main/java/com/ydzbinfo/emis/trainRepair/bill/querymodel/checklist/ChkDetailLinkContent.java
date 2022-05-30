package com.ydzbinfo.emis.trainRepair.bill.querymodel.checklist;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import java.io.Serializable;

import com.ydzbinfo.emis.trainRepair.bill.general.base.IBillLinkCellInfoGeneral;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 单据回填内容关联内容表
 * </p>
 *
 * @author 张天可
 * @since 2021-08-27
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("XZY_M_CHKDETAILLINKCONTENT")
public class ChkDetailLinkContent implements Serializable {

    /**
     * 主键
     */
    @TableId("S_ID")
    private String id;

    /**
     * XZY_C_TEMPLATEPROCESSCONTENT表主键
     */
    @TableField("S_CONTENTID")
    private String contentId;

    /**
     * 关联内容对应的XZY_C_TEMPLATEPROCESSCONTENT表主键
     */
    @TableField("S_LINKCONTENTID")
    private String linkContentId;

    @TableField("S_CHECKLISTSUMMARYID")
    private String checklistSummaryId;
}
