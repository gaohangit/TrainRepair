package com.ydzbinfo.emis.trainRepair.bill.querymodel.basetemplate;

import com.baomidou.mybatisplus.annotations.TableName;
import com.ydzbinfo.emis.trainRepair.bill.querymodel.base.TemplateContentBase;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * <p>
 * 单据模板模板内容表
 * </p>
 *
 * @author 张天可
 * @since 2021-12-24
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("XZY_C_BASETEMPLATECONTENT")
public class BaseTemplateContent extends TemplateContentBase implements Serializable {

}
