package com.ydzbinfo.emis.trainRepair.bill.querymodel.basetemplate;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.ydzbinfo.emis.trainRepair.bill.querymodel.base.ITemplateBase;
import com.ydzbinfo.emis.trainRepair.bill.querymodel.base.key.BaseTemplateUniqueKey;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 基础单据模板主表
 * </p>
 *
 * @author 张天可
 * @since 2021-12-24
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("XZY_C_BASETEMPLATE")
public class BaseTemplate extends BaseTemplateUniqueKey implements ITemplateBase {

    /**
     * 模板ID
     */
    @TableId("S_TEMPLATEID")
    private String templateId;

    /**
     * 模板编号
     */
    @TableField("S_TEMPLATENO")
    private String templateNo;

    /**
     * 模板名称
     */
    @TableField("S_TEMPLATENAME")
    private String templateName;

    /**
     * 备注
     */
    @TableField("S_REMARK")
    private String remark;

    /**
     * 项目名称
     */
    @TableField("S_ITEMNAME")
    private String itemName;

    /**
     * 模板路径
     */
    @TableField("S_TEMPLATEPATH")
    private String templatePath;

    /**
     * 编组形式  1--前   2--后   3--全部
     */
    @TableField("I_MARSHALLINGTYPE")
    private Integer marshallingType;

    /**
     * 是否允许编辑行
     */
    @TableField("C_ALLOWEDITROW")
    private String allowEditRow;

    /**
     * 是否允许编辑列
     */
    @TableField("C_ALLOWEDITCOLUMN")
    private String allowEditColumn;

}
