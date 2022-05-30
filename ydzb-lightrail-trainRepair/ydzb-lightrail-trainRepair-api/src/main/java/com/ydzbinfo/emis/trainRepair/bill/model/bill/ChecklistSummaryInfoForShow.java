package com.ydzbinfo.emis.trainRepair.bill.model.bill;

import com.ydzbinfo.emis.trainRepair.bill.general.IBillSummaryInfoForShowGeneral;
import com.ydzbinfo.emis.trainRepair.bill.querymodel.checklist.ChecklistLinkControl;
import com.ydzbinfo.emis.trainRepair.bill.querymodel.checklist.ChecklistSummary;
import lombok.Data;

import java.util.List;

/**
 * @Description:
 * @Data: 2021/7/31
 * @Author: 韩旭
 */
@Data
public class ChecklistSummaryInfoForShow
    implements IBillSummaryInfoForShowGeneral<ChecklistDetailInfoForShow, ChecklistLinkControl,
    ChecklistAreaInfoForShow, ChecklistSummary> {
    //总表数据
    private ChecklistSummary extraObject;
    //模板类型
    private String templateType;
    //内容数据
    private List<ChecklistDetailInfoForShow> cells;
    //区域数据
    private List<ChecklistAreaInfoForShow> areas;
    //是否显示导入按钮
    private boolean showImportButton;
    //是否显示保存按钮
    private boolean showSaveButton;
}