package com.ydzbinfo.emis.trainRepair.bill.fillback.service;

import com.baomidou.mybatisplus.service.IService;
import com.ydzbinfo.emis.trainRepair.bill.model.bill.OneTwoRepairCheckList;
import com.ydzbinfo.emis.trainRepair.bill.model.bill.QueryCheckListSummary;
import com.ydzbinfo.emis.trainRepair.bill.model.bill.QueryChecklistQueryCondition;
import com.ydzbinfo.emis.trainRepair.bill.querymodel.checklist.ChecklistSummary;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author 韩旭
 * @since 2021-07-24
 */
public interface IChecklistSummaryService extends IService<ChecklistSummary> {
   List<OneTwoRepairCheckList> getChecklistSummaryList(QueryCheckListSummary queryCheckListSummary);

   String getChecklistSource();

   QueryChecklistQueryCondition getChecklistQueryCondition(QueryCheckListSummary queryCheckListSummary,String type);
}
