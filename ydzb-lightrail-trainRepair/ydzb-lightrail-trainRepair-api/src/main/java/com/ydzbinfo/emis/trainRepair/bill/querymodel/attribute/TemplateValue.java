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
@TableName("XZY_B_TEMPLATEVALUE")
public class TemplateValue implements Serializable {


    /**
     * 主键
     */
    @TableId("S_ID")
    private String id;

    /**
     * 属性编码
     */
    @TableField("S_ATTRIBUTECODE")
    private String attributeCode;

    /**
     * 属性范围值
     */
    @TableField("S_ATTRIBUTERANGEVALUE")
    private String attributeRangeValue;

    /**
     * 属性值顺序
     */
    @TableField("I_SORTID")
    private String sortId;

    /**
     * 同步标识，默认0
     */
    @TableField("C_SYNFLAG")
    private String synFlag;

    /**
     * 同步时间，默认空
     */
    @TableField("D_SYNDATE")
    private Date synDate;

    /**
     * 创建时间
     */
    @TableField("D_CREATETIME")
    private Date createTime;

    /**
     * 创建人
     */
    @TableField("S_CREATEUSERCODE")
    private String createUserCode;

    /**
     * 创建人名称
     */
    @TableField("S_CREATEUSERNAME")
    private String createUserName;

    /**
     * 删除时间
     */
    @TableField("D_DELTIME")
    private Date delTime;

    /**
     * 删除人
     */
    @TableField("S_DELUSERCODE")
    private String delUserCode;

    /**
     * 删除人名称
     */
    @TableField("S_DELUSERNAME")
    private String delUserName;

    /**
     * 是否可用  1--可用 0--不可用
     */
    @TableField("C_FLAG")
    private String flag;

    /**
     * 是否默认 1--是 0--不是
     */
    @TableField("C_ISDEFAULT")
    private String isDefault;


}
