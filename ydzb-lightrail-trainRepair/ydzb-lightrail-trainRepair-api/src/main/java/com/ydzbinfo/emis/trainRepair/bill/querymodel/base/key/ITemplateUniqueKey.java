package com.ydzbinfo.emis.trainRepair.bill.querymodel.base.key;

/**
 * 联合起来和标记记录单配置的唯一性的属性集合
 *
 * @author 张天可
 * @date 2021/6/18
 */
public interface ITemplateUniqueKey extends IBaseTemplateUniqueKey {

    String getUnitCode();

    String getDepotCode();
}
