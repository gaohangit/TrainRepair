package com.ydzbinfo.emis.trainRepair.bill.fillback.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.ydzbinfo.emis.trainRepair.bill.querymodel.checklist.ChecklistArea;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author 张天可
 * @since 2021-07-24
 */
public interface ChecklistAreaMapper extends BaseMapper<ChecklistArea> {
    void deleteList(List<ChecklistArea> list);
}
