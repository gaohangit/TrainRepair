package com.ydzbinfo.emis.trainRepair.bill.querymodel.base.key;

import com.baomidou.mybatisplus.annotations.TableField;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 联合起来和标记记录单模板的唯一性的属性集合
 *
 * @author 张天可
 * @date 2021/6/18
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class TemplateUniqueKey extends BaseTemplateUniqueKey implements ITemplateUniqueKey {

    /**
     * 模板使用所
     */
    @TableField("S_UNITCODE")
    private String unitCode;

    /**
     * 模板使用段
     */
    @TableField("S_DEPOTCODE")
    private String depotCode;
}
