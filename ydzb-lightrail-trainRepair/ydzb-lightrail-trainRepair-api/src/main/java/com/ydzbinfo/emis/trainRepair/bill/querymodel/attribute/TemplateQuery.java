package com.ydzbinfo.emis.trainRepair.bill.querymodel.attribute;

import com.baomidou.mybatisplus.annotations.TableField;
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
@TableName("XZY_B_TEMPLATEQUERY")
public class TemplateQuery implements Serializable {

    /**
     * 查询条件CODE
     */
    @TableField("S_QUERYCODE")
    private String queryCode;

    /**
     * 查询条件名称
     */
    @TableField("S_QUERYNAME")
    private String queryName;

    /**
     * 备注
     */
    @TableField("S_REMARK")
    private String remark;


}
