package com.ydzbinfo.emis.trainRepair.trainMonitor.model;

import lombok.Data;

import java.util.List;

/**
 * @author 高晗
 * @description 检修信息
 * @createDate 2021/12/22 9:32
 **/
@Data
public class LevelRepairInfo {
    private  String trainsetId;
    /**
     * 运用修
     */
    List<UnitLevelRepairInfo> unitLevelRepairInfos;

    /**
     * 高级修
     */
    List<HighLevelRepairInfo> highLevelRepairInfos;
}
