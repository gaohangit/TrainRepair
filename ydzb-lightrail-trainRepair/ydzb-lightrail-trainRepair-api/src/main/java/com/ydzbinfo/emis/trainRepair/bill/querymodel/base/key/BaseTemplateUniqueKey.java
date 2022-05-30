package com.ydzbinfo.emis.trainRepair.bill.querymodel.base.key;

import com.baomidou.mybatisplus.annotations.TableField;
import lombok.Data;

/**
 * 联合起来和标记记录单基础模板的唯一性的属性集合
 *
 * @author 张天可
 * @date 2021/6/18
 */
@Data
public class BaseTemplateUniqueKey implements IBaseTemplateUniqueKey {

    /**
     * 模板类型CODE
     */
    @TableField("S_TEMPLATETYPECODE")
    private String templateTypeCode;

    /**
     * 车型
     */
    @TableField("S_TRAINSETTYPE")
    private String trainsetType;

    /**
     * 批次
     */
    @TableField("S_BATCH")
    private String batch;

    /**
     * 编组数量
     */
    @TableField("I_MARSHALCOUNT")
    private Integer marshalCount;

    /**
     * 项目编码
     */
    @TableField("S_ITEMCODE")
    private String itemCode;

}
