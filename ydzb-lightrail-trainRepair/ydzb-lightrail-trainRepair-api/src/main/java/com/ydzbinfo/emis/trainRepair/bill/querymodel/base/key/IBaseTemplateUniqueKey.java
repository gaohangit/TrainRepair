package com.ydzbinfo.emis.trainRepair.bill.querymodel.base.key;

/**
 * 联合起来和标记记录单基础模板的唯一性的属性集合
 *
 * @author 张天可
 * @date 2021/6/18
 */
public interface IBaseTemplateUniqueKey {

    String getTemplateTypeCode();

    String getTrainsetType();

    String getBatch();

    Integer getMarshalCount();

    String getItemCode();
}
