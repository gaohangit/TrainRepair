package com.ydzbinfo.emis.trainRepair.repairworkflow.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 展示节点信息和节点记录信息，在简单展示（非流程图展示）时使用
 * @author 张天可
 * @description
 * @createDate 2021/5/18 9:47
 **/
@Data
@EqualsAndHashCode(callSuper = true)
public class NodeInfoForSimpleShow extends NodeWithRecord {

    /**
     * 子节点信息
     */
    private List<NodeInfoForSimpleShow> children;

}
