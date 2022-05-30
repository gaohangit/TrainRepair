package com.ydzbinfo.emis.trainRepair.bill.model.phoneBill;

import com.ydzbinfo.emis.trainRepair.bill.general.UrlInfo;
import com.ydzbinfo.emis.trainRepair.bill.querymodel.checklist.ChecklistSummary;
import java.util.List;
import java.util.Map;

public class PhoneChecklistTriggerUrlCallResult {
    /**
     * 变更单元格信息
     */
    private List<PhoneChecklistDetailInfo> changedCells;
    /**
     * 额外信息，为null时不进行额外信息的更新
     */
    private ChecklistSummary extraObject;
    /**
     * 是否允许当前单元格变更（仅触发时机为1时有效）（多次调用时仅第一次有效）
     */
    private Boolean allowChange;
    /**
     * 下一个要调用的url信息
     */
    private UrlInfo nextUrlInfo;
    /**
     * url调用前需要用户进行确认的信息
     */
    private String nextUrlConfirmMessage;

    /**
     * 下一个要调用的url信息的url参数
     */
    private Map<String, String> urlParams;

}
