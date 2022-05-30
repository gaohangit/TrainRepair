package com.ydzbinfo.emis.trainRepair.bill.fillback.service;

import com.ydzbinfo.emis.trainRepair.bill.fillback.utils.AttributeEnum;
import com.ydzbinfo.emis.trainRepair.bill.model.bill.ChecklistTriggerUrlCallResult;
import com.ydzbinfo.emis.trainRepair.bill.model.bill.SummaryInfoForSave;
import com.ydzbinfo.emis.trainRepair.bill.model.bill.SummaryInfoForShow;

/**
 * @description:
 * @date: 2021/12/1
 * @author: 冯帅
 */
public interface INoPlanTaskCheckService {

    SummaryInfoForShow getNoPlanTaskSummaryInfo(String summaryId,String templateTypeCode);

    ChecklistTriggerUrlCallResult saveNoPlanTaskRepairCell(SummaryInfoForSave summaryInfoForSave);

}
