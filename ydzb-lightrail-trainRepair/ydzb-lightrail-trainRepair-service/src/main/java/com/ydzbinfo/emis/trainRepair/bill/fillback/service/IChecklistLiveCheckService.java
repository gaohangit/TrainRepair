package com.ydzbinfo.emis.trainRepair.bill.fillback.service;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.IService;
import com.ydzbinfo.emis.trainRepair.bill.model.bill.ChecklistTriggerUrlCallResult;
import com.ydzbinfo.emis.trainRepair.bill.model.bill.LiveCheck.LiveCheckQueryCondition;
import com.ydzbinfo.emis.trainRepair.bill.model.bill.LiveCheck.LiveCheckSummary;
import com.ydzbinfo.emis.trainRepair.bill.model.bill.SummaryInfoForSave;
import com.ydzbinfo.emis.trainRepair.bill.querymodel.checklist.ChecklistLiveCheck;

/**
 * <p>
 * 出所联检单总表 服务类
 * </p>
 *
 * @author 韩旭
 * @since 2021-11-23
 */
public interface IChecklistLiveCheckService extends IService<ChecklistLiveCheck> {

    Page<LiveCheckSummary> getLiveCheckSummaryList(LiveCheckQueryCondition queryCheckListSummary);


    ChecklistTriggerUrlCallResult changeTrainnoByTrainset(SummaryInfoForSave summaryInfoForSave);
}
