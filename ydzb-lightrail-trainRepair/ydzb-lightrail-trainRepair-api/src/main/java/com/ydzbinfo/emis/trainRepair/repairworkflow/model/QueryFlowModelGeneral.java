package com.ydzbinfo.emis.trainRepair.repairworkflow.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author 张天可
 * @since 2021/6/29
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QueryFlowModelGeneral {
    private String flowName;
    private String[] flowTypeCodes;
    private String[] flowIds;
    private String unitCode;
    private String usable;
    private Boolean showDeleted = false;

    /**
     * 可指定必查flowId
     */
    private String[] orFlowIds;
}
