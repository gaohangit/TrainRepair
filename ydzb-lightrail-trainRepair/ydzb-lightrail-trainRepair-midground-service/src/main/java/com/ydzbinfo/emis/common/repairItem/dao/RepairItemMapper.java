package com.ydzbinfo.emis.common.repairItem.dao;

import com.baomidou.mybatisplus.plugins.Page;
import com.ydzbinfo.emis.common.repairItem.utils.ItemKey;
import com.ydzbinfo.emis.trainRepair.taskAllot.model.RepairItemVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

public interface RepairItemMapper {

    List<RepairItemVo> selectRepairItemList(
        @Param("trainType") String trainType,
        @Param("itemName") String itemName,
        @Param("itemCode") String itemCode,
        @Param("existItems") Set<ItemKey> existItems,
        @Param("include") boolean include,
        Page<RepairItemVo> page
    );
}
