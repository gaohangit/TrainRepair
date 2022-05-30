package com.ydzbinfo.emis.trainRepair.bill.querymodel.attribute;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
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
 * @since 2021-08-07
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("XZY_B_TEMPLATEATTRIBUTEMODE")
public class TemplateAttributeMode implements Serializable {


    /**
     * 属性模式CODE
     */
    @TableId("S_ATTRIBUTEMODECODE")
    private String attributeModeCode;

    /**
     * 属性模式名称
     */
    @TableField("S_ATTRIBUTEMODENAME")
    private String attributeModeName;

    /**
     * 创建时间
     */
    @TableField("D_CREATETIME")
    private Date createTime;

    /**
     * 属性扩展说明
     */
    @TableField("S_NOTE")
    private String note;

    /**
     * 控件类型
     */
    @TableField("S_CONTROLTYPE")
    private String controlType;

}
