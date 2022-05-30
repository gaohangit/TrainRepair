package com.ydzbinfo.emis.trainRepair.bill.general;

import com.ydzbinfo.emis.trainRepair.bill.general.base.IBillCellControlGeneral;
import com.ydzbinfo.emis.trainRepair.bill.general.base.IBillLinkCellInfoGeneral;
import com.ydzbinfo.emis.trainRepair.bill.general.base.IBillSummaryInfoGeneralBase;

import java.util.List;

/**
 * 记录单回填，触发单元格时调用url接口时传递的信息结构，除了保存信息和额外信息外，还有触发的单元格信息
 *
 * @author 张天可
 * @since 2021/8/19
 */
public interface IBillTriggerInfoGeneral<
    CELL extends IBillCellInfoForSaveGeneral<CTRL, LINK_CELL>,
    CTRL extends IBillCellControlGeneral,
    LINK_CELL extends IBillLinkCellInfoGeneral,
    AREA extends IBillAreaInfoForSave,
    T
    > extends IBillSummaryInfoGeneralBase<CELL, AREA, T> {

    /**
     * 获取触发单元格信息
     */
    List<BillCellChangeInfo<CELL>> getTriggerCells();

    /**
     * 设置触发单元格信息
     */
    void setTriggerCells(List<BillCellChangeInfo<CELL>> triggerCells);

}
