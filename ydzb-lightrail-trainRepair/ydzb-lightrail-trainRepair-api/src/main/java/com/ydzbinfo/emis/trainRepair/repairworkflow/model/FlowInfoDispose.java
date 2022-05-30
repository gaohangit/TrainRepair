package com.ydzbinfo.emis.trainRepair.repairworkflow.model;

import lombok.Data;

/**
 * @author 高晗
 * @description
 * @createDate 2022/4/24 9:34
 **/
@Data
public class FlowInfoDispose extends FlowInfo{
    private Boolean confirmCancelUsable = false;
}
