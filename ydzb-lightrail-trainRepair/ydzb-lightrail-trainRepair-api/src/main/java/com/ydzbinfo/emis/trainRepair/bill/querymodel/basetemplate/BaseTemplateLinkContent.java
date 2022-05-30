package com.ydzbinfo.emis.trainRepair.bill.querymodel.basetemplate;

import com.baomidou.mybatisplus.annotations.TableName;
import com.ydzbinfo.emis.trainRepair.bill.querymodel.base.TemplateLinkContentBase;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * <p>
 * 基础单据模板关联内容表
 * </p>
 *
 * @author 张天可
 * @since 2021-12-24
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("XZY_C_BASETEMPLATELINKCONTENT")
public class BaseTemplateLinkContent extends TemplateLinkContentBase implements Serializable {

}
