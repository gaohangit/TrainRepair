package com.ydzbinfo.emis.trainRepair.common.querymodel;

/**
 * 计划项目基本信息实体接口
 *
 * @author 张天可
 * @since 2021/11/2
 */
public interface ITaskItemEntityBase {
    String getTaskItemId();

    void setTaskItemId(String taskItemId);

    String getPartId();

    void setPartId(String partId);

    String getItemCode();

    void setItemCode(String itemCode);

    String getItemName();

    void setItemName(String itemName);

    String getItemType();

    void setItemType(String itemType);

    String getCarNo();

    void setCarNo(String carNo);
}
