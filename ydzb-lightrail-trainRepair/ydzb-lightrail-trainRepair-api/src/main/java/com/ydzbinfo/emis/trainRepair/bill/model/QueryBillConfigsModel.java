package com.ydzbinfo.emis.trainRepair.bill.model;

import com.ydzbinfo.emis.trainRepair.bill.querymodel.base.key.TemplateUniqueKey;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 记录单据配置首页查询
 **/
@EqualsAndHashCode(callSuper = true)
@Data
public class QueryBillConfigsModel extends TemplateUniqueKey {
    // 发布状态 0未发布 1已经发布
    private String state;
    // 项目名称
    private String itemName;
    // 当前页码
    private int page;
    // 每页页大小
    private int limit;
    // 排序字段
    private String orderInfo;
    // 排序方式
    private String orderAscOrDesc;
    // 是否显示系统单据
    private Boolean showSystemTemplate = true;
    // 是否显示自定义单据
    private Boolean showCustomTemplate = true;
}
