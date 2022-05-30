package com.ydzbinfo.emis.trainRepair.bill.model.basetemplate;

import com.ydzbinfo.emis.trainRepair.bill.querymodel.base.ITemplateForShow;
import com.ydzbinfo.emis.trainRepair.bill.querymodel.basetemplate.BaseTemplate;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 查询基础模板配置列表使用
 *
 * @author 张天可
 * @since 2022/3/1
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class BaseTemplateGroupForShow extends BaseTemplate implements ITemplateForShow {

    /**
     * 模板类型名称
     */
    private String templateTypeName;

    /**
     * 编组形式名称 1--前 2--后 3--全部
     */
    private String marshallingTypeName;

}
