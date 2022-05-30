package com.ydzbinfo.emis.trainRepair.bill.querymodel.templateprocess;

import com.baomidou.mybatisplus.annotations.TableName;
import com.ydzbinfo.emis.trainRepair.bill.querymodel.base.ConfigTemplateBase;
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
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("XZY_C_TEMPLATEPROCESSSUMMARY")
public class TemplateProcess extends ConfigTemplateBase implements Serializable, ITemplateProcess {

}
