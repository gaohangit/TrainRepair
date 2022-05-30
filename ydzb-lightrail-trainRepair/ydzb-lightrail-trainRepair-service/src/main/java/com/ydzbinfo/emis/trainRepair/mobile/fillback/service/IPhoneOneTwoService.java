package com.ydzbinfo.emis.trainRepair.mobile.fillback.service;

import com.baomidou.mybatisplus.service.IService;
import com.ydzbinfo.emis.trainRepair.bill.model.bill.ChecklistTriggerUrlCallResult;
import com.ydzbinfo.emis.trainRepair.bill.model.bill.OneTwoRepairCheckList;
import com.ydzbinfo.emis.trainRepair.bill.model.bill.QueryCheckListSummary;
import com.ydzbinfo.emis.trainRepair.bill.model.phoneBill.PhoneChecklistSummaryInfo;
import com.ydzbinfo.emis.trainRepair.bill.model.phoneBill.PhoneChecklistSummaryInfoForSave;
import com.ydzbinfo.emis.trainRepair.bill.model.phoneBill.PhoneOneTwoRepairCheck;
import com.ydzbinfo.emis.trainRepair.bill.querymodel.checklist.ChecklistSummary;

/**
 * @description: 手持机记录单列表查询接口类
 * @date: 2021/10/27
 * @author: 冯帅
 */
public interface IPhoneOneTwoService extends IService<ChecklistSummary> {

    PhoneOneTwoRepairCheck getPhoneCheckList(QueryCheckListSummary queryCheckListSummary);

    PhoneChecklistSummaryInfo getPhoneCheckDetailInfo(OneTwoRepairCheckList queryModel);

    ChecklistTriggerUrlCallResult savePhoneRepairCell(PhoneChecklistSummaryInfoForSave phoneChecklistSummaryInfoForSave);

    ChecklistTriggerUrlCallResult setPhoneOneTwoRepairForemanSign(PhoneChecklistSummaryInfoForSave phoneSaveInfo);

    ChecklistTriggerUrlCallResult setPhoneOneTwoRepairQualitySign(PhoneChecklistSummaryInfoForSave phoneSaveInfo);

    ChecklistTriggerUrlCallResult setPhoneOneTwoRepairPersonSign(PhoneChecklistSummaryInfoForSave phoneSaveInfo);
}
