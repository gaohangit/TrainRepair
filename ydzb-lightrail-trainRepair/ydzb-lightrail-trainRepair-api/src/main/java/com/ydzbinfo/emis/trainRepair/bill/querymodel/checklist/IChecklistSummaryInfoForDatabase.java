package com.ydzbinfo.emis.trainRepair.bill.querymodel.checklist;

import com.ydzbinfo.emis.trainRepair.bill.general.base.IBillCellInfoGeneral;
import com.ydzbinfo.emis.trainRepair.bill.general.base.IBillAreaGeneral;

import java.util.List;

/**
 * 由于数据库无法直接查询出全部所需信息，这里为其提炼出一个简版接口
 *
 * @author 张天可
 * @since 2021/6/25
 */
public interface IChecklistSummaryInfoForDatabase<CONTENT extends IBillCellInfoGeneral, AREA extends IBillAreaGeneral> {

    List<CONTENT> getContents();

    List<AREA> getAreas();
}
