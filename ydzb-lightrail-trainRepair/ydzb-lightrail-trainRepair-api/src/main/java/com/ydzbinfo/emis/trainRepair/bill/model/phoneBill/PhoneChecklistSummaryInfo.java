package com.ydzbinfo.emis.trainRepair.bill.model.phoneBill;

import com.ydzbinfo.emis.trainRepair.bill.querymodel.checklist.ChecklistSummary;
import lombok.Data;

import java.util.List;

/**
 * @Description:
 * @Data: 2021/7/31
 * @Author: 韩旭
 */
@Data
public class PhoneChecklistSummaryInfo {

    //总表数据
    private ChecklistSummary extraObject;

    //表头显示内容   只支持文本，多条多行显示
    private List<String> titles;

    //检修工长和质检员签字
    private List<PhoneChecklistDetailInfo> signList;

    //明细数据
    private List<PhoneChecklistContentList> contents;
}
