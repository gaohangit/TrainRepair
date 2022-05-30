package com.ydzbinfo.emis.trainRepair.bill.general;

import lombok.Data;

/**
 * 单元格变更信息包装对象
 *
 * @author 张天可
 * @since 2021/10/28
 */
@Data
public class BillCellChangeInfo<CELL extends IBillCellInfoForSaveGeneral<?, ?>> {

    /**
     * 单元格变更前信息
     */
    CELL beforeChangeCellInfo;

    /**
     * 单元格变更后信息
     */
    CELL afterChangeCellInfo;

}
