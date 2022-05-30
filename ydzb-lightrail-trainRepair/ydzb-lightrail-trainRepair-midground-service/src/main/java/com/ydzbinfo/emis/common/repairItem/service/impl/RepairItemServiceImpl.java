package com.ydzbinfo.emis.common.repairItem.service.impl;

import com.baomidou.mybatisplus.plugins.Page;
import com.ydzbinfo.emis.common.general.service.ICommonService;
import com.ydzbinfo.emis.common.repairItem.dao.RepairItemMapper;
import com.ydzbinfo.emis.common.repairItem.service.IRepairItemService;
import com.ydzbinfo.emis.common.repairItem.utils.ItemKey;
import com.ydzbinfo.emis.trainRepair.common.util.ConfigUtil;
import com.ydzbinfo.emis.trainRepair.taskAllot.model.RepairItemVo;
import com.ydzbinfo.emis.utils.MybatisOgnlUtils;
import com.ydzbinfo.emis.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Set;

/**
 * @description:
 * @date: 2021/11/5
 * @author: 冯帅
 */
@Service
public class RepairItemServiceImpl implements IRepairItemService {

    @Resource
    RepairItemMapper repairItemMapper;

    @Autowired
    ICommonService commonService;

    @Override
    public List<RepairItemVo> selectRepairItemList(String trainType, String itemName, String itemCode, Set<ItemKey> existItems, boolean include, Page<RepairItemVo> page) {
        return repairItemMapper.selectRepairItemList(
            trainType,
            StringUtils.isBlank(itemName) ? null : "%" + MybatisOgnlUtils.replaceWildcardChars(itemName) + "%",
            itemCode,
            existItems,
            include,
            page
        );
    }

    @Override
    public boolean useNewItem() {
        return ConfigUtil.isUseNewItem();
    }
}
