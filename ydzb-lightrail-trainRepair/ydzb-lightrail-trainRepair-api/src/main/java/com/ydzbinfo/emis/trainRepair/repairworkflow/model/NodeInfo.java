package com.ydzbinfo.emis.trainRepair.repairworkflow.model;

import com.ydzbinfo.emis.trainRepair.repairworkflow.model.base.RoleBase;
import com.ydzbinfo.emis.trainRepair.repairworkflow.querymodel.base.BaseNode;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 完整节点配置信息
 *
 * @author 张天可
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class NodeInfo extends BaseNode {

    /**
     * 角色配置信息
     */
    private List<NodeRoleConfig> roleConfigs;

    /**
     * 排除角色id列表
     */
    private List<RoleBase> excludeRoles;

    /**
     * 派工限定
     */
    private Boolean onlyDispatching;

    /**
     * 是否能跳过
     */
    private Boolean skip;

    /**
     * 跳过后是否可补卡
     */
    private Boolean disposeAfterSkip;

    /**
     * 最小处理人数
     */
    private Integer minDisposeNum;

    /**
     * 建议上传图片数量
     */
    private Integer recommendedPicNum;

    /**
     * 是否按子流程处理节点
     */
    private Boolean disposeSubflow;

    /**
     * 业务类型
     */
    private String functionType;

    /**
     * 据上节点超时预警
     */
    private Integer overtimeWaring;

    /**
     * 据上节点最小时间间隔
     */
    private Integer minIntervalRestrict;
}
