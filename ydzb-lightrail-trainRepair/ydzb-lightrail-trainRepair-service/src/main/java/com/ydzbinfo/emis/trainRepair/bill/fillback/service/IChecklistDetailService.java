package com.ydzbinfo.emis.trainRepair.bill.fillback.service;

import com.baomidou.mybatisplus.service.IService;
import com.ydzbinfo.emis.trainRepair.bill.querymodel.checklist.ChecklistDetail;

import java.util.List;

/**
 * <p>
 * 记录单详细信息表 服务类
 * </p>
 *
 * @author 张天可
 * @since 2021-08-27
 */
public interface IChecklistDetailService extends IService<ChecklistDetail> {
    void updateByPrimaryKey(List<ChecklistDetail> ds);
}
