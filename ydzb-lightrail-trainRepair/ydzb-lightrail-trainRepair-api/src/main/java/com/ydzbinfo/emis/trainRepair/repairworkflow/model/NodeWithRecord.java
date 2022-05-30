package com.ydzbinfo.emis.trainRepair.repairworkflow.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 节点信息，携带节点状态信息和节点记录信息
 *
 * @author gaohan
 * @description
 * @createDate 2021/5/18 9:47
 **/
@Data
@EqualsAndHashCode(callSuper = true)
public class NodeWithRecord extends NodeWithRecordBase<NodeRecordInfo> {

    /**
     * 是否完成
     */
    private Boolean finished;

    /**
     * 可处理的角色id列表
     */
    private List<String> couldDisposeRoleIds;
}
