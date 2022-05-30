package com.ydzbinfo.emis.trainRepair.repairworkflow.model;

import lombok.Data;

/**
 * @author gaohan
 * @description
 * @createDate 2021/4/29 17:14
 **/
@Data
public class PairNodeInfo {
    /**
     * 成对节点id
     */
    private String id;

    /**
     * 成对节点 节点id
     */
    private String[] nodeIds;
}
