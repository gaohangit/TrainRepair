package com.ydzbinfo.emis.trainRepair.bill.general;

import com.ydzbinfo.emis.trainRepair.bill.general.constant.BillCellTriggerUrlTypeEnum;
import lombok.Data;

/**
 * url信息
 *
 * @author 张天可
 * @since 2021/6/25
 */
@Data
public class UrlInfo {

    /**
     * url值
     */
    private String url;
    /**
     * TODO 未设置枚举类
     * url类型 (1—方法, 2—页面)
     */
    private BillCellTriggerUrlTypeEnum urlType;
}
