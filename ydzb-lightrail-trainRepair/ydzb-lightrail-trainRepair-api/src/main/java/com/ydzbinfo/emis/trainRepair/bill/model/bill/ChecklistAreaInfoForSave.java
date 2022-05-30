package com.ydzbinfo.emis.trainRepair.bill.model.bill;

import com.ydzbinfo.emis.trainRepair.bill.general.IBillAreaInfoForSave;
import com.ydzbinfo.emis.trainRepair.bill.general.constant.BillEntityChangeTypeEnum;
import com.ydzbinfo.emis.trainRepair.bill.querymodel.checklist.ChecklistArea;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class ChecklistAreaInfoForSave extends ChecklistArea implements IBillAreaInfoForSave {

    private BillEntityChangeTypeEnum changeType;

}
