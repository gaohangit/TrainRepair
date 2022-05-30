package com.ydzbinfo.emis.trainRepair.bill.model.phoneBill;


import com.ydzbinfo.emis.trainRepair.bill.querymodel.checklist.ChecklistSummary;
import lombok.Data;

import java.util.List;

@Data
public class PhoneChecklistSummaryInfoForSave {
    //总表数据
    private ChecklistSummary extraObject;

    //有改动的明细表数据集合
    List<PhoneChecklistDetailInfo> updateDetailList;

    //工长/质检员签字，单元格签字之前数据
    PhoneChecklistDetailInfo beforePhoneDetailInfo;
    //工长/质检员签字，单元格签字之后数据
    PhoneChecklistDetailInfo afterPhoneDetailInfo;

}
