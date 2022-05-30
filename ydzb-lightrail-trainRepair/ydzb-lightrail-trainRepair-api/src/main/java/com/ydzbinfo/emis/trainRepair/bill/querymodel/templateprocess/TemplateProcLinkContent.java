package com.ydzbinfo.emis.trainRepair.bill.querymodel.templateprocess;

import com.baomidou.mybatisplus.annotations.TableName;
import com.ydzbinfo.emis.trainRepair.bill.querymodel.base.TemplateLinkContentBase;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * <p>
 * 单据配置过程内容关联内容表
 * </p>
 *
 * @author 张天可
 * @since 2021-08-27
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("XZY_C_TEMPLATEPROCLINKCONTENT")
public class TemplateProcLinkContent extends TemplateLinkContentBase implements Serializable {

}