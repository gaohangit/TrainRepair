package com.ydzbinfo.emis.trainRepair.repairworkflow.model;

import com.ydzbinfo.emis.trainRepair.repairworkflow.model.base.RoleBase;
import com.ydzbinfo.emis.utils.entity.ToStringUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 流程运行时的流程信息，如果是岗位类型，则需要所在车组信息
 *
 * @author 张天可
 * @description
 * @createDate 2021/6/10 15:48
 **/
@EqualsAndHashCode(callSuper = true)
@Data
public class RuntimeRole extends RoleBase {

    /**
     * 派工岗位所在车组
     */
    private String trainsetId;

    @Override
    public String toString() {
        return ToStringUtil.toString(this);
    }
}
