package com.ydzbinfo.emis.trainRepair.bill.model.bill;

import com.ydzbinfo.emis.trainRepair.bill.general.BillCellChangeInfo;
import com.ydzbinfo.emis.trainRepair.bill.general.IBillSummaryInfoForSaveGeneral;
import com.ydzbinfo.emis.trainRepair.bill.general.IBillTriggerInfoGeneral;
import com.ydzbinfo.emis.trainRepair.bill.querymodel.checklist.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.poi.ss.formula.functions.T;

import java.util.List;
import java.util.Map;

/**
 * @Description:
 * @Data: 2021/7/31
 * @Author: 韩旭
 */
@Data
public class ChecklistSummaryInfoForSave
    implements IBillSummaryInfoForSaveGeneral<ChecklistDetailInfoForSave,
    ChecklistLinkControl, ChkDetailLinkContentForModule, ChecklistAreaInfoForSave, ChecklistSummary> ,
    IBillTriggerInfoGeneral <ChecklistDetailInfoForSave, ChecklistLinkControl, ChkDetailLinkContentForModule,
        ChecklistAreaInfoForSave, ChecklistSummary> {
    //当前单元格
    private List<BillCellChangeInfo<ChecklistDetailInfoForSave>> triggerCells;

    private ChecklistSummary extraObject;

    //模板类型
    private String templateType;

    private List<ChecklistDetailInfoForSave> cells;

    private List<ChecklistAreaInfoForSave> areas;

}