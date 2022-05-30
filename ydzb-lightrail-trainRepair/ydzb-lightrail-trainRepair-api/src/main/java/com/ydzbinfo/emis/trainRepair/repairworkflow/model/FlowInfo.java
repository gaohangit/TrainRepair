package com.ydzbinfo.emis.trainRepair.repairworkflow.model;

import com.ydzbinfo.emis.trainRepair.repairworkflow.model.base.FlowInfoBase;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 完整流程配置信息
 *
 * @author 张天可
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class FlowInfo extends FlowInfoBase implements IFlowBaseWithCondition {

    /**
     * 节点信息列表
     */
    private List<NodeInfo> nodes;

    /**
     * 车型条件
     */
    private List<String> trainTypes;

    /**
     * 车型条件是否为排除模式
     */
    private Boolean trainTypeExclude;

    /**
     * 批次条件
     */
    private List<String> trainTemplates;

    /**
     * 批次条件是否为排除模式
     */
    private Boolean trainTemplateExclude;

    /**
     * 车组条件
     */
    private List<String> trainsetIds;

    /**
     * 车组条件是否为排除模式
     */
    private Boolean trainsetIdExclude;

    /**
     * 关键字条件
     */
    private List<String> keyWords;

    /**
     * 检修类型
     */
    private String repairType;

}
