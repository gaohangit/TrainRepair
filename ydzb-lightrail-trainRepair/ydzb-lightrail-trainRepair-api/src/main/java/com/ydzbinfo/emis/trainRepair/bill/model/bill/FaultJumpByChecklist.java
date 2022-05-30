package com.ydzbinfo.emis.trainRepair.bill.model.bill;

import lombok.Data;

@Data
public class FaultJumpByChecklist {
    //记录单ID
    private String checklistSummaryId;
    //记录单明细ID
    private String detailId;
    //日计划ID
    private String dayPlanId;
    //车组ID
    private String trainsetId;
    //故障ID
    private String faultId;
    //录入人ID
    private String stuffId;
}
