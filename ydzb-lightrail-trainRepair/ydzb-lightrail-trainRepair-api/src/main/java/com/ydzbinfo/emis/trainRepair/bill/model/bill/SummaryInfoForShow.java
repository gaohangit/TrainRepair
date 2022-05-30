package com.ydzbinfo.emis.trainRepair.bill.model.bill;

import com.ydzbinfo.emis.trainRepair.bill.general.IBillSummaryInfoForShowGeneral;
import com.ydzbinfo.emis.trainRepair.bill.querymodel.checklist.ChecklistLinkControl;
import lombok.Data;

import java.util.List;

/**
 * @description: 记录单-主表泛型化的前台展示实体
 * @date: 2021/11/30
 * @author: 冯帅
 */
@Data
public class SummaryInfoForShow<T> implements IBillSummaryInfoForShowGeneral<ChecklistDetailInfoForShow, ChecklistLinkControl, ChecklistAreaInfoForShow, T> {
    //总表数据
    private T extraObject;
    //模板类型
    private String templateType;
    //内容数据
    private List<ChecklistDetailInfoForShow> cells;
    //区域数据
    private List<ChecklistAreaInfoForShow> areas;
    //是否显示导入按钮
    private boolean showImportButton;
}
