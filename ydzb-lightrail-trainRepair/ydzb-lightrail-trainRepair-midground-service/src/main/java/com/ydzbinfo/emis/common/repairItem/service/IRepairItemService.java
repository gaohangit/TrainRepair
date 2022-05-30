package com.ydzbinfo.emis.common.repairItem.service;

import com.baomidou.mybatisplus.plugins.Page;
import com.ydzbinfo.emis.common.repairItem.utils.ItemKey;
import com.ydzbinfo.emis.trainRepair.taskAllot.model.RepairItemVo;

import java.util.List;
import java.util.Set;

/**
 * @description:
 * @date: 2021/11/5
 * @author: 冯帅
 */
public interface IRepairItemService {

    /**
     * 查询派工配置二级修项目
     */
    List<RepairItemVo> selectRepairItemList(String trainType, String itemName, String itemCode, Set<ItemKey> existItems, boolean include, Page<RepairItemVo> page);


    boolean useNewItem();
}
