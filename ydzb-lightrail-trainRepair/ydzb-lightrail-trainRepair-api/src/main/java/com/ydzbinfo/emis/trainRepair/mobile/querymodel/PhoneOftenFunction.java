package com.ydzbinfo.emis.trainRepair.mobile.querymodel;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 
 * </p>
 *
 * @author 韩旭
 * @since 2021-04-07
 */
@Data
@TableName("XZY_C_PHONEOFTENFUNCTION")
public class PhoneOftenFunction implements Serializable {


    /**
     * 主键
     */
    @TableId("S_ID")
    private String id;

    /**
     * 职工ID
     */
    @TableField("S_STAFFID")
    private String staffId;

    /**
     * 模块编号
     */
    @TableField("S_PHONEMODULEID")
    private String phoneModuleId;

    /**
     * 类型  1--首页常用功能    2--常用跳转功能
     */
    @TableField("S_TYPE")
    private String type;

    /**
     * 创建时间
     */
    @TableField("D_CREATETIME")
    private Date createTime;

    /**
     * 是否可用  1--可用 0--不可用
     */
    @TableField("C_FLAG")
    private String flag;

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
     * 删除人姓名
     */
    @TableField("S_DELUSERNAME")
    private String delUserName;
}
