package com.ydzbinfo.emis.trainRepair.bill.querymodel.templatesummary;

import com.baomidou.mybatisplus.annotations.TableName;
import com.ydzbinfo.emis.trainRepair.bill.querymodel.base.TemplateControlBase;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * <p>
 * 单据模板配置内容卡控表
 * </p>
 *
 * @author 张天可
 * @since 2021-08-27
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("XZY_C_TEMPLATESUMMLINKCONTROL")
public class TemplateSummLinkControl extends TemplateControlBase implements Serializable {
    
}
