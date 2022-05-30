package com.ydzbinfo.emis.common.repairItem.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author 张天可
 * @since 2021/12/14
 */
@Data
@AllArgsConstructor
@EqualsAndHashCode
public class ItemKey {
    //项目编号
    String itemCode;
    //车型
    String trainsetType;
    //批次
    String trainsetBatch;
}
