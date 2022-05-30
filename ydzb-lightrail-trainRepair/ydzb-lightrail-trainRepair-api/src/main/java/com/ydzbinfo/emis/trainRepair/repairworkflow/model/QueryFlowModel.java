package com.ydzbinfo.emis.trainRepair.repairworkflow.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 查询流程实体类封装，sql查询专用
 *
 * @author 张天可
 * @since 2021/6/29
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class QueryFlowModel extends QueryFlowModelGeneral {
    private Boolean excludeSubflow = true;
    private Boolean showDeleted = false;
}
