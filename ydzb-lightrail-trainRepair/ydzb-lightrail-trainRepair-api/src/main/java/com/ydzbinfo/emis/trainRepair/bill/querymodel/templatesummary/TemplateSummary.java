package com.ydzbinfo.emis.trainRepair.bill.querymodel.templatesummary;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.ydzbinfo.emis.trainRepair.bill.querymodel.base.ConfigTemplateBase;
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
@TableName("XZY_C_TEMPLATESUMMARY")
public class TemplateSummary extends ConfigTemplateBase implements Serializable, ITemplateSummary {

    /**
     * 平台
     */
    @TableField("S_PLATFORM")
    private String platform;

    /**
     * 版本
     */
    @TableField("S_VERSION")
    private String version;

    /**
     * 是否生效
     */
    @TableField("C_VALIDFLAG")
    private String validFlag;

    /**
     * 生效日期
     */
    @TableField("D_VALIDDATE")
    private Date validDate;

    /**
     * 失效日期
     */
    @TableField("D_UNVALIDDATE")
    private Date unvalidDate;

    /**
     * 是否逻辑删除   1--是；0--否
     */
    @TableField("C_DELFLAG")
    private String delFlag;

    /**
     * 发布状态   1--发布；0--未发布
     */
    @TableField("C_PUBLISH")
    private String publish;

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
