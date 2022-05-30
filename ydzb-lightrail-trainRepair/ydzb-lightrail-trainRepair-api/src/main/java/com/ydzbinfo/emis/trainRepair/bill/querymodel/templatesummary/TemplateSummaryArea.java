package com.ydzbinfo.emis.trainRepair.bill.querymodel.templatesummary;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.ydzbinfo.emis.trainRepair.bill.querymodel.base.TemplateAreaBase;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 *
 * </p>
 *
 * @author 张天可
 * @since 2021-06-17
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("XZY_C_TEMPLATESUMMARYAREA")
public class TemplateSummaryArea extends TemplateAreaBase implements Serializable {

    /**
     * 同步标识，默认0
     */
    @TableField("C_SYNFLAG")
    private String synFlag;

    /**
     * 同步时间，默认空
     */
    @TableField("D_SYNDATE")
    private Date synDate;

}
