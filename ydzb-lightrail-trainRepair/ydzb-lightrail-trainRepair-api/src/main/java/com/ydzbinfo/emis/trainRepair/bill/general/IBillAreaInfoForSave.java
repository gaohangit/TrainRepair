package com.ydzbinfo.emis.trainRepair.bill.general;

import com.ydzbinfo.emis.trainRepair.bill.general.base.IBillAreaGeneral;
import com.ydzbinfo.emis.trainRepair.bill.general.constant.BillEntityChangeTypeEnum;

/**
 * @author 张天可
 * @since 2021/8/19
 */
public interface IBillAreaInfoForSave extends IBillAreaGeneral {
    /**
     * 区域信息id
     */
    String getId();

    void setId(String id);

    /**
     * 区域变更类型
     */
    BillEntityChangeTypeEnum getChangeType();

    void setChangeType(BillEntityChangeTypeEnum changeType);
}
