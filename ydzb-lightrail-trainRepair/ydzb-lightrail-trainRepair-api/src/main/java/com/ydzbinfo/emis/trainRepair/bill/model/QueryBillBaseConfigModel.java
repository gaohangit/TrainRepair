package com.ydzbinfo.emis.trainRepair.bill.model;

import com.ydzbinfo.emis.trainRepair.bill.querymodel.base.key.BaseTemplateUniqueKey;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 记录基础模板配置查询类
 **/
@EqualsAndHashCode(callSuper = true)
@Data
public class QueryBillBaseConfigModel extends BaseTemplateUniqueKey {
    // 项目名称
    private String itemName;
    // 当前页码
    private int page;
    // 每页页大小
    private int limit;
    // // 排序字段
    // private String orderInfo;
    // // 排序方式
    // private String orderAscOrDesc;
}
