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
@TableName("XZY_B_TEMPLATEATTRIBUTE")
public class TemplateAttribute implements Serializable {


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
     * 属性名称
     */
    @TableField("S_ATTRIBUTENAME")
    private String attributeName;

    /**
     * 属性类型
     */
    @TableField("S_ATTRIBUTETYPECODE")
    private String attributeTypeCode;

    /**
     * 属性模式 参照XZY_B_TEMPLATEATTRIBUTEMODE
     */
    @TableField("S_ATTRIBUTEMODECODE")
    private String attributeModeCode;

    /**
     * 属性说明
     */
    @TableField("S_ATTRIBUTENOTE")
    private String attributeNote;

    /**
     * 备注
     */
    @TableField("S_REMARK")
    private String remark;

    /**
     * 是否关联属性  1--是  0 --否
     */
    @TableField("C_LINKATTR")
    private String linkAttr;

    /**
     * 排序
     */
    @TableField("S_SORT")
    private String sort;

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
     * 是否可用    1--可用 0--不可用
     */
    @TableField("C_FLAG")
    private String flag;

    /**
     * 是否必须填写    1--是  0 --否
     */
    @TableField("C_BACKFILLVERIFY")
    private String backFillVerify;

    /**
     * 是否只读     1--是  0 --否
     */
    @TableField("C_READONLY")
    private String readOnly;

    /**
     * 是否系统属性，系统类型用户不能编辑  1--是  0--否
     */
    @TableField("C_SYSTYPE")
    private String sysType;


}
