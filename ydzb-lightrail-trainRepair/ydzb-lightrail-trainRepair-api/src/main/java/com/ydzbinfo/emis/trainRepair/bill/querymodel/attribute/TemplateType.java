package com.ydzbinfo.emis.trainRepair.bill.querymodel.attribute;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.ydzbinfo.emis.trainRepair.bill.querymodel.base.TemplateTypeBase;
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
@TableName("XZY_C_TEMPLATETYPE")
public class TemplateType extends TemplateTypeBase implements Serializable {

    /**
     * 父类型编码
     */
    @TableField("S_FATHERTYPECODE")
    private String fatherTypeCode;

    /**
     * 备注
     */
    @TableField("S_REMARK")
    private String remark;

    /**
     * 是否支持多张单据   1--支持  0--不支持
     */
    @TableField("C_MORECELLFLAG")
    private String moreCellFlag;

    /**
     * 类型  1--单据类型   2--单据子类型   3--单据名称
     */
    @TableField("S_TYPE")
    private String type;

    /**
     * 是否系统类型  1--是  0--否
     */
    @TableField("C_SYSTYPE")
    private String sysType;

    /**
     * 单据是否可编辑  1--是  0--否
     */
    @TableField("C_SYSTEMPLATE")
    private String sysTemplate;

    /**
     * 项目类型CODE
     */
    @TableField("S_ITEMTYPE")
    private String itemType;

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


}
