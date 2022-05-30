package com.ydzbinfo.emis.trainRepair.bill.general;

import com.ydzbinfo.emis.trainRepair.bill.general.base.IBillCellControlGeneral;
import com.ydzbinfo.emis.trainRepair.bill.general.base.IBillSummaryInfoGeneralBase;

/**
 * 记录单回填，返回给前端的记录单数据实体类需要实现的接口
 * 展示用
 *
 * @author 张天可
 * @since 2021/6/25
 */
public interface IBillSummaryInfoForShowGeneral <CELL extends IBillCellInfoForShowGeneral<CTRL>, CTRL extends IBillCellControlGeneral, AREA extends IBillAreaInfoForShow, T>  extends IBillSummaryInfoGeneralBase<CELL, AREA, T> {

}
