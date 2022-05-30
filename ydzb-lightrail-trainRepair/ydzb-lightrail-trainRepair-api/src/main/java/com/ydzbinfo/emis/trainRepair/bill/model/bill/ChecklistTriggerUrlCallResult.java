package com.ydzbinfo.emis.trainRepair.bill.model.bill;

import com.ydzbinfo.emis.trainRepair.bill.general.IBillTriggerUrlCallResult;
import com.ydzbinfo.emis.trainRepair.bill.general.UrlInfo;
import com.ydzbinfo.emis.trainRepair.bill.querymodel.checklist.ChecklistLinkControl;
import com.ydzbinfo.emis.trainRepair.bill.querymodel.checklist.ChecklistSummary;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class ChecklistTriggerUrlCallResult<T>
    implements IBillTriggerUrlCallResult<ChecklistDetailInfoForShow, ChecklistLinkControl, T> {

    /**
     * 变更单元格信息
     */
    private List<ChecklistDetailInfoForShow> changedCells;

    /**
     * 额外信息，为null时不进行额外信息的更新
     */
    private T extraObject;

    //模板类型
    private String templateType;

    /**
     * 是否允许当前单元格变更（仅触发时机为1时有效）（多次调用时仅第一次有效）
     */
    private Boolean allowChange;

    /**
     * 操作结束提示信息
     */
    private String operationResultMessage;

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
    private Map<String, String> nextUrlParams;

}
