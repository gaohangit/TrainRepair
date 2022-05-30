package com.ydzbinfo.emis.trainRepair.bill.querymodel.base;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.ydzbinfo.emis.trainRepair.bill.general.base.BillCellControlGeneral;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * <p>
 *
 * </p>
 *
 * @author 张天可
 * @since 2021-06-17
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class TemplateControlBase extends BillCellControlGeneral implements Serializable, ITemplateControlBase {

    /**
     * 主键
     */
    @TableId("S_ID")
    private String id;

    /**
     * 对应content表主键
     */
    @TableField("S_CONTENTID")
    private String contentId;

}
