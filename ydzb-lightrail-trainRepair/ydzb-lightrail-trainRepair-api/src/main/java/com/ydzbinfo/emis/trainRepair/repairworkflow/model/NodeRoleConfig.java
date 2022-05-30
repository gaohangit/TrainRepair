package com.ydzbinfo.emis.trainRepair.repairworkflow.model;

import com.ydzbinfo.emis.trainRepair.repairworkflow.model.base.RoleBase;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class NodeRoleConfig extends RoleBase {

    /**
     * 当前角色最小处理人数
     */
    private Integer minNum;

    /**
     * 排序值
     */
    private  int sort;
}
