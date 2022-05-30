package com.ydzbinfo.emis.trainRepair.repairworkflow.model;

import com.ydzbinfo.emis.trainRepair.repairworkflow.model.base.RoleBase;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 角色信息
 *
 * @author 张天可
 * @since 2021/7/6
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class RoleInfo extends RoleBase {

    String roleName;
}
