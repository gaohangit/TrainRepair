package com.ydzbinfo.emis.trainRepair.bill.querymodel.base;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.ydzbinfo.emis.trainRepair.bill.general.base.BillAreaGeneral;
import lombok.Data;
import lombok.EqualsAndHashCode;

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
public class TemplateAreaBase extends BillAreaGeneral implements ITemplateAreaBase {

    /**
     * 主键
     */
    @TableId("S_ID")
    private String id;

    /**
     * 父表主键
     */
    @TableField("S_TEMPLATEID")
    private String templateId;

}
