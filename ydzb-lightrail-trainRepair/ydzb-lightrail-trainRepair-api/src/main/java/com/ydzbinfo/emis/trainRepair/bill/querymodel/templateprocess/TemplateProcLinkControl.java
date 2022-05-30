package com.ydzbinfo.emis.trainRepair.bill.querymodel.templateprocess;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import java.io.Serializable;

import com.ydzbinfo.emis.trainRepair.bill.querymodel.base.TemplateControlBase;
import lombok.Data;
import lombok.EqualsAndHashCode;

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
@TableName("XZY_C_TEMPLATEPROCLINKCONTROL")
public class TemplateProcLinkControl extends TemplateControlBase implements Serializable {

}
