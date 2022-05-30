package com.ydzbinfo.emis.trainRepair.bill.querymodel.base;

/**
 * 用于记录单配置展示的基本配置
 */
public interface ITemplateForShow extends ITemplateBase {
    /**
     * 获取模板类型名称
     *
     * @return 模板类型名称
     */
    String getTemplateTypeName();
}
