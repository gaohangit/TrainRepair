package com.ydzbinfo.emis.trainRepair.bill.general;

import com.ydzbinfo.emis.trainRepair.bill.general.base.IBillCellControlGeneral;

import java.util.List;
import java.util.Map;

/**
 * 触发调用接口返回的数据信息结构
 *
 * @author 张天可
 * @since 2021/8/19
 */
public interface IBillTriggerUrlCallResult<CELL extends IBillCellInfoForShowGeneral<CTRL>, CTRL extends IBillCellControlGeneral, T> {

    /**
     * 变更单元格信息
     */
    List<CELL> getChangedCells();

    void setChangedCells(List<CELL> changedCells);

    /**
     * 额外信息，为null时不进行额外信息的更新
     */
    T getExtraObject();

    void setExtraObject(T extraObject);

    /**
     * 是否允许当前单元格变更（仅触发时机为1时有效）（递归调用时仅第一层有效）
     */
    Boolean getAllowChange();

    void setAllowChange(Boolean allowChange);

    /**
     * 操作结束提示信息
     */
    String getOperationResultMessage();

    void setOperationResultMessage(String operationResultMessage);

    /**
     * 下一个要调用的url信息
     */
    UrlInfo getNextUrlInfo();

    void setNextUrlInfo(UrlInfo nextUrlInfo);

    /**
     * url调用前需要用户进行确认的信息
     */
    String getNextUrlConfirmMessage();

    void setNextUrlConfirmMessage(String nextUrlConfirmMessage);

    /**
     * 下一个要调用的url信息的url参数
     */
    Map<String, String> getNextUrlParams();

    void setNextUrlParams(Map<String, String> urlParams);

}
