package com.ydzbinfo.emis.trainRepair.bill.fillback.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.ydzbinfo.emis.trainRepair.bill.querymodel.checklist.ChecklistLinkControl;

import java.util.List;

/**
 * <p>
 * 单据明细内容卡控表 Mapper 接口
 * </p>
 *
 * @author 张天可
 * @since 2021-08-27
 */
public interface ChecklistLinkControlMapper extends BaseMapper<ChecklistLinkControl> {
    void deleteList(List<String> list);
}
