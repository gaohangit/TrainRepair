package com.ydzbinfo.emis.trainRepair.bill.fillback.service;

import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.IService;
import com.ydzbinfo.emis.trainRepair.bill.model.bill.Integration.IntegrationSummary;
import com.ydzbinfo.emis.trainRepair.bill.model.bill.Integration.InterationQueryCondition;
import com.ydzbinfo.emis.trainRepair.bill.querymodel.checklist.ChecklistIntegration;

/**
 * <p>
 * 一体化作业申请单总表 服务类
 * </p>
 *
 * @author 韩旭
 * @since 2021-11-23
 */
public interface IChecklistIntegrationService extends IService<ChecklistIntegration> {

    Page<IntegrationSummary> getChecklistSummaryList(InterationQueryCondition queryCheckListSummary);
    
}
