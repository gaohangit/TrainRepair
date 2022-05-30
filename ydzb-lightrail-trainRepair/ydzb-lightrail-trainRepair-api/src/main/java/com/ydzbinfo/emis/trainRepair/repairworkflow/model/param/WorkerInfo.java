package com.ydzbinfo.emis.trainRepair.repairworkflow.model.param;

import com.ydzbinfo.emis.trainRepair.repairworkflow.model.RoleInfo;
import lombok.Data;

/**
 * @author 张天可
 * @since 2021/7/6
 */
@Data
public class WorkerInfo {
    /**
     * 用户ID
     */
    private String workerId;
    /**
     * 用户名称
     */
    private String workerName;

    /**
     * 班组code
     */
    private String teamCode;

    /**
     * 班组名称
     */
    private String teamName;

    /**
     * 角色信息
     */
    private RoleInfo roleInfo;
}
