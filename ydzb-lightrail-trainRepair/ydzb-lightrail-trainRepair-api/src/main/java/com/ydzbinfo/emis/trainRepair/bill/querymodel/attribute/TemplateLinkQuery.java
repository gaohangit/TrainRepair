package com.ydzbinfo.emis.trainRepair.bill.querymodel.attribute;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * @description:
 * @date: 2022/3/24
 * @author: 冯帅
 */
@EqualsAndHashCode(callSuper = false)
@TableName("XZY_C_TEMPLATELINKQUERY")
@Data
public class TemplateLinkQuery {


    /**
     * 主键
     */
    @TableField("S_ID")
    private String id;

    /**
     * 单据类型编码
     */
    @TableField("S_TEMPLATETYPECODE")
    private String templateTypeCode;

    /**
     * 查询条件编码
     */
    @TableField("S_QUERYCODE")
    private String queryCode;

    /**
     * 创建时间
     */
    @TableField("D_CREATETIME")
    private Date createTime;

    /**
     * 创建人
     */
    @TableField("S_CREATEUSER")
    private String createUser;

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
     * 排序
     */
    @TableField("S_SORT")
    private String sort;

}
