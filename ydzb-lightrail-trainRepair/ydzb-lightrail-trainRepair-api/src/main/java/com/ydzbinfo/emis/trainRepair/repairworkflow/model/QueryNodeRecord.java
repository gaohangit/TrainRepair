package com.ydzbinfo.emis.trainRepair.repairworkflow.model;

import lombok.Data;

/**
 * 节点记录查询对象
 *
 * @author 张天可
 * @since 2021/7/2
 */
@Data
public class QueryNodeRecord {
    private String dayPlanId;
    private String[] flowRunIds;
    private String[] nodeIds;
}
