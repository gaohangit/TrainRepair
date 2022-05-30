package com.ydzbinfo.emis.trainRepair.bill.model.bill;

import com.ydzbinfo.emis.trainRepair.bill.general.BillCellChangeInfo;
import com.ydzbinfo.emis.trainRepair.bill.general.IBillSummaryInfoForSaveGeneral;
import com.ydzbinfo.emis.trainRepair.bill.general.IBillTriggerInfoGeneral;
import com.ydzbinfo.emis.trainRepair.bill.querymodel.checklist.ChecklistLinkControl;
import lombok.Data;

import java.util.List;

/**
 * @description: 记录单-主表泛型化的后台保存实体
 * @date: 2021/11/30
 * @author: 冯帅
 */
@Data
public class SummaryInfoForSave<T> implements
    IBillSummaryInfoForSaveGeneral<ChecklistDetailInfoForSave, ChecklistLinkControl, ChkDetailLinkContentForModule, ChecklistAreaInfoForSave, T>,
    IBillTriggerInfoGeneral<ChecklistDetailInfoForSave, ChecklistLinkControl, ChkDetailLinkContentForModule, ChecklistAreaInfoForSave, T> {

    //当前单元格
    private List<BillCellChangeInfo<ChecklistDetailInfoForSave>> triggerCells;
    //总表数据
    private T extraObject;
    //模板类型
    private String templateType;

    private List<ChecklistDetailInfoForSave> cells;

    private List<ChecklistAreaInfoForSave> areas;
}
