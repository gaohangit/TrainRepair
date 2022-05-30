package com.ydzbinfo.emis.trainRepair.bill.querymodel.basetemplate;

import com.baomidou.mybatisplus.annotations.TableName;
import com.ydzbinfo.emis.trainRepair.bill.querymodel.base.TemplateControlBase;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * <p>
 * 基础单据模板内容卡控表
 * </p>
 *
 * @author 张天可
 * @since 2021-12-24
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("XZY_C_BASETEMPLATELINKCONTROL")
public class BaseTemplateLinkControl extends TemplateControlBase implements Serializable {

}
