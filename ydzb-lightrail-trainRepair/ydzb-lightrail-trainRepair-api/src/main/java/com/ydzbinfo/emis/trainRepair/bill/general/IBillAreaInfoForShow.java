package com.ydzbinfo.emis.trainRepair.bill.general;

import com.ydzbinfo.emis.trainRepair.bill.general.base.IBillAreaGeneral;

/**
 * @author 张天可
 * @since 2021/8/19
 */
public interface IBillAreaInfoForShow extends IBillAreaGeneral {
    /**
     * 区域信息id
     */
    String getId();

    void setId(String id);
}
