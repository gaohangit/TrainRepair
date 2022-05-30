package com.ydzbinfo.emis.trainRepair.bill.querymodel.attribute;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

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
@TableName("XZY_B_TEMPLATEATTRIBUTETYPE")
public class TemplateAttributeType implements Serializable {


    /**
     * 属性类型编码
     */
    @TableId("S_ATTRIBUTETYPECODE")
    private String attributeTypeCode;

    /**
     * 属性类型名称
     */
    @TableField("S_ATTRIBUTETYPENAME")
    private String attributeTypeName;

    /**
     * 备注
     */
    @TableField("S_REMARK")
    private String remark;

    /**
     * 排序
     */
    @TableField("S_SORT")
    private String sort;


}
