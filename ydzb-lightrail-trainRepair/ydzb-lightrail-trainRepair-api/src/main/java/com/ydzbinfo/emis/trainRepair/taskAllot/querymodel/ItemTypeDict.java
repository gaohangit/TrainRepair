package com.ydzbinfo.emis.trainRepair.taskAllot.querymodel;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * <p>
 * 
 * </p>
 *
 * @author 冯帅
 * @since 2021-06-18
 */
@TableName("TI_B_ITEMTYPE_DICT")
@Data
public class ItemTypeDict implements Serializable {


    /**
     * 检修项目类型编号
     */
    @TableField("S_ITEMTYPECODE")
    private String itemTypeCode;

    /**
     * 检修项目类型名称
     */
    @TableField("S_ITEMTYPENAME")
    private String itemTypeName;

    /**
     * 备注
     */
    @TableField("S_REMARK")
    private String remark;

    /**
     * 标识
     */
    @TableField("C_FLAG")
    private String flag;
}
