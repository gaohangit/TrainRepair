package com.ydzbinfo.emis.trainRepair.bill.querymodel.templatesummary;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import java.io.Serializable;
import java.util.Date;

import com.ydzbinfo.emis.trainRepair.bill.querymodel.base.TemplateContentBase;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 单据模板配置内容表
 * </p>
 *
 * @author 张天可
 * @since 2021-08-27
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("XZY_C_TEMPLATESUMMARYCONTENT")
public class TemplateSummaryContent extends TemplateContentBase implements Serializable {

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
