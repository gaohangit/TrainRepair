package com.ydzbinfo.emis.trainRepair.bill.model.bill;

import com.ydzbinfo.emis.trainRepair.bill.general.IBillCellInfoForSaveGeneral;
import com.ydzbinfo.emis.trainRepair.bill.general.constant.BillEntityChangeTypeEnum;
import com.ydzbinfo.emis.trainRepair.bill.querymodel.checklist.ChecklistDetail;
import com.ydzbinfo.emis.trainRepair.bill.querymodel.checklist.ChecklistLinkControl;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * @Description:
 * @Data: 2021/7/31
 * @Author: 韩旭
 */

@EqualsAndHashCode(callSuper = true)
@Data
public class ChecklistDetailInfoForSave extends ChecklistDetail implements IBillCellInfoForSaveGeneral<ChecklistLinkControl, ChkDetailLinkContentForModule> {

    private BillEntityChangeTypeEnum changeType;

    private List<ChkDetailLinkContentForModule> linkCells;

    private List<ChecklistLinkControl> controls;

}
