package com.ydzbinfo.emis.trainRepair.bill.general;

import com.ydzbinfo.emis.trainRepair.bill.general.base.IBillCellControlGeneral;
import com.ydzbinfo.emis.trainRepair.bill.general.base.IBillLinkCellInfoGeneral;
import com.ydzbinfo.emis.trainRepair.bill.general.base.IBillSummaryInfoGeneralBase;

/**
 * 记录单回填，保存时从前端获取到的单据数据实体类需要实现的接口
 * 保存用
 *
 * @author 张天可
 * @since 2021/6/25
 */
public interface IBillSummaryInfoForSaveGeneral<CELL extends IBillCellInfoForSaveGeneral<CTRL, LINK_CELL>, CTRL extends IBillCellControlGeneral, LINK_CELL extends IBillLinkCellInfoGeneral, AREA extends IBillAreaInfoForSave, T> extends IBillSummaryInfoGeneralBase<CELL, AREA, T> {

}
